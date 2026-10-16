package org.codeworks.dsp.openrtb.service.impl;

import org.codeworks.dsp.dao.AdvertiserRepository;
import org.codeworks.dsp.dao.CampaignRepository;
import org.codeworks.dsp.dao.MaterialRepository;
import org.codeworks.dsp.model.entities.Advertiser;
import org.codeworks.dsp.model.entities.Campaign;
import org.codeworks.dsp.model.entities.Material;
import org.codeworks.dsp.openrtb.dto.request.Banner;
import org.codeworks.dsp.openrtb.dto.request.BidRequest;
import org.codeworks.dsp.openrtb.dto.request.Format;
import org.codeworks.dsp.openrtb.dto.request.Imp;
import org.codeworks.dsp.openrtb.dto.response.Bid;
import org.codeworks.dsp.openrtb.dto.response.BidResponse;
import org.codeworks.dsp.dao.rtbMQ.RtbCampaignStatsRepository;
import org.codeworks.dsp.openrtb.dto.response.SeatBid;
import org.codeworks.dsp.openrtb.service.OpenRtbBiddingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * OpenRTB 2.5 实时出价与胜出结算服务实现
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

    private final Map<String, Long> inMemoryProcessedBids = new ConcurrentHashMap<>();
    private final Map<String, Double> inMemoryCampaignSpend = new ConcurrentHashMap<>();

    @Override
    @Transactional(readOnly = true)
    public Optional<BidResponse> processBid(BidRequest request) {
        if (request == null || request.getImp() == null || request.getImp().isEmpty()) {
            return Optional.empty();
        }

        Imp imp = request.getImp().get(0);
        Banner banner = imp.getBanner();
        if (banner == null) {
            // 当前阶段专注于 Banner 展示广告出价
            return Optional.empty();
        }

        int targetW = banner.getW() != null ? banner.getW() : 300;
        int targetH = banner.getH() != null ? banner.getH() : 250;
        if (banner.getFormat() != null && !banner.getFormat().isEmpty()) {
            Format fmt = banner.getFormat().get(0);
            if (fmt.getW() != null) targetW = fmt.getW();
            if (fmt.getH() != null) targetH = fmt.getH();
        }

        double bidfloor = imp.getBidfloor() != null ? imp.getBidfloor() : 0.0;
        String cur = imp.getBidfloorcur() != null ? imp.getBidfloorcur() : "USD";

        // 查询数据库中所有审核通过且处于有效排期的活动
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

            // Item 1: 广告活动预算 (Budget) 与日预算超限熔断检查
            Double budget = campaign.getBudget();
            if (budget != null && budget > 0) {
                Campaign.BudgetType budgetType = campaign.getBudgetType() != null
                        ? campaign.getBudgetType()
                        : Campaign.BudgetType.all;
                double currentSpend = getCampaignSpend(campaign.getId(), budgetType, today);
                if (currentSpend >= budget) {
                    logger.debug("Campaign {} exceeded budget: spent={}, budget={}, type={}",
                            campaign.getId(), currentSpend, budget, budgetType);
                    continue;
                }
            }

            Set<Material> materials = campaign.getMaterials();
            if (materials == null || materials.isEmpty()) {
                continue;
            }

            for (Material material : materials) {
                if (!Material.Review.eligible.equals(material.getReview())) {
                    continue;
                }
                if (material.getWidth() == null || material.getHeight() == null) {
                    continue;
                }
                if (material.getWidth() == targetW && material.getHeight() == targetH) {
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
        }

        // 如果数据库中匹配到了出价物料
        if (winningCampaign != null && winningMaterial != null) {
            String bidId = "bid-" + UUID.randomUUID().toString().replace("-", "");
            String nurl = "/openrtb2/win?bidId=" + bidId
                    + "&price=${AUCTION_PRICE}"
                    + "&advId=" + winningCampaign.getAdvertiser().getId()
                    + "&campaignId=" + winningCampaign.getId()
                    + "&materialId=" + winningMaterial.getId();

            String adm = winningMaterial.renderAdm("${AUCTION_PRICE}", bidId, nurl);

            Bid bid = new Bid();
            bid.setId(bidId);
            bid.setImpid(imp.getId());
            bid.setPrice(highestCpm);
            bid.setAdm(adm);
            bid.setNurl(nurl);
            bid.setCid(String.valueOf(winningCampaign.getId()));
            bid.setCrid("crid-" + winningMaterial.getId());
            bid.setW(targetW);
            bid.setH(targetH);
            bid.setAdomain(Collections.singletonList(
                    winningMaterial.getAdDomain() != null ? winningMaterial.getAdDomain() : "dsp.local"));

            // 补充 Prebid / AppNexus 兼容协议扩展字段
            Map<String, Object> ext = new HashMap<>();
            Map<String, Object> appnexusExt = new HashMap<>();
            appnexusExt.put("bid_ad_type", 0); // 0 = Banner
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

        // 未匹配到出价条件，返回空 (Prebid Server 标准 204 NO_BID)
        return Optional.empty();
    }

    @Override
    @Transactional
    public boolean handleWinNotice(String bidId, Double price, Integer advId, Integer campaignId, Integer materialId) {
        if (price == null || advId == null || price < 0) {
            return false;
        }

        // Item 2: 胜出通知幂等性排重校验（防重复扣费与网络重放）
        if (bidId != null && !bidId.trim().isEmpty()) {
            if (!tryAcquireBidLock(bidId)) {
                logger.warn("Duplicate win notice ignored for bidId: {}", bidId);
                return true; // 幂等返回成功
            }
        }

        // price 为千次曝光出价 CPM，每次真实胜出扣除 price / 1000.0 (Item 8: 高并发原子扣费)
        double cost = price / 1000.0;
        int rows = advertiserRepository.deductBalance(cost, advId);
        if (rows > 0) {
            if (campaignId != null) {
                recordCampaignSpend(campaignId, cost, LocalDate.now());
            }
            logger.info("Win notice processed atomically: bidId={}, price={} CPM, deducted={}, advId={}, campaignId={}",
                    bidId, price, cost, advId, campaignId);
            return true;
        } else {
            logger.warn("Deduct balance failed (insufficient balance or adv not found): advId={}, cost={}", advId, cost);
            return false;
        }
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
        String dailyKey = "dsp:campaign:spend:daily:" + campaignId + ":" + date;
        String totalKey = "dsp:campaign:spend:total:" + campaignId;

        if (stringRedisTemplate != null) {
            try {
                stringRedisTemplate.opsForValue().increment(dailyKey, cost);
                stringRedisTemplate.expire(dailyKey, 3, TimeUnit.DAYS);
                stringRedisTemplate.opsForValue().increment(totalKey, cost);
            } catch (Exception e) {
                logger.debug("Redis spend increment failed: {}", e.getMessage());
            }
        }

        inMemoryCampaignSpend.compute(dailyKey, (k, v) -> (v == null ? 0.0 : v) + cost);
        inMemoryCampaignSpend.compute(totalKey, (k, v) -> (v == null ? 0.0 : v) + cost);
    }
}
