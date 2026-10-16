# DSP 系统部署与运维指南

## 1. 基础环境要求

| 组件 | 最低版本要求 | 推荐配置 | 作用说明 |
| :--- | :--- | :--- | :--- |
| **JDK** | 1.8.0_202+ | OpenJDK 8 / Eclipse Temurin 8 | 核心运行环境 |
| **Maven** | 3.6.0+ | 3.8.8+ | 项目编译构建 |
| **MySQL** | 5.7+ / 8.0+ | 8.0 InnoDB (utf8mb4) | 业务与账单持久化 |
| **Redis** | 3.2+ | 6.0+ / 7.0+ | 胜出回执幂等锁、预算熔断、热点缓存 |
| **RabbitMQ** | 3.6+ | 3.9+ | 异步上报记账、削峰解耦 |
| **Tomcat / Servlet 容器** | 8.0+ | 8.5+ | Web 容器 |

---

## 2. 核心配置文件与环境变量

系统配置文件均放置于 `src/main/resources/config/`：
* **`system.properties`**: 基础系统配置、Redis 主机、消息队列与第三方接口凭证。
* **`spring-db-config.xml`**: MySQL 数据源配置，支持环境变量覆盖（`LOCAL_JDBC_HOST`, `DB_USER`, `DB_PASSWORD`）。
* **`spring-profile.xml`**: 多环境 Profile（开发 `dev`、测试 `test`、生产 `production`）切换。
* **`spring-mvc.xml`**: Spring MVC 调度、静态资源映射、Swagger 资源放行与登录拦截器。

---

## 3. 本地构建与打包

### 3.1 编译与打包 WAR
```bash
# 执行完整单元测试与打包
mvn clean package -Dmaven.test.skip=false

# 若仅快速打包生成 war 文件
mvn clean package -DskipTests
```
构建产物将输出在：`target/dsp.war`。

### 3.2 执行端到端（E2E）集成测试
项目包含端到端完整测试套件，可直接通过 Maven 执行：
```bash
mvn test -Dtest=DspComprehensiveE2ETest,OpenRtbBiddingE2ETest \
  -DLOCAL_JDBC_HOST=localhost \
  -DREDIS_HOST=localhost \
  -DRABBITMQ_HOST=localhost
```

---

## 4. Docker 容器化运行指南

### 4.1 启动依赖中间件容器
```bash
# 启动 MySQL 8.0
docker run -d --name dsp-mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=dsp mysql:8.0

# 启动 Redis
docker run -d --name dsp-redis -p 6379:6379 redis:alpine

# 启动 RabbitMQ
docker run -d --name dsp-rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

### 4.2 使用 Docker 构建与运行 WAR 包
```bash
# 在 Maven 容器中编译打包
docker run --rm \
  -v "$(pwd)":/app \
  -v ~/.m2:/root/.m2 \
  -w /app \
  --add-host=host.docker.internal:host-gateway \
  maven:3.8.8-eclipse-temurin-8 mvn clean package -DskipTests

# 将 WAR 挂载至 Tomcat 8 镜像运行
docker run -d --name dsp-app \
  -p 8080:8080 \
  -v "$(pwd)/target/dsp.war:/usr/local/tomcat/webapps/ROOT.war" \
  --add-host=host.docker.internal:host-gateway \
  tomcat:8.5-jdk8-openjdk
```

---

## 5. 常见运维排查

1. **Swagger UI 无法访问**：
   - 检查 `web.xml` 与 `spring-mvc.xml` 是否放行 `/swagger-ui.html` 与 `/webjars/**`。
   - 访问入口：`http://<host>:<port>/swagger-ui.html`。
2. **OpenRTB 竞价返回 204**：
   - 检查广告主资质是否为审核通过（`Review.eligible`）；
   - 检查广告活动是否在有效排期之内且有今日剩余预算；
   - 检查请求尺寸与物料尺寸是否完全一致。
3. **Redis 胜出防重锁超时设置**：
   - 默认防重锁保留时间为 24 小时，可按需要在 `OpenRtbBiddingServiceImpl` 中调配。
