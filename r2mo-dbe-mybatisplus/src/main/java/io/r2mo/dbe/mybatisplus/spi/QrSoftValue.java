package io.r2mo.dbe.mybatisplus.spi;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.r2mo.base.dbe.syntax.QLeaf;
import io.r2mo.spi.SPI;

import java.util.Collection;

/**
 * @author lang : 2025-09-07
 */
class QrSoftValue {
    /*
     * Fix Issue: Cannot convert string '\xAC\xED\x00\x05sr...' from binary to utf8mb4
     */
    static <T> void in(final QLeaf leaf, final QueryWrapper<T> query) {
        final Object value = leaf.value();
        if (value instanceof Collection<?>) {
            query.in(leaf.field(), (Collection<?>) value);
        } else {
            /*
             * 有可能是实现部分，所以此处的核心转换要借用 UTIL 中的内容来完成
             */
            final Collection<?> values = SPI.V_UTIL.toCollection(value);
            query.in(leaf.field(), values);
        }
    }
}
