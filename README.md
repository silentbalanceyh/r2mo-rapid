# R2MO Rapid Development Framework

**R2MO** = R² Meta-Orchestrated / for Rachel Momo

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## 项目简介 / Project Overview

R2MO 是一个支持 **Vert.x / Spring 双技术栈**的快速开发框架。通过统一的抽象接口，开发者可以在 Vert.x 和 Spring Boot 之间无缝切换，充分利用两种技术栈的优势。

R2MO is a rapid development framework supporting **Vert.x / Spring dual technology stack**. Through unified abstract interfaces, developers can seamlessly switch between Vert.x and Spring Boot, leveraging the advantages of both technology stacks.

## 核心特性 / Key Features

- 🚀 **双技术栈支持** - 同时支持 Vert.x 和 Spring Boot
- 🔧 **统一抽象接口** - 相同的代码可以在不同技术栈上运行
- ⚡ **高性能** - 充分利用 Vert.x 的响应式特性和 Spring 的生态系统
- 📦 **模块化设计** - 核心模块与实现模块解耦
- 🎯 **易于使用** - 简洁的 API 设计，快速上手

## 项目结构 / Project Structure

```
r2mo-rapid/
├── r2mo-core/           # 核心接口和抽象 / Core interfaces and abstractions
├── r2mo-vertx/          # Vert.x 实现 / Vert.x implementation
├── r2mo-spring/         # Spring 实现 / Spring implementation
└── r2mo-examples/       # 示例应用 / Example applications
    ├── vertx-example/   # Vert.x 示例 / Vert.x example
    └── spring-example/  # Spring 示例 / Spring example
```

## 快速开始 / Quick Start

### 前置要求 / Prerequisites

- Java 17 或更高版本 / Java 17 or higher
- Maven 3.6+ 

### 构建项目 / Build Project

```bash
mvn clean install
```

### 运行示例 / Run Examples

#### Vert.x 示例 / Vert.x Example

```bash
cd r2mo-examples/vertx-example
mvn clean package
java -jar target/vertx-example-1.0.0-SNAPSHOT.jar
```

服务将在 http://localhost:8080 启动 / Server will start on http://localhost:8080

#### Spring 示例 / Spring Example

```bash
cd r2mo-examples/spring-example
mvn clean package
java -jar target/spring-example-1.0.0-SNAPSHOT.jar
```

服务将在 http://localhost:8081 启动 / Server will start on http://localhost:8081

### 测试 API / Test APIs

```bash
# Hello endpoint
curl "http://localhost:8080/hello?name=R2MO"
# 输出 / Output: Hello, R2MO! (from Vert.x)

# Status endpoint
curl "http://localhost:8080/status"
# 输出 / Output: Vert.x server is running

# Echo endpoint
curl -X POST -d "Hello World" "http://localhost:8080/echo"
# 输出 / Output: Echo: Hello World
```

## 使用示例 / Usage Example

### 使用 Vert.x 实现 / Using Vert.x Implementation

```java
import io.github.silentbalanceyh.r2mo.core.HttpResponse;
import io.github.silentbalanceyh.r2mo.vertx.VertxHttpServer;

public class App {
    public static void main(String[] args) {
        VertxHttpServer server = new VertxHttpServer(8080);
        
        server.addRoute("/hello", request -> {
            String name = request.getQueryParam("name");
            return HttpResponse.ok("Hello, " + name + "!");
        });
        
        server.start();
    }
}
```

### 使用 Spring 实现 / Using Spring Implementation

```java
import io.github.silentbalanceyh.r2mo.core.HttpResponse;
import io.github.silentbalanceyh.r2mo.spring.SpringHttpServer;

public class App {
    public static void main(String[] args) {
        SpringHttpServer server = new SpringHttpServer(8080);
        
        server.addRoute("/hello", request -> {
            String name = request.getQueryParam("name");
            return HttpResponse.ok("Hello, " + name + "!");
        });
        
        server.start();
    }
}
```

## 核心接口 / Core Interfaces

### HttpServer

统一的 HTTP 服务器接口 / Unified HTTP server interface

```java
public interface HttpServer {
    CompletableFuture<Void> start();
    CompletableFuture<Void> stop();
    int getPort();
    boolean isRunning();
}
```

### HttpRequest

HTTP 请求抽象 / HTTP request abstraction

```java
public interface HttpRequest {
    String getMethod();
    String getPath();
    String getHeader(String name);
    Map<String, String> getHeaders();
    String getBody();
    String getQueryParam(String name);
    Map<String, String> getQueryParams();
}
```

### HttpResponse

HTTP 响应抽象 / HTTP response abstraction

```java
public interface HttpResponse {
    int getStatusCode();
    HttpResponse setStatusCode(int statusCode);
    String getHeader(String name);
    HttpResponse setHeader(String name, String value);
    Map<String, String> getHeaders();
    String getBody();
    HttpResponse setBody(String body);
}
```

### RouteHandler

路由处理器接口 / Route handler interface

```java
public interface RouteHandler {
    HttpResponse handle(HttpRequest request);
}
```

## 技术栈比较 / Technology Stack Comparison

| 特性 / Feature | Vert.x | Spring Boot |
|---------------|--------|-------------|
| 编程模型 / Programming Model | 响应式 / Reactive | 命令式/响应式 / Imperative/Reactive |
| 性能 / Performance | 高 / High | 中 / Medium |
| 生态系统 / Ecosystem | 中 / Medium | 丰富 / Rich |
| 学习曲线 / Learning Curve | 陡 / Steep | 平缓 / Gentle |
| 适用场景 / Use Case | 高并发 I/O / High Concurrency I/O | 企业应用 / Enterprise Apps |

## 贡献 / Contributing

欢迎贡献！请随时提交 Pull Request。

Contributions are welcome! Please feel free to submit a Pull Request.

## 许可证 / License

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 作者 / Author

Lang Yu - [@silentbalanceyh](https://github.com/silentbalanceyh)

## 致谢 / Acknowledgments

- [Vert.x](https://vertx.io/) - Reactive applications on the JVM
- [Spring Boot](https://spring.io/projects/spring-boot) - Spring-based production-ready applications
