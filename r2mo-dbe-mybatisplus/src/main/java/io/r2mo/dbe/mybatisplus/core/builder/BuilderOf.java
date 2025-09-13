package io.r2mo.dbe.mybatisplus.core.builder;

import io.r2mo.typed.common.Ref;
import io.r2mo.typed.exception.web._501NotSupportException;

import java.util.function.Supplier;

/**
 * @author lang : 2025-09-12
 */
public interface BuilderOf<T> {

    @SuppressWarnings("unchecked")
    static <T> BuilderOf<T> of(final Supplier<BuilderOf<T>> constructorFn) {
        return (BuilderOf<T>) AbstractBuilderOf.CC_SUPPLIER
            .pick(constructorFn::get, String.valueOf(constructorFn.hashCode()));
    }

    /**
     * 纯模式创建实体（统一流程）
     *
     * @return 实体
     */
    T create();

    /**
     * 根据第二实体创建新实体
     *
     * @param source 第二实体
     * @param <R>    第二实体类型
     *
     * @return 实体
     */
    default <R> T create(final R source) {
        return this.create();
    }

    /**
     * 根据第二实体按条件更新实体
     *
     * @param target 目标实体
     * @param source 第二实体
     * @param <R>    第二实体类型
     */
    default <R> void updateConditional(final T target, final R source) {
        throw new _501NotSupportException("[ IIAP ] updateConditional 未实现 / " + this.getClass().getName());
    }

    /**
     * 直接拷贝
     *
     * @param source 源实体
     * @param target 目标实体
     */
    void updateOverwrite(T target, Object source);

    default void updateRef(final T target, final Ref ref) {
        throw new _501NotSupportException("[ IIAP ] updateRef 未实现 / " + this.getClass().getName());
    }
}
