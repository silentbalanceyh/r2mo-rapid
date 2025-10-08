# 核心开发库

[![Maven Central](https://img.shields.io/maven-central/v/io.zerows/r2mo-rapid.svg?label=Maven%20Central&style=for-the-badge&color=blue)](https://mvnrepository.com/artifact/io.zerows/r2mo-rapid)

> For Rachel Momo

## 介绍

此库用于统一 `spring-cloud / spring-boot` 中的整体实现模型，提供如下功能：

### 基础功能

- 统一的 Json 数据结构类型
- 统一的 Io 存储对接访问（存储抽象隔离）
- 统一的异常处理架构
- 基于目前支持实现类的代码生成
- 函数式编程模型支持
- 快速编程
    - `DBE` / Database Engine，提供数据库统一访问
    - `HFS` / High-Level File System，存储设备统一方法
    - `RFS` / Remote File System，基于底层抽象存储的上传下载
    - `HED` / High-Level Encrypt Decrypt，加解密专用工具类
- 快速测试框架
- 基于 Bouncy Castle 的增强安全算法 / 国密算法

### 业务功能

- 基于建模常用的 CRUD 部分
- 多租户 / 多应用 基模型，提供应用商店管理
- 许可（激活码）底层服务，支持数字签名格式

## 参考文档

### 引入方式

搭建经典的 `-domain/-provider/-api` 的结构，在父 POM 项目中继承

```xml

<parent>
    <groupId>io.zerows</groupId>
    <artifactId>r2mo-rapid</artifactId>
    <version>${r2mo.version}</version>
</parent>
```

子项目 `-domain` 中引入

```xml

<dependencies>
    <dependency>
        <groupId>io.zerows</groupId>
        <artifactId>r2mo-bootstrap</artifactId>
    </dependency>
    <!-- 实现部分自定 -->
    <dependency>
        <groupId>io.zerows</groupId>
        <artifactId>r2mo-spring-mybatisplus</artifactId>
        <version>${r2mo.version}</version>
    </dependency>
    <dependency>
        <groupId>io.zerows</groupId>
        <artifactId>r2mo-spring-json</artifactId>
        <version>${r2mo.version}</version>
    </dependency>
    <dependency>
        <groupId>io.zerows</groupId>
        <artifactId>r2mo-typed-hutool</artifactId>
        <version>${r2mo.version}</version>
    </dependency>
    <dependency>
        <groupId>io.zerows</groupId>
        <artifactId>r2mo-io-local</artifactId>
        <version>${r2mo.version}</version>
    </dependency>
</dependencies>
```

- [QR] 语法参考：[QR查询引擎](https://lang-yu.gitbook.io/zero/000.index/010.jooq#id-3.1.-ji-ben-yu-fa)

--- 

## 功能矩阵

- Json 类型对接实现

    - [x] Hutool 中的 `JSONObject`
    - [x] Vertx 中的 `JsonObject`
- 统一异常处理，提供三种核心异常类型，统一错误码

    - [x] Web 异常
    - [x] Remote 服务通信异常（Dubbo专用）
    - [x] Secure 安全异常（Security专用）
- Io 类型对接和实现：

    - [x] 本地文件系统
    - [ ] FTP / SFTP
- 数据库访问 DBE 实现

    - [x] Mybatis Plus
    - [ ] JPA
    - [ ] JOOQ