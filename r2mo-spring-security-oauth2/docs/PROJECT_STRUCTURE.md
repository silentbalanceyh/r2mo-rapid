# r2mo-spring-security-oauth2 项目结构

```
r2mo-spring-security-oauth2/
├── pom.xml                                     # Maven 配置（包含 OAuth2 依赖）
├── README.md                                   # 完整文档
├── QUICKSTART.md                               # 快速开始指南
├── DEVELOPMENT_SUMMARY.md                      # 开发完成总结
└── src/
    └── main/
        ├── java/io/r2mo/spring/security/oauth2/
        │   ├── OAuth2SecurityConfigurer.java              # 核心配置器（SPI）
        │   ├── RequestSkipOAuth2.java                     # 开放端点定义（SPI）
        │   │
        │   ├── auth/                                      # 认证相关
        │   │   ├── OAuth2LoginRequest.java               # OAuth2 登录请求基类
        │   │   ├── OAuth2AuthorizationCodeRequest.java   # 授权码模式请求
        │   │   ├── OAuth2ClientCredentialsRequest.java   # 客户端凭证模式请求
        │   │   ├── OAuth2PasswordRequest.java            # 密码模式请求
        │   │   ├── OAuth2RefreshTokenRequest.java        # 刷新令牌请求
        │   │   └── OAuth2LoginResponse.java              # OAuth2 登录响应
        │   │
        │   ├── config/                                    # 配置类
        │   │   ├── ConfigSecurityOAuth2.java             # 主配置（security.oauth2.*）
        │   │   ├── ConfigSecurityOAuth2Native.java       # 原生配置探测器
        │   │   └── OAuth2TokenMode.java                  # Token 模式枚举
        │   │
        │   ├── filter/                                    # 过滤器
        │   │   └── OAuth2JwtCoexistenceMarker.java       # OAuth2 与 JWT 共存标记
        │   │
        │   └── repository/                                # 仓库
        │       └── RegisteredClientInitializer.java      # 客户端初始化器
        │
        └── resources/
            ├── application-oauth2-example.yml            # 配置示例
            │
            ├── sql/                                      # SQL 脚本
            │   └── oauth2-schema-mysql.sql               # MySQL 表结构
            │
            └── META-INF/services/                        # SPI 配置
                ├── io.r2mo.spring.security.config.SecurityWebConfigurer
                └── io.r2mo.spring.security.extension.RequestSkip
```

## 核心组件说明

### 1. 配置层（config/）

- **ConfigSecurityOAuth2**：主配置类，绑定 `security.oauth2.*`
    - 智能判断方法：`isOn()`, `isJwt()`, `isOidc()`
    - 自动复用 `security.jwt.*` 时间配置
    - 固定客户端配置

- **ConfigSecurityOAuth2Native**：原生配置探测
    - 如果存在 `spring.security.oauth2.*` 配置，插件自动让位

- **OAuth2TokenMode**：Token 模式枚举（JWT/OIDC）

### 2. 认证层（auth/）

- **OAuth2LoginRequest**：登录请求基类
    - 通用字段：`clientId`, `clientSecret`, `scope`
    - 抽象方法：`getGrantType()`

- **4 种授权模式请求类**：
    - `OAuth2AuthorizationCodeRequest` - 授权码模式
    - `OAuth2ClientCredentialsRequest` - 客户端凭证模式
    - `OAuth2PasswordRequest` - 密码模式
    - `OAuth2RefreshTokenRequest` - 刷新令牌模式

- **OAuth2LoginResponse**：统一响应格式
    - 标准 OAuth2 Token 响应
    - 支持 OIDC `id_token`

### 3. 过滤器层（filter/）

- **OAuth2JwtCoexistenceMarker**：共存标记 Bean
    - 当 OAuth2 启用 JWT 模式时存在
    - 旧的 JwtAuthenticateFilter 检测此 Bean 并自动跳过

### 4. 仓库层（repository/）

- **RegisteredClientInitializer**：客户端初始化
    - 启动时自动注册固定客户端
    - 支持客户端密钥自动加密
    - 支持多种认证方式和授权类型

### 5. SPI 集成

- **OAuth2SecurityConfigurer** → `SecurityWebConfigurer`
    - 在 Spring Security 初始化时自动加载
    - 配置 Authorization Server
    - 配置 Resource Server（可选）

- **RequestSkipOAuth2** → `RequestSkip`
    - 自动注册 OAuth2 开放端点
    - 与 JWT/Basic 的开放端点合并

## 数据库表结构

### oauth2_registered_client

- 客户端注册信息
- 支持多租户（`tenant_id`）
- 支持扩展字段（`ext`）

### oauth2_authorization

- 授权信息和 Token 存储
- 支持多种 Token 类型
- 支持多租户和扩展字段

### oauth2_authorization_consent

- 用户授权同意记录
- 支持多租户

## 集成方式

### 应用层集成（参考）

```
app-spring-auth/
└── src/main/java/io/zerows/apps/spring/auth/
    ├── controller/authorize/
    │   └── AuthOAuth2Controller.java          # OAuth2 登录 Controller
    └── controller/provider/
        └── OAuth2UserAt.java                  # OAuth2 用户信息提供者
```

### Bean 命名规范

- **用户信息提供者**：`UserAt/OAUTH2`
- **前置认证提供者**：`PreAuth/OAUTH2`（可选）

## 配置优先级

1. **原生配置优先**：
    - 如果存在 `spring.security.oauth2.*`，插件让位

2. **时间配置复用**：
    - `security.oauth2.accessTokenAt` → `security.jwt.expiredAt`
    - `security.oauth2.refreshTokenAt` → `security.jwt.refreshAt`

3. **Issuer 配置优先**：
    - `spring.security.oauth2.authorizationserver.issuer` → `security.oauth2.issuer`

## 启动流程

1. Spring Boot 启动
2. 加载配置 `ConfigSecurityOAuth2`
3. 检查 `isOn()` 是否启用
4. 如果启用：
    - 创建 JWK 密钥源
    - 初始化固定客户端到数据库
    - 配置 Authorization Server
    - 配置 Resource Server（如果启用）
    - 注册 OAuth2 开放端点
5. 如果 OAuth2 JWT 模式：
    - 创建 `OAuth2JwtCoexistenceMarker` Bean
    - 旧 JWT Filter 检测到后自动跳过

## 端点映射

### Authorization Server 端点（Spring 自动提供）

- `GET  /.well-known/openid-configuration` - OIDC 发现
- `GET  /oauth2/jwks` - JWK 公钥
- `GET  /oauth2/authorize` - 授权端点
- `POST /oauth2/token` - Token 端点
- `POST /oauth2/introspect` - Token 内省
- `POST /oauth2/revoke` - Token 撤销

### 自定义端点（应用层可选）

- `POST /oauth2/login/authorization_code` - 授权码登录
- `POST /oauth2/login/client_credentials` - 客户端凭证登录
- `POST /oauth2/login/password` - 密码登录
- `POST /oauth2/login/refresh_token` - 刷新令牌

## 扩展点

### 1. 自定义 Token Customizer

```java

@Bean
public OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer() {
    return (context) -> {
        context.getClaims().claim("tenant_id", "xxx");
    };
}
```

### 2. 自定义 UserInfo 端点

```java

@Bean
public OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
    // 自定义实现
}
```

### 3. 缓存集成

使用 `CacheAt` 缓存：

- 授权码（60秒）
- Access Token
- Refresh Token
- 客户端信息

## 注意事项

1. **依赖版本**：Spring Authorization Server 1.3.2
2. **数据库必需**：必须配置 JdbcTemplate
3. **HTTPS 强烈建议**：生产环境必须使用
4. **密钥管理**：JWK 密钥当前为内存生成，生产环境建议持久化
5. **多租户支持**：表结构已支持，需在查询时添加过滤

