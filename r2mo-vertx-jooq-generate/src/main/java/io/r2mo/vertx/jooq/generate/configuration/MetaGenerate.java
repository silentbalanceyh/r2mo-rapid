package io.r2mo.vertx.jooq.generate.configuration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.r2mo.base.dbe.Database;
import io.r2mo.typed.json.jackson.DatabaseDeserializer;
import io.r2mo.typed.json.jackson.DatabaseSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * <pre>
 *     vertx-generate.yml 配置项
 * </pre>
 *
 * @author lang : 2025-10-20
 */
@Data
public class MetaGenerate implements Serializable {

    @JsonIgnore
    private String schema;

    @JsonSerialize(using = DatabaseSerializer.class)
    @JsonDeserialize(using = DatabaseDeserializer.class)
    private Database database;

    private MetaSource source = new MetaSource();

    /** 把 other 合并进当前实例，右侧不为 null 则覆盖；返回新对象 */
    public MetaGenerate merge(final MetaGenerate other) {
        if (other == null) {
            return this;
        }
        final MetaGenerate m = new MetaGenerate();
        m.schema = other.schema != null ? other.schema : this.schema;
        m.database = other.database != null ? other.database : this.database;
        m.source = (this.source == null)
            ? other.source
            : this.source.merge(other.source);
        return m;
    }
}
