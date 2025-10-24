package io.r2mo.dbe.mybatisplus.spi;

import com.github.yulichang.base.MPJBaseMapper;
import com.github.yulichang.query.MPJQueryWrapper;
import io.r2mo.base.dbe.common.DBFor;
import io.r2mo.base.dbe.common.DBNode;
import io.r2mo.base.dbe.common.DBRef;
import io.r2mo.base.dbe.common.DBResult;
import io.r2mo.base.util.R2MO;
import io.r2mo.dbe.mybatisplus.JoinProxy;
import io.r2mo.dbe.mybatisplus.core.domain.BaseEntity;
import io.r2mo.typed.exception.web._501NotSupportException;
import io.r2mo.typed.json.JObject;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * @author lang : 2025-10-23
 */
@Slf4j
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
        final DBNode first = this.ref.findPrimary();


        // 主键实体数据交换
        final JObject clonedMajor = request.copy();


        // 主键实体反序列化和主键设置
        final Object waitFor = this.createEntity(clonedMajor, first, null);


        // 插入主键实体
        final MPJBaseMapper mapper = this.executor().mapper(first.entity());
        final int rows = mapper.insert(waitFor);
        // BaseEntity 的特殊处理
        if (waitFor instanceof final BaseEntity waitForClean) {
            waitForClean.setExtension(Map.of());
        }

        // ------------------ 处理连接数据集 ------------------
        final Set<Object> childSet = new HashSet<>();
        this.ref.findByExclude(first.entity()).forEach(standBy -> {
            // 暂时只有一个元素留下
            final Map<String, Object> joinData = this.ref.mapOf(waitFor, standBy);

            // ------------------ 处理其他关联实体 ------------------
            // 辅助实体数据交换
            final JObject clonedMinor = request.copy();
            final Object waitMinor = this.createEntity(clonedMinor, standBy,
                minorJ -> minorJ.put(joinData));

            // 插入辅助实体
            final MPJBaseMapper minorMapper = this.executor().mapper(standBy.entity());
            final int minorRows = minorMapper.insert(waitMinor);

            log.info("[ R2MO ] 主实体 {} / 辅助实体 {}", rows, minorRows);
            if (waitMinor instanceof final BaseEntity waitMinorClean) {
                waitMinorClean.setExtension(Map.of());
            }
            childSet.add(waitMinor);
        });

        // 合并之后的最终结果
        return DBResult.of(this.ref).build(waitFor, childSet, first);
    }

    @SuppressWarnings("unchecked")
    private <R> R createEntity(final JObject requestJ, final DBNode node,
                               final Consumer<JObject> beforeFn) {
        final JObject exchanged = DBFor.ofAlias().exchange(requestJ, node, this.ref);


        if (Objects.nonNull(beforeFn)) {
            beforeFn.accept(exchanged);
        }
        // 主键实体反序列化和主键设置
        final Object waitFor = R2MO.deserializeJ(exchanged.data(), node.entity());
        if (Objects.isNull(waitFor)) {
            // 反序列化失败
            return null;
        }


        // 主键实体添加过程中的主键补齐
        final Object pkValue = node.vPrimary(waitFor);
        if (Objects.isNull(pkValue)) {
            // 如果主键类型是 String 内置会自动转换
            node.vPrimary(waitFor, UUID.randomUUID());
        }
        return (R) waitFor;
    }

    /**
     * 此处的 id 只能是主实体，而不可以是主键实体，因为主键实体可能会面临关联多个实体的情况！-- 直接底层做约束拦截
     *
     * @param id 主实体主键
     *
     * @return 删除结果
     */
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
