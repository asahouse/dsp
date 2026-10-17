package org.codeworks.dsp.openrtb.service.impl;

import org.codeworks.dsp.openrtb.service.OpenRtbTrackingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * OpenRTB 点击追踪与 S2S 转化归因服务实现类
 * <p>
 * 依托 Redis 存储 7 天点击窗口归因令牌 (click_id)，支持转化幂等排重，
 * 实时累计点击量、转化量与 GMV 统计，并通过 RabbitMQ 削峰异步派发归因事件。
 * </p>
 *
 * @author benjaminkc
 */
@Service
public class OpenRtbTrackingServiceImpl implements OpenRtbTrackingService {

    private static final Logger logger = LoggerFactory.getLogger(OpenRtbTrackingServiceImpl.class);

    @Autowired(required = false)
    private StringRedisTemplate stringRedisTemplate;

    @Autowired(required = false)
    private AmqpTemplate amqpTemplate;

    /**
     * 点击上下文对象
     */
    public static class ClickContext implements Serializable {
        private static final long serialVersionUID = 1L;
        public String clickId;
        public String bidId;
        public Integer campaignId;
        public Integer advId;
        public Integer materialId;
        public long timestamp;

        public ClickContext() {}

        public ClickContext(String clickId, String bidId, Integer campaignId, Integer advId, Integer materialId, long timestamp) {
            this.clickId = clickId;
            this.bidId = bidId;
            this.campaignId = campaignId;
            this.advId = advId;
            this.materialId = materialId;
            this.timestamp = timestamp;
        }
    }

    /** 内存降级点击令牌缓存 (TTL 自动周期性清理) */
    private final Map<String, ClickContext> inMemoryClicks = new ConcurrentHashMap<>();

    /** 内存转化去重排重集合 */
    private final Set<String> inMemoryConvertedClicks = Collections.newSetFromMap(new ConcurrentHashMap<>());

    /** 内存实时点击量统计 */
    private final Map<String, Long> inMemoryClickCounts = new ConcurrentHashMap<>();

    /** 内存实时转化量统计 */
    private final Map<String, Long> inMemoryConvCounts = new ConcurrentHashMap<>();

    /** 内存实时转化金额 (GMV) 统计 */
    private final Map<String, Double> inMemoryConvRevenues = new ConcurrentHashMap<>();

    @Override
    public String recordClick(String bidId, Integer campaignId, Integer advId, Integer materialId, String redirectUrl) {
        String clickId = "clk-" + UUID.randomUUID().toString().replace("-", "");
        long now = System.currentTimeMillis();
        ClickContext ctx = new ClickContext(clickId, bidId, campaignId, advId, materialId, now);

        String clickData = serializeClickContext(ctx);
        String clickKey = "openrtb:click:" + clickId;

        if (stringRedisTemplate != null) {
            try {
                stringRedisTemplate.opsForValue().set(clickKey, clickData, 7, TimeUnit.DAYS);
            } catch (Exception e) {
                logger.warn("Failed to store click in Redis, fallback to memory: {}", e.getMessage());
            }
        }
        if (inMemoryClicks.size() > 50000) {
            inMemoryClicks.clear();
        }
        inMemoryClicks.put(clickId, ctx);

        // 累加点击量计数
        if (campaignId != null) {
            LocalDate today = LocalDate.now();
            String statsKey = "openrtb:stats:clicks:" + campaignId + ":" + today;
            if (stringRedisTemplate != null) {
                try {
                    stringRedisTemplate.opsForValue().increment(statsKey, 1);
                    stringRedisTemplate.expire(statsKey, 30, TimeUnit.DAYS);
                } catch (Exception e) {
                    logger.debug("Failed to increment click stats in Redis: {}", e.getMessage());
                }
            }
            inMemoryClickCounts.compute(statsKey, (k, v) -> (v == null ? 0L : v) + 1L);
        }

        // 组装最终携带 click_id 参数的跳转 URL
        String target = (redirectUrl != null && !redirectUrl.trim().isEmpty()) ? redirectUrl.trim() : "https://www.example.com";
        String separator = target.contains("?") ? "&" : "?";
        String finalUrl = target + separator + "click_id=" + clickId;

        logger.info("Recorded click: clickId={}, bidId={}, campaignId={}, advId={}, materialId={}, redirect={}",
                clickId, bidId, campaignId, advId, materialId, finalUrl);

        return finalUrl;
    }

    @Override
    public Map<String, Object> attributeConversion(String clickId, String convType, Double orderAmount) {
        Map<String, Object> result = new HashMap<>();
        if (clickId == null || clickId.trim().isEmpty()) {
            result.put("status", "error");
            result.put("message", "Missing click_id");
            return result;
        }

        String cleanClickId = clickId.trim();
        String clickKey = "openrtb:click:" + cleanClickId;
        ClickContext ctx = null;

        if (stringRedisTemplate != null) {
            try {
                String val = stringRedisTemplate.opsForValue().get(clickKey);
                if (val != null) {
                    ctx = deserializeClickContext(val);
                }
            } catch (Exception e) {
                logger.debug("Failed to query click from Redis: {}", e.getMessage());
            }
        }

        if (ctx == null) {
            ctx = inMemoryClicks.get(cleanClickId);
        }

        if (ctx == null) {
            result.put("status", "unattributed");
            result.put("clickId", cleanClickId);
            result.put("message", "Click token not found or attribution window expired");
            logger.warn("Conversion unattributed: clickId={}", cleanClickId);
            return result;
        }

        // 转化幂等排重 (防重复计费或重复归因)
        String dedupeKey = "openrtb:conv:lock:" + cleanClickId;
        if (stringRedisTemplate != null) {
            try {
                Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent(dedupeKey, "1");
                if (Boolean.TRUE.equals(lock)) {
                    stringRedisTemplate.expire(dedupeKey, 7, TimeUnit.DAYS);
                    inMemoryConvertedClicks.add(cleanClickId);
                } else {
                    // Redis 明确已存在此 lock，说明已被记录过，直接判定为重复转化
                    result.put("status", "duplicate");
                    result.put("clickId", cleanClickId);
                    result.put("campaignId", ctx.campaignId);
                    result.put("message", "Conversion already recorded for this click_id");
                    logger.info("Ignored duplicate conversion from Redis for clickId={}", cleanClickId);
                    return result;
                }
            } catch (Exception e) {
                logger.warn("Redis conversion lock failed, fallback to memory: {}", e.getMessage());
                if (!inMemoryConvertedClicks.add(cleanClickId)) {
                    result.put("status", "duplicate");
                    result.put("clickId", cleanClickId);
                    result.put("campaignId", ctx.campaignId);
                    result.put("message", "Conversion already recorded for this click_id");
                    logger.info("Ignored duplicate conversion from memory for clickId={}", cleanClickId);
                    return result;
                }
            }
        } else {
            if (!inMemoryConvertedClicks.add(cleanClickId)) {
                result.put("status", "duplicate");
                result.put("clickId", cleanClickId);
                result.put("campaignId", ctx.campaignId);
                result.put("message", "Conversion already recorded for this click_id");
                logger.info("Ignored duplicate conversion from memory for clickId={}", cleanClickId);
                return result;
            }
        }

        // 累加转化量与转化金额
        LocalDate today = LocalDate.now();
        Integer campaignId = ctx.campaignId;
        double amount = (orderAmount != null && orderAmount > 0) ? orderAmount : 0.0;
        String type = (convType != null && !convType.trim().isEmpty()) ? convType.trim() : "purchase";

        if (campaignId != null) {
            String convCountKey = "openrtb:stats:conversions:" + campaignId + ":" + today;
            String convRevKey = "openrtb:stats:conv_revenue:" + campaignId + ":" + today;
            if (stringRedisTemplate != null) {
                try {
                    stringRedisTemplate.opsForValue().increment(convCountKey, 1);
                    stringRedisTemplate.expire(convCountKey, 30, TimeUnit.DAYS);
                    if (amount > 0) {
                        stringRedisTemplate.opsForValue().increment(convRevKey, amount);
                        stringRedisTemplate.expire(convRevKey, 30, TimeUnit.DAYS);
                    }
                } catch (Exception e) {
                    logger.debug("Failed to increment conversion stats in Redis: {}", e.getMessage());
                }
            }
            inMemoryConvCounts.compute(convCountKey, (k, v) -> (v == null ? 0L : v) + 1L);
            inMemoryConvRevenues.compute(convRevKey, (k, v) -> (v == null ? 0.0 : v) + amount);
        }

        // 异步派发事件至 RabbitMQ 削峰处理
        if (amqpTemplate != null) {
            try {
                String payload = String.format("{\"eventType\":\"CONVERSION\",\"clickId\":\"%s\",\"campaignId\":%d,\"advId\":%d,\"materialId\":%d,\"convType\":\"%s\",\"orderAmount\":%.2f,\"timestamp\":%d}",
                        cleanClickId, ctx.campaignId, ctx.advId, ctx.materialId, type, amount, System.currentTimeMillis());
                amqpTemplate.convertAndSend("campaign.log", payload);
            } catch (Exception e) {
                logger.warn("Failed to push conversion event to RabbitMQ: {}", e.getMessage());
            }
        }

        result.put("status", "success");
        result.put("clickId", cleanClickId);
        result.put("bidId", ctx.bidId);
        result.put("campaignId", ctx.campaignId);
        result.put("advId", ctx.advId);
        result.put("materialId", ctx.materialId);
        result.put("convType", type);
        result.put("orderAmount", amount);

        logger.info("Attributed conversion successfully: clickId={}, campaignId={}, advId={}, type={}, amount={}",
                cleanClickId, ctx.campaignId, ctx.advId, type, amount);

        return result;
    }

    @Override
    public long getClickCount(Integer campaignId, LocalDate date) {
        if (campaignId == null) return 0L;
        String key = "openrtb:stats:clicks:" + campaignId + ":" + date;
        if (stringRedisTemplate != null) {
            try {
                String val = stringRedisTemplate.opsForValue().get(key);
                if (val != null) return Long.parseLong(val);
            } catch (Exception ignored) {}
        }
        Long count = inMemoryClickCounts.get(key);
        return count != null ? count : 0L;
    }

    @Override
    public long getConversionCount(Integer campaignId, LocalDate date) {
        if (campaignId == null) return 0L;
        String key = "openrtb:stats:conversions:" + campaignId + ":" + date;
        if (stringRedisTemplate != null) {
            try {
                String val = stringRedisTemplate.opsForValue().get(key);
                if (val != null) return Long.parseLong(val);
            } catch (Exception ignored) {}
        }
        Long count = inMemoryConvCounts.get(key);
        return count != null ? count : 0L;
    }

    @Override
    public double getConversionRevenue(Integer campaignId, LocalDate date) {
        if (campaignId == null) return 0.0;
        String key = "openrtb:stats:conv_revenue:" + campaignId + ":" + date;
        if (stringRedisTemplate != null) {
            try {
                String val = stringRedisTemplate.opsForValue().get(key);
                if (val != null) return Double.parseDouble(val);
            } catch (Exception ignored) {}
        }
        Double rev = inMemoryConvRevenues.get(key);
        return rev != null ? rev : 0.0;
    }

    private String serializeClickContext(ClickContext ctx) {
        return String.format("%s|%s|%s|%s|%s|%d",
                ctx.clickId,
                ctx.bidId != null ? ctx.bidId : "",
                ctx.campaignId != null ? ctx.campaignId : "",
                ctx.advId != null ? ctx.advId : "",
                ctx.materialId != null ? ctx.materialId : "",
                ctx.timestamp);
    }

    private ClickContext deserializeClickContext(String data) {
        try {
            String[] parts = data.split("\\|", -1);
            if (parts.length >= 6) {
                String clickId = parts[0];
                String bidId = parts[1].isEmpty() ? null : parts[1];
                Integer campaignId = parts[2].isEmpty() ? null : Integer.parseInt(parts[2]);
                Integer advId = parts[3].isEmpty() ? null : Integer.parseInt(parts[3]);
                Integer materialId = parts[4].isEmpty() ? null : Integer.parseInt(parts[4]);
                long timestamp = Long.parseLong(parts[5]);
                return new ClickContext(clickId, bidId, campaignId, advId, materialId, timestamp);
            }
        } catch (Exception e) {
            logger.warn("Failed to deserialize ClickContext: {}", e.getMessage());
        }
        return null;
    }
}
