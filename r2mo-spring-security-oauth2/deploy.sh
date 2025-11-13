#!/bin/bash
# r2mo-spring-security-oauth2 快速部署脚本

echo "========================================="
echo "  r2mo-spring-security-oauth2 部署向导"
echo "========================================="
echo ""

# 检查 Java 版本
echo "🔍 检查 Java 版本..."
java -version 2>&1 | head -1
echo ""

# 检查 Maven
echo "🔍 检查 Maven..."
mvn -version | head -1
echo ""

# 检查 MySQL
echo "🔍 检查 MySQL..."
mysql --version 2>&1 | head -1
echo ""

echo "========================================="
echo "  步骤 1: 编译项目"
echo "========================================="
read -p "是否编译项目? (y/n): " compile
if [ "$compile" = "y" ]; then
    echo "📦 开始编译..."
    cd ../../.. && mvn clean install -DskipTests -pl r2mo-spring-security-oauth2 -am
    echo "✅ 编译完成"
fi
echo ""

echo "========================================="
echo "  步骤 2: 初始化数据库"
echo "========================================="
read -p "是否初始化数据库? (y/n): " initdb
if [ "$initdb" = "y" ]; then
    read -p "请输入数据库名称: " dbname
    read -p "请输入 MySQL 用户名: " dbuser
    read -sp "请输入 MySQL 密码: " dbpass
    echo ""

    echo "📊 创建数据库..."
    mysql -u"$dbuser" -p"$dbpass" -e "CREATE DATABASE IF NOT EXISTS $dbname CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

    echo "📊 执行建表脚本..."
    mysql -u"$dbuser" -p"$dbpass" "$dbname" < src/main/resources/sql/oauth2-schema-mysql.sql

    echo "✅ 数据库初始化完成"

    echo ""
    echo "验证表是否创建成功:"
    mysql -u"$dbuser" -p"$dbpass" "$dbname" -e "SHOW TABLES;"
fi
echo ""

echo "========================================="
echo "  步骤 3: 生成配置文件"
echo "========================================="
read -p "是否生成配置文件? (y/n): " genconfig
if [ "$genconfig" = "y" ]; then
    cat > application-oauth2-generated.yml << EOF
security:
  # JWT 配置（建议禁用，让 OAuth2 接管）
  jwt:
    enabled: false

  # Basic 认证（可继续使用）
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
    reuseRefreshToken: true
    resourceEnabled: true

    # 客户端配置
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
          - api.read
        requireConsent: true

# 数据源配置
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/$dbname?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: $dbuser
    password: $dbpass
    driver-class-name: com.mysql.cj.jdbc.Driver

  jpa:
    hibernate:
      ddl-auto: none
    show-sql: false
EOF

    echo "✅ 配置文件已生成: application-oauth2-generated.yml"
fi
echo ""

echo "========================================="
echo "  步骤 4: 测试端点"
echo "========================================="
read -p "应用是否已启动? (y/n): " apprunning
if [ "$apprunning" = "y" ]; then
    echo "🧪 测试 OIDC 发现端点..."
    curl -s http://localhost:8080/.well-known/openid-configuration | jq . 2>/dev/null || curl -s http://localhost:8080/.well-known/openid-configuration
    echo ""

    echo "🧪 测试 JWK 端点..."
    curl -s http://localhost:8080/oauth2/jwks | jq . 2>/dev/null || curl -s http://localhost:8080/oauth2/jwks
    echo ""

    echo "🧪 测试 Token 端点..."
    curl -s -X POST http://localhost:8080/oauth2/token \
      -H "Content-Type: application/x-www-form-urlencoded" \
      -u demo-client:demo-secret \
      -d "grant_type=client_credentials" \
      -d "scope=api.read" | jq . 2>/dev/null
fi
echo ""

echo "========================================="
echo "  部署完成！"
echo "========================================="
echo ""
echo "📚 文档位置:"
echo "  - 完整文档: README.md"
echo "  - 快速开始: QUICKSTART.md"
echo "  - 部署清单: DEPLOYMENT_CHECKLIST.md"
echo "  - 项目结构: PROJECT_STRUCTURE.md"
echo "  - 完成报告: COMPLETION_REPORT.md"
echo ""
echo "🔗 重要端点:"
echo "  - OIDC 发现: http://localhost:8080/.well-known/openid-configuration"
echo "  - JWK 公钥: http://localhost:8080/oauth2/jwks"
echo "  - 授权端点: http://localhost:8080/oauth2/authorize"
echo "  - Token 端点: http://localhost:8080/oauth2/token"
echo ""
echo "✅ OAuth2 模块部署完成！"
echo ""

