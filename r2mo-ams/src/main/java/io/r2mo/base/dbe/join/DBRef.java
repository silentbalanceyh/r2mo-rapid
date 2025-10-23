package io.r2mo.base.dbe.join;

import cn.hutool.core.util.StrUtil;
import io.r2mo.base.program.R2Mapping;
import io.r2mo.typed.common.Kv;
import io.r2mo.typed.common.MultiKeyMap;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * Join 的描述主要包含一套新的结构，和原始结构不放到一起，等价于每个实体都会包含一套 {@link DBRef} 的结构用来做 Join
 * 而 Join 本身是独立的。
 * <pre>
 *     参考 SQL 语句
 * SELECT
 *     emp.employee_id,
 *     emp.name,
 *     dept.department_name
 * FROM
 *     employees AS emp
 * LEFT JOIN
 *     departments AS dept ON emp.department_id = dept.department_id;
 * WHERE
 *     dept.department_name = 'Engineering';
 * </pre>
 *
 * @author lang : 2025-10-22
 */
@Slf4j
public class DBRef implements Serializable {
    private final DBNode left;
    private final DBNode right;
    private int counter = 0;
    /**
     * 别名表，用于保存表对应的别名，别名必须唯一且别名是为了可以直接引导到真实名称
     * <pre>
     *     alias 01 -> field 01 ( A1 )
     *     alias 02 -> field 02 ( A1 )
     *     alias 03 -> field 01 ( A2 )
     * </pre>
     * 对应的 field = Kv ( table(A1) = field )，table 在此处作为跳板可进行中间运算
     */
    private final ConcurrentMap<String, DBAlias> aliasMap = new ConcurrentHashMap<>();
    /**
     * 表别名保存，用于存储表名的别名，当出现了 A AS A1 JOIN B AS B1 时，此处的值对应
     * <pre>
     *     A1 -> A
     *     B1 -> B
     * </pre>
     * 此处的别名处理为：
     * <pre>
     *     A1 -> TL  -> Table Left
     *     A2 -> TR0 -> Table Right 0 -> others[0]
     *     A3 -> TR1 -> Table Right 1 -> others[1]
     * </pre>
     */
    private final R2Mapping prefixMap = new R2Mapping();

    /**
     * 对应存储的值信息
     * <pre>
     *     A1  -> {@link DBNode}
     *     A2 -> {@link DBNode}
     * </pre>
     */
    private final MultiKeyMap<DBNode> tableRef = new MultiKeyMap<>();

    /**
     * 追加一个数据结构用于存储列名 -> 表名的映射关系，这个填充会包含两部分
     * <pre>
     *     1. 实际字段对应表相关信息 column = table（倒排表）
     *     2. 重复字段自动忽略，除非用户定义别名，否则相同字段只返回主表信息
     * </pre>
     */
    private final ConcurrentMap<String, String> columnMap = new ConcurrentHashMap<>();

    /**
     * 根据 {@link DBNode} 的 left / right 来构造 tableRef 存储，方便后期快速定位，通过表名直接提取到和表直接
     * 相关的内容信息，由于类型是 MultiKeyMap，所以此处的映射为：
     * <pre>
     *     className1 = DBNode 01
     *     table1     = DBNode 01
     *     className2 = DBNode 02
     *     table1     = DBNode 02
     * </pre>
     *
     * @param nodeLeft  主实体信息
     * @param nodeRight 辅助实体（一般第二实体）
     * @param waitFor   Join 键值信息
     */
    private DBRef(final DBNode nodeLeft, final DBNode nodeRight,
                  final Kv<String, String> waitFor) {
        this.left = nodeLeft;
        this.right = nodeRight;
        // 填充
        this.tableRef.put(nodeLeft.table(), nodeLeft, nodeLeft.name());
        this.tableRef.put(nodeRight.table(), nodeRight, nodeRight.name());
        // 表名别名处理（自动计算）
        this.prefixMap.setMapping(nodeLeft.table(), "TL");
        this.prefixMap.setMapping(nodeRight.table(), "TR0");
        this.counter++;
        // 设置向量处理
        this.addVector(nodeRight, waitFor);
    }

    public DBRef configure(final String column, final String table) {
        if (StrUtil.isEmpty(column) || StrUtil.isEmpty(table)) {
            return this;
        }
        if (this.columnMap.containsKey(column)) {
            return this;
        }
        this.columnMap.put(column, table);
        return this;
    }

    public DBRef configure(final ConcurrentMap<String, String> columnMap) {
        if (Objects.nonNull(columnMap)) {
            this.columnMap.putAll(columnMap);
        }
        return this;
    }

    private void ifColumnOk() {
        if (this.columnMap.isEmpty()) {
            log.warn("[ R2MO ] 当前列映射表为空，无法进行列名定位操作，请检查！");
        }
    }

    /**
     * 多表 JOIN 的时候，此处的结构如：
     * <pre>
     *     T2
     *        T2 Field 01 = T1 Field 01
     *        T2 Field 02 = T1 Field 02
     *     T3
     *        T3 Field 01 = T1 Field 03
     *        T3 Field 02 = T1 Field 04
     * </pre>
     * 暂时只考虑双表多字段模式
     */
    private final ConcurrentMap<String, Set<Kv<String, String>>> kvMap = new ConcurrentHashMap<>();

    private void addVector(final DBNode joined, final Kv<String, String> waitFor) {
        if (joined == null || waitFor == null) {
            return; // 或者抛异常，看你项目约定
        }
        final String table = joined.table();
        this.kvMap.computeIfAbsent(table, k -> ConcurrentHashMap.newKeySet())
            /*
             * FIX-DBE: 此处需要逆序操作，waitFor 中存储的是 left.xx JOIN right.xx，但是此处应该保存的是
             *
             *          right.table = <right.xxx, left.xxx>
             *          这样保存才能保证后续的多表连接
             *          旧代码：.add(waitFor);
             */
            .add(Kv.create(waitFor.value(), waitFor.key()));
    }

    public DBNode find() {
        this.ifColumnOk();
        return this.left;
    }

    public DBNode findSecond() {
        this.ifColumnOk();
        return this.right;
    }

    public Set<DBNode> findAll() {
        return this.tableRef.values();
    }

    public DBNode find(final String name) {
        this.ifColumnOk();
        return this.tableRef.getOr(name);
    }

    public Set<String> alias() {
        this.ifColumnOk();
        return this.aliasMap.keySet();
    }

    public DBAlias alias(final String alias) {
        this.ifColumnOk();
        return this.aliasMap.get(alias);
    }

    public boolean isAlias(final String column) {
        return this.aliasMap.containsKey(column);
    }

    /**
     * 此处的 Kv /
     *
     * @param pkInfo 主键信息
     *
     * @return 是否主键属性
     */
    public boolean isPrimaryKey(final Kv<String, String> pkInfo) {
        this.ifColumnOk();
        final String table = pkInfo.key();
        if (this.kvMap.containsKey(table)) {
            // 存在则是非主表
            final Set<Kv<String, String>> set = this.kvMap.get(table);
            final Set<String> values = set.stream().map(Kv::key).collect(Collectors.toSet());
            return values.contains(pkInfo.value());
        } else {
            // 不存在一定是主表
            final Set<String> values = this.kvMap.values()
                .stream().flatMap(Collection::stream)
                .map(Kv::value).collect(Collectors.toSet());
            return values.contains(pkInfo.value());
        }
    }

    /**
     * 底层构造模式，唯一的构造方法，每次都创建一个新的
     *
     * @return 引用
     */
    public static DBRef of(final DBNode nodeLeft, final DBNode nodeRight,
                           final Kv<String, String> waitFor) {
        return new DBRef(nodeLeft, nodeRight, waitFor);
    }

    public DBRef alias(final DBAlias alias) {
        this.ifColumnOk();
        if (alias.isOk()) {
            this.aliasMap.put(alias.alias(), alias);
        }
        return this;
    }

    public DBRef alias(final String table, final String name, final String alias) {
        this.ifColumnOk();
        return this.alias(new DBAlias(table, name, alias));
    }

    public DBRef alias(final String fieldExpr, final String alias) {
        this.ifColumnOk();
        final String[] fields = Objects.requireNonNull(fieldExpr).split("\\.");
        if (2 != fields.length) {
            log.warn("[ R2MO ] 参数有问题，无法构造别名记录：{} / {}", fieldExpr, alias);
            return this;
        }
        return this.alias(fields[0], fields[1], alias);
    }

    public DBRef add(final DBNode thirdOr, final Kv<String, String> waitFor) {
        final String prefix = "TR" + this.counter;
        this.prefixMap.setMapping(thirdOr.table(), prefix);
        this.counter++;
        // 设置向量处理
        this.addVector(thirdOr, waitFor);
        return this;
    }

    // ---- 运算专用 API
    public String seekAlias(final Class<?> clazz) {
        this.ifColumnOk();
        final DBNode found = this.tableRef.getOr(clazz.getName());
        if (Objects.isNull(found)) {
            return null;
        }
        // 找到表名，通过表名提取别名
        return this.prefixMap.mapTo(found.table());
    }

    public Class<?> seekType(final String table) {
        this.ifColumnOk();
        final DBNode found = this.tableRef.getOr(table);
        return found.entity();  // 内部判断
    }

    public Class<?> seekTypeByColumn(final String column) {
        final String table = this.columnMap.get(column);
        return this.seekType(table);
    }

    public Set<Kv<String, String>> seekJoin(final Class<?> clazz) {
        this.ifColumnOk();
        final DBNode found = this.tableRef.getOr(clazz.getName());
        if (Objects.isNull(found)) {
            return Set.of();
        }
        // 找到表名，通过表名提取 Join 信息
        return this.kvMap.get(found.table());
    }
}
