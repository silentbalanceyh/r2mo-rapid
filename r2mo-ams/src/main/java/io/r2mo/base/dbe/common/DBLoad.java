package io.r2mo.base.dbe.common;

import io.r2mo.base.program.R2Vector;

/**
 * 填充专用的 {@link DBLoad} 接口，用来填充 {@link DBNode} 相关信息，每次构造时通过加载的方式来填充对应的元数据信息，
 * 保证元数据可以被完全填充实现整体的统一流程，这种跳跃性的处理比起其他模式更有价值，且在加载过程中可内部实现统一的缓存管
 * 理，一次分析，多处使用，提升整体的运行效率。
 *
 * @author lang : 2025-10-24
 */
public interface DBLoad {
    String DEFAULT_SPID_META = "DEFAULT_DB_META";

    DBNode configure(Class<?> entity, R2Vector vector);

    default DBNode configure(final Class<?> entity) {
        return this.configure(entity, null);
    }
}
