package io.r2mo.base.dbe.syntax;

import io.r2mo.base.dbe.constant.QCV;
import io.r2mo.spi.SPI;
import io.r2mo.typed.common.Kv;
import io.r2mo.typed.json.JArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lang : 2025-08-28
 */
public class QSorter implements Serializable, QRequest {

    private final List<Kv<String, Boolean>> fields = new ArrayList<>();

    private QSorter() {
    }

    public static QSorter of() {
        return of(null);
    }

    public static QSorter of(final JArray sorterA) {
        if (SPI.V_UTIL.isEmpty(sorterA)) {
            return new QSorter();
        }
        final QSorter sorter = new QSorter();
        sorterA.itString().forEach(exprOr -> {
            if (exprOr.contains(",")) {
                // field,ASC | field,DESC
                final String[] split = exprOr.split(",");
                if (split.length == 2) {
                    final String field = split[0].trim();
                    final boolean isAsc = "asc".equalsIgnoreCase(split[1].trim());
                    sorter.add(field, isAsc);
                }
            } else {
                // field -> field,ASC
                sorter.add(exprOr, true);
            }
        });
        return sorter;
    }

    // 高频使用方法
    public static QSorter of(final String field, final boolean isAsc) {
        return QSorter.of().add(field, isAsc);
    }

    @Override
    public boolean isOk() {
        return !this.fields.isEmpty();
    }

    @Override
    @SuppressWarnings("all")
    public JArray data() {
        final JArray data = SPI.A();
        this.fields.forEach(kv -> data.add(kv.key() + "," + (kv.value() ? "ASC" : "DESC")));
        return data;
    }

    @Override
    @SuppressWarnings("all")
    public List<Kv<String, Boolean>> item() {
        return this.fields;
    }

    @Override
    public String field() {
        return QCV.P_SORTER;
    }

    // ---- QSorter 特殊方法
    public QSorter add(final String field, final boolean asc) {
        if (field == null || field.isBlank()) {
            return this;
        }
        this.fields.add(Kv.create(field, asc));
        return this;
    }

    public QSorter reset() {
        this.fields.clear();
        return this;
    }

    public List<Kv<String, Boolean>> items() {
        return this.fields;
    }
}
