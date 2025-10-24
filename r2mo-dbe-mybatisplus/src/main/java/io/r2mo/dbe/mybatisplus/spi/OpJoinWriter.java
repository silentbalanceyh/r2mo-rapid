package io.r2mo.dbe.mybatisplus.spi;

import com.github.yulichang.base.MPJBaseMapper;
import com.github.yulichang.query.MPJQueryWrapper;
import io.r2mo.base.dbe.common.DBFor;
import io.r2mo.base.dbe.common.DBNode;
import io.r2mo.base.dbe.common.DBRef;
import io.r2mo.base.util.R2MO;
import io.r2mo.dbe.mybatisplus.JoinProxy;
import io.r2mo.spi.SPI;
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
    private boolean isReady = false;

    OpJoinWriter(final DBRef ref, final JoinProxy<T> executor) {
        this.ref = ref;
        this.executor = executor;
    }

    private JoinProxy<T> executor() {
        if (this.isReady) {
            return this.executor;
        }
        final Set<Class<?>> entitySet = this.ref.findJoined();
        this.isReady = entitySet.stream().anyMatch(this.executor::isReady);
        if (!this.isReady) {
            throw new _501NotSupportException("[ R2MO ] 由于执行器不完整，JOIN 模式的写操作无法执行，请检查是否为所有的实体都配置了对应的执行器！");
        }
        return this.executor;
    }

    /**
     * 注意，本方法中的 “主实体” 和 “主键实体” 有可能不是相同的实体，这点要开发人员特别注意
     * <pre>
     *     1. 主实体是当前类中定义的 {@link T} 类型实体，它取决于代码调用中的
     *        {@code this.db(Join.of(
     *             OrderItemEntity.class, "orderId",
     *             OrderEntity.class
     *        ))}
     *        此处的 OrderItemEntity 就是主实体，主实体就是 {@link Join} 中的实体 from
     *     2. 但是上述代码中实际 JOIN 语句是
     *        ORDER_ITEM JOIN ORDER ON ( ORDER_ITEM.order_id = ORDER.id )
     *        此处 OrderEntity 才是主键实体，因为它使用了主键做 JOIN 条件
     * </pre>
     * 主实体不会影响增删改的顺序，但主键实体会影响增删改的顺序
     *
     * @param request
     *
     * @return
     */
    @SuppressWarnings("all")
    public JObject create(final JObject request) {

        // ------------------ 先插入主键实体 ------------------
        // 查找使用主键做 Join 的实体
        final DBNode found = this.ref.findPrimary();


        // 主键实体数据交换
        final JObject cloned = request.copy();
        final JObject exchanged = DBFor.ofC(true).exchange(cloned, found, this.ref);


        // 主键实体反序列化和主键设置
        final Object waitFor = R2MO.deserializeJ(exchanged.data(), found.entity());
        if (Objects.isNull(waitFor)) {
            // 反序列化失败
            return SPI.J();
        }


        // 主键实体添加过程中的主键补齐
        final Object pkValue = found.vPrimary(waitFor);
        if (Objects.isNull(pkValue)) {
            // 如果主键类型是 String 内置会自动转换
            found.vPrimary(waitFor, UUID.randomUUID());
        }


        // 插入主键实体
        final MPJBaseMapper mapper = this.executor().mapper(found.entity());
        final Object created = mapper.insert(waitFor);
        // 插入实体之后做一次交换


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
