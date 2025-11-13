# r2mo-spring-security-oauth2

OAuth2 Authorization Server 插件模块，基于 Spring Authorization Server 实现。

## 功能特性

### 1. 完整的 OAuth2 支持

- ✅ 授权码模式（Authorization Code）
- ✅ 客户端凭证模式（Client Credentials）
- ✅ 密码模式（Resource Owner Password，不推荐）
- ✅ 刷新令牌（Refresh Token）
- ✅ OIDC 支持（可选）

### 2. 灵活的 Token 模式

- **JWT 模式**：使用 JWT 作为 Access Token
- **OIDC 模式**：在 JWT 基础上支持 ID Token 和 UserInfo 端点

### 3. 数据持久化

- 使用 JDBC 存储客户端信息
- 使用 JDBC 存储授权信息
- 支持多租户（tenant_id 字段）
- 支持扩展字段（ext CLOB 字段）

### 4. 插件化设计

- SPI 自动发现和注册
- 与现有 Basic/JWT 认证共存
- OAuth2 JWT 模式可自动禁用旧 JWT Filter

### 5. 客户端管理

- 支持配置文件定义固定客户端
- 启动时自动注册到数据库
- 支持动态客户端注册（通过数据库）

## 快速开始

### 1. 添加依赖

在项目的 `pom.xml` 中添加：

```xml

<dependency>
    <groupId>io.zerows</groupId>
    <artifactId>r2mo-spring-security-oauth2</artifactId>
</dependency>
```

### 2. 初始化数据库

执行 SQL 脚本创建必要的表：

```bash
mysql -u root -p your_database < src/main/resources/sql/oauth2-schema-mysql.sql
```

主要表：

- `oauth2_registered_client` - 客户端注册表
- `oauth2_authorization` - 授权和 Token 存储表
- `oauth2_authorization_consent` - 授权同意表

### 3. 配置

在 `application.yml` 中添加配置：

```yaml
security:
  oauth2:
    enabled: true           # 启用 OAuth2
    mode: JWT              # JWT 或 OIDC
    issuer: "https://auth.example.com"

    # Token 配置
    accessTokenAt: 30m
    refreshTokenAt: 7d
    reuseRefreshToken: true

    # Resource Server
    resourceEnabled: true

    # 固定客户端
    clients:
      - clientId: demo-client
        clientSecret: demo-secret
        clientName: Demo Application
        authMethods:
          - client_secret_basic
        grantTypes:
          - authorization_code
          - refresh_token
        redirectUris:
          - http://localhost:8080/login/oauth2/code/demo
        scopes:
          - openid
          - profile
          - api.read
        requireConsent: true
```

### 4. 创建用户信息提供者

```java

@Service("UserAt/OAUTH2")
public class OAuth2UserAt extends ServiceUserAtBase {

    @Autowired
    private UserService service;

    @Override
    public UserAt findUser(final String id) {
        final MSUser user = this.service.findById(id);
        return this.ofUserAt(user);
    }

    @Override
    public TypeLogin loginType() {
        return TypeLogin.OAUTH2;
    }
}
```

## OAuth2 端点

### Authorization Server 端点

所有端点均由 Spring Authorization Server 自动提供：

| 端点                     | 路径                                  | 说明          |
|------------------------|-------------------------------------|-------------|
| Authorization Endpoint | `/oauth2/authorize`                 | 授权码流程的授权端点  |
| Token Endpoint         | `/oauth2/token`                     | Token 颁发端点  |
| JWK Set Endpoint       | `/oauth2/jwks`                      | JWK 公钥端点    |
| Token Introspection    | `/oauth2/introspect`                | Token 内省端点  |
| Token Revocation       | `/oauth2/revoke`                    | Token 撤销端点  |
| OIDC Discovery         | `/.well-known/openid-configuration` | OIDC 发现端点   |
| OIDC UserInfo          | `/userinfo`                         | OIDC 用户信息端点 |

### 自定义登录端点（可选）

如果需要自定义登录逻辑，可以创建 Controller：

```java

@RestController
public class AuthOAuth2Controller {

    @Autowired
    private AuthService authService;

    @PostMapping("/oauth2/login/password")
    public OAuth2LoginResponse loginPassword(@RequestBody JObject requestJ) {
        final OAuth2PasswordRequest request = new OAuth2PasswordRequest(requestJ);
        final UserAt userAt = this.authService.login(request);
        return new OAuth2LoginResponse(userAt);
    }
}
```

## 授权流程示例

### 1. 授权码模式

**步骤 1：获取授权码**

```
GET /oauth2/authorize?
    response_type=code&
    client_id=demo-client&
    redirect_uri=http://localhost:8080/callback&
    scope=openid profile&
    state=xyz
```

**步骤 2：用户登录并授权**

（浏览器重定向到登录页面，用户输入凭证）

**步骤 3：获取 Access Token**

```bash
curl -X POST http://localhost:8080/oauth2/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=authorization_code" \
  -d "code=授权码" \
  -d "redirect_uri=http://localhost:8080/callback" \
  -d "client_id=demo-client" \
  -d "client_secret=demo-secret"
```

**响应：**

```json
{
    "access_token": "eyJhbGci...",
    "token_type": "Bearer",
    "expires_in": 1800,
    "refresh_token": "FMJ9qgF...",
    "scope": "openid profile"
}
```

### 2. 客户端凭证模式

```bash
curl -X POST http://localhost:8080/oauth2/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=client_credentials" \
  -d "client_id=service-client" \
  -d "client_secret=service-secret" \
  -d "scope=api.read api.write"
```

### 3. 刷新令牌

```bash
curl -X POST http://localhost:8080/oauth2/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=refresh_token" \
  -d "refresh_token=FMJ9qgF..." \
  -d "client_id=demo-client" \
  -d "client_secret=demo-secret"
```

## 与旧 JWT 的共存

当 `security.oauth2.enabled=true` 且 `mode=JWT` 时：

1. OAuth2 的 JWT 验证会接管 Resource Server
2. 旧的 `JwtAuthenticateFilter` 会自动禁用
3. Basic 认证不受影响，继续工作

**配置示例：**

```yaml
security:
  basic:
    enabled: true    # Basic 继续工作

  jwt:
    enabled: false   # 建议禁用旧 JWT

  oauth2:
    enabled: true
    mode: JWT        # OAuth2 接管 JWT 验证
```

## 多租户支持

数据库表已包含 `tenant_id` 字段，可以通过以下方式启用多租户：

1. 在客户端配置中添加租户信息
2. 在查询时添加租户过滤条件
3. 在 Token 中包含租户信息

## 扩展字段

所有主要表都包含 `ext` 字段（LONGTEXT），可以存储 JSON 格式的扩展信息：

```json
{
    "customField1": "value1",
    "customField2": "value2"
}
```

## 缓存建议

以下数据建议使用 `CacheAt` 缓存：

1. **授权码**：60 秒过期
2. **Access Token**：根据配置过期
3. **Refresh Token**：根据配置过期
4. **客户端信息**：长期缓存

## 安全建议

1. **客户端密钥**：使用 BCrypt 加密存储
2. **HTTPS**：生产环境必须使用 HTTPS
3. **密码模式**：仅在完全可信的客户端中使用
4. **授权同意**：敏感操作启用 `requireConsent`
5. **Scope 设计**：细粒度的权限控制

## 故障排查

### 1. Bean 创建失败

**问题**：`JdbcTemplate` 未找到

**解决**：确保配置了数据源

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/oauth2_db
    username: root
    password: password
```

### 2. 表不存在

**问题**：`Table 'oauth2_registered_client' doesn't exist`

**解决**：执行 SQL 脚本创建表

### 3. 旧 JWT 与 OAuth2 冲突

**问题**：两个 JWT Filter 同时生效

**解决**：

- 方式 1：禁用旧 JWT（`security.jwt.enabled=false`）
- 方式 2：框架会自动处理（通过 `OAuth2JwtCoexistenceMarker`）

## 参考资料

- [Spring Authorization Server 官方文档](https://docs.spring.io/spring-authorization-server/docs/current/reference/html/)
- [OAuth 2.0 RFC 6749](https://datatracker.ietf.org/doc/html/rfc6749)
- [OIDC Specification](https://openid.net/specs/openid-connect-core-1_0.html)

