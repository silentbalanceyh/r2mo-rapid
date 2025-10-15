# 代码生成

## 1. 抽象业务对接

> 推荐在自己的项目中搭建一层 `XxxOperationCommon<T>` 的抽象业务接口。

```java
package io.r2mo.spring.app.takeout.base;

import io.r2mo.io.common.HFS;
import io.r2mo.spring.mybatisplus.program.ActOperationMybatisPlus;

/**
 * @author lang : 2025-09-03
 */
public abstract class ActOperationCommon<T> extends ActOperationMybatisPlus<T> {

    private final HFS hfs;

    public ActOperationCommon() {
        super();
        this.hfs = HFS.of();
    }

    protected HFS fs() {
        return this.hfs;
    }
}
```

搭建此抽象接口的原因：提供一个项目内部统一继承的抽象业务接口，防止和框架过度融合，若后期要替换实现则直接更改此抽象类的父类即可，而不用去更改项目中既定的代码，且此抽象接口提供了 `HFS/DBE` 的基础访问方式：

```java
// 子类中有方法
public List<T> serviceXxx() {
    // 子类中访问 DBE
    this.db();
    // 子类中访问 HFS
    this.fs();
}
```

---

## 2. 生成配置

参考如下生成配置的代码（位于 `-domain` 中）：

```java
package io.r2mo.spring.app.takeout;

import io.r2mo.spring.app.takeout.base.ActOperationCommon;
import io.r2mo.base.generator.GenMeta;
import io.r2mo.base.generator.SourceStructure;
import io.r2mo.dbe.mybatisplus.generator.config.GenConfigMybatisPlus;
import io.r2mo.typed.enums.DatabaseType;

/**
 * @author lang : 2025-08-29
 */
public class GenConfigAppTakeout extends GenConfigMybatisPlus {

    @Override
    public GenMeta getMetadata() {
        return GenMeta.builder()
            .schema("V1__init_schema.sql")
            .spi("GenMybatisPlus")
            .database(DatabaseType.MYSQL_8)
            .structure(SourceStructure.DPA)
            .version("v1")
            .baseAct(ActOperationCommon.class)
            .build();
    }
}
```

各个参数的含义

- `schema`：生成的 `Flyway` 的初始化表结构的SQL文件名
- `spi`：生成器的实现名称
    - [x] GenMybatisPlus / MyBatis-Plus 实现
- `database`：数据库类型
    - [x] DatabaseType
        - [x] `MYSQL_5`
        - [x] `MYSQL_8`
        - [x] `MYSQL_9`
        - [x] `PGSQL`
        - [x] `SQLITE_3`
        - [x] `H2`
- `structure`：项目结构
    - [x] DPA - `-domain/-provider/-api` 三层结构（微服务应用都可使用）
    - [ ] ONE
- `version`：版本号，此处版本号会生成对应的包名 `v1`，后期迁移过程可直接生成新版本形成多版本底层结构，主要为 Migration 场景量身打造，由于生成本身具有幂等性，所以反复执行并不影响项目本身的运行。
- `baseAct`：业务抽象接口，生成的业务接口均继承于此接口，若没有自己的则可直接使用现有的。

> ❗️配置类 `GenConfigAppTakeout` 的包名很重要，所有生成的代码均会基于此包名进行生成根包。

---

## 3. 运行设置

参考如下生成器的代码（位于 `-provider` 中）：

```java
package io.r2mo.spring.app.takeout;


import io.r2mo.boot.spring.generator.SourceGenerator;

/**
 * @author lang : 2025-08-29
 */
public class GenAppTakeoutModule {

    public static void main(final String[] args) {
        final SourceGenerator generator = new SourceGenerator(GenConfigAppTakeout.class);
        generator.generate();
    }
}
```

> 不同项目同包本来不推荐，但由于是独立运行的 main 入口，只是一次性生成。

⚠️ IDEA 中的运行：

1. `Modify Run Configuration...`
2. `Working directory` -> 一定要选择当前项目的根目录。

---

## 4. 生成结果

```bash
# 项目结构
app-takeout  # Working directory
| - app-takeout-api
| - app-takeout-domain
| - app-takeout-provider
| - generated
```

上述生成结果中，锁文件 `.lock` 位于 `generated` 之内，会锁定 `controller` 的生成，防止接口的重复生成覆盖！无法避免开发人员对接口进行过部分定制。

```bash
# 结果路径，此处 
#  -- APP = app-takeout,
#  -- PKG = io.r2mo.spring.app.takeout     ( 配置对象包名 )
#  -- VER = v1                             ( 配置对象版本 )

# -domain 中
src/main/resources/${APP}/database/schema/V1__init_schema.sql  # Flyway 文件命名

# -provider 中
src/main/resources/${APP}/mapper/*.xml            # MyBatis-Plus 的 Mapper XML
src/main/java/${PKG}/mapper/*.java                # MyBatis-Plus 的 Mapper 接口
src/main/java/${PKG}/service/gen/xxx/*.java       # 业务接口，xxx 代表某个 Entity 的模块

# -api 中
# 控制层的重新生成依赖 generated 中的锁文件
src/main/java/${PKG}/controller/gen/xxx/*V1.java  # 控制层接口，xxx 代表某个 Entity 的模块
```




