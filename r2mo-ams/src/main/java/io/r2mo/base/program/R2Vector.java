package io.r2mo.base.program;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.r2mo.base.io.HStore;
import io.r2mo.function.Fn;
import io.r2mo.spi.SPI;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.exception.web._404NotFoundException;
import io.r2mo.typed.exception.web._501NotSupportException;
import io.r2mo.typed.json.JObject;
import io.r2mo.typed.json.jackson.ClassDeserializer;
import io.r2mo.typed.json.jackson.ClassSerializer;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

/**
 * 🧩 原子部件
 * 用于存储双向映射表，可关联到 Yml 的数据结构上，在处理映射的过程中十分有效，对应的 yml 数据结构如下
 * <pre>
 *     场景一：实体类到 Json 对象的映射
 *     type: {@link Class}                  # 向量的绑定实体类，此处实体类只能有一个，不可以多个
 *     mapping:
 *       field: fieldJson
 *
 *     场景二：数据列到 Json 对象的映射
 *     column:
 *       field: fieldColumn
 *
 * </pre>
 * 详细说明：
 * <pre>
 *     - field          必须是实体类中的字段名称
 *     - fieldColumn    数据库表中的列名
 *     - fieldJson      必须是输入 / 输出 Json 对象中的属性名
 * </pre>
 *
 * @author lang : 2025-10-17
 */
@Data
@Slf4j
public class R2Vector implements Serializable {
    private static final Cc<String, R2Vector> CC_VECTOR = Cc.open();
    private static final HStore STORE = SPI.V_STORE;
    @JsonSerialize(using = ClassSerializer.class)
    @JsonDeserialize(using = ClassDeserializer.class)
    private Class<?> type;
    @JsonIgnore
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private R2Mapping vField = new R2Mapping();
    @JsonIgnore
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private R2Mapping vColumn = new R2Mapping();

    public static R2Vector of(final String mappingFile) {
        if (StrUtil.isEmpty(mappingFile)) {
            throw new _501NotSupportException("[ R2MO ] 映射文件路径不能为空！");
        }
        if (Objects.isNull(STORE)) {
            throw new _404NotFoundException("[ R2MO ] 无法找到存储实现，此操作依赖存储实现！");
        }
        return CC_VECTOR.pick(() -> {
            /* 先从当前目录加载，然后从 ClassPath 加载 */
            final String filename = STORE.pHome(mappingFile);
            JObject data = STORE.inYaml(filename);
            if (Objects.isNull(data)) {
                log.info("[ R2MO ] 尝试从 ClassPath 中加载映射文件：{}", mappingFile);
                final URL url = Thread.currentThread().getContextClassLoader().getResource(filename);
                data = STORE.inYaml(url);
            }
            final JObject mapping = SPI.V_UTIL.valueJObject(data, "mapping");
            final R2Vector vector = SPI.V_UTIL.deserializeJson(data, R2Vector.class);
            final ConcurrentMap<String, String> mapData = new ConcurrentHashMap<>();
            mapping.toMap().forEach((field, fieldJ) -> mapData.put(field, (String) fieldJ));
            vector.mapping(mapData);
            return vector;
        }, mappingFile);
    }

    /**
     * 结合两个 Vector 信息进行合并，但是合并过程不可以将引用切换掉，简单说要更改 target 中的数据才可以，
     * 外层包含了 {@link R2Vector} 对象的引用，如果此方法是创建新的，那么会导致外层对象无法感知到变化
     * <pre>
     *     1. 外层 -> vector ( 引用 1 )
     *     2. 内存 -> vector ( 引用 1 target )
     *     3. 执行 combine(source) 之后，旧版 vector 的引用变成了此处的 combined ( 引用 2 ) 外层
     *        对象依然持有引用 1，无法感知到变化，导致映射关系失效
     *     4. 新版直接更改 target
     * </pre>
     * 自己一定不为空，且此方法会包含副作用更改当前对象对应的值，这个过程中不该变引用信息，直接合并之后得到
     * 新的结构对象。
     *
     * @param source 外层传入的 Vector 信息
     */
    public R2Vector combine(final R2Vector source) {
        if (Objects.isNull(source)) {
            return this;
        }
        Class<?> entityCls = this.getType();
        if (Objects.isNull(entityCls)) {
            entityCls = source.getType();
        }
        if (Objects.isNull(entityCls)) {
            entityCls = this.type;
        }
        // 计算完成后回写
        this.type = entityCls;
        // 合并 mapping
        this.mapping(source.mapTo(), false);
        // 合并 columnMapping
        // FIX-DBE: java.sql.SQLSyntaxErrorException: Unknown column 'code' in 'where clause'
        // this.mappingColumn(source.mapTo(), false);
        // 旧代码有问题，导致 column 映射关系丢失（主要问题是逻辑错误）
        this.mappingColumn(source.mapToColumn(), false);
        return this;
    }

    /**
     * field -> fieldJson
     *
     * @param mapping 映射表
     */
    public void mapping(final ConcurrentMap<String, String> mapping) {
        this.vField.setMapping(mapping, true);
    }

    public void mapping(final ConcurrentMap<String, String> mapping, final boolean isClean) {
        this.vField.setMapping(mapping, isClean);
    }

    /**
     * field -> column
     *
     * @param mapping 映射表
     */
    public void mappingColumn(final ConcurrentMap<String, String> mapping) {
        this.vColumn.setMapping(mapping, true);
    }

    public void mappingColumn(final ConcurrentMap<String, String> mapping, final boolean isClean) {
        this.vColumn.setMapping(mapping, isClean);
    }

    public void putColumn(final String field, final String column) {
        this.vColumn.setMapping(field, column);
    }

    public void put(final String field, final String fieldJson) {
        this.vField.setMapping(field, fieldJson);
    }

    /**
     * {@link Class} 定义中的字段 -> 输出 Json 对象中的属性
     * <pre>
     *     1. 字段在 `Pojo` 类中声明
     *     2. 输出属性未声明
     *
     *     📌 示例:
     *     zName -> name
     *     zCode -> code
     * </pre>
     *
     * @return 映射表
     *
     */
    public ConcurrentMap<String, String> mapTo() {
        return this.vField.mapTo();
    }

    public String mapTo(final String field) {
        // 若没有映射关系则返回原始 field
        return this.vField.mapTo(field);
    }

    public void mapTo(final BiPredicate<String, String> kvFn,
                      final BiConsumer<String, String> entryFn) {
        this.vField.mapTo().forEach((field, fieldJson) -> {
            if (kvFn.test(field, fieldJson)) {
                Fn.jvmAt(() -> entryFn.accept(field, fieldJson));
            }
        });
    }

    public void mapTo(final BiConsumer<String, String> entryFn) {
        this.mapTo((k, v) -> true, entryFn);
    }

    /**
     * 输出 Json 对象中的属性 -> {@link Class} 定义中的字段
     * <pre>
     *     与 `toJ` 相反，不获取
     * </pre>
     *
     * @return 逆向映射表
     */
    public ConcurrentMap<String, String> mapBy() {
        return this.vField.mapBy();
    }

    public String mapBy(final String fieldJson) {
        // 若没有映射关系则返回原始 fieldJson
        return this.vField.mapBy(fieldJson);
    }

    public void mapBy(final BiPredicate<String, String> kvFn,
                      final BiConsumer<String, String> entryFn) {
        this.vField.mapBy().forEach((fieldJson, field) -> {
            if (kvFn.test(fieldJson, field)) {
                Fn.jvmAt(() -> entryFn.accept(fieldJson, field));
            }
        });
    }

    public void mapBy(final BiConsumer<String, String> entryFn) {
        this.mapBy((k, v) -> true, entryFn);
    }

    // 读取数据，读数据
    public ConcurrentMap<String, String> mapToColumn() {
        return this.vColumn.mapTo();
    }

    public String mapToColumn(final String field) {
        return this.vColumn.mapTo(field, null);
    }

    public ConcurrentMap<String, String> mapByColumn() {
        return this.vColumn.mapBy();
    }

    public String mapByColumn(final String column) {
        return this.vColumn.mapBy(column, null);
    }

    public boolean hasMapping() {
        return !this.vField.mapTo().isEmpty();
    }
}
