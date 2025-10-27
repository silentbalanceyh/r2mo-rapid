package io.r2mo.dbe.jooq.spi;

import io.r2mo.base.dbe.common.DBFor;
import io.r2mo.base.dbe.common.DBNode;
import io.r2mo.base.dbe.common.DBRef;
import io.r2mo.base.dbe.common.DBResult;
import io.r2mo.dbe.common.DBEWait;
import io.r2mo.dbe.jooq.DBE;
import io.r2mo.typed.json.JObject;
import org.jooq.DSLContext;

import java.util.HashSet;
import java.util.Set;

/**
 * 写入操作的实现
 *
 * @author lang : 2025-10-27
 */
@SuppressWarnings("all")
class OpJoinWriter {
    private final DBRef ref;
    private final DSLContext context;

    OpJoinWriter(final DBRef ref, final DSLContext context) {
        this.ref = ref;
        this.context = context;
    }

    public JObject create(final JObject request) {

        // --------------- 先插入主键实体 -----------------
        // 查找使用主键做 Join 的实体
        final DBNode first = this.ref.findPrimary();


        // 主键实体反序列化和主键设置
        final Object waitFor = DBEWait.of(this.ref).build(request, first);


        // 插入主键实体
        final DBE dbe = DBE.of(first.entity(), this.context);
        final Object waitDone = dbe.create(waitFor);


        final Set<Object> childSet = new HashSet<>();
        this.ref.findByExclude(first.entity()).forEach(standBy -> {
            // --------------- 处理其他关联实体 -----------------
            // 辅助实体数据交换
            final Object waitMinor = DBEWait.of(this.ref).build(request, standBy, waitDone);

            // 插入辅助实体
            final DBE minorDbe = DBE.of(standBy.entity(), this.context);
            final Object minorDone = minorDbe.create(waitMinor);

            childSet.add(minorDone);
        });
        return DBResult.of(this.ref).build(waitFor, childSet, first, LoadREF.of()::loadClass);
    }

    public Boolean removeBy(final JObject stored) {
        final DBNode first = this.ref.findPrimary();
        this.ref.findByExclude(first.entity()).forEach(standBy ->
            this.removeBy(stored, standBy));

        this.removeBy(stored, first);
        return true;
    }

    private void removeBy(final JObject removedJ, final DBNode node) {
        final DBE dbe = DBE.of(node.entity(), this.context);
        final JObject condition = DBFor.ofRemove().exchange(removedJ, node, this.ref);
        dbe.removeBy(condition.toMap());
    }

    public JObject update(final JObject updatedJ) {
        final DBNode first = this.ref.findPrimary();


        // 主键实体反序列化和主键设置
        final Object waitFor = DBEWait.of(this.ref).build(updatedJ, first);
        final DBE dbe = DBE.of(first.entity(), this.context);
        final Object waitDone = dbe.update(waitFor);

        final Set<Object> childSet = new HashSet<>();
        this.ref.findByExclude(first.entity()).forEach(standBy -> {
            // 辅助实体数据交换
            final Object waitMinor = DBEWait.of(this.ref).build(updatedJ, standBy, waitDone);
            final DBE minorDbe = DBE.of(standBy.entity(), this.context);
            final Object minorDone = minorDbe.update(waitMinor);
            childSet.add(minorDone);
        });
        return DBResult.of(this.ref).build(waitFor, childSet, first, LoadREF.of()::loadClass);
    }
}
