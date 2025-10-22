package io.r2mo.base.dbe.syntax;

import io.r2mo.base.dbe.constant.QCV;
import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JObject;
import lombok.Getter;

import java.io.Serializable;

/**
 * @author lang : 2025-08-28
 */
@Getter
public class QPager implements Serializable, QRequest {
    private static final String PAGE = "page";
    private static final String SIZE = "size";

    // ---- Pager 特殊方法
    private final int page;     // 第几页，从1开始
    private final int size;     // 每页多少条
    private final int start;    // 开始索引，从0开始
    private final int end;      // 结束索引，非包含

    private QPager(final int page, final int size) {
        this.page = page;
        this.size = size;
        this.start = (page - 1) * size;
        this.end = this.page * size;        // page 从 1 开始
    }

    public static <T> QPager of(final T json) {
        return of(SPI.J(json));
    }

    public static QPager of(final JObject pageJ) {
        final int page = pageJ.getInt(PAGE, 1);
        final int size = pageJ.getInt(SIZE, 10);
        return new QPager(page, size);
    }

    @Override
    public boolean isOk() {
        return this.page > 0 && this.size > 0;
    }

    @Override
    public String field() {
        return QCV.P_PAGER;
    }

    @Override
    @SuppressWarnings("all")
    public JObject data() {
        final JObject data = SPI.J();
        data.put(PAGE, this.page);
        data.put(SIZE, this.size);
        return data;
    }

    @Override
    public <R> R item() {
        throw new UnsupportedOperationException("[ R2MO ] 该方法调用不支持！");
    }

}
