package io.r2mo.base.program;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2025-10-17
 */
public class R2VarSet implements Serializable {

    private final ConcurrentMap<String, R2Var> vars = new ConcurrentHashMap<>();

    private R2VarSet() {
    }

    public static R2VarSet of() {
        return new R2VarSet();
    }


    public <T> R2VarSet add(final String name, final T valueDefault) {
        return this.add(name, null, String.class, valueDefault);
    }

    public <T> R2VarSet add(final String name, final T valueDefault, final Class<?> type) {
        return this.add(name, null, type, valueDefault);
    }

    public <T> R2VarSet addWith(final String name, final String alias, final T valueDefault) {
        return this.add(name, alias, String.class, valueDefault);
    }

    public <T> R2VarSet addWith(final String name, final String alias, final Class<?> type) {
        return this.add(name, alias, type, null);
    }

    public R2Var get(final String name) {
        return this.vars.getOrDefault(name, null);
    }

    public <T> R2VarSet set(final String name, final T value) {
        final R2Var var = this.get(name);
        if (Objects.nonNull(var)) {
            var.value(value);
        }
        return this;
    }

    public Set<String> names() {
        return this.vars.keySet();
    }

    /**
     * 五维添加：name / alias / type / value / valueDefault
     * - 并发安全：使用 compute 合并
     * - 类型规则：
     * 1) 若显式传入 type，则优先使用；
     * 2) 否则依据 value 或 valueDefault 推断；
     * 3) 都没有则用 Object.class；
     * - 合并规则：
     * - alias 非空则覆盖；
     * - type 若已存在且传入非空且不同，抛出异常（避免脏数据）；
     * - value / valueDefault 仅在传入非空时覆盖。
     */
    private <T> R2VarSet add(final String name,
                             final String alias,
                             final Class<?> type,
                             final T valueDefault) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("[ R2MO ] 变量名 name 不能为空或全空白。");
        }

        this.vars.compute(name, (k, existing) -> {
            final R2Var v = (existing == null) ? new R2Var().name(name) : existing;

            // alias：有就覆盖
            if (alias != null && !alias.isBlank()) {
                v.alias(alias);
            }

            // 计算应使用的类型
            final Class<?> inferred;
            if (Objects.isNull(type)) {
                inferred = Objects.isNull(valueDefault) ? Object.class : valueDefault.getClass();
            } else {
                inferred = type;
            }

            // type：若已存在且不同，判冲突
            if (v.type() != null && !Objects.equals(v.type(), inferred)) {
                throw new IllegalArgumentException(
                    "[ R2MO ] 变量 \"" + name + "\" 的类型冲突：已存在类型="
                        + v.type().getName() + "，新传入类型=" + inferred.getName() + "。");
            }
            v.type(inferred);
            // 值：仅在传入非空时覆盖
            if (valueDefault != null) {
                v.valueDefault(valueDefault);
            }
            return v;
        });
        return this;
    }
}
