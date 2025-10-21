package io.r2mo.dbe.common;

import io.r2mo.SourceReflect;
import io.r2mo.typed.annotation.Identifiers;
import io.r2mo.typed.constant.DefaultField;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <p><b>DBETool</b> —— MyBatis-Plus 语境下的 Java 侧 GroupBy 与实体标识条件构造辅助工具。</p>
 *
 * <pre>
 * 🧭 设计动机
 * - 在 MyBatis-Plus 某些场景下（尤其是动态列、跨表、或开启 ONLY_FULL_GROUP_BY 的严格 SQL 模式时）、
 *   无法直接或不便在数据库侧编写「无聚合的 GROUP BY」来获得“分桶后的原始记录列表”。
 * - 本工具提供在 <b>Java 侧</b> 对已查询的实体集合进行分组（grouping）的能力，
 *   以及从实体注解（@Identifiers）提取「复合业务主键/标识条件」的能力，便于后续查询或幂等更新。
 *
 * 📌 使用边界与共识
 * <pre>
 * - ⚠️ 数据库侧 GROUP BY 更适于聚合（SUM/COUNT/AVG...）；若仅需“分桶后的原始行”，推荐 Java 侧分组。
 * - 🧮 大数据量统计请仍尽量落在数据库侧聚合，Java 侧 groupBy 适合中小批量数据或需要内存内进一步加工的场景。
 * - 🧱 @Identifiers 约定用于定义业务标识字段（如 appId/tenantId/enabled 等），便于构造 WHERE 条件。
 * </pre>
 *
 * 🛠️ 典型用法
 * <pre>
 * 1) Java 侧分组（Map&lt;K, List&lt;T&gt;&gt;）：
 *    List&lt;Order&gt; rows = orderMapper.selectList(...);
 *    Map&lt;Long, List&lt;Order&gt;&gt; grouped = DBETool.groupBy(rows, "buyerId", Order.class);
 *
 * 2) 从实体构建查询条件（@Identifiers）：
 *    Order entity = ... // 具有 @Identifiers 注解
 *    Map&lt;String, Object&gt; where = DBETool.getIdentifier(entity);
 *    // where 形如：{ "id": 1001, "appId": "...", "tenantId": "...", "enabled": true }
 * </pre>
 *
 * ⚙️ 性能与并发注意
 * <pre>
 * - 🚦 当前 groupBy 使用 parallelStream() + groupingBy(...)，并发 Collector 会在内部做合并；
 *   对于中小规模集合可接受，若在高并发/超大集合下更建议：
 *     a) 使用串行 stream() + groupingBy(...)（更易诊断）；
 *     b) 使用 groupingByConcurrent(...) 获取 ConcurrentMap（需注意下游收集器是否线程安全）。
 * - 🧰 若分组键计算较重（反射读取），可考虑预先缓存字段访问器或使用方法引用减少反射次数。
 * </pre>
 *
 * ✅ 空值与健壮性
 * <pre>
 * - entities 为空/为 null：建议在调用前做判空，或在工具方法里显式返回空 Map（当前实现未做判空短路）。
 * - field 为 null/错误字段：SourceReflect.value(...) 应抛出或返回 null，调用方应留意 NPE/Key 为 null 的分组桶。
 * </pre>
 *
 * 🧩 与 MyBatis-Plus 的关系
 * <pre>
 * - 这里的 groupBy 完全在内存侧进行，不依赖 MyBatis-Plus 的 Wrapper 语法。
 * - 若你需要 SUM/COUNT 等聚合统计，请优先使用数据库侧 GROUP BY，或编写 XML/@Select 自定义 SQL。
 * </pre>
 *
 * @author lang : 2025-08-28
 */
public class DBETool {

    /**
     * 根据实体类上的 {@link Identifiers} 注解，构造该实体的「业务标识条件」映射（形如 WHERE 条件的 K/V）。
     *
     * <pre>
     * 🧠 适用场景
     * - ✅ 按业务唯一键做查询/更新/幂等判断：以注解元信息统一声明“标识字段”。
     *
     * 🧩 行为说明
     * - 若实体类未声明 @Identifiers：返回 null（调用方可据此回退到主键或其他策略）。
     * - 若声明了 @Identifiers：
     *   1) 读取 identifiers.value() 中列出的字段名，put 到条件 Map；
     *   2) 根据 ifApp/ifTenant/ifEnabled 标志，附加默认字段：
     *      - appId    → {@link DefaultField#APP_ID}
     *      - tenantId → {@link DefaultField#TENANT_ID}
     *      - enabled  → {@link DefaultField#IS_ENABLED}（固定 true）
     *
     * 🧪 示例
     * <pre>
     * // 假设实体 Order 上有：
     * // @Identifiers(value = {"id"}, ifApp = true, ifTenant = true, ifEnabled = true)
     *
     * Map&lt;String, Object&gt; where = DBETool.getIdentifier(order);
     * // 结果可能是：
     * // {
     * //   "id": 1001,
     * //   "appId": "...",
     * //   "tenantId": "...",
     * //   "enabled": true
     * // }
     * </pre>
     *
     * 🧰 健壮性
     * - entity 为 null：返回 null；
     * - 注解存在但某字段值为 null：依然会 put(null)，是否允许由上层 SQL 构造策略决定；
     * - 字段名与实体不匹配：{@code SourceReflect.value(...)} 可能返回 null 或抛异常，调用方可按需捕获。
     * </pre>
     *
     * @param entity 带有 {@link Identifiers} 注解的实体实例
     * @param <T>    实体类型
     *
     * @return 业务标识条件 Map；若无注解或 entity 为 null 返回 null
     */
    @SuppressWarnings("unchecked")
    public static <T> Map<String, Object> getIdentifier(final Object entity) {
        if (Objects.isNull(entity)) {
            return null;
        }
        final Class<T> entityCls = (Class<T>) entity.getClass();
        final Identifiers identifiers = entityCls.getDeclaredAnnotation(Identifiers.class);
        if (Objects.isNull(identifiers)) {
            return null;
        }
        final Map<String, Object> condition = new HashMap<>();
        final String[] fields = identifiers.value();
        final T instance = (T) entity;

        // WHERE field1 = ? AND field2 = ? ...
        for (final String field : fields) {
            condition.put(field, SourceReflect.value(instance, field, entityCls));
        }
        // WHERE appId = ? AND tenantId = ? AND enabled = true
        if (identifiers.ifApp()) {
            condition.put(DefaultField.APP_ID,
                SourceReflect.value(instance, DefaultField.APP_ID, entityCls));
        }
        if (identifiers.ifTenant()) {
            condition.put(DefaultField.TENANT_ID,
                SourceReflect.value(instance, DefaultField.TENANT_ID, entityCls));
        }
        if (identifiers.ifEnabled()) {
            condition.put(DefaultField.IS_ENABLED, true);
        }
        return condition;
    }
}
