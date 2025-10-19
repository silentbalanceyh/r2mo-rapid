package io.r2mo.dbe.jooq.core.condition;

import io.r2mo.base.dbe.constant.QCV;
import io.r2mo.base.dbe.syntax.QValue;
import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JArray;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author lang : 2025-10-19
 */
@SuppressWarnings("all")
class ClauseFun {
    static Object eachFn(final Object value, final Function<Object, Object> convert) {
        if (value instanceof final JArray array) {
            final JArray result = SPI.A();
            array.toList().stream().map(convert)
                .filter(Objects::nonNull)
                .forEach(result::add);
            return result;
        } else {
            return convert.apply(value);
        }
    }

    private static Condition dateOr(final Field field,
                                    final Supplier<Condition> dateSupplier,
                                    final Supplier<Condition> otherSupplier) {
        final Class<?> type = field.getType();
        if (LocalDate.class == type) { // 如果字段是 LocalDate
            return dateSupplier.get();
        } else { // 如果是其他类型
            return otherSupplier.get();
        }
    }

    private static Condition dateEq(final Field field, QValue qValue) {
        final LocalDate date = qValue.toDate();
        final Condition min = field.ge(date.atStartOfDay());
        final Condition max = field.lt(date.plusDays(1).atStartOfDay());
        return min.and(max);
    }

    static ConcurrentMap<Class<?>, Supplier<Clause>> CLAUSE_MAP = new ConcurrentHashMap<>() {
        {
            this.put(Objects.class, ClauseString::new);
            this.put(String.class, ClauseString::new);
            this.put(Boolean.class, ClauseBoolean::new);
            this.put(Instant.class, ClauseInstant::new);
            this.put(LocalDateTime.class, ClauseInstant::new);
            this.put(LocalDate.class, ClauseInstant::new);
            this.put(LocalTime.class, ClauseInstant::new);
            this.put(Number.class, ClauseNumber::new);
            this.put(Long.class, ClauseNumber::new);
            this.put(Short.class, ClauseNumber::new);
            this.put(Integer.class, ClauseNumber::new);
        }
    };

    static ConcurrentMap<String, BiFunction<Field, QValue, Condition>> NORM_MAP = new ConcurrentHashMap<>() {
        {
            // LT
            this.put(QCV.Op.LT, (field, qValue) -> field.lt(qValue.value()));
            // LE
            this.put(QCV.Op.LE, (field, qValue) -> field.le(qValue.value()));
            // GT
            this.put(QCV.Op.GT, (field, qValue) -> field.gt(qValue.value()));
            // GE
            this.put(QCV.Op.GE, (field, qValue) -> field.ge(qValue.value()));
            // EQ
            this.put(QCV.Op.EQ, (field, qValue) -> field.eq(qValue.value()));
            // NEQ
            this.put(QCV.Op.NEQ, (field, qValue) -> field.ne(qValue.value()));
            // NOT NULL
            this.put(QCV.Op.NOT_NULL, (field, qValue) -> field.isNotNull());
            // NULL
            this.put(QCV.Op.NULL, (field, qValue) -> field.isNull());
            // TRUE
            this.put(QCV.Op.TRUE, (field, qValue) -> field.isTrue());
            // FALSE
            this.put(QCV.Op.FALSE, (field, qValue) -> field.isFalse());
            // IN
            this.put(QCV.Op.IN, (field, qValue) -> field.in(qValue.toCollection()));
            // NOT IN
            this.put(QCV.Op.NOT_IN, (field, qValue) -> field.notIn(qValue.toCollection()));
            // START
            this.put(QCV.Op.START, (field, qValue) -> field.startsWith(qValue.value()));
            // END
            this.put(QCV.Op.END, (field, qValue) -> field.endsWith(qValue.value()));
            // CONTAIN
            this.put(QCV.Op.CONTAIN, (field, qValue) -> field.contains(qValue.value()));
        }
    };

    static ConcurrentMap<String, BiFunction<Field, QValue, Condition>> DATE_MAP = new ConcurrentHashMap<>() {
        {
            // LT 小于
            this.put(QCV.Op.LT, (field, qValue) -> dateOr(field,
                () -> {
                    final LocalDate date = qValue.toDate();
                    return field.lt(date.plusDays(1).atStartOfDay());
                },
                () -> field.lt(qValue.value())
            ));
            // LE 小于等于
            this.put(QCV.Op.LE, (field, qValue) -> dateOr(field,
                () -> {
                    final LocalDate date = qValue.toDate();
                    return field.le(date.plusDays(1).atStartOfDay());
                },
                () -> field.le(qValue.value())
            ));
            // GT 大于
            this.put(QCV.Op.GT, (field, qValue) -> dateOr(field
                , () -> {
                    final LocalDate date = qValue.toDate();
                    return field.gt(date.atStartOfDay());
                },
                () -> field.gt(qValue.value())
            ));
            // GE 大于等于
            this.put(QCV.Op.GE, (field, qValue) -> dateOr(field,
                () -> {
                    final LocalDate date = qValue.toDate();
                    return field.ge(date.atStartOfDay());
                },
                () -> field.ge(qValue.value())
            ));
            // EQ 等于
            this.put(QCV.Op.EQ, (field, qValue) -> dateOr(field,
                () -> ClauseFun.dateEq(field, qValue),
                () -> field.eq(qValue.value())
            ));
            // NOT NULL
            this.put(QCV.Op.NOT_NULL, (field, qValue) -> field.isNotNull());
            // NULL
            this.put(QCV.Op.NULL, (field, qValue) -> field.isNull());
        }
    };

    static ConcurrentMap<String, BiFunction<Field, QValue, Condition>> MARK_MAP = new ConcurrentHashMap<>() {
        {
            // Day 精度的日期范围查询
            this.put(QCV.Mark.DAY, ClauseFun::dateEq);
            // Year 精度的日期范围查询
            this.put(QCV.Mark.YEAR, (field, qValue) -> {
                final LocalDate date = qValue.toDate();
                int year = date.getYear();
                LocalDate startOfYear = LocalDate.of(year, Month.JANUARY, 1);  // 1月1日
                LocalDate endOfYear = LocalDate.of(year, Month.DECEMBER, 31);  // 12月31日

                // 转换为 LocalDateTime
                LocalDateTime startOfDay = startOfYear.atStartOfDay(); // 1月1日 00:00:00
                LocalDateTime endOfDay = endOfYear.atTime(23, 59, 59, 999999999); // 12月31日 23:59:59.999999999

                // 构造Jooq的BETWEEN条件
                return DSL.condition("{0} BETWEEN {1} AND {2}", field, startOfDay, endOfDay);
            });
            // DateTime 精度的日期范围查询
            this.put(QCV.Mark.DATETIME, (field, qValue) -> {
                final LocalDate date = qValue.toDate();
                // 构造日期范围条件
                LocalDateTime startOfDay = date.atStartOfDay(); // 00:00:00
                LocalDateTime endOfDay = date.atTime(23, 59, 59, 999999999);
                return DSL.condition("{0} BETWEEN {1} AND {2}", field, startOfDay, endOfDay);
            });
        }
    };
}
