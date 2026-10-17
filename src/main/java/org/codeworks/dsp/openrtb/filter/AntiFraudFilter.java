package org.codeworks.dsp.openrtb.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 预竞价反作弊与无效流量（IVT - Invalid Traffic）过滤器
 * <p>
 * 在进入复杂的广告检索与出价决策前，对请求实施毫秒级前置快速过滤：
 * 1. 爬虫与自动化工具 User-Agent 过滤（基于关键词与正则匹配）
 * 2. 知名公有云/数据中心机房 IP 阻断（基于内存 CIDR 网段树查找，零网络开销）
 * 3. 运营动态 IP 黑名单过滤（基于 Redis 键校验）
 * </p>
 *
 * @author benjaminkc
 */
@Component
public class AntiFraudFilter {

    private static final Logger logger = LoggerFactory.getLogger(AntiFraudFilter.class);

    @Autowired(required = false)
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 爬虫与自动化测试工具特征正则
     */
    private static final Pattern BOT_UA_PATTERN = Pattern.compile(
            "(?i)(curl|python-requests|httpclient|headlesschrome|phantomjs|scrapy|spider|bot|crawler|wget|postman|apachebench|feedly)"
    );

    /**
     * 机房 IP 网段条目（网络地址与子网掩码）
     */
    private static class CidrRange {
        final long network;
        final long mask;
        final String name;

        CidrRange(String cidr, String name) {
            this.name = name;
            String[] parts = cidr.split("/");
            long ip = ipToLong(parts[0]);
            int prefix = Integer.parseInt(parts[1]);
            this.mask = prefix == 0 ? 0L : (~0L << (32 - prefix)) & 0xFFFFFFFFL;
            this.network = ip & this.mask;
        }

        boolean matches(long ipLong) {
            return (ipLong & mask) == network;
        }
    }

    private final List<CidrRange> datacenterCidrs = new ArrayList<>();

    @PostConstruct
    public void init() {
        // 预加载国内外主流云服务商与 IDC 机房网段（非真实自然人流量来源）
        // AWS
        addCidr("3.0.0.0/9", "AWS");
        addCidr("13.0.0.0/11", "AWS");
        addCidr("18.0.0.0/11", "AWS");
        addCidr("34.192.0.0/12", "AWS");
        addCidr("52.0.0.0/11", "AWS");
        addCidr("54.0.0.0/12", "AWS");

        // Google Cloud (GCP)
        addCidr("34.64.0.0/10", "GCP");
        addCidr("35.184.0.0/13", "GCP");
        addCidr("35.208.0.0/12", "GCP");
        addCidr("35.224.0.0/12", "GCP");

        // DigitalOcean
        addCidr("159.203.0.0/16", "DigitalOcean");
        addCidr("165.227.0.0/16", "DigitalOcean");
        addCidr("167.99.0.0/16", "DigitalOcean");
        addCidr("178.62.0.0/16", "DigitalOcean");
        addCidr("188.166.0.0/16", "DigitalOcean");
        addCidr("206.189.0.0/16", "DigitalOcean");

        // Hetzner
        addCidr("78.46.0.0/15", "Hetzner");
        addCidr("88.198.0.0/16", "Hetzner");
        addCidr("136.243.0.0/16", "Hetzner");
        addCidr("144.76.0.0/16", "Hetzner");
        addCidr("148.251.0.0/16", "Hetzner");

        // 阿里云 (Aliyun Datacenter)
        addCidr("47.74.0.0/15", "Aliyun");
        addCidr("47.88.0.0/14", "Aliyun");
        addCidr("47.96.0.0/11", "Aliyun");
        addCidr("121.40.0.0/15", "Aliyun");

        // 腾讯云 (Tencent Cloud)
        addCidr("49.232.0.0/14", "TencentCloud");
        addCidr("118.24.0.0/14", "TencentCloud");
        addCidr("129.204.0.0/14", "TencentCloud");
        addCidr("129.226.0.0/15", "TencentCloud");

        logger.info("AntiFraudFilter initialized with {} datacenter CIDR blocks", datacenterCidrs.size());
    }

    private void addCidr(String cidr, String name) {
        try {
            datacenterCidrs.add(new CidrRange(cidr, name));
        } catch (Exception e) {
            logger.warn("Failed to parse CIDR {}: {}", cidr, e.getMessage());
        }
    }

    /**
     * 判定是否属于无效流量 (IVT)
     *
     * @param ip 客户端 IP
     * @param userAgent 客户端 User-Agent
     * @return true 表示为无效/作弊流量需阻断，false 表示正常流量
     */
    public boolean isInvalidTraffic(String ip, String userAgent) {
        // 1. 爬虫/工具 User-Agent 快速校验
        if (userAgent != null && BOT_UA_PATTERN.matcher(userAgent).find()) {
            logger.debug("Anti-fraud rejected request with bot UA: {}", userAgent);
            return true;
        }

        if (ip == null || ip.trim().isEmpty()) {
            return false;
        }

        String cleanIp = ip.trim();

        // 2. Redis 动态黑名单检测
        if (stringRedisTemplate != null) {
            try {
                Boolean isBlacklisted = stringRedisTemplate.hasKey("openrtb:blacklist:ip:" + cleanIp);
                if (Boolean.TRUE.equals(isBlacklisted)) {
                    logger.debug("Anti-fraud rejected blacklisted IP: {}", cleanIp);
                    return true;
                }
            } catch (Exception e) {
                logger.warn("Redis check blacklist failed: {}", e.getMessage());
            }
        }

        // 3. 机房 IP 段快速匹配 (内存 bitset/mask 运算，纯 CPU 指令)
        long ipLong = ipToLong(cleanIp);
        if (ipLong > 0) {
            for (CidrRange range : datacenterCidrs) {
                if (range.matches(ipLong)) {
                    logger.debug("Anti-fraud rejected datacenter IP: {} ({})", cleanIp, range.name);
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 将 IPv4 点分十进制字符串转为 long 整数
     */
    private static long ipToLong(String ip) {
        try {
            String[] parts = ip.split("\\.");
            if (parts.length != 4) return 0L;
            long result = 0;
            for (int i = 0; i < 4; i++) {
                int octet = Integer.parseInt(parts[i]);
                if (octet < 0 || octet > 255) return 0L;
                result = (result << 8) | octet;
            }
            return result & 0xFFFFFFFFL;
        } catch (Exception e) {
            return 0L;
        }
    }
}
