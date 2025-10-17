package io.r2mo.base.program;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.r2mo.base.io.HStore;
import io.r2mo.function.Fn;
import io.r2mo.spi.SPI;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.common.Kv;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
 *       fieldT: fieldJ
 *
 *     场景二：数据列到 Json 对象的映射
 *     column:
 *       fieldD: fieldJ
 *
 * </pre>
 * 详细说明：
 * <pre>
 *     - fieldT 必须是实体类中的字段名称
 *     - fieldD 数据库表中的列名
 *     - fieldJ 必须是输入 / 输出 Json 对象中的属性名
 * </pre>
 *
 * @author lang : 2025-10-17
 */
@Data
@Slf4j
public class R2Vector implements Serializable {
    @JsonSerialize(using = ClassSerializer.class)
    @JsonDeserialize(using = ClassDeserializer.class)
    private Class<?> type;

    /**
     * 由于可以重复，所以此处必须是这种结构才能真正被使用起来，此处的 mapping 是基础映射表，里面包含了 key = value 的基本映射关系，
     * 如果 key 重复则直接存放在另外的变量中！序列化时只考虑 mapping 变量！
     */
    private final ConcurrentMap<String, String> mapping = new ConcurrentHashMap<>();

    public void setMapping(final ConcurrentMap<String, String> mapping) {
        if (Objects.isNull(mapping)) {
            return;
        }
        this.mapping.clear();
        this.revert.clear();

        for (final Map.Entry<String, String> entry : mapping.entrySet()) {
            final String k = entry.getKey();
            final String v = entry.getValue();
            this.mapping.put(k, v);         // 正向填充
            if (Objects.isNull(v)) {
                continue;
            }
            if (this.revert.containsKey(k)) {
                // 扩展填充
                this.extension.add(Kv.create(k, v));
            }
            this.revert.put(v, k);      // 逆向填充
        }
    }

    @JsonIgnore
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private final ConcurrentMap<String, String> revert = new ConcurrentHashMap<>();

    @JsonIgnore
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private final ConcurrentMap<String, String> columnMapping = new ConcurrentHashMap<>();

    @JsonIgnore
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private final ConcurrentMap<String, String> columnRevert = new ConcurrentHashMap<>();

    @JsonIgnore
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private final List<Kv<String, String>> extension = new ArrayList<>();

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
                final URL url = Thread.currentThread().getContextClassLoader().getResource(filename);
                data = STORE.inYaml(url);
            }
            return SPI.V_UTIL.deserializeJson(data, R2Vector.class);
        }, mappingFile);
    }

    private static final Cc<String, R2Vector> CC_VECTOR = Cc.open();
    private static final HStore STORE = SPI.V_STORE;

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
        return this.mapping;
    }

    public String mapTo(final String key) {
        // 若没有映射关系则返回原始 key
        return this.mapping.getOrDefault(key, key);
    }

    public void mapTo(final BiPredicate<String, String> kvFn,
                      final BiConsumer<String, String> entryFn) {
        this.mapping.forEach((in, out) -> {
            if (kvFn.test(in, out)) {
                Fn.jvmAt(() -> entryFn.accept(in, out));
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
        return this.revert;
    }

    public String mapBy(final String key) {
        // 若没有映射关系则返回原始 key
        return this.revert.getOrDefault(key, key);
    }

    public void mapBy(final BiPredicate<String, String> kvFn,
                      final BiConsumer<String, String> entryFn) {
        this.revert.forEach((in, out) -> {
            if (kvFn.test(in, out)) {
                Fn.jvmAt(() -> entryFn.accept(in, out));
            }
        });
    }

    public void mapBy(final BiConsumer<String, String> entryFn) {
        this.mapBy((k, v) -> true, entryFn);
    }

    // --------------------- 数据库相关的操作 ----------------------
    // 绑定数据，写数据
    public R2Vector stored(final JObject data) {
        if (Objects.isNull(data)) {
            log.warn("[ R2MO ] 存储数据列时传入的 JObject 为空，跳过处理");
            return this;
        }
        final Map<String, Object> mapColumn = data.toMap();
        return this.stored(mapColumn);
    }

    public R2Vector stored(final Map<String, Object> data) {
        if (Objects.isNull(data)) {
            log.warn("[ R2MO ] 存储数据列时传入的 Map 为空，跳过处理");
            return this;
        }
        for (final Map.Entry<String, Object> entry : data.entrySet()) {
            final String k = entry.getKey();
            final Object v = entry.getValue();
            if (Objects.isNull(v)) {
                continue;
            }
            final String value = Objects.toString(v);
            this.columnMapping.put(k, value);
            this.columnRevert.put(value, k);
        }
        return this;
    }

    // 读取数据，读数据
    public ConcurrentMap<String, String> mapToCol() {
        return this.columnMapping;
    }

    public String mapToCol(final String key) {
        return this.columnMapping.getOrDefault(key, null);
    }

    public ConcurrentMap<String, String> mapByCol() {
        return this.columnRevert;
    }

    public String mapByCol(final String key) {
        return this.columnRevert.getOrDefault(key, null);
    }
}
