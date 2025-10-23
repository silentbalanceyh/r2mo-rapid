package io.r2mo.dbe.mybatisplus;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.github.yulichang.base.MPJBaseMapper;
import com.github.yulichang.query.MPJQueryWrapper;
import io.r2mo.base.dbe.join.DBNode;
import io.r2mo.base.dbe.join.DBRef;
import io.r2mo.base.dbe.operation.QrAnalyzer;
import io.r2mo.dbe.common.DBEJ;
import io.r2mo.dbe.mybatisplus.spi.OpJoinAnalyzer;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.common.Kv;

/**
 * @author lang : 2025-10-23
 */
public class DBJ extends DBEJ<MPJQueryWrapper<?>, MPJBaseMapper<?>> {
    private final QrAnalyzer<MPJQueryWrapper<?>> analyzer;

    private static final Cc<String, DBJ> CCT_DBE = Cc.openThread();

    /**
     * 此处的 DBRef 必须是完整的
     *
     * @param ref        DBRef
     * @param baseMapper EXECUTOR
     */
    private DBJ(final DBRef ref, final MPJBaseMapper<?> baseMapper) {
        super(ref, baseMapper);
        this.analyzer = new OpJoinAnalyzer(ref);
    }

    @Override
    protected QrAnalyzer<MPJQueryWrapper<?>> analyzer() {
        return this.analyzer;
    }

    /**
     * 常见构造，可直接使用
     * <pre>
     *     DBJ.of(Join.of(
     *          classFrom, from,
     *          classTo, to,
     *     )).xxxMethod(???)
     * </pre>
     *
     * @param meta       JOIN 元信息
     * @param baseMapper EXECUTOR
     *
     * @return 当前引用
     */
    public static DBJ of(final Join meta, final MPJBaseMapper<?> baseMapper) {
        final DBNode leftNode = createNode(meta.from());
        final DBNode rightNode = createNode(meta.to());
        final Kv<String, String> kvJoin = Kv.create(meta.fromField(), meta.toField());
        return of(DBRef.of(leftNode, rightNode, kvJoin), baseMapper);
    }

    private static DBNode createNode(final Class<?> entityCls) {
        final TableInfo tableInfo = TableInfoHelper.getTableInfo(entityCls);
        return DBNode.of().entity(entityCls)
            .table(tableInfo.getTableName())
            .key(entityCls.getName());
    }

    public static DBJ of(final DBRef ref, final MPJBaseMapper<?> baseMapper) {
        final String cacheKey = ref.hashCode() + "@" + baseMapper.hashCode();
        return CCT_DBE.pick(() -> new DBJ(ref, baseMapper), cacheKey);
    }
}
