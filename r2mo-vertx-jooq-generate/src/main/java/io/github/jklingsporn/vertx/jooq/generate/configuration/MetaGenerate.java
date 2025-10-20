package io.github.jklingsporn.vertx.jooq.generate.configuration;

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
}
