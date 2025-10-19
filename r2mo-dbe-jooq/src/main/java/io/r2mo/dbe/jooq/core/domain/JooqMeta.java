package io.r2mo.dbe.jooq.core.domain;

import cn.hutool.core.util.StrUtil;
import io.r2mo.SourceReflect;
import io.r2mo.base.program.R2Vector;
import io.r2mo.typed.cc.Cc;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Field;
import org.jooq.Table;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 存储元数据信息，将字段转换封装在此处，只有 ZERO 中支持 {@link R2Vector} 的映射信息，此处映射信息会帮助分析
 * <pre>
 *     1. {@link Class} 存储实体类信息
 *     2. {@link R2Vector} 存储字段映射信息
 * </pre>
 *
 * @author lang : 2025-10-18
 */
@Data
@Accessors(fluent = true, chain = true)
@Slf4j
public class JooqMeta {
    private static final Cc<Class<?>, JooqMeta> CC_META = Cc.open();

    @Setter(AccessLevel.NONE)
    private final Class<?> entityCls;
    /**
     * 正常而言，一旦带有 vector 在访问数据库过程中就不可能被拿掉，所以可以将 {@link R2Vector} 存储在 Meta 中使用，
     * 一般是用于迁移，比如配置 pojoFile -> 旧数据库往新数据库做迁移，使用此映射来实现所有操作，直接加载了此处的
     * {@link R2Vector} 之后就可以在 DB 的前置和后置工作做先处理部分事情来实现整体的映射流程，注意此处的对象中
     * <pre>
     *     1. {@link R2Vector#mapByColumn()} 是一定有值
     *        column -> field 映射关系
     *     2. {@link R2Vector#mapToColumn()} 也一定有值
     *        field -> column 映射关系
     * </pre>
     */
    private R2Vector vector = new R2Vector();


    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private JooqKey key;
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private JooqField field;


    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)   // 这个变量是锁定的，不会改动
    private final ConcurrentMap<String, Field<?>> fieldColumn = new ConcurrentHashMap<>();
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)   // 这个变量是会变化的，会随着 vector 的变化而追加
    private final ConcurrentMap<String, Class<?>> fieldType = new ConcurrentHashMap<>();

    public JooqMeta vector(final R2Vector vector) {
        final R2Vector combined = this.vectorCombine(this.vector, vector);
        Optional.ofNullable(this.key).ifPresent(key -> key.vector(combined));
        Optional.ofNullable(this.field).ifPresent(field -> field.vector(combined, this.fieldColumn));
        this.vector = combined;
        return this;
    }

    /**
     * 结合两个 Vector 信息进行合并
     */
    private R2Vector vectorCombine(final R2Vector target, final R2Vector source) {
        Objects.requireNonNull(target, "[ R2MO ] 目标 Vector 信息部可能为空，检查系统！");
        final R2Vector combined = new R2Vector();
        // 先提取 Class<?> 信息
        Class<?> entityCls = target.getType();
        if (Objects.isNull(entityCls)) {
            entityCls = source.getType();
        }
        if (Objects.isNull(entityCls)) {
            entityCls = this.entityCls;
        }
        combined.setType(entityCls);

        // 合并 mapping 信息
        combined.mapping(target.mapTo());
        combined.mapping(source.mapTo(), false);

        // 合并 columnMapping 信息
        combined.mappingColumn(target.mapToColumn());
        combined.mappingColumn(source.mapToColumn(), false);
        return combined;
    }

    private Table<?> table;

    public JooqMeta table(final Table<?> table) {
        if (Objects.isNull(table)) {
            log.warn("[ R2MO ] 尝试设置 JooqMeta 的 Table 信息时传入了空值，忽略此次操作！");
            return this;
        }


        /*
         * 一定要有了 Table 才可以执行此操作
         */
        this.table = table;
        this.key = new JooqKey(table);
        this.field = new JooqField();



        /*
         * 此处的顺序处理很重要，正常模式下生成代码过程中 getDeclaredFields 的顺序和 Table.fields() 的顺序
         * 是一致的，可以直接通过下标进行一一对应的映射关系建立
         */
        final java.lang.reflect.Field[] fields = SourceReflect.fields(this.entityCls);
        final Field<?>[] columns = table.fields();
        final ConcurrentMap<String, String> columnMap = new ConcurrentHashMap<>();
        for (int idx = 0; idx < columns.length; idx++) {
            final Field<?> column = columns[idx];
            final java.lang.reflect.Field field = fields[idx];


            /*
             * 追加映射关系
             * 1）name = Field
             * 2）name = Class<?>
             * 3）field -> column / column -> field
             * 此处这个操作是固定不变的顺序，如果出现了 pojo 的字段则再追加一次形成多对一的模式
             * - field          = ???
             * - fieldAlias     = ???
             */
            this.fieldColumn.put(field.getName(), column);
            this.fieldType.put(field.getName(), field.getType());
            columnMap.put(field.getName(), column.getName());
        }
        this.vector.mappingColumn(columnMap);
        return this;
    }

    private JooqMeta(final Class<?> entityCls) {
        this.entityCls = entityCls;
    }

    public static JooqMeta of(final Class<?> entityCls, final Table<?> table) {
        return CC_META.pick(() -> new JooqMeta(entityCls).table(table), entityCls);
    }

    public TreeSet<String> fieldSet() {
        // mapTo 返回的是 field -> column 的映射关系
        return new TreeSet<>(this.vector.mapToColumn().keySet());
    }

    // 提取
    public ConcurrentMap<String, Class<?>> fieldType() {
        if (Objects.nonNull(this.vector)) {
            /*
             * 延迟填充，避免重复，此处的 typeMap 会包含两种
             * 1）field -> Class<?>
             * 2）column -> Class<?>
             * 所以需要进行二次过滤，根据过滤信息来提取最终的数据信息
             */
            this.fieldColumn.forEach((field, columnField) -> {
                final String columnName = this.vector.mapTo(field);
                if (StrUtil.isNotEmpty(columnName)) {
                    this.fieldType.put(columnName, columnField.getType());
                }
            });
        }
        return this.fieldType;
    }

    // 键操作 -----------------------------------------------------------
    public TreeSet<String> keyPrimaryN() {
        return this.key.pkSet();
    }

    public String keyPrimary() {
        return this.key.pkOne();
    }

    public <T> Object keyPrimary(final T input) {
        return this.key.pkValue(input);
    }

    public <T> List<Object> keyPrimary(final List<T> list) {
        return this.key.pkValue(list);
    }

    public List<TreeSet<String>> keyUniqueL() {
        return this.key.ukList();
    }

    // 列操作 -----------------------------------------------------------
    public ConcurrentMap<String, Field<?>> columns() {
        return this.fieldColumn;
    }

    public Field<?>[] findColumns(final String... fields) {
        return this.field.findColumn(fields);
    }

    public Field<?> findColumn(final String fieldOr) {
        return this.field.findColumn(fieldOr);
    }

    // 此处静态方法，表示已经被缓存，不可以再创建新的 Meta 了
    public static JooqMeta getOr(final Class<?> entityCls) {
        return CC_META.getOrDefault(entityCls, null);
    }
}
