package io.r2mo.dbe.mybatisplus;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.github.yulichang.base.MPJBaseMapper;
import com.github.yulichang.query.MPJQueryWrapper;
import io.r2mo.SourceReflect;
import io.r2mo.base.dbe.join.DBNode;
import io.r2mo.base.dbe.join.DBRef;
import io.r2mo.base.dbe.operation.QrAnalyzer;
import io.r2mo.dbe.common.DBEJ;
import io.r2mo.dbe.mybatisplus.spi.OpJoinAnalyzer;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.common.Kv;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

/**
 * @author lang : 2025-10-23
 */
public class DBJ<T> extends DBEJ<MPJQueryWrapper<T>, T, MPJBaseMapper<T>> {
    private final QrAnalyzer<MPJQueryWrapper<T>> analyzer;

    private static final Cc<String, DBJ<?>> CCT_DBE = Cc.openThread();

    /**
     * 此处的 DBRef 必须是完整的
     *
     * @param ref        DBRef
     * @param baseMapper EXECUTOR
     */
    private DBJ(final DBRef ref, final MPJBaseMapper<T> baseMapper) {
        super(ref, baseMapper);
        this.analyzer = new OpJoinAnalyzer<>(ref);
    }

    @Override
    protected QrAnalyzer<MPJQueryWrapper<T>> analyzer() {
        return this.analyzer;
    }

    public DBJ<T> alias(final String table, final String field, final String alias) {
        this.ref.alias(table, field, alias);
        return this;
    }

    public DBJ<T> alias(final Class<?> entityCls, final String field, final String alias) {
        final TableInfo tableInfo = TableInfoHelper.getTableInfo(entityCls);
        return this.alias(tableInfo.getTableName(), field, alias);
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
    public static <T> DBJ<T> of(final Join meta, final MPJBaseMapper<T> baseMapper) {
        final DBNode leftNode = createNode(meta.from());
        final DBNode rightNode = createNode(meta.to());
        final Kv<String, String> kvJoin = Kv.create(meta.fromField(), meta.toField());
        return of(DBRef.of(leftNode, rightNode, kvJoin), baseMapper);
    }

    private static DBNode createNode(final Class<?> entityCls) {
        final TableInfo tableInfo = TableInfoHelper.getTableInfo(entityCls);
        final DBNode node = DBNode.of().entity(entityCls).table(tableInfo.getTableName()).key(entityCls.getName());
        final Field[] fields = SourceReflect.fieldsN(entityCls);
        Arrays.stream(fields)
            .filter(field -> !Modifier.isStatic(field.getModifiers()))
            .forEach(field -> node.put(field.getName(), field.getType()));
        return node;
    }

    @SuppressWarnings("unchecked")
    public static <T> DBJ<T> of(final DBRef ref, final MPJBaseMapper<T> baseMapper) {
        final String cacheKey = ref.hashCode() + "@" + baseMapper.hashCode();
        return (DBJ<T>) CCT_DBE.pick(() -> new DBJ(ref, baseMapper), cacheKey);
    }
}
