package io.r2mo.dbe.mybatisplus.spi;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import io.r2mo.base.dbe.DBMeta;
import io.r2mo.base.dbe.common.DBLoad;
import io.r2mo.base.dbe.common.DBLoadBase;
import io.r2mo.base.dbe.common.DBNode;
import io.r2mo.base.program.R2Vector;
import io.r2mo.typed.annotation.SPID;
import io.r2mo.typed.common.Kv;

import java.util.Objects;

/**
 * 核心的 Metadata 管理模型，用来填充当前环境中所有的元数据信息，结合 MyBatis-Plus 的 TableInfoHelper 来完成元数据的提取和填充工作，
 * 且一次性将所有信息全部填充完成，而不造成二次提取的低效问题。
 *
 * @author lang : 2025-10-24
 */
@SPID(DBLoad.DEFAULT_SPID_META)
public class LoadMyBatis extends DBLoadBase {

    @Override
    public DBNode configure(final Class<?> entity, final R2Vector vector) {
        /*
         * - dao
         * - vector
         * - entity
         * - field -> Class<?>
         */
        final DBNode node = DBNode.of(entity, vector);
        node.entity(entity);

        this.setupFields(node, entity);
        /*
         * - table
         * - key ( primaryKey = primaryColumn )
         * - field -> column
         * - column -> field
         */
        final TableInfo tableInfo = TableInfoHelper.getTableInfo(entity);
        node.table(tableInfo.getTableName());

        final R2Vector vectorRef = node.vector();
        Objects.requireNonNull(vectorRef, "[ R2MO ] 此处的 vectorRef 一定不会为空，即使没有内容也会包含默认值！");
        // 键值设置
        if (tableInfo.havePK()) {
            final Kv<String, String> pk = Kv.create(tableInfo.getKeyColumn(), tableInfo.getKeyProperty());
            node.key(pk);

            vectorRef.putColumn(tableInfo.getKeyProperty(), tableInfo.getKeyColumn());
        }

        // 字段设置
        tableInfo.getFieldList().forEach(field ->
            vectorRef.putColumn(field.getProperty(), field.getColumn()));
        node.vector(vectorRef);

        
        /*
         * 加载之后注册，注册后使用此处的内容统一管理，可通过三种方法查询对应的 DBNode
         * - entity 实体名称
         * - table  表名称
         * - entityCls 实体类类型 / daoCls 类型也可以
         */
        DBMeta.of().registry(entity, node);
        return node;
    }
}
