# DSP 端到端自动化测试与质量保障指南

本文档全面介绍 DSP 系统的自动化集成测试与端到端（End-to-End, E2E）测试架构、测试用例清单、隔离策略与执行规范。

---

## 1. 测试工程概述

系统采用 **分层测试与全链路闭环验证** 策略，涵盖广告主生命周期管理、资质审核、多维权限安全隔离、OpenRTB 2.5 实时竞价协议、胜出核销幂等、反作弊风控、频控、预算平滑及转化归因等全部核心业务。

全工程拥有 **21 项端到端核心集成测试用例**，全部通过持续集成保证 100% 通过（0 Failures, 0 Errors）。

---

## 2. 测试套件矩阵

### 2.1 业务全链路测试：`DspComprehensiveE2ETest` (6 项用例)
聚焦广告业务系统生命周期、资质流转、资金预算与水平/垂直越权隔离：

| 测试用例方法名 | 验证的核心业务逻辑 | 预期断言 |
| :--- | :--- | :--- |
| `testAdvertiserLifecycleAndStatus` | 广告主注册、资质提交、风控审核通过/驳回生命周期 | 状态流转符合状态机，非合规状态不可投放 |
| `testCampaignCreationAndValidation` | 广告活动创建、排期校验、日预算合法性检验 | 非法排期/负数预算抛出校验异常 |
| `testMaterialUploadAndReview` | 广告创意物料上传、尺寸规格验证、审核状态同步 | 规格符合方可过审进入候选池 |
| `testVerticalPermissionSecurity` | 垂直越权检查：普通广告主禁止调用管理员审核/调账接口 | 拦截并返回 403 / 越权禁止异常 |
| `testHorizontalPermissionSecurity` | 水平越权检查：广告主 A 禁止查看或篡改广告主 B 的活动/报表 | 数据查询隔离，禁止跨租户访问 |
| `testBudgetDeductionAndRecharge` | 广告主账户充值、冻结、扣款与账单对齐 | 账户余额计算精确，杜绝负数与并发超扣 |

### 2.2 OpenRTB 核心出价测试：`OpenRtbBiddingE2ETest` (6 项用例)
聚焦标准 OpenRTB 2.5 协议解析、候选匹配、底价过滤与胜出计费防重：

| 测试用例方法名 | 验证的核心业务逻辑 | 预期断言 |
| :--- | :--- | :--- |
| `testNormalBannerBiddingSuccess` | 标准 PC/Mobile Banner 询价与最优创意匹配 | 返回 HTTP 200，组装合规 `BidResponse` 与 `seatbid` |
| `testFloorPriceFiltering` | 底价过滤：竞价出价低于媒体底价（`bidfloor`）时放弃 | 引擎过滤低价候选，返回 HTTP 204 No Content |
| `testDimensionAndFormatFiltering` | 尺寸与格式过滤：媒体位尺寸与物料尺寸不匹配 | 精准尺寸比对，不匹配时放弃出价 (204) |
| `testCampaignScheduleAndBudgetFiltering`| 排期与预算过滤：未在投放日期区间或今日预算已耗尽 | 预算熔断与排期过滤生效，返回 204 |
| `testAuctionPriceMacroSubstitution` | `${AUCTION_PRICE}` 宏替换：胜出回执价格解密与动态替换 | 胜出通知 URL 中成功替换真实结算价格 |
| `testWinNoticeIdempotentAndBilling` | 胜出回执幂等性：基于 Redis 排他锁防止重复扣款 | 首次胜出成功扣款，重复回执返回已处理，不重复扣费 |

### 2.3 Phase 1 & 2 高级特性测试：`OpenRtbAdvancedFeaturesE2ETest` (9 项用例)
聚焦纳秒级反作弊、频次控制、Pacing 算法、视频/原生物料与效果归因：

| 测试用例方法名 | 验证的核心业务逻辑 | 预期断言 |
| :--- | :--- | :--- |
| `testAntiFraud_BotUserAgent_ShouldReturnNoBid204` | 爬虫与自动化工具 UA 拦截（curl, python, spider 等） | 命中正则，直接响应 HTTP 204 NO_BID |
| `testAntiFraud_CloudIpSubnet_ShouldReturnNoBid204` | 机房/IDC 云厂商 IP 位运算子网阻断（AWS, GCP, 阿里云等） | 命中 29 个云服务商 CIDR，直接响应 HTTP 204 |
| `testAntiFraud_RedisBlacklist_ShouldReturnNoBid204` | 动态 Redis IP 黑名单惩罚拦截 | 命中黑名单 Key，直接响应 HTTP 204 |
| `testFrequencyCapping_ShouldFilterWhenDailyCapExceeded` | 受众精细化频次控制：同一用户达到天级上限时过滤 | 达到最大展示频次后，出价引擎主动放弃该活动 (204) |
| `testFrequencyCapping_WinNoticeShouldIncrementCounter` | 胜出曝光确认：仅在收到有效 WinNotice 时原子递增频次 | 未胜出不虚扣，胜出后 Redis 计数精准 +1 |
| `testBudgetPacing_EvenMode_Throttling` | 平滑预算消耗调度：EVEN 模式概率节流 (Probabilistic Pacing) | 超额进度触发动态降权节流，有效抑制高峰脉冲 |
| `testVideoAd_Vast3XmlAdm` | 视频广告支持：识别 Video 位并生成 IAB VAST 3.0 XML | `adm` 输出标准 `<VAST version="3.0">`，包含追踪与时长 |
| `testNativeAd_JsonAdm` | 原生广告支持：识别 Native 位并生成 Native 1.2 JSON | `adm` 输出原生结构化资产（title, img, data 等） |
| `testClickTrackingAndConversionAttribution` | 点击追踪与 S2S 效果转化归因全闭环 | 点击 302 签发 `click_id`，S2S 回传排重与 GMV 记账 |

---

## 3. 测试环境与隔离设计

1. **Spring 容器上下文隔离**：
   - 采用 `@RunWith(SpringJUnit4ClassRunner.class)` 与 `@ContextConfiguration`；
   - 核心配置文件：`classpath:config/spring/spring.xml`；
   - 由于 `spring.xml` 显式排除了 `@Controller` 扫描，测试类中通过手动实例化控制器并注入 Service（如 `new OpenRtbTrackingController()`），杜绝容器上下文冲突。
2. **中间件解耦与 Mock 策略**：
   - 使用内存级或 Mock 方式隔离外部中间件（MySQL、Redis、RabbitMQ），支持在无外部依赖的纯净 CI/CD 容器中毫秒级执行完毕；
   - 对 Redis 客户端服务采用内存 Map 模拟原子计数与 SETNX 排他锁；
   - 对 RabbitMQ 消息队列采用即时捕获验证，确保事件投递契约正确。

---

## 4. 测试执行命令

### 4.1 执行全量端到端测试
```bash
# 宿主机 Maven 执行 (Windows / Linux / macOS)
mvn test "-Dtest=DspComprehensiveE2ETest,OpenRtbBiddingE2ETest,OpenRtbAdvancedFeaturesE2ETest"

# 使用 Docker 容器化执行 (免本地配置环境)
docker run --rm \
  -v "$(pwd)":/app \
  -v ~/.m2:/root/.m2 \
  -w /app \
  maven:3.8.8-eclipse-temurin-8 \
  mvn test "-Dtest=DspComprehensiveE2ETest,OpenRtbBiddingE2ETest,OpenRtbAdvancedFeaturesE2ETest"
```

### 4.2 单独执行特定测试套件
```bash
# 仅执行 OpenRTB 高阶特性测试
mvn test "-Dtest=OpenRtbAdvancedFeaturesE2ETest"

# 仅执行 OpenRTB 基础竞价测试
mvn test "-Dtest=OpenRtbBiddingE2ETest"

# 仅执行业务全链路管理测试
mvn test "-Dtest=DspComprehensiveE2ETest"
```

---

## 5. 持续集成与质量门禁

为确保系统代码长期处于高可用与高鲁棒性状态，所有 Pull Request 与分支合并均须满足：
1. **0 Test Failure & 0 Test Error**：21 项端到端测试必须全部绿色通过；
2. **代码规范与坏味道归零**：杜绝未关闭流、浮点金额计算、非原子计数及越权漏洞；
3. **协议兼容性**：修改竞价链路必须保证对 IAB OpenRTB 2.5 规范的向前/向后兼容。
