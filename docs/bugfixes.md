# DSP 核心缺陷修复与安全加固清单 (18 项)

本版本针对系统中存在的并发隐患、越权风险、协议缺失、兼容性故障等 18 项核心缺陷实施了深度重构与修复，并通过端到端（E2E）全链路测试验证。

---

## 缺陷与加固清单对照表

| 序号 | 缺陷分类 | 问题描述 | 修复与改造方案 | 涉及关键类 |
| :---: | :--- | :--- | :--- | :--- |
| **01** | **并发与资金** | 并发竞价胜出导致日预算超支（超卖） | 引入 Redis 原子递增与毫秒级日预算熔断机制，超支即刻下线活动 | `OpenRtbBiddingServiceImpl` |
| **02** | **行业标准协议** | 缺乏 OpenRTB 2.5 与 Prebid Server 标头竞价支持 | 完整实现 OpenRTB 2.5 出价决策与胜出通知回执标准链路 | `OpenRtbBidController`, `OpenRtbWinNoticeController` |
| **03** | **计费与核销** | 胜出回执网络重试导致重复计费 | 实施 Redis 分布式防重锁（SETNX + 24h 过期）与内存兜底双轨幂等控制 | `OpenRtbBiddingServiceImpl` |
| **04** | **权限与越权** | 广告主接口存在水平与垂直越权漏洞 | 在 Controller 层强校验 Session 登录账号身份，拒绝非管理员操作他人资产 | `CampaignController`, `MaterialController` |
| **05** | **合规安全** | 未审核或审核驳回广告主可创建/投放广告 | 引入广告主资质强校验 `Review.eligible` 前置拦截守卫 | `CampaignController`, `CampaignManageController` |
| **06** | **稳定性** | ReportController 日期入参为空时触发空指针异常 | 重构日期解析逻辑，提供默认回退区间（最近 7 天）兜底保障 | `ReportController` |
| **07** | **框架兼容** | Spring Data Redis 1.7.2 原生 setIfAbsent 方法签名不兼容 | 适配 2 参数 `setIfAbsent(k, v)` + `expire()` 两阶段原子性调度 | `OpenRtbBiddingServiceImpl` |
| **08** | **业务逻辑** | 广告活动开始与结束排期越界及逆序 | 完善排期区间合法性检查，禁止结束日期早于开始日期 | `CampaignServiceImpl` |
| **09** | **ORM 映射** | 行业字典与移动设备定向字段级映射异常 | 修正 CategoryDictionary, MobileDevice 实体属性与 MySQL 表结构映射 | `CategoryDictionary.java`, `MobileDevice.java` |
| **10** | **异步削峰** | 竞价统计 MQ 消息体格式不标准导致消费异常 | 规范化 RabbitMQ 消息载体模型并增加消费幂等处理 | `RtbDataService`, `RtbStats` |
| **11** | **缓存雪崩** | 外部站点统计分析接口在无数据时触发穿透 | 在 `EhcacheUtil` 增加空值保护与二级缓存命中率优化 | `AnalysisController`, `EhcacheUtil` |
| **12** | **存储安全** | 七牛云上传 Token 存在前缀路径逃逸风险 | 强行将 `saveKey` 绑定在广告主特定私有目录 `creatives/{advId}/...` | `QiniuController` |
| **13** | **状态一致性** | 物料审核状态更新未及时同步至 RTB 检索引擎 | 审核通过/拒绝后自动触发拦截器向 RTB 引擎推送变更物料 | `MaterialAuditController`, `AuditServiceImpl` |
| **14** | **事务与性能** | 只读查询占用读写事务连接，降低并发吞吐量 | 为所有仅查询接口添加 `@Transactional(readOnly = true)` | `OpenRtbBiddingServiceImpl`, `CampaignServiceImpl` |
| **15** | **异常安全** | 系统未捕获异常泄露底层 SQL 与框架调用堆栈 | 在 `ExceptionController` 统一全局拦截并返回脱敏的 JSON 提示 | `ExceptionController` |
| **16** | **账号安全** | 密码散列校验与登录上下文安全强化 | 加固登录参数校验分组与 Session 鉴权会话隔离 | `LoginController`, `LoginInterceptor` |
| **17** | **接口工程化** | 接口缺乏统一文档规范，对接联调成本高 | 深度集成 Swagger 2.9.2，全量控制器注解标注与 OpenAPI 规范生成 | `SwaggerConfig`, 核心 Controller 全集 |
| **18** | **质量体系** | 缺乏端到端自动化测试验证体系 | 建立涵盖 16 项关键场景的端到端集成测试套件 | `DspComprehensiveE2ETest`, `OpenRtbBiddingE2ETest` |
