package io.r2mo.jce.common;

import io.r2mo.typed.cc.Cc;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * 编解码专用接口
 *
 * @author lang : 2025-09-19
 */
interface Coder {

    Cc<String, Coder> CCT_CODING = Cc.openThread();

    static Coder of(final Supplier<Coder> supplier) {
        Objects.requireNonNull(supplier, "[ R2MO ] Coder 参数不能为空");
        return CCT_CODING.pick(supplier, String.valueOf(supplier.hashCode()));
    }

    String encode(String data);

    String decode(String data);
}
