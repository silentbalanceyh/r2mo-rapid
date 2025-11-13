# OAuth2 快速开始指南

## 1️⃣ 添加依赖

在应用的 `pom.xml` 中添加：

```xml

<dependency>
    <groupId>io.zerows</groupId>
    <artifactId>r2mo-spring-security-oauth2</artifactId>
    <version>1.0.29</version>
</dependency>
```

## 2️⃣ 初始化数据库

```bash
cd r2mo-spring-security-oauth2/src/main/resources/sql
mysql -u root -p your_database < oauth2-schema-mysql.sql
```

## 3️⃣ 配置 application.yml

```yaml
security:
  # 如果启用 OAuth2 JWT，建议禁用旧的 JWT
  jwt:
    enabled: false

  # Basic 认证继续工作
  basic:
    enabled: true

  # OAuth2 配置
  oauth2:
    enabled: true
    mode: JWT  # 或 OIDC
    issuer: "http://localhost:8080"

    # Token 配置
    accessTokenAt: 30m
    refreshTokenAt: 7d

    # 固定客户端
    clients:
      - clientId: demo-client
        clientSecret: demo-secret
        clientName: Demo Client
        authMethods:
          - client_secret_basic
        grantTypes:
          - authorization_code
          - refresh_token
        redirectUris:
          - http://localhost:3000/callback
        scopes:
          - openid
          - profile
        requireConsent: true

# 数据源配置（必需）
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/oauth2_db
    username: root
    password: password
```

## 4️⃣ 创建用户信息提供者

```java
package com.yourapp.auth;

import io.r2mo.jaas.element.MSUser;
import io.r2mo.jaas.enums.TypeLogin;
import io.r2mo.jaas.session.UserAt;
import io.r2mo.spring.security.auth.ServiceUserAtBase;
import org.springframework.stereotype.Service;

@Service("UserAt/OAUTH2")
public class OAuth2UserAt extends ServiceUserAtBase {

    @Override
    public UserAt findUser(final String id) {
        // 从数据库加载用户
        MSUser user = userRepository.findById(id);
        return this.ofUserAt(user);
    }

    @Override
    public TypeLogin loginType() {
        return TypeLogin.OAUTH2;
    }
}
```

## 5️⃣ 启动应用

```bash
mvn spring-boot:run
```

## 6️⃣ 测试

### 查看 OIDC 配置

```bash
curl http://localhost:8080/.well-known/openid-configuration
```

### 测试授权码流程

**步骤 1：浏览器访问授权端点**

```
http://localhost:8080/oauth2/authorize?response_type=code&client_id=demo-client&redirect_uri=http://localhost:3000/callback&scope=openid%20profile&state=xyz
```

**步骤 2：用户登录后获取授权码**

浏览器会重定向到：

```
http://localhost:3000/callback?code=AUTHORIZATION_CODE&state=xyz
```

**步骤 3：用授权码换取 Token**

```bash
curl -X POST http://localhost:8080/oauth2/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -u demo-client:demo-secret \
  -d "grant_type=authorization_code" \
  -d "code=AUTHORIZATION_CODE" \
  -d "redirect_uri=http://localhost:3000/callback"
```

响应：

```json
{
    "access_token": "eyJhbGci...",
    "token_type": "Bearer",
    "expires_in": 1800,
    "refresh_token": "FMJ9qgF...",
    "scope": "openid profile"
}
```

### 测试客户端凭证模式

```bash
curl -X POST http://localhost:8080/oauth2/token \
  -H "Content-Type": application/x-www-form-urlencoded" \
  -u demo-client:demo-secret \
  -d "grant_type=client_credentials" \
  -d "scope=api.read"
```

### 使用 Access Token 访问资源

```bash
curl -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  http://localhost:8080/api/resource
```

### 刷新 Token

```bash
curl -X POST http://localhost:8080/oauth2/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -u demo-client:demo-secret \
  -d "grant_type=refresh_token" \
  -d "refresh_token=YOUR_REFRESH_TOKEN"
```

## 7️⃣ 验证配置

访问以下端点确认配置正确：

- `/.well-known/openid-configuration` - OIDC 发现端点
- `/oauth2/jwks` - JWK 公钥端点

## 🔧 常见问题

### Q: 客户端未找到？

A: 检查数据库中 `oauth2_registered_client` 表是否有数据，应用启动时会自动注册固定客户端。

### Q: Token 验证失败？

A: 确认 Resource Server 已启用（`resourceEnabled: true`）

### Q: 旧 JWT 和 OAuth2 冲突？

A: 设置 `security.jwt.enabled=false` 禁用旧 JWT

### Q: 数据库连接失败？

A: 检查 `spring.datasource` 配置是否正确

## 📚 更多资源

- [完整文档](../README.md)
- [开发总结](DEVELOPMENT_SUMMARY.md)
- [SQL 脚本](../src/main/resources/sql/oauth2-schema-mysql.sql)
- [配置示例](../src/main/resources/application-oauth2-example.yml)

