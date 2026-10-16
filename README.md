# DSP 程序化广告投放与实时竞价系统

[![Java Version](https://img.shields.io/badge/Java-1.8-blue.svg)](https://www.oracle.com/java/)
[![Spring Framework](https://img.shields.io/badge/Spring-4.0.9.RELEASE-green.svg)](https://spring.io/)
[![OpenRTB](https://img.shields.io/badge/OpenRTB-2.5-orange.svg)](https://www.iab.com/guidelines/openrtb/)
[![Swagger](https://img.shields.io/badge/Swagger-2.9.2-brightgreen.svg)](https://swagger.io/)

企业级程序化广告需求方平台（Demand-Side Platform, DSP），提供全链路广告主自主投放、多维定向广告活动管理、创意物料审核，以及原生支持 **OpenRTB 2.5** 协议和 **Prebid Server**（Header Bidding）毫秒级实时竞价与胜出结算。

---

## 核心特性

* **实时竞价引擎 (OpenRTB 2.5 / Prebid Server)**：毫秒级多维度条件过滤（排期、定向、尺寸、底价），支持 `${AUCTION_PRICE}` 第二价格动态宏替换与 HTTP 204 快速放弃出价机制。
* **高并发预算熔断与防重核销**：基于 Redis 原子操作实现实时日预算熔断（防止并发超卖），结合分布式排他锁实现胜出通知幂等防重计费。
* **多层级权限与安全隔离**：全系统严格防垂直/水平越权控制，强制执行广告主资质认证拦截（未过审禁止投放），敏感数据脱敏展示。
* **标准 API 文档化 (Swagger / OpenAPI)**：集成 Springfox Swagger 2.9.2，全量核心控制器添加规范化注解与在线交互调试。

---

## 快速上手

### 1. 环境准备
* JDK 1.8+
* Maven 3.8+
* MySQL 5.7+ / 8.0+
* Redis 3.2+
* RabbitMQ 3.6+

### 2. 构建与运行测试
```bash
# 编译并执行完整端到端测试套件（含 OpenRTB 真实竞价链路与并发防超卖验证）
mvn test -Dtest=DspComprehensiveE2ETest,OpenRtbBiddingE2ETest

# 打包 WAR
mvn clean package -DskipTests
```
产物将输出在 `target/dsp.war`。

### 3. 在线 API 文档
启动服务后访问：
* **Swagger UI 交互页面**: `http://localhost:8080/swagger-ui.html`
* **OpenAPI 规范源文件**: `http://localhost:8080/v2/api-docs`

---

## 详细技术文档与章节导航

为保持主文档简明扼要，系统各项详细设计已拆分至专属章节文档：

| 章节名称 | 核心内容说明 | 文档跳转 |
| :--- | :--- | :---: |
| **系统架构与模块设计** | 整体微服务与拓扑、Prebid Server 对接流、模块划分、并发安全策略 | [查看架构设计](docs/architecture.md) |
| **OpenRTB 2.5 接入规范** | BidRequest/Response 协议规范、`${AUCTION_PRICE}` 宏替换、胜出回执核销 | [查看协议规范](docs/openrtb-spec.md) |
| **部署与运维指南** | 中间件依赖、Profile 环境切换、Docker 容器化部署与故障排查 | [查看部署指南](docs/deployment.md) |
| **核心缺陷修复与加固清单** | 梳理 18 项核心缺陷修复（含防超卖、防重核销、越权加固与兼容性） | [查看修复清单](docs/bugfixes.md) |
| **OpenAPI / Swagger 规范定义** | 标准 OpenAPI 3.0 / YAML 格式规范文件，支持导入 Apifox / Postman | [查看 API 规范](docs/api/swagger-openrtb-api.yaml) |

---

## 许可说明
本项目遵循项目内部授权许可协议。