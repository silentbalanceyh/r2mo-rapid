package io.r2mo.dbe.mybatisplus.spi;

import com.github.yulichang.query.MPJQueryWrapper;
import io.r2mo.base.dbe.join.DBNode;
import io.r2mo.base.dbe.join.DBRef;
import io.r2mo.dbe.mybatisplus.JoinProxy;
import io.r2mo.typed.common.Kv;
import io.r2mo.typed.exception.web._501NotSupportException;
import io.r2mo.typed.json.JObject;

import java.io.Serializable;
import java.util.Set;

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

    public JObject create(final JObject latest) {

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

    /**
     * 查找使用主键做 Join 的实体
     *
     * @return DBNode
     */
    private DBNode joinByPrimaryKey() {
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
