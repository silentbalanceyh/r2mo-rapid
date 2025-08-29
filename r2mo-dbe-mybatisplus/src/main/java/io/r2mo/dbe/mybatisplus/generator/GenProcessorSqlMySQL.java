package io.r2mo.dbe.mybatisplus.generator;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.r2mo.dbe.common.constant.SourceField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.JdbcType;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lang : 2025-07-28
 */
@Slf4j
class GenProcessorSqlMySQL implements GenProcessor {

    private static int getFieldPriority(final String fieldName) {
        final int index = SourceField.FIELD_ORDER.indexOf(fieldName);
        return index == -1 ? Integer.MAX_VALUE : index; // 不在列表中的字段放到最后
    }

    /**
     * 根据实体类生成建表 SQL（支持继承关系）
     */
    private static String generate(final Class<?> entityClass) {
        final StringBuilder sql = new StringBuilder();
        final String tableName = getTableName(entityClass);

        // 收集所有字段（包括父类字段）
        final List<FieldInfo> allFields = collectAllFields(entityClass);

        sql.append("DROP TABLE IF EXISTS ").append(tableName).append(";\n");
        sql.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (\n");

        final List<String> columnDefinitions = new ArrayList<>();
        String primaryKeyDefinition = null;

        // 处理所有字段
        for (final FieldInfo fieldInfo : allFields) {
            final String columnDefinition = generateColumnDefinition(fieldInfo);
            if (fieldInfo.isPrimaryKey) {
                // uuid 默认
                columnDefinitions.add(fieldInfo.columnName + " VARCHAR(36)");
                primaryKeyDefinition = "PRIMARY KEY (" + fieldInfo.columnName + ")";
            } else {
                columnDefinitions.add(columnDefinition);
            }
        }

        // 添加普通字段
        for (int i = 0; i < columnDefinitions.size(); i++) {
            sql.append("    ").append(columnDefinitions.get(i));
            if (i < columnDefinitions.size() - 1 || primaryKeyDefinition != null) {
                sql.append(",");
            }
            sql.append("\n");
        }

        // 添加主键约束
        if (primaryKeyDefinition != null) {
            sql.append("    ").append(primaryKeyDefinition).append("\n");
        }

        sql.append(");");
        sql.append("\n-- End\n");
        return sql.toString();
    }

    /**
     * 收集类及其父类的所有字段
     */
    private static List<FieldInfo> collectAllFields(final Class<?> clazz) {
        final List<FieldInfo> fields = new ArrayList<>();
        final Set<String> processedFields = new HashSet<>();

        // 递归处理继承链
        Class<?> currentClass = clazz;
        while (currentClass != null && currentClass != Object.class) {
            final Field[] declaredFields = currentClass.getDeclaredFields();
            for (final Field field : declaredFields) {
                // 避免重复处理（子类覆盖父类字段的情况）
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                if (field.isAnnotationPresent(JsonIgnore.class)) {
                    continue; // 忽略被 @JsonIgnore 注解标记的字段
                }
                final TableField annotation = field.getAnnotation(TableField.class);
                if (Objects.nonNull(annotation) && !annotation.exist()) {
                    continue; // 如果 @TableField 注解的 exist 属性为 false，则忽略该字段
                }
                if (!processedFields.contains(field.getName())) {
                    fields.add(new FieldInfo(field));
                    processedFields.add(field.getName());
                }
            }
            currentClass = currentClass.getSuperclass();
        }
        // 字段排序
        return fields.stream()
            .sorted((f1, f2) -> {
                final int priority1 = getFieldPriority(f1.fieldName);
                final int priority2 = getFieldPriority(f2.fieldName);

                if (priority1 != priority2) {
                    return Integer.compare(priority1, priority2);
                } else {
                    // 优先级相同的按字段名字典序排序
                    return f1.fieldName.compareTo(f2.fieldName);
                }
            })
            .collect(Collectors.toList());
    }

    /**
     * 生成列定义
     */
    private static String generateColumnDefinition(final FieldInfo fieldInfo) {
        final StringBuilder definition = new StringBuilder();
        definition.append(fieldInfo.columnName).append(" ").append(fieldInfo.columnType);

        // 非空约束
        if (!fieldInfo.nullable) {
            definition.append(" NOT NULL");
        }

        // 默认值
        if (fieldInfo.defaultValue != null) {
            definition.append(" DEFAULT ").append(fieldInfo.defaultValue);
        }

        // 注释
        if (fieldInfo.comment != null && !fieldInfo.comment.isEmpty()) {
            definition.append(" COMMENT '").append(fieldInfo.comment).append("'");
        }

        return definition.toString();
    }

    /**
     * 获取表名
     */
    private static String getTableName(final Class<?> entityClass) {
        final TableName tableNameAnnotation =
            entityClass.getAnnotation(TableName.class);

        if (tableNameAnnotation != null && !tableNameAnnotation.value().isEmpty()) {
            return tableNameAnnotation.value();
        }

        return camelToUnderline(entityClass.getSimpleName());
    }

    /**
     * 获取列名
     */
    private static String getColumnName(final Field field) {
        // 检查 @TableId 注解
        final com.baomidou.mybatisplus.annotation.TableId tableIdAnnotation =
            field.getAnnotation(com.baomidou.mybatisplus.annotation.TableId.class);

        if (tableIdAnnotation != null && !tableIdAnnotation.value().isEmpty()) {
            return tableIdAnnotation.value();
        }

        // 检查 @TableField 注解
        final TableField tableFieldAnnotation =
            field.getAnnotation(TableField.class);

        if (tableFieldAnnotation != null && !tableFieldAnnotation.value().isEmpty()) {
            return tableFieldAnnotation.value();
        }

        // 默认使用字段名转下划线
        return camelToUnderline(field.getName());
    }

    /**
     * 获取列类型
     */
    private static String getColumnType(final Field field) {
        final Class<?> type = field.getType();

        // 检查 @TableField 注解中的 jdbcType
        final TableField tableFieldAnnotation =
            field.getAnnotation(TableField.class);

        if (tableFieldAnnotation != null) {
            // 可以根据需要处理 jdbcType
            // 大文本必须追加 TableField 的注解
            final JdbcType jdbcType = tableFieldAnnotation.jdbcType();
            if (jdbcType == JdbcType.CLOB) {
                return "TEXT";
            } else if (jdbcType == JdbcType.NCLOB) {
                return "LONGTEXT";
            }
        }

        // 根据 Java 类型映射到 SQL 类型
        if (type == String.class) {
            return "VARCHAR(255)";
        } else if (type == Integer.class || type == int.class) {
            return "INT";
        } else if (type == Long.class || type == long.class) {
            return "BIGINT";
        } else if (type == Double.class || type == double.class) {
            return "DOUBLE";
        } else if (type == Float.class || type == float.class) {
            return "FLOAT";
        } else if (type == Boolean.class || type == boolean.class) {
            return "TINYINT(1)";
        } else if (type == Date.class) {
            return "DATETIME";
        } else if (type == java.time.LocalDateTime.class) {
            return "DATETIME";
        } else if (type == java.time.LocalDate.class) {
            return "DATE";
        } else if (type == java.math.BigDecimal.class) {
            return "DECIMAL(10,2)";
        } else {
            return "VARCHAR(255)"; // 默认类型
        }
    }

    /**
     * 检查是否为主键
     */
    private static boolean isPrimaryKey(final Field field) {
        return field.isAnnotationPresent(com.baomidou.mybatisplus.annotation.TableId.class);
    }

    /**
     * 检查是否允许为空
     */
    private static boolean isNullable(final Field field) {
        final TableField tableFieldAnnotation =
            field.getAnnotation(TableField.class);

        if (tableFieldAnnotation != null) {
            // exist = false 表示该字段不存在，通常也不需要非空约束
            if (!tableFieldAnnotation.exist()) {
                return true;
            }
            // 可以根据其他属性判断
        }

        // 主键默认非空
        return !isPrimaryKey(field);// 默认允许为空
    }

    /**
     * 获取默认值
     */
    private static String getDefaultValue(final Field field) {
        // 可以根据业务需要添加默认值逻辑
        return null;
    }

    /**
     * 获取注释
     */
    private static String getComment(final Field field) {
        // 可以从 @ApiModelProperty 等注解获取注释
        final Schema schema = field.getDeclaredAnnotation(Schema.class);
        return Objects.nonNull(schema) ? schema.description() : null;
    }

    /**
     * 驼峰转下划线
     */
    private static String camelToUnderline(final String camelCase) {
        if (camelCase == null || camelCase.isEmpty()) {
            return camelCase;
        }

        final StringBuilder result = new StringBuilder();
        for (int i = 0; i < camelCase.length(); i++) {
            final char c = camelCase.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    result.append("_");
                }
                result.append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    @SuppressWarnings("all")
    @Override
    public void generate(final Class<?> entity, final GenConfig config) {
        final String sqlContent = generate(entity);
        final Path database = config.outSql();
        final Path dbSchema = database.resolve("schema");

        final TableName table = entity.getAnnotation(TableName.class);
        if (Objects.isNull(table)) {
            log.warn("[ GEN ] Entity {} does not have @TableName annotation, skipping SQL generation.", entity.getSimpleName());
            return;
        }

        final String filename = "V1__init_schema.sql";
        final Path sqlFilePath = dbSchema.resolve(filename);
        // 确保目录存在
        try {
            Files.createDirectories(dbSchema);

            // 写入 SQL 文件
            Files.writeString(sqlFilePath, sqlContent,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND,
                StandardOpenOption.WRITE
            );

            log.info("[GEN] Generated SQL File: {}", sqlFilePath);
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 字段信息包装类
     */
    private static class FieldInfo {
        String fieldName;
        String columnName;
        String columnType;
        boolean isPrimaryKey;
        boolean nullable;
        String defaultValue;
        String comment;

        FieldInfo(final Field field) {
            this.fieldName = field.getName();
            this.columnName = GenProcessorSqlMySQL.getColumnName(field);
            this.columnType = GenProcessorSqlMySQL.getColumnType(field);
            this.isPrimaryKey = GenProcessorSqlMySQL.isPrimaryKey(field);
            this.nullable = GenProcessorSqlMySQL.isNullable(field);
            this.defaultValue = GenProcessorSqlMySQL.getDefaultValue(field);
            this.comment = GenProcessorSqlMySQL.getComment(field);
        }
    }
}
