package io.r2mo.typed.json;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.deser.std.UUIDDeserializer;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.databind.ser.std.UUIDSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.r2mo.base.dbe.Database;
import io.r2mo.spi.SPI;
import io.r2mo.typed.common.Ref;
import io.r2mo.typed.json.jackson.BigNumberSerializer;
import io.r2mo.typed.json.jackson.DatabaseDeserializer;
import io.r2mo.typed.json.jackson.DatabaseSerializer;
import io.r2mo.typed.json.jackson.JArrayDeserializer;
import io.r2mo.typed.json.jackson.JArraySerializer;
import io.r2mo.typed.json.jackson.JObjectDeserializer;
import io.r2mo.typed.json.jackson.JObjectSerializer;
import io.r2mo.typed.json.jackson.MultiLocalDateTimeDeserializer;
import io.r2mo.typed.json.jackson.RefDeserializer;
import io.r2mo.typed.json.jackson.RefSerializer;
import io.r2mo.typed.json.jackson.StringDateDeserializer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 工具类，辅助 JObject 和 JArray 的实现
 *
 * @author lang : 2025-09-07
 */
class JBaseUtil {
    /**
     * Jackson 全局命名与大小写策略说明（为啥要这么配 & 有啥好处） ✨
     * =================================================================
     *
     * 【目标｜我们到底想要什么】🎯
     * <pre>
     * 1) 「大小写敏感」：JSON key 必须与 Java 字段名（或 {@link JsonProperty @JsonProperty} 指定名）精确一致。✅
     * 2) 「以字段为准」：使用“字段名”作为 JSON 名字的唯一来源，不受 getter/setter 推断影响。🔒
     * 3) 「最小约束 & 最稳定行为」：不依赖历史遗留的 Bean 命名规则，避免莫名其妙的大小写变化。🧘
     * 4) 「可逐点特例化」：遇到个别字段需要“固定别名/兼容历史 key”时，用注解就能精准覆盖。🧩
     * </pre>
     *
     * -----------------------------------------------------------------
     * 【配置选择 & 理由】🛠️
     * <pre>
     * ① 关闭大小写不敏感（{@link MapperFeature#ACCEPT_CASE_INSENSITIVE_PROPERTIES} /
     *    {@link MapperFeature#ACCEPT_CASE_INSENSITIVE_ENUMS} /
     *    {@link MapperFeature#ACCEPT_CASE_INSENSITIVE_VALUES} = false）🧵
     *    - 防止属性名大小写被“模糊匹配”后映射到错误字段（多模块/多团队协作时尤甚）。🛡️
     *    - 例：希望字段是 zCreateBy，就只接受 "zCreateBy"，而不是 "zcreateBy"/"ZCreateBy"。✅
     *
     * ② 关闭标准 Bean 命名推断（{@link MapperFeature#USE_STD_BEAN_NAMING} = false）🚦
     *    - 标准推断会综合 getter/setter 命名生成“属性名”，在边界大小写（如 “zC…”）上可能不符合直觉。⚠️
     *    - 关掉它 → 不再受方法名约定影响，避免历史规则或三方库“偷偷”改名。🫥
     *
     * ③ 仅启用“字段可见性”，禁用 getter/setter 可见性 👀
     *    - {@link PropertyAccessor#FIELD} -> {@link JsonAutoDetect.Visibility#ANY}，而
     *      {@link PropertyAccessor#GETTER} /
     *      {@link PropertyAccessor#IS_GETTER} /
     *      {@link PropertyAccessor#SETTER} -> {@link JsonAutoDetect.Visibility#NONE}。📌
     *    - JSON 名字即“字段名本身”（或 {@link JsonProperty @JsonProperty} 指定的名字），不再推断“属性名”。🧷
     *    - 对自动生成或手写的访问器命名细节不再敏感（例如非标准的 getzCreateBy()）。🧯
     *
     * ④ 命名策略设为 {@link PropertyNamingStrategies#LOWER_CAMEL_CASE}（可选但推荐）🐪
     *    - 不会“强改”已是小驼峰的字段名，但可保证整体风格一致（团队统一）。🤝
     *    - 在仅字段可见性的前提下，它主要作为“风格声明”，不会把 zCreateBy 变成 zcreateBy。👍
     *
     * ⑤ {@link JsonProperty @JsonProperty} 的优先级最高（点对点控制）🏷️
     *    - 需要绝对锁死某个字段名：在字段上加 {@link JsonProperty @JsonProperty} 即可。🔒
     *    - 覆盖策略/推断，序列化与反序列化同时生效，是最可靠的“强约束”手段。💯
     * </pre>
     *
     * -----------------------------------------------------------------
     * 【这样做的好处】🌟
     * <pre>
     * 1) 稳定与可预期：字段名“写什么就是什么”，避免大小写边界条件/历史规则带来的“神奇变化”。🧭
     * 2) 降低维护成本：看到字段名 ≈ 知道 JSON key；排查更快，协作更少歧义。⏱️
     * 3) 精准控制：兼容老数据时——
     *      - 用 {@link JsonProperty @JsonProperty} 固定“标准名”；🧲
     *      - 用 {@link JsonAlias @JsonAlias}（如 {@code @JsonAlias({"zcreateBy","ZCreateBy"})}）
     *        容忍旧 key（仅反序列化，不影响输出）。🪄
     * 4) 跨团队/多语言/多模块更友好：大家遵守“字段名即协议名”的简单规则即可。🤗
     * 5) 安全性提升：大小写敏感避免“看似相同但并非同字段”的混淆（zCreateBy vs zcreateBy）。🛡️
     * </pre>
     *
     * -----------------------------------------------------------------
     * 【与其它常见配置的关系】🔗
     * <pre>
     * - {@link DeserializationFeature#FAIL_ON_UNKNOWN_PROPERTIES}（此处保持宽松为 false）🌬️
     *   * 与“大小写敏感”是两回事；保持 false 可忽略未知字段，避免频繁抛错。🙂
     *   * 若要更严格：测试/灰度阶段临时设为 true，或挂 {@link DeserializationProblemHandler}
     *     打日志以观测未知字段。🧪
     *
     * - 自定义模块/反序列化器 🧩
     *   * 避免对 key 做 {@code toLowerCase()}/{@code lowerFirst()} 等处理，否则抵消“大小写敏感 + 字段为准”。🚫
     *   * 如需统一入口做兼容，优先用 {@link JsonAlias @JsonAlias} 或
     *     {@link DeserializationProblemHandler}，而非直接改 key 大小写。💡
     * </pre>
     *
     * -----------------------------------------------------------------
     * 【典型行为举例】📖
     * <pre>
     * 字段：private String zCreateBy;
     * - 接受： {"zCreateBy":"jack"}               ✅ 大小写完全匹配
     * - 忽略： {"zcreateBy":"jack"}               ✅ 被忽略（unknown），是否抛错取决于
     *           {@link DeserializationFeature#FAIL_ON_UNKNOWN_PROPERTIES}
     * - 忽略： {"ZCreateBy":"jack"}               ✅ 同上
     *
     * 若字段上标注：{@link JsonProperty @JsonProperty}("zCreateBy")
     * - 接受：{"zCreateBy":"jack"}                 ✅
     * - 另加：{@link JsonAlias @JsonAlias}({"zcreateBy","ZCreateBy"})
     *         → 反序列化接受别名，但序列化仍输出 "zCreateBy"。🔁
     * </pre>
     *
     * -----------------------------------------------------------------
     * 【实现细节所涉类】🧠
     * <pre>
     * - 全局 Mapper：{@link JsonMapper}
     * - 大小写敏感特性：{@link MapperFeature}
     * - 可见性控制：{@link PropertyAccessor} / {@link JsonAutoDetect.Visibility}
     * - 命名策略：{@link PropertyNamingStrategies}
     * - 反序列化开关：{@link DeserializationFeature}
     * - 序列化开关：{@link SerializationFeature}
     * - JSON 解析特性：{@link JsonParser.Feature}
     * - 空值策略：{@link JsonInclude.Include}
     * - 注解：{@link JsonProperty} / {@link JsonAlias}
     * - 问题处理器：{@link DeserializationProblemHandler}
     * </pre>
     *
     * -----------------------------------------------------------------
     * 【结论】🧷
     * 这组配置建立了“字段名即协议名，大小写敏感”的清晰边界，在不牺牲兼容性的前提下提供极高的确定性。🧱
     * 生产落地时，个别历史字段用 {@link JsonProperty @JsonProperty} /
     * {@link JsonAlias @JsonAlias} 局部覆盖即可，无需引入全局复杂规则。🚀
     */


    private static final JsonMapper MAPPER = JsonMapper.builder()
        // 开启大小写敏感，防止属性混淆
        .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, false)
        .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, false)
        .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_VALUES, false)

        // 关键：不要用标准 Bean 命名推断（会影响大小写）
        .configure(MapperFeature.USE_STD_BEAN_NAMING, false)

        // 关键：只看字段，忽略 getter/setter；字段名就是 JSON 名
        .visibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
        .visibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE)
        .visibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE)
        .visibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE)

        // （可选）确保是小驼峰语义；对已是小驼峰的字段不会改名
        .propertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE)
        .build();

    static {
        // 配置项：忽略空值
        MAPPER.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);

        // Non-standard JSON but we allow C style comments stream our JSON
        // 配置项：允许注释
        MAPPER.configure(JsonParser.Feature.ALLOW_COMMENTS, true);

        // 配置项：关闭日期作为时间戳
        MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        // 启用特性排序 -> 序列化数据相同时结果一致
        MAPPER.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);

        // 配置项：忽略未知的属性
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // Big Decimal
        // 配置项：使用 BigDecimal 替代 float/double
        MAPPER.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);

        final List<Module> modules = jacksonModules();
        MAPPER.registerModules(modules.toArray(new Module[0]));
    }

    static JsonMapper jacksonMapper() {
        return MAPPER;
    }

    static List<Module> jacksonModules() {
        final List<Module> modules = new ArrayList<>();
        // 全局配置序列化返回 JSON 处理
        final JavaTimeModule moduleJavaTime = new JavaTimeModule();
        moduleJavaTime.addSerializer(Long.class, BigNumberSerializer.INSTANCE);
        moduleJavaTime.addSerializer(Long.TYPE, BigNumberSerializer.INSTANCE);
        moduleJavaTime.addSerializer(BigInteger.class, BigNumberSerializer.INSTANCE);
        moduleJavaTime.addSerializer(BigDecimal.class, ToStringSerializer.instance);
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        moduleJavaTime.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));
        // 多格式反序列化
        moduleJavaTime.addDeserializer(LocalDateTime.class, new MultiLocalDateTimeDeserializer());
        moduleJavaTime.addDeserializer(Date.class, new StringDateDeserializer());
        modules.add(moduleJavaTime);


        // 特殊类型
        final SimpleModule moduleJson = new SimpleModule();
        moduleJson.addSerializer(JObject.class, new JObjectSerializer());
        moduleJson.addDeserializer(JObject.class, new JObjectDeserializer());
        moduleJson.addSerializer(JArray.class, new JArraySerializer());
        moduleJson.addDeserializer(JArray.class, new JArrayDeserializer());
        moduleJson.addSerializer(UUID.class, new UUIDSerializer());
        moduleJson.addDeserializer(UUID.class, new UUIDDeserializer());
        moduleJson.addSerializer(Ref.class, new RefSerializer());
        moduleJson.addDeserializer(Ref.class, new RefDeserializer());
        /*
         * 针对 Database 特殊反序列化和序列化，DBE 要使用，虽然不是所有的 DBE 都会用到这里的行为，但
         * 对于需要 DIY 底层处理的过程中，提供 DS / Database 的基本管理，让整个流程变成可控。
         */
        moduleJson.addSerializer(Database.class, new DatabaseSerializer());
        moduleJson.addDeserializer(Database.class, new DatabaseDeserializer());
        modules.add(moduleJson);
        return modules;
    }

    @SuppressWarnings("unchecked")
    static <T extends JBase> T parse(final String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        final String content = json.trim();
        if (content.startsWith("[") && content.endsWith("]")) {
            return (T) SPI.A(json);
        } else if (content.startsWith("{") && content.endsWith("}")) {
            return (T) SPI.J(json);
        } else {
            throw new IllegalStateException("[ R2MO ] 无法解析该 JSON 内容：" + json);
        }
    }
}
