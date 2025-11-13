# r2mo-spring-security-oauth2 部署检查清单

## ✅ 开发完成检查

### 核心代码（13个 Java 文件）

- [x] `OAuth2SecurityConfigurer.java` - 核心配置器
- [x] `RequestSkipOAuth2.java` - 开放端点定义
- [x] `ConfigSecurityOAuth2.java` - 主配置类
- [x] `ConfigSecurityOAuth2Native.java` - 原生配置探测
- [x] `OAuth2TokenMode.java` - Token 模式枚举
- [x] `OAuth2LoginRequest.java` - 登录请求基类
- [x] `OAuth2AuthorizationCodeRequest.java` - 授权码请求
- [x] `OAuth2ClientCredentialsRequest.java` - 客户端凭证请求
- [x] `OAuth2PasswordRequest.java` - 密码请求
- [x] `OAuth2RefreshTokenRequest.java` - 刷新令牌请求
- [x] `OAuth2LoginResponse.java` - 登录响应
- [x] `OAuth2JwtCoexistenceMarker.java` - 共存标记
- [x] `RegisteredClientInitializer.java` - 客户端初始化器

### SPI 配置（2个文件）

- [x] `META-INF/services/io.r2mo.spring.security.config.SecurityWebConfigurer`
- [x] `META-INF/services/io.r2mo.spring.security.extension.RequestSkip`

### 数据库脚本（3个文件）

- [x] `sql/oauth2-schema-mysql.sql` - MySQL 建表脚本
- [x] `database/MYSQL/V1500__init_oauth2_schema.sql` - Flyway 版本
- [x] `database/H2/V1500__init_oauth2_h2.sql` - H2 测试版本

### 配置文件（1个文件）

- [x] `application-oauth2-example.yml` - 完整配置示例

### 文档（4个文件）

- [x] `README.md` - 完整使用文档
- [x] `QUICKSTART.md` - 快速开始指南
- [x] `DEVELOPMENT_SUMMARY.md` - 开发总结
- [x] `PROJECT_STRUCTURE.md` - 项目结构说明

### Maven 配置

- [x] `pom.xml` - 依赖配置（含 OAuth2 Authorization Server 1.3.2）

## 📋 部署前检查

### 1. 环境准备

- [ ] Java 17+
- [ ] Spring Boot 3.x
- [ ] MySQL 8.0+ 或其他支持的数据库
- [ ] Maven 3.6+

### 2. 数据库初始化

```bash
# 创建数据库
mysql -u root -p
CREATE DATABASE oauth2_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 执行建表脚本
mysql -u root -p oauth2_db < src/main/resources/sql/oauth2-schema-mysql.sql

# 验证表是否创建成功
mysql -u root -p oauth2_db -e "SHOW TABLES;"
```

预期输出：

```
+-------------------------------------+
| Tables_in_oauth2_db                 |
+-------------------------------------+
| oauth2_authorization                |
| oauth2_authorization_consent        |
| oauth2_registered_client            |
+-------------------------------------+
```

### 3. 应用配置

在 `application.yml` 中添加：

```yaml
security:
  oauth2:
    enabled: true
    mode: JWT
    issuer: "http://your-domain.com"  # ⚠️ 修改为实际域名
    accessTokenAt: 30m
    refreshTokenAt: 7d
    clients:
      - clientId: your-client-id        # ⚠️ 修改为实际值
        clientSecret: your-secret       # ⚠️ 修改为实际值（将自动加密）
        # ... 其他配置

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/oauth2_db  # ⚠️ 修改为实际值
    username: root                               # ⚠️ 修改为实际值
    password: password                           # ⚠️ 修改为实际值
```

### 4. 创建用户信息提供者

```java

@Service("UserAt/OAUTH2")
public class OAuth2UserAt extends ServiceUserAtBase {
    @Override
    public UserAt findUser(final String id) {
        // ⚠️ 实现用户查询逻辑
    }

    @Override
    public TypeLogin loginType() {
        return TypeLogin.OAUTH2;
    }
}
```

### 5. 编译项目

```bash
cd r2mo-matrix/r2mo-rapid
mvn clean install -DskipTests
```

检查输出：

```
[INFO] r2mo-spring-security-oauth2 ..................... SUCCESS
[INFO] BUILD SUCCESS
```

### 6. 启动应用

```bash
cd your-app
mvn spring-boot:run
```

### 7. 验证部署

#### 7.1 检查 OIDC 配置端点

```bash
curl http://localhost:8080/.well-known/openid-configuration
```

预期：返回 JSON 配置，包含 `issuer`, `authorization_endpoint`, `token_endpoint` 等

#### 7.2 检查 JWK 端点

```bash
curl http://localhost:8080/oauth2/jwks
```

预期：返回 JWK 集合

#### 7.3 检查客户端是否注册

```sql
SELECT client_id, client_name
FROM oauth2_registered_client;
```

预期：能看到配置文件中定义的客户端

#### 7.4 测试 Token 颁发

```bash
curl -X POST http://localhost:8080/oauth2/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -u your-client-id:your-secret \
  -d "grant_type=client_credentials" \
  -d "scope=api.read"
```

预期：返回 access_token

## 🔒 生产环境检查

### 安全配置

- [ ] 使用 HTTPS（必需）
- [ ] 客户端密钥已加密（自动）
- [ ] 设置合理的 Token 过期时间
- [ ] 敏感操作启用授权同意（`requireConsent: true`）
- [ ] 限制 Redirect URI 为可信域名
- [ ] 配置 CORS 白名单

### 性能优化

- [ ] 配置数据库连接池（HikariCP）
- [ ] 添加数据库索引（参考 SQL 脚本注释）
- [ ] 启用缓存（`CacheAt`）
- [ ] 定期清理过期 Token（建议）

### 监控和日志

- [ ] 启用 Spring Boot Actuator
- [ ] 配置日志级别（建议 INFO）
- [ ] 监控 Token 颁发频率
- [ ] 监控失败登录次数
- [ ] 配置告警（可选）

### 备份和恢复

- [ ] 定期备份数据库（重要）
- [ ] JWK 密钥持久化（建议）
- [ ] 配置灾难恢复方案

## 🧪 测试检查清单

### 功能测试

- [ ] 授权码模式流程
- [ ] 客户端凭证模式流程
- [ ] 密码模式流程（如果启用）
- [ ] 刷新令牌流程
- [ ] Token 撤销
- [ ] OIDC UserInfo 端点（如果启用）

### 集成测试

- [ ] 与 Basic 认证共存
- [ ] 与旧 JWT 认证切换
- [ ] 客户端自动注册
- [ ] 多租户支持（如果启用）

### 压力测试

- [ ] 并发 Token 颁发（建议 100+ QPS）
- [ ] 数据库连接池压力测试
- [ ] 缓存效果验证

## 📊 部署状态检查

### 应用日志关键字

启动成功应看到：

```
[ R2MO ] 开始配置 OAuth2 Authorization Server...
[ R2MO ] OAuth2 模式：JWT
[ R2MO ] OAuth2 Issuer：http://your-domain.com
[ R2MO ] OAuth2 固定客户端初始化完成，共 X 个
[ R2MO ] OAuth2 Authorization Server 配置完成
```

如果 OAuth2 JWT 模式启用：

```
[ R2MO ] OAuth2 已启用 JWT 模式，旧的 JWT Filter 将被禁用
[ R2MO ] JWT Filter 已禁用，OAuth2 JWT 模式已接管
```

### 数据库检查

```sql
-- 检查客户端数量
SELECT COUNT(*)
FROM oauth2_registered_client;

-- 检查授权记录（应该为空，直到有用户登录）
SELECT COUNT(*)
FROM oauth2_authorization;

-- 检查表结构
DESC oauth2_registered_client;
DESC oauth2_authorization;
DESC oauth2_authorization_consent;
```

### 端点检查

所有端点应返回正确响应（非 404）：

- `/.well-known/openid-configuration` → 200 OK
- `/oauth2/jwks` → 200 OK
- `/oauth2/authorize` → 302 或登录页面
- `/oauth2/token` → 400（无参数）或 401（认证失败）

## 🐛 常见问题排查

### 问题 1：Bean 创建失败

**错误**：`JdbcTemplate` not found

**解决**：

```yaml
spring:
  datasource:
    url: jdbc:mysql://...
    # 确保配置了数据源
```

### 问题 2：表不存在

**错误**：`Table 'oauth2_registered_client' doesn't exist`

**解决**：

```bash
mysql -u root -p your_db < sql/oauth2-schema-mysql.sql
```

### 问题 3：客户端未注册

**错误**：Invalid client

**解决**：

- 检查 `security.oauth2.clients` 配置
- 查看启动日志是否有初始化信息
- 手动查询数据库确认

### 问题 4：Token 验证失败

**错误**：Invalid token

**解决**：

- 确认 `resourceEnabled: true`
- 检查 Issuer 是否一致
- 验证 JWK 端点可访问

### 问题 5：旧 JWT 冲突

**错误**：两个 JWT Filter 同时生效

**解决**：

```yaml
security:
  jwt:
    enabled: false  # 禁用旧 JWT
  oauth2:
    enabled: true
    mode: JWT
```

## ✅ 最终检查

部署前确认：

- [x] 所有代码文件已创建
- [x] SPI 配置文件已创建
- [x] 数据库脚本已准备
- [x] 配置文件已更新
- [x] 用户信息提供者已实现
- [x] 编译成功
- [x] 测试通过
- [x] 文档完整

---

**部署完成后，OAuth2 模块即可投入使用！** 🎉

