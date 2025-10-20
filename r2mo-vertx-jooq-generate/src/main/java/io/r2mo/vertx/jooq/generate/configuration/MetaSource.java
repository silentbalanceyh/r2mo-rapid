package io.r2mo.vertx.jooq.generate.configuration;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.r2mo.typed.json.jackson.ClassDeserializer;
import io.r2mo.typed.json.jackson.ClassSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lang : 2025-10-20
 */
@Data
public class MetaSource implements Serializable {
    private String includes;
    private String directory;

    @JsonSerialize(using = ClassSerializer.class)
    @JsonDeserialize(using = ClassDeserializer.class)
    private Class<?> classStrategy;

    @JsonSerialize(using = ClassSerializer.class)
    @JsonDeserialize(using = ClassDeserializer.class)
    private Class<?> classGenerator;

    /** 把 other 合并进当前实例，右侧不为 null 则覆盖；返回新对象 */
    public MetaSource merge(final MetaSource other) {
        if (other == null) {
            return this;
        }
        final MetaSource m = new MetaSource();
        m.includes = other.includes != null ? other.includes : this.includes;
        m.directory = other.directory != null ? other.directory : this.directory;
        m.classStrategy = other.classStrategy != null ? other.classStrategy : this.classStrategy;
        m.classGenerator = other.classGenerator != null ? other.classGenerator : this.classGenerator;
        return m;
    }
}
