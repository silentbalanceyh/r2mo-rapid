package io.r2mo.dbe.mybatisplus.generator.field;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.r2mo.base.generator.GenConfig;
import io.r2mo.base.generator.GenField;
import io.swagger.v3.oas.annotations.media.Schema;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author lang : 2025-09-04
 */
public class GenFieldMybatisPlus implements GenField {

    private static final Set<String> DEFAULT_REQ_IGNORE = Set.of(
        "createdAt", "createdBy", "updatedAt", "updatedBy",
        "language", "version", "cMetadata", "appId", "tenantId"
    );

    private static final Set<String> DEFAULT_RESP_IGNORE = Set.of(
        "language", "version", "cMetadata", "appId", "tenantId"
    );

    @Override
    public String generateReq(final Class<?> entity, final GenConfig config) {
        final Set<String> ignoreSet = new HashSet<>(DEFAULT_REQ_IGNORE);
        final Set<String> customIgnore = config.getMetadata().getIgnoreReq();
        if(customIgnore != null) {
            ignoreSet.addAll(customIgnore);
        }
        return this.generate(entity, ignoreSet);
    }

    @Override
    public String generateResp(final Class<?> entity, final GenConfig config) {
        final Set<String> ignoreSet = new HashSet<>(DEFAULT_RESP_IGNORE);
        final Set<String> customIgnore = config.getMetadata().getIgnoreResp();
        if(customIgnore != null) {
            ignoreSet.addAll(customIgnore);
        }
        return this.generate(entity, ignoreSet);
    }

    private String generate(final Class<?> entity, final Set<String> ignoreSet) {
        final List<String> lines = new ArrayList<>();
        final Field[] fields = entity.getDeclaredFields();
        Arrays.stream(fields)
            .filter(field -> !ignoreSet.contains(field.getName()))
            .filter(this::isValid)
            .forEach(field -> lines.addAll(this.buildLines(field)));
        return String.join("\n", lines);
    }

    @Override
    public String generateEnum(final Class<?> entity) {
        final List<String> lines = new ArrayList<>();
        final Field[] fields = entity.getDeclaredFields();
        Arrays.stream(fields)
            .filter(this::isValid)
            .filter(field -> field.getType().isEnum())
            .forEach(field -> lines.add("import " + field.getType().getName() + ";"));
        return String.join("\n", lines);
    }

    private boolean isValid(final Field field){
        // 静态字段去掉
        if(Modifier.isStatic(field.getModifiers())) {
            return false;
        }
        if(field.isAnnotationPresent(JsonIgnore.class)){
            return false;
        }
        final TableField tableField = field.getDeclaredAnnotation(TableField.class);
        return tableField == null || tableField.exist();
    }

    private List<String> buildLines(final Field field) {
        final List<String> lines = new ArrayList<>();
        final Schema schema = field.getAnnotation(Schema.class);
        final StringBuilder lineSwagger = new StringBuilder();
        if(schema != null) {
            lineSwagger.append("@Schema(");
            if (!schema.description().isBlank()) {
                lineSwagger.append("description = \"").append(schema.description()).append("\"");
            }
            if (!schema.name().isBlank()) {
                if (lineSwagger.length() > 7) {
                    lineSwagger.append(", ");
                }
                lineSwagger.append("name = \"").append(schema.name()).append("\"");
            }
            lineSwagger.append(")");
            lines.add(lineSwagger.toString());
        }
        lines.add("    private " + field.getType().getSimpleName() + " " + field.getName() + ";");
        return lines;
    }
}
