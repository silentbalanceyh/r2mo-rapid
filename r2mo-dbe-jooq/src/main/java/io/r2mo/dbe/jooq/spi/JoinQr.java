package io.r2mo.dbe.jooq.spi;

import io.r2mo.base.dbe.common.DBNode;
import io.r2mo.base.dbe.common.DBRef;
import io.r2mo.base.dbe.syntax.QBranch;
import io.r2mo.base.dbe.syntax.QLeaf;
import io.r2mo.base.dbe.syntax.QNode;
import io.r2mo.dbe.jooq.core.domain.JooqMeta;
import io.r2mo.typed.exception.web._501NotSupportException;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableOnConditionStep;
import org.jooq.impl.DSL;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author lang : 2025-10-26
 */
class JoinQr {

    static Condition whereTree(final QNode node, final Condition query,
                               final Function<QLeaf, String> columnFn,
                               final Function<String, String> prefixFn) {
        if (node instanceof final QBranch branch) {

        } else if (node instanceof final QLeaf leaf) {

        }
        throw new _501NotSupportException("[ R2MO ] 节点类型不支持：" + node);
    }

    /**
     * SELECT A AS A1 JOIN B AS B2 ON A1.field1 = B2.field2
     *
     * @param ref 引用
     *
     * @return 连接专用步骤
     */
    @SuppressWarnings("all")
    static TableOnConditionStep<Record> buildJoin(final DBRef ref) {
        final DBNode major = ref.find();
        final String majorAlias = ref.seekAlias(major.entity());

        final DBNode minor = ref.findSecond();
        final String minorAlias = ref.seekAlias(minor.entity());


        final JooqMeta fromMeta = LoadREF.of().loadMeta(major.dao());
        final JooqMeta toMeta = LoadREF.of().loadMeta(minor.dao());
        final Table<Record> from = (Table<Record>) fromMeta.table().as(DSL.name(majorAlias));
        final Table<Record> to = (Table<Record>) toMeta.table().as(DSL.name(minorAlias));


        /*
         * 此处提取的只是 seekJoinOn 配置对应的字段信息，但没有计算方向相关信息，所以此处要计算方向，还是要依靠
         * DBRef 的 seekJoinOn 方法来提取正确的字段映射关系，否则会因为字段提取的信息不对导致 JOIN 失败的问题。
         */
        final List<Condition> conditions = new ArrayList<>();
        ref.seekJoinOn(minor.entity()).forEach(kv -> {
            final String toColumn = kv.key();
            final String fromColumn = kv.value();

            // 提取
            final Field fromField = fromMeta.findColumn(fromColumn);
            final Field toField = toMeta.findColumn(toColumn);

            final Field fromFinal = from.field(fromField.getQualifiedName());
            final Field toFinal = to.field(toField.getQualifiedName());
            conditions.add(fromFinal.eq(toFinal));
        });
        if (conditions.isEmpty()) {
            throw new _501NotSupportException("[ R2MO ] 无法构建 JOIN 条件，请检查 DBRef 配置：" + ref);
        }
        return from.leftJoin(to).on(DSL.and(conditions));
    }
}
