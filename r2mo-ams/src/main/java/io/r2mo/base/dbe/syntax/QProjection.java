package io.r2mo.base.dbe.syntax;

import io.r2mo.base.dbe.constant.QCV;
import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author lang : 2025-08-28
 */
public class QProjection implements Serializable, QRequest {

    private List<String> filters = null;

    private QProjection() {
    }

    public static QProjection of(final JArray projectionA) {
        Objects.requireNonNull(projectionA, "[ R2MO ] 输入参数 JArray 不可为 null");
        final QProjection projection = new QProjection();
        projectionA.itString().forEach(projection::add);
        return projection;
    }

    @Override
    @SuppressWarnings("all")
    public List<String> item() {
        return this.filters;
    }

    @Override
    public String field() {
        return QCV.P_PROJECTION;
    }

    @Override
    @SuppressWarnings("all")
    public JArray data() {
        final JArray data = SPI.A();
        data.addAll(this.filters);
        return data;
    }

    @Override
    public boolean isOk() {
        return Objects.nonNull(this.filters);
    }

    // ---- Projection 特殊方法
    public QProjection add(final String field) {
        if (field == null || field.isBlank()) {
            return this;
        }
        if (Objects.isNull(this.filters)) {
            this.filters = new ArrayList<>();
        }
        this.filters.add(field);
        return this;
    }

    public QProjection reset() {
        this.filters = null;
        return this;
    }
}
