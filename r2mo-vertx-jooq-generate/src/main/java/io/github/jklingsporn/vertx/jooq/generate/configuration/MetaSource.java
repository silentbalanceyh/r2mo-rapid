package io.github.jklingsporn.vertx.jooq.generate.configuration;

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
}
