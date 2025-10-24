package io.r2mo.dbe.mybatisplus.spi;

import com.github.yulichang.base.MPJBaseMapper;
import com.github.yulichang.query.MPJQueryWrapper;
import io.r2mo.base.dbe.common.DBNode;
import io.r2mo.base.dbe.common.DBRef;
import io.r2mo.base.util.R2MO;
import io.r2mo.dbe.mybatisplus.JoinProxy;
import io.r2mo.typed.exception.web._501NotSupportException;
import io.r2mo.typed.json.JObject;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * @author lang : 2025-10-23
 */
class OpJoinWriter<T> {
    private final JoinProxy<T> executor;
    private final DBRef ref;

    OpJoinWriter(final DBRef ref, final JoinProxy<T> executor) {
        this.ref = ref;
        this.executor = executor;

        final Set<Class<?>> entitySet = ref.findJoined();
        final boolean isReady = entitySet.stream().anyMatch(executor::isReady);
        if (!isReady) {
            throw new _501NotSupportException("[ R2MO ] 由于执行器不完整，JOIN 模式的写操作无法执行，请检查是否为所有的实体都配置了对应的执行器！");
        }
    }

    @SuppressWarnings("all")
    public JObject create(final JObject latest) {
        // ------------------ 先插入主键实体 ------------------
        // 查找使用主键做 Join 的实体
        final DBNode found = this.ref.findPrimary();
        final Class<?> entity = found.entity();



        /*
         * 插入新的实体 waitFor，此处不能将内容放到私有方法中，因为牵涉到两个返回值的问题新创建的值是主键，要在 pk 中提取
         * - 主键名 / 主键值
         * 然后是反序列化的结果，此处是 Object 类型，实际是 T 类型，对应到 Class<T> 中的泛型信息
         */
        final Object waitFor = R2MO.deserializeJ(latest.data(), entity);
        final Object pkValue = found.vPrimary(waitFor);
        if (Objects.isNull(pkValue)) {
            found.vPrimary(waitFor, UUID.randomUUID());
        }
        // 先插入主键实体
        final MPJBaseMapper mapper = this.executor.mapper(entity);
        // 插入实体之后做一次交换
        final Object created = mapper.insert(waitFor);


        // ------------------ 处理其他关联实体 ------------------
        return null;
    }

    public Boolean removeById(final Serializable id) {
        return null;
    }

    public Boolean removeBy(final MPJQueryWrapper<T> queryWrapper) {
        return null;
    }

    public JObject updateById(final Serializable id, final JObject latest) {
        return null;
    }

    public JObject update(final MPJQueryWrapper<T> queryWrapper, final JObject latest) {
        return null;
    }
}
