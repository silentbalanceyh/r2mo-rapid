package io.r2mo.base.dbe.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.r2mo.base.program.R2Mapping;
import io.r2mo.base.util.R2MO;
import io.r2mo.typed.common.Kv;
import io.r2mo.typed.common.MultiKeyMap;
import io.r2mo.typed.exception.web._400BadRequestException;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
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
    private final Set<Class<?>> registrySet = new HashSet<>();
    private int counter = 0;
    /**
     * 别名表，用于保存表对应的别名，别名必须唯一且别名是为了可以直接引导到真实名称
     * <pre>
     *     概念结构
     *     findAlias 01 -> field 01 ( A1 )
     *     findAlias 02 -> field 02 ( A1 )
     *     findAlias 03 -> field 01 ( A2 )
     *     实际数据结构
     *     findAlias 01 -> DBAlias ( table = A, name = field 01, findAlias = findAlias 01 )
     *     findAlias 02 -> DBAlias ( table = A, name = field 02, findAlias = findAlias 02 )
     *     ...
     * </pre>
     * 对应的 field = Kv ( table(A1) = field )，table 在此处作为跳板可进行中间运算，此处的 {@link DBAlias} 的结构如：
     * <pre>
     *     key = 别名信息
     *           注意在查询过程中，别名也同样位于 SELECT *,T?.{ALIAS} FROM 中的 {ALIAS} 位置，目前的设计中，一旦定义了
     *           别名，别名名称和字段名保持一致，省略掉透过别名去检索字段的过程，数据库 SQL 语句中使用什么名称做列名，则查询
     *           最终结果里就使用什么名字做列名返回给用户，如果别名和实际字段名有冲突会抛出 400 的异常信息！
     *     {@link DBAlias} 中的信息
     *           - table / 表名
     *           - name  / 字段名（实体属性名）
     *           - findAlias / 别名
     * </pre>
     * 且还有一个特殊逻辑：别名通常无法直接在实体中扫描到，所以在响应方法时，几乎可以使用：⚠️ 无法查找的字段直接用别名返回！！！
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
     * 追加一个数据结构用于存储列名 -> 表名的映射关系，这个填充会包含两部分
     * <pre>
     *     1. 实际字段对应表相关信息 column = table（倒排表）
     *     2. 重复字段自动忽略，除非用户定义别名，否则相同字段只返回主表信息
     * </pre>
     */
    private final ConcurrentMap<String, String> columnVector = new ConcurrentHashMap<>();


    /**
     * 此处的 joined 是非全局变量，用于存储当前 DBRef 中已经连接的节点信息，方便后续处理，多表连接的时候之中会限定
     * 对应的连接内容，不允许出现从中全局提取的情况，所以此处的 joined 结构用于存储已经连接的节点信息。
     */
    private final MultiKeyMap<DBNode> joined = new MultiKeyMap<>();

    /**
     * 根据 {@link DBNode} 的 left / right 来构造 tableRef 存储，方便后期快速定位，通过表名直接提取到和表直接
     * 相关的内容信息，由于类型是 MultiKeyMap，所以此处的映射为：
     * <pre>
     *     className1 = DBNode 01
     *     table1     = DBNode 01
     *     className2 = DBNode 02
     *     table1     = DBNode 02
     * </pre>
     * 此处构造时有几个东西是必须存在的 /
     * <pre>
     *     1. DBNode
     *        - 第一实体（主实体）/ left
     *        - 第二实体（辅助实体）/ right
     *     2. Join 键值对
     *     3. DBNode 中
     *        - table
     *        - entity 或 dao
     *        - types（属性类型映射表）
     *        - key
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
        // 表名别名处理（自动计算）
        this.prefixMap.setMapping(nodeLeft.table(), "TL");
        this.prefixMap.setMapping(nodeRight.table(), "TR0");
        this.counter++;

        this.registrySet.add(nodeLeft.entity());
        this.registrySet.add(nodeRight.entity());

        // 列 -> 表的映射关系填充
        nodeLeft.vector().mapByColumn().forEach((column, field) ->
            this.columnVector.put(column, nodeLeft.table()));
        nodeRight.vector().mapByColumn().forEach((column, field) ->
            this.columnVector.put(column, nodeRight.table()));

        // 填充当前内容
        this.joined.put(nodeLeft.name(), nodeLeft, nodeLeft.table());
        this.joined.put(nodeRight.name(), nodeRight, nodeRight.table());

        // 设置向量处理
        this.addVector(nodeRight, waitFor);
    }

    // -------------------------- （设置）配置当前 DBRef 信息 --------------------------

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

    public static DBRef of(final DBNode nodeLeft, final DBNode nodeRight,
                           final Kv<String, String> waitFor) {
        return new DBRef(nodeLeft, nodeRight, waitFor);
    }


    /**
     * 别名和主要属性名的冲突检查，此处用来检查定义的别名是否和主字段名相同，如果出现相同那么别名会直接抛出异常，这样可以防止用户在定义别名过程中
     * 随意而为。别名设置在构造函数之后，所以此处已经确定 DBRef 中大部分元数据信息已经有了，特别是 {@link Class} 的实际类定义信息。别名的重载
     * 方法在此处最终都会调用此方法注册对应别名，验证只需此处即可
     * <pre>
     *     1. 验证 alias 别名是否和 {@link Class} 中的字段名冲突，检查时
     *        - 优先考虑 {@link JsonProperty} 中的名字冲突
     *        - 其次考虑属性名冲突
     *     2. 别名中不可以包含 -,. 等特殊符号的操作符，必须是可用的合法 Java 标识符
     * </pre>
     *
     * @param alias 别名对象
     *
     * @return Fluent 方法
     */
    public DBRef alias(final DBAlias alias) {
        if (alias.isOk()) {
            this.aliasMap.put(alias.alias(), alias);
        }
        return this;
    }

    public DBRef alias(final String table, final String name, final String alias) {
        return this.alias(new DBAlias(table, name, alias));
    }

    public DBRef alias(final String fieldExpr, final String alias) {
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


    // -------------------------- （检查）检查专用的 API 信息 --------------------------

    public boolean isAliasConflict(final DBAlias alias) {
        if (!R2MO.isNamedJava(alias.alias()) || !R2MO.isNamedSQL(alias.alias())) {
            // Java / SQL 标识符检查，冲突
            return true;
        }

        final String table = alias.table();
        final DBNode found = this.findBy(table);
        if (Objects.isNull(found)) {
            // 无法找到对应实体，冲突
            return true;
        }
        final Class<?> entity = found.entity();
        if (Objects.isNull(entity)) {
            // 无法找到 Class 类名，冲突
            return true;
        }
        return Arrays.stream(entity.getDeclaredFields())
            .filter(field -> !Modifier.isStatic(field.getModifiers()))  // 实例变量检查
            .anyMatch(field -> {
                if (field.getName().equals(alias.alias())) {
                    // 别名和属性名冲突，直接 Conflict
                    return true;
                }
                final JsonProperty jProperty = field.getDeclaredAnnotation(JsonProperty.class);
                if (Objects.nonNull(jProperty)) {
                    // 别名和属性名不同，但 jProperty 名称冲突
                    return alias.alias().equals(jProperty.value());
                }
                return false; // 无冲突
            });
    }


    public boolean isAlias(final String column) {
        return this.aliasMap.containsKey(column);
    }

    private boolean isPrimary(final Kv<String, String> pkInfo) {
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

    // -------------------------- （查询）提取当前 DBRef 信息 --------------------------
    public DBNode find() {
        return this.left;
    }

    public DBNode findSecond() {
        return this.right;
    }

    public DBNode findBy(final String name) {
        return this.joined.getOr(name);
    }

    public DBNode findBy(final Class<?> entity) {
        return this.joined.getOr(entity.getName());
    }

    /**
     * 查找使用主键做 Join 的核心实体
     *
     * @return DBNode
     */
    public DBNode findPrimary() {
        // table -> property
        Kv<String, String> vId = this.left.vId();
        if (this.isPrimary(vId)) {
            return this.left;
        }
        vId = this.right.vId();
        if (this.isPrimary(vId)) {
            return this.right;
        }
        // 如果都找不到返回主实体
        return this.left;
    }

    public Set<String> findAlias() {
        return this.aliasMap.keySet();
    }

    public DBAlias findAlias(final String alias) {
        return this.aliasMap.get(alias);
    }

    public Set<Class<?>> findJoined() {
        return this.joined.values().stream()
            .map(DBNode::entity)
            .collect(Collectors.toSet());
    }

    // ---- 运算专用 API

    /**
     * 逆向查找，通过属性名找列信息
     * <pre>
     *     1. 列中包含了表别名，出现在 SQL 语句中
     *     2. 属性名 -> 列名
     * </pre>
     *
     * @param field 属性名
     *
     * @return 表别名 + 列名
     */
    public String seekColumn(final String field) {
        // 优先考虑主实体
        final String c1 = this.left.vColumn(field);
        if (Objects.nonNull(c1)) {
            final String tableAlias = this.seekAlias(this.left.entity());
            return tableAlias + "." + c1;
        }

        // 其他实体（第一个匹配即返回）
        for (final Class<?> other : this.registrySet) {
            if (other == this.left.entity()) {
                // 滤掉主实体
                continue;
            }
            final String c2 = this.findBy(other).vColumn(field);
            if (Objects.nonNull(c2)) {
                final String tableAlias = this.seekAlias(other);
                return tableAlias + "." + c2;
            }
        }

        // 都找不到
        throw new _400BadRequestException("[ R2MO ] 无法识别的查询字段: " + field);
    }

    public String seekAlias(final Class<?> clazz) {
        final DBNode found = this.findBy(clazz);
        if (Objects.isNull(found)) {
            return null;
        }
        // 找到表名，通过表名提取别名
        return this.prefixMap.mapTo(found.table());
    }

    public Class<?> seekType(final String table) {
        final DBNode found = this.findBy(table);
        return found.entity();  // 内部判断
    }

    public Class<?> seekTypeByColumn(final String column) {
        final String table = this.columnVector.get(column);
        return this.seekType(table);
    }

    public Set<Kv<String, String>> seekJoinOn(final Class<?> clazz) {
        final DBNode found = this.findBy(clazz);
        if (Objects.isNull(found)) {
            return Set.of();
        }
        // 找到表名，通过表名提取 Join 信息
        return this.kvMap.get(found.table());
    }
}
