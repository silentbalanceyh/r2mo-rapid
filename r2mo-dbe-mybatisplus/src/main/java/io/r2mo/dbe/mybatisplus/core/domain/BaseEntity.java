package io.r2mo.dbe.mybatisplus.core.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.r2mo.dbe.common.constant.SchemaExampleValue;
import io.r2mo.dbe.mybatisplus.core.typehandler.TypedJObjectHandler;
import io.r2mo.dbe.mybatisplus.core.typehandler.TypedUUIDHandler;
import io.r2mo.function.Fn;
import io.r2mo.typed.domain.BaseAudit;
import io.r2mo.typed.domain.BaseScope;
import io.r2mo.typed.json.JObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Entity基类
 * 核心基类，用于处理通用性字段专用，此处的核心类可以作为基础字段保留，以保证子类的一致性，针对当前数据本身有一个基本归属，
 * 基本归属主要包含如下
 * <pre>
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
 *     - appId / 所属应用
 *     - tenantId / 所属租户
 * </pre>
 */
@Schema(description = "实体基类", discriminatorProperty = "className")
@Data
public class BaseEntity implements BaseScope, BaseAudit, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description = "类名")
    @JsonIgnore
    @TableField(exist = false)
    private String className = this.getClass().getSimpleName();
    @Schema(description = "主键", example = SchemaExampleValue.DEFAULT_UUID)
    /*
     * 由于此处使用了 @TableId 会导致 @TableField 失效，所以无法直接使用 TypedUUIDHandler，加上此处类型是 java.util.UUID，
     * 而 Mybatis-Plus 默认只支持 Long/String 类型的主键自定义生成策略，所以只能使用 IdType.INPUT
     */
    @TableId(value = "id", type = IdType.INPUT)
    private UUID id;

    /** 编码 */
    @Schema(description = "编码", example = SchemaExampleValue.DEFAULT_CODE)
    private String code;

    /** 创建者 */
    @Schema(description = "创建者", example = SchemaExampleValue.DEFAULT_UUID)
    @TableField(fill = FieldFill.INSERT)
    private UUID createdBy;

    /** 创建时间 */
    @Schema(description = "创建时间", example = SchemaExampleValue.DEFAULT_TIME)
    @TableField(fill = FieldFill.INSERT)
    @DateTimeFormat(
        pattern = "yyyy-MM-dd HH:mm:ss"
    )
    private LocalDateTime createdAt;

    /** 更新者 */
    @Schema(description = "更新者", example = SchemaExampleValue.DEFAULT_UUID)
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private UUID updatedBy;

    /** 更新时间 */
    @Schema(description = "更新时间", example = SchemaExampleValue.DEFAULT_TIME)
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @DateTimeFormat(
        pattern = "yyyy-MM-dd HH:mm:ss"
    )
    private LocalDateTime updatedAt;

    @Schema(description = "是否启用", example = SchemaExampleValue.DEFAULT_BOOL)
    @TableField(value = "is_enabled")
    private boolean enabled = true;

    @Schema(description = "语言", example = SchemaExampleValue.DEFAULT_LANGUAGE)
    private String language = "zh-CN";

    @Schema(description = "版本号", example = SchemaExampleValue.DEFAULT_VERSION)
    private String version = "1.0.0";

    @Schema(description = "所属租户", example = SchemaExampleValue.DEFAULT_UUID)
    @TableField(typeHandler = TypedUUIDHandler.class)
    private UUID tenantId;

    @Schema(description = "所属应用", example = SchemaExampleValue.DEFAULT_UUID)
    @TableField(typeHandler = TypedUUIDHandler.class)
    private UUID appId;

    @Schema(description = "辅助元配置", example = SchemaExampleValue.DEFAULT_METADATA)
    @TableField(jdbcType = JdbcType.CLOB, typeHandler = TypedJObjectHandler.class)
    @JsonProperty("cMetadata")
    private JObject cMetadata;
    // ---------------- 和表无关
    /** （保留）搜索值 */
    @JsonIgnore
    @TableField(exist = false)
    private String searchValue;

    /** 请求参数 */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @TableField(exist = false)
    private Map<String, Object> params = new HashMap<>();

    @Override
    public void app(final String appId) {
        if (Objects.isNull(appId)) {
            return;
        }
        Fn.jvmAt(() -> this.appId = UUID.fromString(appId));
    }

    @Override
    public String app() {
        return Objects.isNull(this.appId) ? null : this.appId.toString();
    }

    @Override
    public void tenant(final String tenantId) {
        if (Objects.isNull(tenantId)) {
            return;
        }
        Fn.jvmAt(() -> this.tenantId = UUID.fromString(tenantId));
    }

    @Override
    public String tenant() {
        return Objects.isNull(this.tenantId) ? null : this.tenantId.toString();
    }

    public String dgInfo() {
        return BaseDebug.dgInfo(this);
    }
}
