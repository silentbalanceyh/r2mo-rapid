package io.r2mo.spring.common.webflow;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JObject;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author lang : 2025-09-04
 */
@Data
public class PreRequest implements Serializable {

    @Schema(hidden = true)
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @JsonIgnore
    private final PreRequestContext context = new PreRequestContext();
    @Schema(hidden = true)
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @JsonIgnore
    private final PreRequestApply apply = PreRequestApply.of();
    @Schema(hidden = true)
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @JsonIgnore
    private final PreRequestQuery query = PreRequestQuery.of();
    @Schema(description = "关联App", hidden = true)
    private UUID appId;     // X-App-Id
    @Schema(description = "关联租户", hidden = true)
    private UUID tenantId;  // X-Tenant-Id
    @Schema(hidden = true)
    @Getter
    @Setter(AccessLevel.NONE)
    private HttpServletRequest request;
    @Schema(description = "会话ID", hidden = true)
    private String sessionId;
    @Schema(description = "记录ID", hidden = true)
    private UUID id;

    public PreRequest() {
        this.request = this.context.request();
        this.sessionId = this.context.sessionId();
        this.appId = this.context.appId(true);
        this.tenantId = this.context.tenantId(true);
    }

    // 数据处理
    protected void writeAudit(final Object entityObj, final boolean created) {
        this.apply.writeAudit(this.context, entityObj, created);
    }

    protected void writeScope(final Object entityObj) {
        this.apply.writeScope(this.context, entityObj);
    }

    protected void writeTo(final Object target) {
        BeanUtil.copyProperties(this, target,
            new CopyOptions().ignoreNullValue().ignoreError());
    }

    // 查询条件
    public JObject withScope(final Class<?> clazz, final JObject condition) {
        return this.query.withScope(this.context, condition, clazz);
    }

    public JObject withScope(final Class<?> clazz) {
        return this.withScope(clazz, SPI.J());
    }

    public <K, V> JObject withScope(final Class<?> clazz,
                                    final K k1, final V v1) {
        return this.query.withMapN(this.context, clazz, k1, v1);
    }

    public <K, V> JObject withScope(final Class<?> clazz,
                                    final K k1, final V v1, final K k2, final V v2) {
        return this.query.withMapN(this.context, clazz, k1, v1, k2, v2);
    }

    public <K, V> JObject withScope(final Class<?> clazz,
                                    final K k1, final V v1, final K k2, final V v2, final K k3, final V v3) {
        return this.query.withMapN(this.context, clazz, k1, v1, k2, v2, k3, v3);
    }
}
