package io.r2mo.dbe.jooq.spi;

import io.r2mo.base.dbe.common.DBLoad;
import io.r2mo.base.dbe.common.DBNode;
import io.r2mo.dbe.jooq.core.domain.JooqMeta;
import io.r2mo.typed.cc.Cc;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Table;

import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.Set;

/**
 * Jooq 本身没有元数据加载功能，所以目前只能通过 {@link LoadREF} 来实现元数据的基础加载，Meta 元数据信息可以直接通过 Entity 对应的
 * {@link Class} 的分析来加载实现，Jooq 却无法使用这种方式来加载元数据，这就是特殊性，也是动态 SQL 框架的问题。所以为了兼容 Meta 的
 * 分析，Jooq 使用了下边的步骤来处理
 * <pre>
 *     1. 同步模式 {@link JooqMeta} 构造时 {@link LoadREF} 已经 Ready
 *     2. 异步模式 {@see AsyncJooqMeta} 构造时 {@link LoadREF} 会被初始化 Ready
 *     3. 整体流程如
 *        -> AsyncJooqMeta 构造时：VertxDao -> Entity -> {@link Table} -> 初始化 {@link LoadREF}
 *                         当前环境中存储了：
 *                         >> {@link Class} ( DAO ) = {@link Class} ( Entity )
 *                                                  = {@link Table} ( Table )
 *        -> {@link JooqMeta} 构造时：{@link LoadREF} 已经 Ready，可以直接使用，通过键值 {@link Class} ( Dao ) 来提取相对应
 *                         的 Meta 信息，此时才会调用 {@link DBLoad} 的实现类 {@link LoadJooq} 来完成元数据的首次初始化加载
 *                         的动作，创建真正意义上的 {@link DBNode}
 * </pre>
 * ‼️ Jooq 的特殊性只能通过双阶段加载来完成元数据的处理流程也是源于 Jooq 框架的特殊性
 * <pre>
 *     1. 其他框架可以在对象没有构造的时候直接针对 {@link Annotation} 进行元数据的静态分析。
 *     2. Jooq 框架没有注解，所以只能在对象构造之后才能进行元数据的动态分析，也是此时才可能提取到 {@link Table} 对象。这就是
 *        Jooq 使用双阶段加载的根本原因。
 *     3. 不论同步还是异步，只要保证如下条件就足够了：
 *        - 🍁 调用 {@link DBLoad} 之前保证 {@link LoadREF} 已经 Ready
 *        - 🌷 同步模式（目前没有场景）要额外的机制来保证 {@link LoadREF} 完成注册流程
 *        - 🌷 异步模式 {@see AsyncJooqMeta} 构造时会自动完成注册流程
 *        - 🪼 {@link LoadREF} 中包含的数据结构
 *             Dao {@link Class}     = Entity {@link Class}
 *                                   = {@link Table}
 * </pre>
 *
 * @author lang : 2025-10-25
 */
@Slf4j
public class LoadREF {
    // Dao -> Entity
    private static final Cc<Class<?>, Class<?>> CC_ENTITY = Cc.open();
    // Dao -> Table
    private static final Cc<Class<?>, Table<?>> CC_TABLE = Cc.open();

    // Class<?> -> JooqMeta
    private static final Cc<Class<?>, JooqMeta> CC_META = Cc.open();

    private static final LoadREF INSTANCE = new LoadREF();

    private LoadREF() {
    }

    public static LoadREF of() {
        return INSTANCE;
    }

    public LoadREF registry(final Class<?> daoCls, final Class<?> entityCls, final Table<?> table) {
        if (CC_ENTITY.containsKey(daoCls)) {
            return this;
        }
        CC_ENTITY.put(daoCls, entityCls);
        CC_ENTITY.put(entityCls, daoCls);


        CC_TABLE.put(daoCls, table);
        CC_TABLE.put(entityCls, table);
        log.debug("[ R2MO ] 注册 Jooq 元数据映射：{} ( Dao ) -> {} / {}", daoCls.getName(), entityCls.getName(), table.getName());


        final JooqMeta metadata = JooqMeta.of(entityCls, table);
        CC_META.put(daoCls, metadata);
        return this;
    }

    public JooqMeta loadMeta(final Class<?> daoCls) {
        JooqMeta metadata = CC_META.get(daoCls);
        if (Objects.isNull(metadata)) {
            final Class<?> entityCls = CC_ENTITY.get(daoCls);
            if (Objects.isNull(entityCls)) {
                return null;
            }
            metadata = CC_META.get(entityCls);
        }
        return metadata;
    }

    public Class<?> loadClass(final Class<?> daoOrEntity) {
        return CC_ENTITY.get(daoOrEntity);
    }

    public Table<?> loadTable(final Class<?> daoCls) {
        return CC_TABLE.get(daoCls);
    }

    public void loadVerify(final Set<Class<?>> daoSet) {
        for (final Class<?> daoCls : daoSet) {
            if (!CC_ENTITY.containsKey(daoCls) || !CC_TABLE.containsKey(daoCls)) {
                throw new IllegalStateException("[ R2MO ] Jooq 元数据加载失败，缺少必要的映射，请检查 Dao 类 " + daoCls.getName() + " 是否正确注册！");
            }
        }
    }
}
