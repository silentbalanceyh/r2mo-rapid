# POJO实体

## 1. 基础模型

所有的 `Entity` 都在 `-domain` 项目中定义，Spring 中的生成器会根据 `-domain` 中的 `Java`
代码定义做双向生成，以兼容不同的底层数据库，且没有特殊说明的场景下系统提供基模型：[BaseEntity](https://gitee.com/silentbalanceyh/r2mo-rapid/blob/master/r2mo-dbe-mybatisplus/src/main/java/io/r2mo/dbe/mybatisplus/core/domain/BaseEntity.java)。

```java
/*
 *     - id         / 主键
 *     - code       / 编码（随机10位字符串）
 *     - createdAt  / 创建时间
 *     - createdBy  / 创建人
 *     - updatedAt  / 更新时间
 *     - updatedBy  / 更新人
 *
 *     - enabled    / 是否启用
 *     - language   / 语言
 *     - version    / 版本
 *     - metadata   / 元配置
 *
 *     - appId      / 所属应用
 *     - tenantId   / 所属租户
 */
```

---

## 2. 特殊字段

*大文本Json格式*

```java

@Schema(description = "辅助元配置", example = SchemaExampleValue.DEFAULT_METADATA)
@TableField(jdbcType = JdbcType.CLOB, typeHandler = TypedJObjectHandler.class)
@JsonProperty("cMetadata")
private JObject cMetadata;
```

- 此处实现使用了 `MyBatis-Plus`。
- `jdbcType` 在大文本模式下必须是 `JdbcType.CLOB`，才能正确生成对应的 `SQL`。
- `JObject` 的数据类型是 `io.r2mo.typed.json.JObject` 接口，具体实现取决于引入的 `-typed` 项目。

*时间日期格式*

```java

@TableField(fill = FieldFill.INSERT_UPDATE)
@DateTimeFormat(pattern = DefaultConstantValue.DEFAULT_META_DATETIME_PATTERN)
private LocalDateTime heartbeatAt;
```

*UUID格式*

```java

@TableField(typeHandler = TypedUUIDHandler.class)
private UUID orderId;
```

*业务标识属性*

很多场景下，数据记录是否重复不依赖主键 `id`，这种场景下可以考虑使用注解 `io.r2mo.typed.annotation.Identifiers` 实现针对 Entity 的业务标识属性，有了标识属性之后服务层可直接调用保存方法实现**添加/编辑**合并的保存动作，且在标识业务记录时，还支持如下字段的条件封装：

- `APP_ID`：是否追加应用条件。
- `TENANT_ID`：是否追加租户条件。
- `ENABLED`：是否追加启用/禁用条件。

> 此注解只针对继承于 `BaseEntity` 的实体类有效。

---

## 3. 参考示例

```java

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.r2mo.dbe.mybatisplus.core.domain.BaseEntity;
import io.r2mo.dbe.mybatisplus.core.typehandler.TypedUUIDHandler;
import io.r2mo.typed.annotation.Identifiers;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.JdbcType;

import java.util.UUID;

/**
 * @author lang : 2025-09-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tk_product_cat")
@Identifiers({"name"})
@Schema(name = "商品分类")
public class ProductCatEntity extends BaseEntity {

    private String name;

    private String catId;

    @TableField(jdbcType = JdbcType.CLOB)
    private String description;

    private String icon;

    private int sort;

    @TableField(typeHandler = TypedUUIDHandler.class)
    private UUID parentId;
}
```