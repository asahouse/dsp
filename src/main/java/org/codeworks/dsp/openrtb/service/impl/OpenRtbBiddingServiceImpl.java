package org.codeworks.dsp.openrtb.service.impl;

import org.codeworks.dsp.dao.AdvertiserRepository;
import org.codeworks.dsp.dao.CampaignRepository;
import org.codeworks.dsp.dao.MaterialRepository;
import org.codeworks.dsp.dao.rtbMQ.RtbCampaignStatsRepository;
import org.codeworks.dsp.model.entities.Advertiser;
import org.codeworks.dsp.model.entities.Campaign;
import org.codeworks.dsp.model.entities.Material;
import org.codeworks.dsp.openrtb.dto.request.Banner;
import org.codeworks.dsp.openrtb.dto.request.BidRequest;
import org.codeworks.dsp.openrtb.dto.request.Format;
import org.codeworks.dsp.openrtb.dto.request.Imp;
import org.codeworks.dsp.openrtb.dto.request.Native;
import org.codeworks.dsp.openrtb.dto.request.Video;
import org.codeworks.dsp.openrtb.dto.response.Bid;
import org.codeworks.dsp.openrtb.dto.response.BidResponse;
import org.codeworks.dsp.openrtb.dto.response.SeatBid;
import org.codeworks.dsp.openrtb.filter.AntiFraudFilter;
import org.codeworks.dsp.openrtb.service.OpenRtbBiddingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * OpenRTB 2.5 实时出价与胜出结算服务实现
 * <p>
 * 支持多格式广告分流匹配 (Banner / Video VAST 3.0 / Native 1.2)、
 * 毫秒级预竞价反作弊过滤、用户级频控 (日/小时) 与平滑消耗 (EVEN / ASAP) 控制。
 * </p>
 *
 * @author benjaminkc
 */
@Service
public class OpenRtbBiddingServiceImpl implements OpenRtbBiddingService {

    private static final Logger logger = LoggerFactory.getLogger(OpenRtbBiddingServiceImpl.class);

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private AdvertiserRepository advertiserRepository;

    @Autowired(required = false)
    private RtbCampaignStatsRepository rtbCampaignStatsRepository;

    @Autowired(required = false)
    private StringRedisTemplate stringRedisTemplate;

    @Autowired(required = false)
    private AntiFraudFilter antiFraudFilter;

    /** 胜出幂等排重内存降级缓存 */
    private final Map<String, Long> inMemoryProcessedBids = new ConcurrentHashMap<>();

    /** 活动消耗统计内存降级缓存 */
    private final Map<String, Double> inMemoryCampaignSpend = new ConcurrentHashMap<>();

    /** 用户展示频控内存降级缓存 */
    private final Map<String, Long> inMemoryUserFreq = new ConcurrentHashMap<>();

    /** 出价元数据 (bidId -> userId) 暂存缓存 */
    private final Map<String, String> inMemoryBidMeta = new ConcurrentHashMap<>();

    @Override
    @Transactional(readOnly = true)
    public Optional<BidResponse> processBid(BidRequest request) {
        if (request == null || request.getImp() == null || request.getImp().isEmpty()) {
            return Optional.empty();
        }

        // 1. 预竞价反作弊校验 (机房 IP、Bot UA、动态黑名单)
        String ip = null;
        String ua = null;
        String ifa = null;
        if (request.getDevice() != null) {
            ip = request.getDevice().getIp();
            ua = request.getDevice().getUa();
            ifa = request.getDevice().getIfa();
        }

        if (antiFraudFilter != null && antiFraudFilter.isInvalidTraffic(ip, ua)) {
            logger.debug("BidRequest rejected by AntiFraudFilter: ip={}, ua={}", ip, ua);
            return Optional.empty();
        }

        // 2. 提取用户唯一受众标识 (IFA -> UID -> IP+UA MD5)
        String userId = extractUserId(ifa, request.getUser() != null ? request.getUser().getId() : null, ip, ua);

        // 3. 广告位规格与多媒体格式解析
        Imp imp = request.getImp().get(0);
        Banner banner = imp.getBanner();
        Video video = imp.getVideo();
        Native nativeObj = imp.getNativeObj();

        String reqFormat;
        int targetW = 0;
        int targetH = 0;

        if (video != null) {
            reqFormat = "VIDEO";
            targetW = video.getW() != null ? video.getW() : 640;
            targetH = video.getH() != null ? video.getH() : 360;
        } else if (nativeObj != null) {
            reqFormat = "NATIVE";
        } else if (banner != null) {
            reqFormat = "BANNER";
            targetW = banner.getW() != null ? banner.getW() : 300;
            targetH = banner.getH() != null ? banner.getH() : 250;
            if (banner.getFormat() != null && !banner.getFormat().isEmpty()) {
                Format fmt = banner.getFormat().get(0);
                if (fmt.getW() != null) targetW = fmt.getW();
                if (fmt.getH() != null) targetH = fmt.getH();
            }
        } else {
            // 未识别的广告格式
            return Optional.empty();
        }

        double bidfloor = imp.getBidfloor() != null ? imp.getBidfloor() : 0.0;
        String cur = imp.getBidfloorcur() != null ? imp.getBidfloorcur() : "USD";

        // 4. 查询数据库中所有有效排期与审核通过的投放活动
        LocalDate today = LocalDate.now();
        List<Campaign> campaigns = campaignRepository.findActiveCampaignsWithinDate(today, today);

        Campaign winningCampaign = null;
        Material winningMaterial = null;
        double highestCpm = bidfloor;

        for (Campaign campaign : campaigns) {
            Advertiser adv = campaign.getAdvertiser();
            if (adv == null || adv.getBalance() == null || adv.getBalance() <= 0) {
                continue;
            }

            double campaignBid = campaign.getBid() != null ? campaign.getBid().doubleValue() : 0.0;
            if (campaignBid < bidfloor) {
                continue;
            }

            // 4.1 广告活动预算与日预算超限熔断检查
            Double budget = campaign.getBudget();
            Campaign.BudgetType budgetType = campaign.getBudgetType() != null
                    ? campaign.getBudgetType()
                    : Campaign.BudgetType.all;
            if (budget != null && budget > 0) {
                double currentSpend = getCampaignSpend(campaign.getId(), budgetType, today);
                if (currentSpend >= budget) {
                    logger.debug("Campaign {} exceeded budget: spent={}, budget={}, type={}",
                            campaign.getId(), currentSpend, budget, budgetType);
                    continue;
                }

                // 4.2 平滑消耗 (Pacing) 节流检查
                String pacingMode = campaign.getPacingMode();
                if ("EVEN".equalsIgnoreCase(pacingMode)) {
                    double dailyBudget = (budgetType == Campaign.BudgetType.daily) ? budget : budget;
                    double hourlyBudget = dailyBudget / 24.0;
                    int currentHour = LocalTime.now().getHour();
                    double hourSpend = getCampaignHourlySpend(campaign.getId(), today, currentHour);
                    if (hourSpend >= hourlyBudget) {
                        logger.debug("Campaign {} hourly budget exhausted in EVEN mode: hourSpend={}, hourlyBudget={}",
                                campaign.getId(), hourSpend, hourlyBudget);
                        continue;
                    }

                    int minute = LocalTime.now().getMinute();
                    double expectedProgress = Math.max(0.1, (minute + 1) / 60.0);
                    double targetSpend = hourlyBudget * expectedProgress;
                    if (hourSpend > targetSpend) {
                        double passRatio = Math.max(0.05, targetSpend / (hourSpend + 0.01));
                        if (Math.random() > passRatio) {
                            logger.debug("Campaign {} throttled by smooth pacing (EVEN): hourSpend={}, targetSpend={}, passRatio={}",
                                    campaign.getId(), hourSpend, targetSpend, passRatio);
                            continue;
                        }
                    }
                }
            }

            // 4.3 用户展示频控核验 (每日与每小时频控)
            if (userId != null) {
                Integer capDaily = campaign.getFreqCapDaily();
                if (capDaily != null && capDaily > 0) {
                    long dailyCount = getUserImpressionCount(campaign.getId(), userId, "daily", today);
                    if (dailyCount >= capDaily) {
                        logger.debug("Campaign {} daily freq cap reached for user {}: count={}, cap={}",
                                campaign.getId(), userId, dailyCount, capDaily);
                        continue;
                    }
                }

                Integer capHourly = campaign.getFreqCapHourly();
                if (capHourly != null && capHourly > 0) {
                    int currentHour = LocalTime.now().getHour();
                    long hourlyCount = getUserImpressionCount(campaign.getId(), userId, "hour:" + currentHour, today);
                    if (hourlyCount >= capHourly) {
                        logger.debug("Campaign {} hourly freq cap reached for user {}: count={}, cap={}",
                                campaign.getId(), userId, hourlyCount, capHourly);
                        continue;
                    }
                }
            }

            // 4.4 物料审核状态与媒体格式匹配
            Set<Material> materials = campaign.getMaterials();
            if (materials == null || materials.isEmpty()) {
                continue;
            }

            for (Material material : materials) {
                if (!Material.Review.eligible.equals(material.getReview())) {
                    continue;
                }

                String ctype = material.getCreativeType();
                if ("VIDEO".equals(reqFormat)) {
                    if (!"VIDEO".equalsIgnoreCase(ctype)) {
                        continue;
                    }
                    if (video.getMaxduration() != null && material.getVideoDuration() != null
                            && material.getVideoDuration() > video.getMaxduration()) {
                        continue;
                    }
                    if (video.getMinduration() != null && material.getVideoDuration() != null
                            && material.getVideoDuration() < video.getMinduration()) {
                        continue;
                    }
                } else if ("NATIVE".equals(reqFormat)) {
                    if (!"NATIVE".equalsIgnoreCase(ctype)) {
                        continue;
                    }
                } else { // BANNER
                    if (ctype != null && !"BANNER".equalsIgnoreCase(ctype) && !ctype.trim().isEmpty()) {
                        continue;
                    }
                    if (material.getWidth() == null || material.getHeight() == null) {
                        continue;
                    }
                    if (material.getWidth() != targetW || material.getHeight() != targetH) {
                        continue;
                    }
                }

                double effectivePrice = (material.getCpmPrice() != null && material.getCpmPrice() > 0)
                        ? material.getCpmPrice()
                        : campaignBid;

                if (effectivePrice >= highestCpm) {
                    highestCpm = effectivePrice;
                    winningCampaign = campaign;
                    winningMaterial = material;
                }
            }
        }

        // 5. 组装 OpenRTB 2.5 出价响应报文
        if (winningCampaign != null && winningMaterial != null) {
            String bidId = "bid-" + UUID.randomUUID().toString().replace("-", "");
            String encodedUserId = "";
            if (userId != null) {
                try {
                    encodedUserId = "&userId=" + java.net.URLEncoder.encode(userId, "UTF-8");
                } catch (Exception ignored) {}
            }

            String nurl = "/openrtb2/win?bidId=" + bidId
                    + "&price=${AUCTION_PRICE}"
                    + "&advId=" + winningCampaign.getAdvertiser().getId()
                    + "&campaignId=" + winningCampaign.getId()
                    + "&materialId=" + winningMaterial.getId()
                    + encodedUserId;

            String adm = winningMaterial.renderAdm("${AUCTION_PRICE}", bidId, nurl);

            if (userId != null) {
                cacheBidUser(bidId, userId);
            }

            Bid bid = new Bid();
            bid.setId(bidId);
            bid.setImpid(imp.getId());
            bid.setPrice(highestCpm);
            bid.setAdm(adm);
            bid.setNurl(nurl);
            bid.setCid(String.valueOf(winningCampaign.getId()));
            bid.setCrid("crid-" + winningMaterial.getId());
            if (targetW > 0) bid.setW(targetW);
            if (targetH > 0) bid.setH(targetH);
            bid.setAdomain(Collections.singletonList(
                    winningMaterial.getAdDomain() != null ? winningMaterial.getAdDomain() : "dsp.local"));

            // 补充 Prebid / AppNexus 兼容协议扩展字段 (0: Banner, 1: Video, 2: Native)
            Map<String, Object> ext = new HashMap<>();
            Map<String, Object> appnexusExt = new HashMap<>();
            int bidAdType = "VIDEO".equalsIgnoreCase(winningMaterial.getCreativeType()) ? 1
                    : ("NATIVE".equalsIgnoreCase(winningMaterial.getCreativeType()) ? 2 : 0);
            appnexusExt.put("bid_ad_type", bidAdType);
            ext.put("appnexus", appnexusExt);
            bid.setExt(ext);

            SeatBid seatBid = new SeatBid();
            seatBid.setSeat("dsp");
            seatBid.setBid(Collections.singletonList(bid));

            BidResponse response = new BidResponse();
            response.setId(request.getId());
            response.setSeatbid(Collections.singletonList(seatBid));
            response.setBidid("bidid-" + request.getId());
            response.setCur(cur);

            return Optional.of(response);
        }

        // 无符合条件的广告出价 (Prebid Server 标准 204 NO_BID)
        return Optional.empty();
    }

    @Override
    @Transactional
    public boolean handleWinNotice(String bidId, Double price, Integer advId, Integer campaignId, Integer materialId) {
        return handleWinNotice(bidId, price, advId, campaignId, materialId, null);
    }

    @Override
    @Transactional
    public boolean handleWinNotice(String bidId, Double price, Integer advId, Integer campaignId, Integer materialId, String userId) {
        if (price == null || advId == null || price < 0) {
            return false;
        }

        // 胜出通知幂等性排重校验（防重复扣费与网络重放）
        if (bidId != null && !bidId.trim().isEmpty()) {
            if (!tryAcquireBidLock(bidId)) {
                logger.warn("Duplicate win notice ignored for bidId: {}", bidId);
                return true; // 幂等返回成功
            }
        }

        // price 为 CPM，单次胜出扣除 price / 1000.0 (原子扣费)
        double cost = price / 1000.0;
        int rows = advertiserRepository.deductBalance(cost, advId);
        if (rows > 0) {
            if (campaignId != null) {
                recordCampaignSpend(campaignId, cost, LocalDate.now());
            }

            if (userId == null && bidId != null) {
                userId = getCachedBidUser(bidId);
            }

            if (userId != null && campaignId != null) {
                recordUserImpression(campaignId, userId, LocalDate.now(), LocalTime.now().getHour());
            }

            logger.info("Win notice processed atomically: bidId={}, price={} CPM, deducted={}, advId={}, campaignId={}, userId={}",
                    bidId, price, cost, advId, campaignId, userId);
            return true;
        } else {
            logger.warn("Deduct balance failed (insufficient balance or adv not found): advId={}, cost={}", advId, cost);
            return false;
        }
    }

    private String extractUserId(String ifa, String uid, String ip, String ua) {
        if (ifa != null && !ifa.trim().isEmpty()) {
            return "ifa:" + ifa.trim();
        }
        if (uid != null && !uid.trim().isEmpty()) {
            return "uid:" + uid.trim();
        }
        if (ip != null && !ip.trim().isEmpty() && ua != null && !ua.trim().isEmpty()) {
            return "ipua:" + md5(ip.trim() + ":" + ua.trim());
        }
        return null;
    }

    private String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return Integer.toHexString(input.hashCode());
        }
    }

    private void cacheBidUser(String bidId, String userId) {
        if (bidId == null || userId == null) return;
        String key = "openrtb:bidmeta:user:" + bidId;
        if (stringRedisTemplate != null) {
            try {
                stringRedisTemplate.opsForValue().set(key, userId, 30, TimeUnit.MINUTES);
            } catch (Exception e) {
                logger.debug("Redis cache bid user failed: {}", e.getMessage());
            }
        }
        inMemoryBidMeta.put(bidId, userId);
    }

    private String getCachedBidUser(String bidId) {
        if (bidId == null) return null;
        String key = "openrtb:bidmeta:user:" + bidId;
        if (stringRedisTemplate != null) {
            try {
                String val = stringRedisTemplate.opsForValue().get(key);
                if (val != null) return val;
            } catch (Exception ignored) {}
        }
        return inMemoryBidMeta.get(bidId);
    }

    private long getUserImpressionCount(Integer campaignId, String userId, String period, LocalDate date) {
        if (campaignId == null || userId == null) return 0;
        String key = "openrtb:fc:" + period + ":" + campaignId + ":" + date + ":" + userId;
        if (stringRedisTemplate != null) {
            try {
                String val = stringRedisTemplate.opsForValue().get(key);
                if (val != null) {
                    return Long.parseLong(val);
                }
            } catch (Exception e) {
                logger.debug("Redis read freq cap failed for key {}: {}", key, e.getMessage());
            }
        }
        Long count = inMemoryUserFreq.get(key);
        return count != null ? count : 0L;
    }

    private void recordUserImpression(Integer campaignId, String userId, LocalDate date, int hour) {
        if (campaignId == null || userId == null) return;
        String dailyKey = "openrtb:fc:daily:" + campaignId + ":" + date + ":" + userId;
        String hourlyKey = "openrtb:fc:hour:" + hour + ":" + campaignId + ":" + date + ":" + userId;

        if (stringRedisTemplate != null) {
            try {
                stringRedisTemplate.opsForValue().increment(dailyKey, 1);
                stringRedisTemplate.expire(dailyKey, 2, TimeUnit.DAYS);
                stringRedisTemplate.opsForValue().increment(hourlyKey, 1);
                stringRedisTemplate.expire(hourlyKey, 3, TimeUnit.HOURS);
            } catch (Exception e) {
                logger.debug("Redis freq increment failed: {}", e.getMessage());
            }
        }
        inMemoryUserFreq.compute(dailyKey, (k, v) -> (v == null ? 0L : v) + 1L);
        inMemoryUserFreq.compute(hourlyKey, (k, v) -> (v == null ? 0L : v) + 1L);
    }

    private double getCampaignHourlySpend(Integer campaignId, LocalDate date, int hour) {
        if (campaignId == null) return 0.0;
        String hourlyKey = "dsp:campaign:spend:hourly:" + campaignId + ":" + date + ":" + hour;
        if (stringRedisTemplate != null) {
            try {
                String val = stringRedisTemplate.opsForValue().get(hourlyKey);
                if (val != null) {
                    return Double.parseDouble(val);
                }
            } catch (Exception e) {
                logger.debug("Redis read hourly spend failed: {}", e.getMessage());
            }
        }
        Double memVal = inMemoryCampaignSpend.get(hourlyKey);
        return memVal != null ? memVal : 0.0;
    }

    private boolean tryAcquireBidLock(String bidId) {
        if (bidId == null || bidId.trim().isEmpty()) {
            return true;
        }
        if (stringRedisTemplate != null) {
            try {
                Boolean acquired = stringRedisTemplate.opsForValue()
                        .setIfAbsent("openrtb:win:" + bidId, "1");
                if (Boolean.TRUE.equals(acquired)) {
                    stringRedisTemplate.expire("openrtb:win:" + bidId, 24, TimeUnit.HOURS);
                    return true;
                }
                return false;
            } catch (Exception e) {
                logger.warn("Redis idempotency check failed, falling back to memory: {}", e.getMessage());
            }
        }
        if (inMemoryProcessedBids.size() > 50000) {
            inMemoryProcessedBids.clear();
        }
        return inMemoryProcessedBids.putIfAbsent(bidId, System.currentTimeMillis()) == null;
    }

    private double getCampaignSpend(Integer campaignId, Campaign.BudgetType budgetType, LocalDate date) {
        if (campaignId == null) return 0.0;
        String cacheKey = (budgetType == Campaign.BudgetType.daily)
                ? "dsp:campaign:spend:daily:" + campaignId + ":" + date
                : "dsp:campaign:spend:total:" + campaignId;

        if (stringRedisTemplate != null) {
            try {
                String val = stringRedisTemplate.opsForValue().get(cacheKey);
                if (val != null) {
                    return Double.parseDouble(val);
                }
            } catch (Exception e) {
                logger.debug("Redis read failed for key {}: {}", cacheKey, e.getMessage());
            }
        }

        Double memVal = inMemoryCampaignSpend.get(cacheKey);
        if (memVal != null) {
            return memVal;
        }

        // 尝试从持久化统计表中拉取历史花费基数
        double dbSpend = 0.0;
        if (rtbCampaignStatsRepository != null) {
            try {
                if (budgetType == Campaign.BudgetType.daily) {
                    Double s = rtbCampaignStatsRepository.sumConsumeByCampaignIdAndDate(campaignId, date);
                    if (s != null) dbSpend = s;
                } else {
                    Double s = rtbCampaignStatsRepository.sumConsumeByCampaignId(campaignId);
                    if (s != null) dbSpend = s;
                }
            } catch (Exception e) {
                logger.debug("Query rtb stats failed: {}", e.getMessage());
            }
        }
        inMemoryCampaignSpend.put(cacheKey, dbSpend);
        return dbSpend;
    }

    private void recordCampaignSpend(Integer campaignId, double cost, LocalDate date) {
        if (campaignId == null || cost <= 0) return;
        int hour = LocalTime.now().getHour();
        String dailyKey = "dsp:campaign:spend:daily:" + campaignId + ":" + date;
        String totalKey = "dsp:campaign:spend:total:" + campaignId;
        String hourlyKey = "dsp:campaign:spend:hourly:" + campaignId + ":" + date + ":" + hour;

        if (stringRedisTemplate != null) {
            try {
                stringRedisTemplate.opsForValue().increment(dailyKey, cost);
                stringRedisTemplate.expire(dailyKey, 3, TimeUnit.DAYS);
                stringRedisTemplate.opsForValue().increment(totalKey, cost);
                stringRedisTemplate.opsForValue().increment(hourlyKey, cost);
                stringRedisTemplate.expire(hourlyKey, 2, TimeUnit.DAYS);
            } catch (Exception e) {
                logger.debug("Redis spend increment failed: {}", e.getMessage());
            }
        }

        inMemoryCampaignSpend.compute(dailyKey, (k, v) -> (v == null ? 0.0 : v) + cost);
        inMemoryCampaignSpend.compute(totalKey, (k, v) -> (v == null ? 0.0 : v) + cost);
        inMemoryCampaignSpend.compute(hourlyKey, (k, v) -> (v == null ? 0.0 : v) + cost);
    }
}
