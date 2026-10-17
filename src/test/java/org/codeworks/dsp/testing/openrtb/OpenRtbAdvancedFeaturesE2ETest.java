package org.codeworks.dsp.testing.openrtb;

import org.codeworks.dsp.dao.AdvertiserRepository;
import org.codeworks.dsp.dao.CampaignRepository;
import org.codeworks.dsp.dao.MaterialRepository;
import org.codeworks.dsp.model.entities.Advertiser;
import org.codeworks.dsp.model.entities.Campaign;
import org.codeworks.dsp.model.entities.Material;
import org.codeworks.dsp.model.entities.Qualification;
import org.codeworks.dsp.openrtb.dto.request.Banner;
import org.codeworks.dsp.openrtb.dto.request.BidRequest;
import org.codeworks.dsp.openrtb.dto.request.Device;
import org.codeworks.dsp.openrtb.dto.request.Imp;
import org.codeworks.dsp.openrtb.dto.request.Native;
import org.codeworks.dsp.openrtb.dto.request.User;
import org.codeworks.dsp.openrtb.dto.request.Video;
import org.codeworks.dsp.openrtb.dto.response.Bid;
import org.codeworks.dsp.openrtb.dto.response.BidResponse;
import org.codeworks.dsp.openrtb.service.OpenRtbBiddingService;
import org.codeworks.dsp.openrtb.service.OpenRtbTrackingService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * OpenRTB 2.5 高级特性端到端测试套件 (Phase 1 & Phase 2)
 * <p>
 * 覆盖测试场景：
 * 1. 预竞价反作弊与无效流量 (IVT) 过滤：机房 IP 段拦截与爬虫 Bot UA 识别阻断；
 * 2. 用户级展示频控 (每日/每小时) 严格拦截与计数；
 * 3. 广告预算平滑消耗控制 (EVEN 模式限速节流与 ASAP 模式全量放行对比)；
 * 4. 视频广告 (VAST 3.0 XML) 与原生广告 (OpenRTB Native 1.2 JSON) 竞价与 adm 动态装配；
 * 5. 广告点击 token 生成、302 落地页组装与 S2S 转化归因链路及防重复排重。
 * </p>
 *
 * @author benjaminkc
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:config/spring/spring.xml")
@ActiveProfiles("local")
public class OpenRtbAdvancedFeaturesE2ETest {

    @Autowired
    private OpenRtbBiddingService biddingService;

    @Autowired
    private OpenRtbTrackingService trackingService;

    @Autowired
    private AdvertiserRepository advertiserRepository;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private MaterialRepository materialRepository;

    private Advertiser adv;
    private Campaign bannerCampaign;
    private Material bannerMaterial;

    private Campaign videoCampaign;
    private Material videoMaterial;

    private Campaign nativeCampaign;
    private Material nativeMaterial;

    @Before
    public void setUp() {
        String rand = String.valueOf(System.currentTimeMillis() % 10000000L);

        // 创建测试资质与广告主
        Qualification qual = new Qualification();
        qual.setLicence("LIC-ADV-TEST-" + rand);
        qual.setCompanyName("Advanced DSP Tech");
        qual.setOfficialSite("https://tech.dsp.local");
        qual.setSiteName("DSP Tech");
        qual.setTradeId(1);

        adv = new Advertiser();
        adv.setUserName("adv" + rand);
        adv.setPassword("e10adc3949ba59abbe56e057f20f883e");
        adv.setEmail("adv" + rand + "@dsp.local");
        adv.setBalance(500.00);
        adv.setIsAdmin(false);
        adv.setReview(Advertiser.Review.eligible);
        adv.setStatus(Advertiser.Status.eligible);
        adv.setSendAdx(Advertiser.SendAdx.no);
        adv.setCreateDate(LocalDateTime.now());
        adv.setLastLoginDate(LocalDateTime.now());
        adv.setQualification(qual);
        adv = advertiserRepository.save(adv);

        // 1. 创建 Banner 活动与素材 (480x320，使用独立尺寸以防跨测试用例尺寸冲突)
        bannerCampaign = createBaseCampaign("Banner Campaign " + rand, 4.00, 1000.00, "EVEN", 0);
        bannerMaterial = createMaterial("Banner 480x320", bannerCampaign, "BANNER", 480, 320, 4.00);
        bannerCampaign.setMaterials(Collections.singleton(bannerMaterial));
        bannerCampaign = campaignRepository.save(bannerCampaign);

        // 2. 创建 Video 活动与素材 (640x360, 15秒)
        videoCampaign = createBaseCampaign("Video Campaign " + rand, 6.00, 1000.00, "EVEN", 0);
        videoMaterial = createMaterial("Video PreRoll 15s", videoCampaign, "VIDEO", 640, 360, 6.00);
        videoMaterial.setVideoDuration(15);
        videoMaterial.setVideoMimes("video/mp4");
        videoMaterial = materialRepository.save(videoMaterial);
        videoCampaign.setMaterials(Collections.singleton(videoMaterial));
        videoCampaign = campaignRepository.save(videoCampaign);

        // 3. 创建 Native 原生活动与素材
        nativeCampaign = createBaseCampaign("Native Campaign " + rand, 5.00, 1000.00, "EVEN", 0);
        nativeMaterial = createMaterial("Native Feed Item", nativeCampaign, "NATIVE", 0, 0, 5.00);
        nativeMaterial.setNativeTitle("精品热卖推荐");
        nativeMaterial.setNativeDesc("全新升级，品质保证");
        nativeMaterial.setNativeCtaText("立即购买");
        nativeMaterial = materialRepository.save(nativeMaterial);
        nativeCampaign.setMaterials(Collections.singleton(nativeMaterial));
        nativeCampaign = campaignRepository.save(nativeCampaign);
    }

    private Campaign createBaseCampaign(String name, double bid, double budget, String pacingMode, int freqCapDaily) {
        Campaign c = new Campaign();
        c.setName(name);
        c.setAdvertiser(adv);
        c.setBid(bid);
        c.setBudget(budget);
        c.setBudgetType(Campaign.BudgetType.daily);
        c.setPacingMode(pacingMode);
        c.setFreqCapDaily(freqCapDaily);
        c.setFreqCapHourly(0);
        c.setStatus(Campaign.Status.eligible);
        c.setReview(Campaign.Review.eligible);
        c.setStartDate(LocalDate.now().minusDays(1));
        c.setEndDate(LocalDate.now().plusDays(30));
        c.setCreateDate(LocalDateTime.now());
        c.setSync(Campaign.Sync.not);
        return campaignRepository.save(c);
    }

    private Material createMaterial(String name, Campaign campaign, String creativeType, int w, int h, double cpm) {
        Material m = new Material();
        m.setName(name);
        m.setAdvertiser(adv);
        m.setCampaign(campaign);
        m.setCreativeType(creativeType);
        m.setWidth(w);
        m.setHeight(h);
        m.setUrl("http://cdn.dsp.local/creatives/" + name.replace(" ", "_") + ".png");
        m.setLandingUrl("https://shop.example.com/item/1001");
        m.setAdDomain("example.com");
        m.setCpmPrice(cpm);
        m.setStatus(Material.Status.eligible);
        m.setReview(Material.Review.eligible);
        m.setCreateDate(LocalDateTime.now());
        m.setType(Material.Type.image);
        m.setTradeId(1);
        return materialRepository.save(m);
    }

    /**
     * 测试 1: 预竞价反作弊过滤 (IDC 机房 IP 与爬虫 Bot UA 拦截)
     */
    @Test
    public void testAntiFraudFiltering() {
        // 场景 A: 模拟来自 AWS 知名数据中心 IP (3.0.1.2) 的请求 -> 必须被前置反作弊拦截返回 204 (Optional.empty)
        BidRequest reqAwsIp = createBannerBidRequest("req-anti-aws", 480, 320, 1.0);
        Device devAws = new Device();
        devAws.setIp("3.0.1.2");
        devAws.setUa("Mozilla/5.0 (iPhone; CPU iPhone OS 16_0 like Mac OS X)");
        reqAwsIp.setDevice(devAws);

        Optional<BidResponse> resAws = biddingService.processBid(reqAwsIp);
        Assert.assertFalse("Data center IP (AWS) must be rejected by AntiFraudFilter", resAws.isPresent());

        // 场景 B: 模拟来自 阿里云机房 IP (47.74.10.20) 的请求 -> 必须被拦截
        BidRequest reqAliIp = createBannerBidRequest("req-anti-ali", 480, 320, 1.0);
        Device devAli = new Device();
        devAli.setIp("47.74.10.20");
        devAli.setUa("Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
        reqAliIp.setDevice(devAli);

        Optional<BidResponse> resAli = biddingService.processBid(reqAliIp);
        Assert.assertFalse("Data center IP (Aliyun) must be rejected by AntiFraudFilter", resAli.isPresent());

        // 场景 C: 模拟自动化爬虫工具 User-Agent (curl / python-requests) -> 必须被拦截
        BidRequest reqBot = createBannerBidRequest("req-anti-bot", 480, 320, 1.0);
        Device devBot = new Device();
        devBot.setIp("114.248.15.22"); // 正常真实民用宽带 IP
        devBot.setUa("python-requests/2.28.1");
        reqBot.setDevice(devBot);

        Optional<BidResponse> resBot = biddingService.processBid(reqBot);
        Assert.assertFalse("Bot User-Agent must be rejected by AntiFraudFilter", resBot.isPresent());

        // 场景 D: 正常民用自然人请求 -> 正常竞价胜出
        BidRequest reqValid = createBannerBidRequest("req-anti-valid", 480, 320, 1.0);
        Device devValid = new Device();
        devValid.setIp("114.248.15.22");
        devValid.setUa("Mozilla/5.0 (iPhone; CPU iPhone OS 16_5 like Mac OS X) AppleWebKit/605.1.15");
        reqValid.setDevice(devValid);

        Optional<BidResponse> resValid = biddingService.processBid(reqValid);
        Assert.assertTrue("Legitimate traffic must pass anti-fraud filter and bid successfully", resValid.isPresent());
    }

    /**
     * 测试 2: 用户级展示频控 (Frequency Capping) 校验与拦截
     */
    @Test
    public void testUserFrequencyCapping() {
        String testUserId = "ifa:TEST-USER-FREQ-" + System.currentTimeMillis();
        int fcW = 1000 + (int)((System.currentTimeMillis() / 10) % 8000);
        int fcH = 251;

        // 创建专用于频控测试的独立活动与素材 (单用户每日展示上限为 2 次，动态唯一尺寸避免跨测试活动干扰)
        Campaign fcCampaign = createBaseCampaign("FreqCap Campaign " + System.currentTimeMillis(), 4.50, 1000.00, "EVEN", 2);
        Material fcMaterial = createMaterial("Banner FC " + fcW, fcCampaign, "BANNER", fcW, fcH, 4.50);
        fcCampaign.setMaterials(Collections.singleton(fcMaterial));
        fcCampaign = campaignRepository.save(fcCampaign);

        // 构造带有该 IFA 用户标识的询价请求
        BidRequest req = createBannerBidRequest("req-fc-1", fcW, fcH, 1.0);
        Device dev = new Device();
        dev.setIp("114.248.20.10");
        dev.setUa("Mozilla/5.0 (Linux; Android 13)");
        dev.setIfa(testUserId.replace("ifa:", ""));
        req.setDevice(dev);

        // 第 1 次出价: 应当成功，并模拟胜出计费打点
        Optional<BidResponse> res1 = biddingService.processBid(req);
        Assert.assertTrue("First bid must succeed", res1.isPresent());
        String bidId1 = res1.get().getSeatbid().get(0).getBid().get(0).getId();
        biddingService.handleWinNotice(bidId1, 4.50, adv.getId(), fcCampaign.getId(), fcMaterial.getId(), testUserId);

        // 第 2 次出价: 应当成功，并再次触发胜出计费打点 (累计达到 2 次上限)
        Optional<BidResponse> res2 = biddingService.processBid(req);
        Assert.assertTrue("Second bid must succeed", res2.isPresent());
        String bidId2 = res2.get().getSeatbid().get(0).getBid().get(0).getId();
        biddingService.handleWinNotice(bidId2, 4.50, adv.getId(), fcCampaign.getId(), fcMaterial.getId(), testUserId);

        // 第 3 次出价: 用户当日展示量已达 2 次上限，DSP 频控拦截，返回 204 (Optional.empty)
        Optional<BidResponse> res3 = biddingService.processBid(req);
        Assert.assertFalse("Third bid for same user must be blocked by freqCapDaily", res3.isPresent());

        // 验证另一位全新用户: 依然可以正常出价
        BidRequest reqOtherUser = createBannerBidRequest("req-fc-other", fcW, fcH, 1.0);
        Device devOther = new Device();
        devOther.setIp("114.248.20.11");
        devOther.setUa("Mozilla/5.0 (Linux; Android 13)");
        devOther.setIfa("ANOTHER-USER-9999");
        reqOtherUser.setDevice(devOther);

        Optional<BidResponse> resOther = biddingService.processBid(reqOtherUser);
        Assert.assertTrue("Different user must not be impacted by previous user's freq cap", resOther.isPresent());
    }

    /**
     * 测试 3: 预算平滑消耗 (Pacing: EVEN vs ASAP) 对比验证
     */
    @Test
    public void testBudgetPacingModes() {
        int pacingW = 2000 + (int)((System.currentTimeMillis() / 10) % 7000);
        int pacingH = 91;

        // 创建一个日预算为 24.0 元的平滑消耗 (EVEN) 活动 (小时均摊预算 = 1.0 元，动态唯一尺寸避免跨测试活动干扰)
        Campaign pacingCampaign = createBaseCampaign("Pacing Campaign " + System.currentTimeMillis(), 3.00, 24.00, "EVEN", 0);
        Material m = createMaterial("Pacing Mat " + pacingW, pacingCampaign, "BANNER", pacingW, pacingH, 3.00);
        pacingCampaign.setMaterials(Collections.singleton(m));
        campaignRepository.save(pacingCampaign);

        BidRequest req = createBannerBidRequest("req-pacing", pacingW, pacingH, 1.0);

        // 初始状态下尚未超标，应当可以正常出价
        Optional<BidResponse> resBefore = biddingService.processBid(req);
        Assert.assertTrue("Initial bid within pacing budget should succeed", resBefore.isPresent());

        // 模拟当前小时已累计扣费胜出 1.2 元 (达到并超过了当前小时平滑上限 1.0 元)
        // 触发胜出通知累计小时消耗 (使用动态 UUID 防止 Redis 24h 胜出排重键冲突)
        String bidFake1 = "bid-fake-1-" + UUID.randomUUID().toString().replace("-", "");
        String bidFake2 = "bid-fake-2-" + UUID.randomUUID().toString().replace("-", "");
        biddingService.handleWinNotice(bidFake1, 600.0, adv.getId(), pacingCampaign.getId(), m.getId()); // 0.6 元
        biddingService.handleWinNotice(bidFake2, 600.0, adv.getId(), pacingCampaign.getId(), m.getId()); // 0.6 元，合计 1.2 元

        // 在 EVEN 模式下，小时预算用尽应被平滑节流阻断
        Optional<BidResponse> resEvenBlocked = biddingService.processBid(req);
        Assert.assertFalse("EVEN mode must throttle bids when hourly budget is exhausted", resEvenBlocked.isPresent());

        // 将活动消耗模式切换为 ASAP (全量冲刺跑量模式)
        pacingCampaign.setPacingMode("ASAP");
        campaignRepository.save(pacingCampaign);

        // 在 ASAP 模式下，即便当小时消耗超过均摊预算，只要日总预算 (24.0) 未耗尽，依然全力放行出价！
        Optional<BidResponse> resAsapAllowed = biddingService.processBid(req);
        Assert.assertTrue("ASAP mode must continue bidding as long as total daily budget is not exhausted", resAsapAllowed.isPresent());
    }

    /**
     * 测试 4: 多广告位格式出价与渲染 (Video VAST 3.0 与 Native 1.2)
     */
    @Test
    public void testVideoAndNativeBidding() {
        // 1. 测试 Video (VAST 3.0) 广告竞价
        BidRequest videoReq = new BidRequest();
        videoReq.setId("req-video-1");
        Imp videoImp = new Imp();
        videoImp.setId("imp-video");
        Video v = new Video();
        v.setW(640);
        v.setH(360);
        v.setMinduration(5);
        v.setMaxduration(30);
        v.setMimes(Collections.singletonList("video/mp4"));
        videoImp.setVideo(v);
        videoImp.setBidfloor(2.00);
        videoReq.setImp(Collections.singletonList(videoImp));

        Optional<BidResponse> videoRes = biddingService.processBid(videoReq);
        Assert.assertTrue("Video bid must succeed with video campaign", videoRes.isPresent());
        Bid videoBid = videoRes.get().getSeatbid().get(0).getBid().get(0);
        Assert.assertEquals(videoCampaign.getBid(), videoBid.getPrice());
        // 校验 AppNexus / Prebid 扩展协议 bid_ad_type == 1 (Video)
        Map<?, ?> ext = (Map<?, ?>) videoBid.getExt().get("appnexus");
        Assert.assertEquals(1, ext.get("bid_ad_type"));
        // 校验 adm 代码中包含了完整的 VAST 3.0 XML 结构与 MediaFile
        String vastXml = videoBid.getAdm();
        Assert.assertNotNull(vastXml);
        Assert.assertTrue("adm must be VAST 3.0", vastXml.contains("<VAST version=\"3.0\">"));
        Assert.assertTrue("adm must contain Linear creative", vastXml.contains("<Linear>"));
        Assert.assertTrue("adm must contain media mp4", vastXml.contains("video/mp4"));
        Assert.assertTrue("adm must contain Duration 15s", vastXml.contains("<Duration>00:00:15</Duration>"));

        // 2. 测试 Native (OpenRTB Native 1.2) 广告竞价
        BidRequest nativeReq = new BidRequest();
        nativeReq.setId("req-native-1");
        Imp nativeImp = new Imp();
        nativeImp.setId("imp-native");
        Native nat = new Native();
        nat.setVer("1.2");
        nativeImp.setNativeObj(nat);
        nativeImp.setBidfloor(2.00);
        nativeReq.setImp(Collections.singletonList(nativeImp));

        Optional<BidResponse> nativeRes = biddingService.processBid(nativeReq);
        Assert.assertTrue("Native bid must succeed with native campaign", nativeRes.isPresent());
        Bid nativeBid = nativeRes.get().getSeatbid().get(0).getBid().get(0);
        Assert.assertEquals(nativeCampaign.getBid(), nativeBid.getPrice());
        // 校验 AppNexus / Prebid 扩展协议 bid_ad_type == 2 (Native)
        Map<?, ?> nativeExt = (Map<?, ?>) nativeBid.getExt().get("appnexus");
        Assert.assertEquals(2, nativeExt.get("bid_ad_type"));
        // 校验 adm 代码中包含了标准的 Native 1.2 JSON
        String nativeAdm = nativeBid.getAdm();
        Assert.assertNotNull(nativeAdm);
        Assert.assertTrue("adm must contain native ver 1.2", nativeAdm.contains("\"ver\":\"1.2\""));
        Assert.assertTrue("adm must contain native title", nativeAdm.contains("精品热卖推荐"));
        Assert.assertTrue("adm must contain native cta", nativeAdm.contains("立即购买"));
    }

    /**
     * 测试 5: 点击追踪、落地页 302 重定向组装与 S2S 效果转化归因链路
     */
    @Test
    public void testClickTrackingAndConversionAttribution() {
        Integer cId = bannerCampaign.getId();
        Integer mId = bannerMaterial.getId();
        Integer aId = adv.getId();
        String bidId = "bid-clk-test-" + System.currentTimeMillis();
        String landingUrl = "https://shop.example.com/checkout?source=dsp";

        // 1. 模拟用户点击广告打点
        long clickCountBefore = trackingService.getClickCount(cId, LocalDate.now());
        String redirectTarget = trackingService.recordClick(bidId, cId, aId, mId, landingUrl);

        Assert.assertNotNull(redirectTarget);
        Assert.assertTrue("Redirect target must preserve original url", redirectTarget.startsWith(landingUrl));
        Assert.assertTrue("Redirect target must contain generated click_id", redirectTarget.contains("&click_id=clk-"));

        // 校验点击量统计指标累加
        long clickCountAfter = trackingService.getClickCount(cId, LocalDate.now());
        Assert.assertEquals(clickCountBefore + 1, clickCountAfter);

        // 提取生成的 click_id 令牌
        String clickId = redirectTarget.substring(redirectTarget.indexOf("click_id=") + "click_id=".length());

        // 2. 模拟广告主落地页 S2S 转化回传 (Postback): 发生 purchase 转化，金额 128.50 元
        long convCountBefore = trackingService.getConversionCount(cId, LocalDate.now());
        double convRevBefore = trackingService.getConversionRevenue(cId, LocalDate.now());

        Map<String, Object> convResult = trackingService.attributeConversion(clickId, "purchase", 128.50);
        Assert.assertEquals("success", convResult.get("status"));
        Assert.assertEquals(clickId, convResult.get("clickId"));
        Assert.assertEquals(cId, convResult.get("campaignId"));
        Assert.assertEquals(aId, convResult.get("advId"));
        Assert.assertEquals(128.50, (Double) convResult.get("orderAmount"), 0.001);

        // 校验转化量与 GMV 累计
        long convCountAfter = trackingService.getConversionCount(cId, LocalDate.now());
        double convRevAfter = trackingService.getConversionRevenue(cId, LocalDate.now());
        Assert.assertEquals(convCountBefore + 1, convCountAfter);
        Assert.assertEquals(convRevBefore + 128.50, convRevAfter, 0.001);

        // 3. 模拟同一 click_id 重复回传 (防止重复记账与重复归因)
        Map<String, Object> dupResult = trackingService.attributeConversion(clickId, "purchase", 128.50);
        Assert.assertEquals("duplicate", dupResult.get("status"));

        // 转化量与金额不应再次累加
        Assert.assertEquals(convCountAfter, trackingService.getConversionCount(cId, LocalDate.now()));
        Assert.assertEquals(convRevAfter, trackingService.getConversionRevenue(cId, LocalDate.now()), 0.001);

        // 4. 模拟未知非法 click_id 转化回传
        Map<String, Object> unattrResult = trackingService.attributeConversion("clk-non-existent-token", "purchase", 50.0);
        Assert.assertEquals("unattributed", unattrResult.get("status"));
    }

    private BidRequest createBannerBidRequest(String id, int w, int h, double bidfloor) {
        BidRequest req = new BidRequest();
        req.setId(id);
        Imp imp = new Imp();
        imp.setId("imp-" + id);
        Banner banner = new Banner();
        banner.setW(w);
        banner.setH(h);
        imp.setBanner(banner);
        imp.setBidfloor(bidfloor);
        imp.setBidfloorcur("USD");
        req.setImp(Collections.singletonList(imp));
        return req;
    }
}
