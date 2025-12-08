package io.r2mo.typed.domain.extension;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.r2mo.typed.domain.BaseScope;
import lombok.Data;

/**
 * @author lang : 2025-11-11
 */
@Data
public abstract class AbstractScope implements BaseScope {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String app;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String tenant;

    @Override
    public void app(final String app) {
        this.app = app;
    }

    @Override
    public String app() {
        return this.app;
    }

    @Override
    public void tenant(final String tenant) {
        this.tenant = tenant;
    }

    @Override
    public String tenant() {
        return this.tenant;
    }
}
