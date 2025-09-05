# 核心开发库

## 介绍

此库用于统一 `spring-cloud / spring-boot` 中的整体实现模型，提供如下功能：

- 统一的 Json 数据结构类型
- 统一的 Io 存储对接访问
- 统一的异常处理架构
- 基于目前支持实现类的代码生成
- 提供数据库统一访问 DBE / 存储设备统一方法 HFS
- 基于建模常用的 CRUD 部分
- 多租户 / 多应用 基模型

## 参考文档

### 引入方式

搭建经典的 `-domain/-provider/-api` 的结构，在父 POM 项目中继承

```xml
    <parent>
        <groupId>io.zerows</groupId>
        <artifactId>r2mo-rapid</artifactId>
        <version>1.0-M5</version>
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
    - [ ] Vertx 中的 `JsonObject`
    - [ ] Fastjson2
    - [ ] Jackson
    - [ ] Gson
    - [ ] Spring
- 统一异常处理，提供三种核心异常类型

    - [x] Web 异常
    - [x] Remote 服务通信异常（Dubbo专用）
    - [x] Secure 安全异常（Security专用）
- Io 类型对接和实现：

    - [x] 本地文件系统
    - [ ] HDFS
    - [ ] CephFS
    - [ ] FTP / SFTP
    - [ ] Minio
    - [ ] 阿里云 OSS
    - [ ] 腾讯云 COS
    - [ ] 七牛云 Kodo
    - [ ] 华为云 OBS
- 数据库访问 DBE 实现

    - [x] Mybatis Plus
    - [ ] JPA
    - [ ] JOOQ
    - [ ] Spring JDBC