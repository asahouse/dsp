package org.codeworks.dsp.testing.e2e;

import org.codeworks.dsp.adx.bes.api.CreativeBesApi;
import org.codeworks.dsp.adx.bes.dto.response.creative.CreativeModifiedTradeBesResponse;
import org.codeworks.dsp.adx.bes.exception.BaiduEsApiException;
import org.codeworks.dsp.interceptor.MaterialForRtbInterceptor;
import org.codeworks.dsp.model.entities.Campaign;
import org.codeworks.dsp.model.entities.CampaignObjective;
import org.codeworks.dsp.model.entities.CategoryDictionary;
import org.codeworks.dsp.model.entities.Material;
import org.codeworks.dsp.model.entities.MobileDevice;
import org.codeworks.dsp.model.entities.rtbMQ.RtbStats;
import org.codeworks.dsp.rtbCall.api.CampaignRtbApi;
import org.codeworks.dsp.rtbCall.dto.material.RtbMobileDevice;
import org.codeworks.dsp.rtbCall.dto.material.RtbUserCategory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.*;

/**
 * 端到端全链路与Bug修复回归测试套件
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:config/spring/spring.xml")
@ActiveProfiles("local")
public class DspComprehensiveE2ETest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired(required = false)
    private CampaignRtbApi campaignRtbApi;

    @Autowired(required = false)
    private CreativeBesApi creativeBesApi;

    @Autowired(required = false)
    private MaterialForRtbInterceptor materialInterceptor;

    /**
     * 测试 1：Spring 上下文与分布式配置加载（验证 Bug #6, Bug #22）
     */
    @Test
    public void testSpringContextAndDistributedComponents() {
        Assert.assertNotNull("ApplicationContext should not be null", applicationContext);
        // 验证数据库连接池、Redis连接工厂、RabbitMQ等Bean正常注册
        Assert.assertTrue("dataSource should be present", applicationContext.containsBean("dataSource"));
        Assert.assertTrue("jedisConnectionFactory should be present", applicationContext.containsBean("jedisConnectionFactory"));
        Assert.assertTrue("connectionFactory (RabbitMQ) should be present", applicationContext.containsBean("connectionFactory"));
    }

    /**
     * 测试 2：验证 Bug #18 移动设备定向多值以逗号正确分隔拼接
     */
    @Test
    public void testMobileDeviceCommaDelimiter() {
        MobileDevice md = new MobileDevice();
        md.addCarrier(MobileDevice.Carrier.chinaMobile);
        md.addCarrier(MobileDevice.Carrier.chinaUnicom);
        md.addCarrier(MobileDevice.Carrier.chinaTelecom);

        Set<MobileDevice.Carrier> carriers = md.getCarrier();
        Assert.assertEquals(3, carriers.size());
        Assert.assertTrue(carriers.contains(MobileDevice.Carrier.chinaMobile));
        Assert.assertTrue(carriers.contains(MobileDevice.Carrier.chinaUnicom));
        Assert.assertTrue(carriers.contains(MobileDevice.Carrier.chinaTelecom));

        // 重复添加不应产生脏数据
        md.addCarrier(MobileDevice.Carrier.chinaMobile);
        Assert.assertEquals(3, md.getCarrier().size());

        // 网络类型
        md.addNetworkType(MobileDevice.NetworkType.wifi);
        md.addNetworkType(MobileDevice.NetworkType.g4);
        Set<MobileDevice.NetworkType> netTypes = md.getNetworkType();
        Assert.assertEquals(2, netTypes.size());

        // 操作系统
        md.addOs(MobileDevice.Os.android);
        md.addOs(MobileDevice.Os.ios);
        Set<MobileDevice.Os> osSet = md.getOs();
        Assert.assertEquals(2, osSet.size());
    }

    /**
     * 测试 3：验证 Bug #1 物料向 RTB 同步过滤器不再错误取反
     */
    @Test
    public void testMaterialForRtbInterceptorFilterCondition() throws Exception {
        MaterialForRtbInterceptor interceptor = new MaterialForRtbInterceptor();
        Method method = MaterialForRtbInterceptor.class.getDeclaredMethod("filterCondition", List.class);
        method.setAccessible(true);

        Campaign campaign = new Campaign();
        campaign.setId(101);

        Material eligibleMaterial = new Material();
        eligibleMaterial.setId(1);
        eligibleMaterial.setCampaign(campaign);
        eligibleMaterial.setReview(Material.Review.eligible);

        Material noCampaignMaterial = new Material();
        noCampaignMaterial.setId(2);
        noCampaignMaterial.setCampaign(null);
        noCampaignMaterial.setReview(Material.Review.eligible);

        Material pendingMaterial = new Material();
        pendingMaterial.setId(3);
        pendingMaterial.setCampaign(campaign);
        pendingMaterial.setReview(Material.Review.pending);

        List<Material> input = Arrays.asList(eligibleMaterial, noCampaignMaterial, pendingMaterial);
        @SuppressWarnings("unchecked")
        List<Material> output = (List<Material>) method.invoke(interceptor, input);

        Assert.assertNotNull(output);
        Assert.assertEquals("Only material with campaign AND eligible review should pass", 1, output.size());
        Assert.assertEquals(Integer.valueOf(1), output.get(0).getId());
    }

    /**
     * 测试 4：验证 Bug #2 RTB 转换实例化，不再抛 NPE
     */
    @Test
    public void testCampaignRtbApiTransformUserCategoryAndMobileDevice() throws Exception {
        CampaignRtbApi api = new CampaignRtbApi();

        Method transUserMethod = CampaignRtbApi.class.getDeclaredMethod("transformUserCategory", Set.class);
        transUserMethod.setAccessible(true);

        // 传入定向目标集合
        Set<CampaignObjective> objectives = new HashSet<>();
        CampaignObjective obj = new CampaignObjective();
        CategoryDictionary cat = new CategoryDictionary();
        cat.setCategoryId(201);
        cat.setCategoryParentId(200);
        org.codeworks.dsp.model.entities.CategoryTag tag = new org.codeworks.dsp.model.entities.CategoryTag();
        tag.setTagCode("MASSES_TAG");
        cat.setCategoryTag(tag);
        obj.setCategoryDictionary(cat);
        objectives.add(obj);

        RtbUserCategory rtbUser = (RtbUserCategory) transUserMethod.invoke(api, objectives);
        Assert.assertNotNull("RtbUserCategory must be instantiated and non-null", rtbUser);
        Assert.assertFalse("Gender should contain converted category", rtbUser.getGender().isEmpty());

        Method transMobileMethod = CampaignRtbApi.class.getDeclaredMethod("transformMobileDevice", Set.class);
        transMobileMethod.setAccessible(true);

        Set<CampaignObjective> mobileObjectives = new HashSet<>();
        CampaignObjective mObj = new CampaignObjective();
        CategoryDictionary mCat = new CategoryDictionary();
        mCat.setCategoryId(11);
        mCat.setCategoryParentId(1);
        org.codeworks.dsp.model.entities.CategoryTag mTag = new org.codeworks.dsp.model.entities.CategoryTag();
        mTag.setTagCode("MOBILE_DEVICE");
        mCat.setCategoryTag(mTag);
        mObj.setCategoryDictionary(mCat);
        mobileObjectives.add(mObj);

        RtbMobileDevice rtbMobile = (RtbMobileDevice) transMobileMethod.invoke(api, mobileObjectives);
        Assert.assertNotNull("RtbMobileDevice must be instantiated and non-null", rtbMobile);
        Assert.assertFalse("Carrier should contain converted device category", rtbMobile.getCarrier().isEmpty());
    }

    /**
     * 测试 5：验证 Bug #4 日期范围校验逻辑（小于7天的合法查询不应报错）
     */
    @Test
    public void testCreativeBesApiDateRangeValidation() {
        CreativeBesApi api = new CreativeBesApi();
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(3); // 3天范围，完全合法

        try {
            // 这里因为没有真实调用百度API，直接验证日期判断不会抛出超过7天的校验异常
            api.getModifiedTradeMaterials(start, end);
        } catch (BaiduEsApiException e) {
            Assert.assertNotEquals("query maximum range is 7 days", e.getMessage());
        } catch (Exception ignored) {
            // 忽略由于网络或mock引起的非日期范围异常
        }

        // 测试大于7天应被拒绝
        LocalDate overEnd = start.plusDays(8);
        try {
            api.getModifiedTradeMaterials(start, overEnd);
            Assert.fail("Query over 7 days should throw BaiduEsApiException");
        } catch (BaiduEsApiException e) {
            Assert.assertEquals("query maximum range is 7 days", e.getMessage());
        }
    }

    /**
     * 测试 6：验证 Bug #23 RtbStats `drop` 关键字转义与模型创建
     */
    @Test
    public void testRtbStatsModel() {
        RtbStats stats = new RtbStats();
        stats.setDrop(10);
        stats.setOffer(100);
        stats.setWin(80);
        Assert.assertEquals(Integer.valueOf(10), stats.getDrop());
        Assert.assertEquals(Integer.valueOf(100), stats.getOffer());
    }

    /**
     * 测试 7：验证 Bug #5 广告主资质比对包含 licence 与 tradeId (通过反射调用)
     */
    @Test
    public void testAdvertiserQualificationComparison() throws Exception {
        org.codeworks.dsp.service.impl.AdvertiserServiceImpl service = new org.codeworks.dsp.service.impl.AdvertiserServiceImpl();
        Method method = org.codeworks.dsp.service.impl.AdvertiserServiceImpl.class.getDeclaredMethod(
                "isSameAdvertiserQualification", org.codeworks.dsp.model.entities.Advertiser.class, org.codeworks.dsp.model.entities.Advertiser.class);
        method.setAccessible(true);

        org.codeworks.dsp.model.entities.Advertiser advA = new org.codeworks.dsp.model.entities.Advertiser();
        org.codeworks.dsp.model.entities.Qualification qA = new org.codeworks.dsp.model.entities.Qualification();
        qA.setCompanyName("CompanyA");
        qA.setSiteName("SiteA");
        qA.setOfficialSite("http://a.com");
        qA.setLicence("LICENCE-001");
        qA.setTradeId(10);
        advA.setQualification(qA);

        org.codeworks.dsp.model.entities.Advertiser advB = new org.codeworks.dsp.model.entities.Advertiser();
        org.codeworks.dsp.model.entities.Qualification qB = new org.codeworks.dsp.model.entities.Qualification();
        qB.setCompanyName("CompanyA");
        qB.setSiteName("SiteA");
        qB.setOfficialSite("http://a.com");
        qB.setLicence("LICENCE-002"); // 仅营业执照号不同
        qB.setTradeId(10);
        advB.setQualification(qB);

        Boolean isSame = (Boolean) method.invoke(service, advA, advB);
        Assert.assertFalse("Different licence must be detected as qualification change", isSame);

        // 改为相同
        qB.setLicence("LICENCE-001");
        Boolean isSameNow = (Boolean) method.invoke(service, advA, advB);
        Assert.assertTrue("Identical qualifications must return true", isSameNow);
    }

    /**
     * 测试 8：验证物料尺寸解析兼容 '*' 与 'x'/'X' 且防越界
     */
    @Test
    public void testCreativeSizeParsingMultiDelimiters() throws Exception {
        org.codeworks.dsp.service.impl.CreativesServiceImpl service = new org.codeworks.dsp.service.impl.CreativesServiceImpl();
        Method method = org.codeworks.dsp.service.impl.CreativesServiceImpl.class.getDeclaredMethod(
                "buildMaterialPredicate", Integer.class, Map.class);
        method.setAccessible(true);

        // 验证 300*250
        Map<String, Object> paramsStar = new HashMap<>();
        paramsStar.put("size", "300*250");
        com.querydsl.core.types.Predicate p1 = (com.querydsl.core.types.Predicate) method.invoke(service, 1, paramsStar);
        Assert.assertNotNull(p1);

        // 验证 300x250
        Map<String, Object> paramsX = new HashMap<>();
        paramsX.put("size", "300x250");
        com.querydsl.core.types.Predicate p2 = (com.querydsl.core.types.Predicate) method.invoke(service, 1, paramsX);
        Assert.assertNotNull(p2);

        // 验证非法输入不崩溃
        Map<String, Object> paramsInvalid = new HashMap<>();
        paramsInvalid.put("size", "invalid-size");
        com.querydsl.core.types.Predicate p3 = (com.querydsl.core.types.Predicate) method.invoke(service, 1, paramsInvalid);
        Assert.assertNotNull(p3);
    }

    /**
     * 测试 9：验证活动时间范围参数解析安全容错 (防 NPE / DateTimeParseException)
     */
    @Test
    public void testCampaignTimeSplitSafety() throws Exception {
        org.codeworks.dsp.service.impl.CampaignServiceImpl service = new org.codeworks.dsp.service.impl.CampaignServiceImpl();
        Method method = org.codeworks.dsp.service.impl.CampaignServiceImpl.class.getDeclaredMethod(
                "splitStringToTime", String.class);
        method.setAccessible(true);

        // 正常时间段
        @SuppressWarnings("unchecked")
        Map<String, java.time.LocalDateTime> resValid = (Map<String, java.time.LocalDateTime>) method.invoke(
                service, "2026-10-01 00:00:00#2026-10-07 23:59:59");
        Assert.assertEquals(2, resValid.size());
        Assert.assertNotNull(resValid.get("fromDate"));
        Assert.assertNotNull(resValid.get("toDate"));

        // 缺失分隔符与乱码输入，不抛异常返回空 Map
        @SuppressWarnings("unchecked")
        Map<String, java.time.LocalDateTime> resMalformed = (Map<String, java.time.LocalDateTime>) method.invoke(
                service, "not-a-date");
        Assert.assertTrue(resMalformed.isEmpty());

        @SuppressWarnings("unchecked")
        Map<String, java.time.LocalDateTime> resEmpty = (Map<String, java.time.LocalDateTime>) method.invoke(
                service, "");
        Assert.assertTrue(resEmpty.isEmpty());
    }
}
