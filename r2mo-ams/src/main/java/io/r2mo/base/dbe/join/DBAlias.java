package io.r2mo.base.dbe.join;

import cn.hutool.core.util.StrUtil;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * @author lang : 2025-10-22
 */
@Slf4j
@Accessors(chain = true, fluent = true)
public record DBAlias(String table, String name, String alias) implements Serializable {
    
    public boolean isOk() {
        final boolean isOk = StrUtil.isNotBlank(this.table) && StrUtil.isNotBlank(this.name) && StrUtil.isNotBlank(this.alias);
        if (!isOk) {
            log.warn("[ R2MO ] 字段：`{}.{}` 的别名定义有问题 / {}", this.table, this.name, this.alias);
        }
        return isOk;
    }
}
