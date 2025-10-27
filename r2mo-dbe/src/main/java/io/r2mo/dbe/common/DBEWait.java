package io.r2mo.dbe.common;

import io.r2mo.base.dbe.common.DBFor;
import io.r2mo.base.dbe.common.DBNode;
import io.r2mo.base.dbe.common.DBRef;
import io.r2mo.base.program.R2Vector;
import io.r2mo.base.util.R2MO;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.json.JObject;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * @author lang : 2025-10-27
 */
@Slf4j
public class DBEWait {
    private final static Cc<String, DBEWait> CCT_DBE_ACTION = Cc.openThread();
    private final DBRef ref;

    private DBEWait(final DBRef ref) {
        this.ref = ref;
    }

    public static DBEWait of(final DBRef ref) {
        return CCT_DBE_ACTION.pick(() -> new DBEWait(ref), String.valueOf(ref));
    }

    public <R> R build(final JObject request, final DBNode node) {
        return this.buildEntity(request, node, waitJ -> {
            // 映射层
            final R2Vector vector = node.vector();
            if (vector.hasMapping()) {
                vector.mapBy((fieldJson, field) -> waitJ.put(field, request.get(fieldJson)));
            }
        });
    }

    public <R> R build(final JObject request, final DBNode node, final Object waitFor) {
        return this.buildEntity(request, node, waitJ -> {
            // 映射层
            final R2Vector vector = node.vector();
            if (vector.hasMapping()) {
                vector.mapBy((fieldJson, field) -> waitJ.put(field, request.get(fieldJson)));
            }
            // 暂时只有一个元素留下
            final Map<String, Object> joinData = this.ref.mapOf(waitFor, node);
            waitJ.put(joinData);
        });
    }

    @SuppressWarnings("unchecked")
    private <R> R buildEntity(final JObject requestJ, final DBNode node,
                              final Consumer<JObject> beforeFn) {
        // - 先执行 R2Vector 的映射处理，此处做混合处理保证全字段和拷贝
        final R2Vector vector = node.vector();
        final JObject waitJ = requestJ.copy();

        if (Objects.nonNull(beforeFn)) {
            beforeFn.accept(waitJ);
        }

        // - 执行别名操作
        final JObject exchanged = DBFor.ofAlias().exchange(waitJ, node, this.ref);
        // 主键实体反序列化和主键设置
        final Object waitFor = R2MO.deserializeJ(exchanged.data(), node.entity());
        if (Objects.isNull(waitFor)) {
            // 反序列化失败
            log.warn("[ R2MO ] 主键实体反序列化失败，实体类：{}，数据：{}", node.entity().getName(), exchanged.encode());
            return null;
        }


        // - 提取主键属性
        final Object pkValue = node.vPrimary(waitFor);
        if (Objects.isNull(pkValue)) {
            // 如果主键是 String 类型会自动转换
            node.vPrimary(waitFor, UUID.randomUUID());
            // FIX-DBE: 消费一次就删除，防止子表和主表同主键
            requestJ.remove(node.key().value());
            if (vector.hasMapping()) {
                final String pkJson = vector.mapTo(node.key().value());
                requestJ.remove(pkJson);
            }
        }
        return (R) waitFor;
    }
}
