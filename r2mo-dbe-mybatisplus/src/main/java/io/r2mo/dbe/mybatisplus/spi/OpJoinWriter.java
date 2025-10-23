package io.r2mo.dbe.mybatisplus.spi;

import com.github.yulichang.base.MPJBaseMapper;
import com.github.yulichang.query.MPJQueryWrapper;
import io.r2mo.SourceReflect;
import io.r2mo.base.dbe.join.DBNode;
import io.r2mo.base.dbe.join.DBRef;
import io.r2mo.base.util.R2MO;
import io.r2mo.dbe.mybatisplus.JoinProxy;
import io.r2mo.typed.common.Kv;
import io.r2mo.typed.exception.web._501NotSupportException;
import io.r2mo.typed.json.JObject;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * @author lang : 2025-10-23
 */
class OpJoinWriter<T> extends OpJoinPre<T> {
    private final JoinProxy<T> executor;

    OpJoinWriter(final DBRef ref, final JoinProxy<T> executor) {
        super(ref);
        this.executor = executor;

        final Set<Class<?>> entitySet = this.getJoinedEntities();
        final boolean isReady = entitySet.stream().anyMatch(executor::isReady);
        if (!isReady) {
            throw new _501NotSupportException("[ R2MO ] 由于执行器不完整，JOIN 模式的写操作无法执行，请检查是否为所有的实体都配置了对应的执行器！");
        }
    }

    @SuppressWarnings("all")
    public JObject create(final JObject latest) {
        // ------------------ 先插入主键实体 ------------------
        // 查找使用主键做 Join 的实体
        final DBNode found = this.getJoinedPkEntity();
        final Class<?> entity = found.entity();
        // 构造实体对象
        final Object waitFor = this.createUUID(latest, entity);
        // 先插入主键实体
        final MPJBaseMapper mapper = this.executor.mapper(entity);
        final Object created = mapper.insert(waitFor);

        // ------------------ 处理其他关联实体 ------------------
        return null;
    }

    private <R> R createUUID(final JObject latest, final Class<R> entityCls) {
        final R waitFor = R2MO.deserializeJ(latest.data(), entityCls);
        final Kv<String, Object> pk = this.valuePrimary(waitFor, entityCls);
        if (Objects.isNull(pk.value())) {
            SourceReflect.value(waitFor, pk.key(), UUID.randomUUID());
        }
        return waitFor;
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

    /**
     * 查找使用主键做 Join 的实体
     *
     * @return DBNode
     */
    private DBNode getJoinedPkEntity() {
        Kv<String, String> pkInfo = this.getFieldId(this.ref.find());
        if (this.ref.isPrimaryKey(pkInfo)) {
            return this.ref.find(pkInfo.key());
        }
        pkInfo = this.getFieldId(this.ref.findSecond());
        if (this.ref.isPrimaryKey(pkInfo)) {
            return this.ref.find(pkInfo.key());
        }
        // 如果不存在则直接使用主实体
        return this.ref.find();
    }
}
