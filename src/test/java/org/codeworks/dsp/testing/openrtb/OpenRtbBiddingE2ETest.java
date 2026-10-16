package org.codeworks.dsp.testing.openrtb;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.codeworks.dsp.dao.AdvertiserRepository;
import org.codeworks.dsp.dao.CampaignRepository;
import org.codeworks.dsp.dao.MaterialRepository;
import org.codeworks.dsp.model.entities.Advertiser;
import org.codeworks.dsp.model.entities.Campaign;
import org.codeworks.dsp.model.entities.Material;
import org.codeworks.dsp.model.entities.Qualification;
import org.codeworks.dsp.openrtb.dto.request.Banner;
import org.codeworks.dsp.openrtb.dto.request.BidRequest;
import org.codeworks.dsp.openrtb.dto.request.Format;
import org.codeworks.dsp.openrtb.dto.request.Imp;
import org.codeworks.dsp.openrtb.dto.response.Bid;
import org.codeworks.dsp.openrtb.dto.response.BidResponse;
import org.codeworks.dsp.openrtb.service.OpenRtbBiddingService;
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
 * OpenRTB 2.5 核心出价引擎与胜出结算端到端测试
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:config/spring/spring.xml")
@ActiveProfiles("local")
public class OpenRtbBiddingE2ETest {

    @Autowired
    private OpenRtbBiddingService biddingService;

    @Autowired
    private AdvertiserRepository advertiserRepository;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private MaterialRepository materialRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Advertiser testAdv;
    private Campaign testCampaign;
    private Material testMaterial;

    @Before
    public void setupData() {
        // 构造测试资质
        Qualification qual = new Qualification();
        qual.setLicence("LIC-TEST-12345");
        qual.setCompanyName("Test DSP Corp");
        qual.setOfficialSite("https://shop.example.com");
        qual.setSiteName("DSP Shop");
        qual.setTradeId(1);

        // 构造测试广告主 (用户名长度限制为 20 字符以内)
        String username = "adv" + (System.currentTimeMillis() % 10000000L);
        testAdv = new Advertiser();
        testAdv.setUserName(username);
        testAdv.setPassword("e10adc3949ba59abbe56e057f20f883e");
        testAdv.setEmail(username + "@dsp.local");
        testAdv.setBalance(100.00);
        testAdv.setIsAdmin(false);
        testAdv.setReview(Advertiser.Review.eligible);
        testAdv.setStatus(Advertiser.Status.eligible);
        testAdv.setSendAdx(Advertiser.SendAdx.no);
        testAdv.setCreateDate(LocalDateTime.now());
        testAdv.setLastLoginDate(LocalDateTime.now());
        testAdv.setQualification(qual);
        testAdv = advertiserRepository.save(testAdv);

        // 构造测试活动 (CPM 出价 3.50，预算 500.00)
        testCampaign = new Campaign();
        testCampaign.setName("Test OpenRTB Campaign");
        testCampaign.setAdvertiser(testAdv);
        testCampaign.setBid(3.50);
        testCampaign.setBudget(500.00);
        testCampaign.setBudgetType(Campaign.BudgetType.daily);
        testCampaign.setStatus(Campaign.Status.eligible);
        testCampaign.setReview(Campaign.Review.eligible);
        testCampaign.setStartDate(LocalDate.now().minusDays(1));
        testCampaign.setEndDate(LocalDate.now().plusDays(30));
        testCampaign.setCreateDate(LocalDateTime.now());
        testCampaign.setSync(Campaign.Sync.not);
        testCampaign = campaignRepository.save(testCampaign);

        // 构造测试素材 (300x250)
        testMaterial = new Material();
        testMaterial.setName("Banner 300x250 Creative");
        testMaterial.setAdvertiser(testAdv);
        testMaterial.setCampaign(testCampaign);
        testMaterial.setWidth(300);
        testMaterial.setHeight(250);
        testMaterial.setUrl("http://cdn.dsp.local/creatives/banner300x250.png");
        testMaterial.setLandingUrl("https://shop.example.com/product/123");
        testMaterial.setAdDomain("example.com");
        testMaterial.setCpmPrice(3.50);
        testMaterial.setStatus(Material.Status.eligible);
        testMaterial.setReview(Material.Review.eligible);
        testMaterial.setCreateDate(LocalDateTime.now());
        testMaterial.setType(Material.Type.image);
        testMaterial.setTradeId(1);
        testMaterial = materialRepository.save(testMaterial);

        // 绑定活动下素材关系
        Set<Material> materials = new HashSet<>();
        materials.add(testMaterial);
        testCampaign.setMaterials(materials);
        campaignRepository.save(testCampaign);
    }

    /**
     * 测试 1：验证 OpenRTB 2.5 JSON 序列化与反序列化
     */
    @Test
    public void testOpenRtbJsonSerialization() throws Exception {
        BidRequest req = new BidRequest();
        req.setId("test-auction-req-001");

        Imp imp = new Imp();
        imp.setId("imp-1");
        imp.setBidfloor(1.20);
        imp.setBidfloorcur("USD");

        Banner banner = new Banner();
        banner.setW(300);
        banner.setH(250);
        banner.setFormat(Collections.singletonList(new Format(300, 250)));
        imp.setBanner(banner);

        req.setImp(Collections.singletonList(imp));

        String json = objectMapper.writeValueAsString(req);
        Assert.assertTrue(json.contains("test-auction-req-001"));
        Assert.assertTrue(json.contains("300"));
        Assert.assertTrue(json.contains("250"));

        BidRequest parsed = objectMapper.readValue(json, BidRequest.class);
        Assert.assertEquals("test-auction-req-001", parsed.getId());
        Assert.assertEquals(1, parsed.getImp().size());
        Assert.assertEquals(Integer.valueOf(300), parsed.getImp().get(0).getBanner().getW());
    }

    /**
     * 测试 2：端到端出价匹配，底价低于出价时成功返回出价响应
     */
    @Test
    public void testBiddingSuccessWhenFloorMet() {
        BidRequest req = new BidRequest();
        req.setId("auction-" + UUID.randomUUID());

        Imp imp = new Imp();
        imp.setId("imp-slot-1");
        imp.setBidfloor(1.50); // 底价 1.50，我们的出价为 3.50
        imp.setBidfloorcur("USD");

        Banner banner = new Banner();
        banner.setW(300);
        banner.setH(250);
        imp.setBanner(banner);

        req.setImp(Collections.singletonList(imp));

        Optional<BidResponse> responseOpt = biddingService.processBid(req);
        Assert.assertTrue("Should return a valid winning bid response", responseOpt.isPresent());

        BidResponse resp = responseOpt.get();
        Assert.assertEquals(req.getId(), resp.getId());
        Assert.assertEquals(1, resp.getSeatbid().size());

        Bid winningBid = resp.getSeatbid().get(0).getBid().get(0);
        Assert.assertEquals("imp-slot-1", winningBid.getImpid());
        Assert.assertEquals(Double.valueOf(3.50), winningBid.getPrice());
        Assert.assertEquals(Integer.valueOf(300), winningBid.getW());
        Assert.assertEquals(Integer.valueOf(250), winningBid.getH());
        Assert.assertTrue("Creative adm must contain click landingUrl", winningBid.getAdm().contains(testMaterial.getLandingUrl()));
        Assert.assertTrue("Creative adm must contain image url", winningBid.getAdm().contains(testMaterial.getUrl()));
        Assert.assertTrue("Win notice URL must contain macro ${AUCTION_PRICE}", winningBid.getNurl().contains("${AUCTION_PRICE}"));
    }

    /**
     * 测试 3：当底价高于我方出价时，应返回 NO_BID (Optional.empty)
     */
    @Test
    public void testBiddingNoBidWhenFloorTooHigh() {
        BidRequest req = new BidRequest();
        req.setId("auction-high-floor");

        Imp imp = new Imp();
        imp.setId("imp-slot-2");
        imp.setBidfloor(10.00); // 底价 10.00，我方仅出价 3.50，无法满足
        imp.setBidfloorcur("USD");

        Banner banner = new Banner();
        banner.setW(300);
        banner.setH(250);
        imp.setBanner(banner);

        req.setImp(Collections.singletonList(imp));

        Optional<BidResponse> responseOpt = biddingService.processBid(req);
        Assert.assertFalse("Should return NO_BID (empty) when bid floor exceeds budget/bid", responseOpt.isPresent());
    }

    /**
     * 测试 4：当尺寸不匹配时，应返回 NO_BID (Optional.empty)
     */
    @Test
    public void testBiddingNoBidWhenDimensionMismatch() {
        BidRequest req = new BidRequest();
        req.setId("auction-size-mismatch");

        Imp imp = new Imp();
        imp.setId("imp-slot-3");
        imp.setBidfloor(1.00);

        Banner banner = new Banner();
        banner.setW(728);
        banner.setH(90); // 媒体要求 728x90，我们只有 300x250
        imp.setBanner(banner);

        req.setImp(Collections.singletonList(imp));

        Optional<BidResponse> responseOpt = biddingService.processBid(req);
        Assert.assertFalse("Should return NO_BID when dimension does not match", responseOpt.isPresent());
    }

    /**
     * 测试 5：胜出结算与扣费闭环 (Win Notice 处理)
     */
    @Test
    public void testWinNoticeSettlement() {
        double initialBalance = testAdv.getBalance();
        double clearingCpm = 2.80; // 第二价格竞价胜出结算价为 $2.80 CPM
        double singleCost = clearingCpm / 1000.0; // 本次实际扣费 $0.0028
        String uniqueBidId = "bid-test-settle-" + System.currentTimeMillis();

        boolean settled = biddingService.handleWinNotice(
                uniqueBidId, clearingCpm, testAdv.getId(), testCampaign.getId(), testMaterial.getId());

        Assert.assertTrue("Settlement should succeed", settled);

        // 重新从数据库拉取广告主最新余额
        Advertiser reloadedAdv = advertiserRepository.getById(testAdv.getId()).get();
        double expectedBalance = initialBalance - singleCost;

        Assert.assertEquals(expectedBalance, reloadedAdv.getBalance(), 0.00001);
    }

    /**
     * 测试 6：验证胜出通知幂等性 (重复 Win Notice 防重放与重复扣费)
     */
    @Test
    public void testWinNoticeIdempotency() {
        String uniqueBidId = "bid-idempotent-" + System.currentTimeMillis();
        double clearingCpm = 2.00; // $2.00 CPM = $0.002
        double singleCost = clearingCpm / 1000.0;

        Advertiser advBefore = advertiserRepository.getById(testAdv.getId()).get();
        double balanceBefore = advBefore.getBalance();

        // 第一次胜出通知
        boolean firstCall = biddingService.handleWinNotice(
                uniqueBidId, clearingCpm, testAdv.getId(), testCampaign.getId(), testMaterial.getId());
        Assert.assertTrue("First win notice must succeed", firstCall);

        Advertiser advAfterFirst = advertiserRepository.getById(testAdv.getId()).get();
        Assert.assertEquals(balanceBefore - singleCost, advAfterFirst.getBalance(), 0.00001);

        // 第二次重复通知 (模拟网络重试或重复打点)
        boolean secondCall = biddingService.handleWinNotice(
                uniqueBidId, clearingCpm, testAdv.getId(), testCampaign.getId(), testMaterial.getId());
        Assert.assertTrue("Second duplicate win notice must also return true (idempotent)", secondCall);

        // 余额必须保持不变，绝不重复扣费！
        Advertiser advAfterSecond = advertiserRepository.getById(testAdv.getId()).get();
        Assert.assertEquals("Balance must not be deducted a second time", advAfterFirst.getBalance(), advAfterSecond.getBalance(), 0.00001);
    }

    /**
     * 测试 7：验证活动预算熔断 (日预算超限跳过出价)
     */
    @Test
    public void testCampaignBudgetCircuitBreaker() {
        // 先将当前库中其他活跃活动暂时屏蔽，以严格单变量隔离测试当前活动预算
        List<Campaign> allActive = campaignRepository.findActiveCampaignsWithinDate(LocalDate.now(), LocalDate.now());
        List<Campaign> others = new ArrayList<>();
        for (Campaign c : allActive) {
            if (!c.getId().equals(testCampaign.getId())) {
                c.setStatus(Campaign.Status.ineligible);
                campaignRepository.save(c);
                others.add(c);
            }
        }

        try {
            // 设置一个微型日预算 $0.002
            testCampaign.setBudget(0.002);
            testCampaign.setBudgetType(Campaign.BudgetType.daily);
            campaignRepository.save(testCampaign);

            // 触发一次胜出结算，扣除 $0.003 (超过 $0.002 预算)
            String winBidId = "bid-budget-burst-" + System.currentTimeMillis();
            biddingService.handleWinNotice(winBidId, 3.00, testAdv.getId(), testCampaign.getId(), testMaterial.getId());

            // 再次发起竞价请求，应触发预算熔断返回 NO_BID
            BidRequest req = new BidRequest();
            req.setId("auction-budget-test");
            Imp imp = new Imp();
            imp.setId("imp-budget-1");
            imp.setBidfloor(1.00);
            Banner banner = new Banner();
            banner.setW(300);
            banner.setH(250);
            imp.setBanner(banner);
            req.setImp(Collections.singletonList(imp));

            Optional<BidResponse> responseOpt = biddingService.processBid(req);
            Assert.assertFalse("Should return NO_BID because campaign budget has been exceeded", responseOpt.isPresent());
        } finally {
            // 恢复活动状态
            testCampaign.setBudget(50000.0);
            campaignRepository.save(testCampaign);
            for (Campaign c : others) {
                c.setStatus(Campaign.Status.eligible);
                campaignRepository.save(c);
            }
        }
    }
}
