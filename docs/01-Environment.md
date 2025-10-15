# 基本环境

## 1. 环境要求

| 环境    | 版本要求        |
|-------|-------------|
| JDK   | `17+`       |
| Maven | `3.8.6 及以上` |
| MySQL | `8.0+`      |

> 其他依赖库的版本直接参考：<https://gitee.com/silentbalanceyh/rachel-momo>

---

## 2. 项目结构

| 目录          | 说明                      |
|-------------|-------------------------|
| `-domain`   | 领域模型，包含实体类和 Mapper      |
| `-provider` | 服务层，包含业务逻辑以及特殊服务组件的实现选择 |
| `-api`      | 控制层，包含接口和请求处理           |

> 依赖引入参考主页

---

## 3. 双环境

为了保证配置本身的干净，所以整体结构并没有开启不同的 `profile` 功能，而是使用不同的入口程序来实现不同的环境，如此一份 `bootstrap.yml / application.yml` 可直接重用相互不影响。

- 开发环境的环境变量使用 `properties` 配置文件提供（测试环境可使用另外的 `properties`）。
- 生产环境的环境变量则直接在启动之前提供，典型如 `Docker` 环境提供 / 宿主环境提供。

### 3.1. 开发入口

```java
package io.r2mo.spring.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:env.properties")
public class XxxAppDev {
    public static void main(final String[] args) {
        SpringApplication.run(XxxAppDev.class, args);
    }
}
```

### 3.2. 生产入口

```java
package io.r2mo.spring.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author lang : 2025-08-29
 */
@SpringBootApplication
public class XxxApplication {
    public static void main(final String[] args) {
        SpringApplication.run(XxxApplication.class, args);
    }
}
```

项目本身直接打包 `XxxApplication` 为入口即可，而 `XxxAppDev` 仅提供给 IDE 在开发环境使用。

```xml

<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <configuration>
                <mainClass>io.r2mo.spring.app.XxxApplication</mainClass>
                <layout>JAR</layout>
            </configuration>
            <executions>
                <execution>
                    <id>repackage</id>
                    <goals>
                        <goal>repackage</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```