package io.r2mo.base.dbe.common;

import io.r2mo.SourceReflect;
import io.r2mo.base.dbe.DBMeta;
import io.r2mo.base.dbe.DBS;
import io.r2mo.base.program.R2Vector;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author lang : 2025-10-24
 */
public abstract class DBLoadBase implements DBLoad {

    protected void setupField(final DBNode node, final Class<?> clazz) {
        Objects.requireNonNull(clazz, "[ DBLoad ] 实体类 Class<?> 不可为 null！");
        final Field[] fields = SourceReflect.fieldsN(clazz);
        Arrays.stream(fields)
            .filter(field -> !Modifier.isStatic(field.getModifiers()))
            .forEach(field -> node.types(field.getName(), field.getType()));
    }

    protected abstract void setupTable(DBNode node, Class<?> entity);

    @Override
    public DBNode configure(final Class<?> entity, final R2Vector vector, final DBS dbs) {
        /*
         * FIX-DBE: 快速截断处理，提取已经存在的 entity，DBNode 本身就是不可变的对象，一旦创建之后
         * 就维持一致性，不再发生变化，所以这种场景下，直接返回已经存在的 DBNode 即可
         */
        final DBNode found = DBMeta.of().findBy(entity);
        if (Objects.nonNull(found)) {
            return found;
        }

        /*
         * - dao
         * - vector
         * - entity
         * - field -> Class<?>
         */
        final DBNode node = DBNode.of(entity, vector);
        node.entity(entity);

        // 字段设置
        this.setupField(node, entity);
        // 列设置
        this.setupTable(node, entity);

        /*
         * 加载之后注册，注册后使用此处的内容统一管理，可通过三种方法查询对应的 DBNode
         * - entity 实体名称
         * - table  表名称
         * - entityCls 实体类类型 / daoCls 类型也可以
         * FIX-DBE: 注意此处的注册是全局注册，后续处理流程中可以考虑直接从此处构建基本的 NODE 信息
         */
        DBMeta.of().registry(entity, node);
        return node;
    }
}
