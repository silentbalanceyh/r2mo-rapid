package io.r2mo.spring.mybatisplus.program;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author lang : 2025-08-29
 */
public abstract class ServiceOfMybatisPlus<T> {

    protected final Class<T> entityCls;

    @SuppressWarnings("all")
    public ServiceOfMybatisPlus() {
        final Type genericType = this.getClass().getGenericSuperclass();
        if(genericType instanceof final ParameterizedType parameterizedType) {
            final Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            if(0 < actualTypeArguments.length) {
                this.entityCls = (Class<T>) actualTypeArguments[0];
            }else{
                throw new IllegalStateException("[ R2MO ] 泛型定义长度不对！");
            }
        }else{
            throw new IllegalStateException("[ R2MO ] 泛型类型获取失败！");
        }
    }

    protected abstract <M extends BaseMapper<T>> M executor();

    protected DBE<T> db(){
        return DBE.of(this.entityCls, this.executor());
    }
}
