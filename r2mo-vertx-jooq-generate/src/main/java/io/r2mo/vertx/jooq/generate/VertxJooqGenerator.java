package io.r2mo.vertx.jooq.generate;

import io.r2mo.vertx.jooq.shared.JsonArrayConverter;
import io.r2mo.vertx.jooq.shared.JsonObjectConverter;
import io.r2mo.vertx.jooq.shared.ObjectToJsonArrayBinding;
import io.r2mo.vertx.jooq.shared.ObjectToJsonObjectBinding;
import io.r2mo.vertx.jooq.shared.internal.AbstractVertxDAO;
import io.r2mo.vertx.jooq.shared.internal.VertxPojo;
import io.r2mo.vertx.jooq.shared.postgres.PgConverter;
import io.vertx.core.Vertx;
import io.vertx.core.impl.Arguments;
import org.jooq.Constants;
import org.jooq.Record;
import org.jooq.codegen.GeneratorStrategy;
import org.jooq.codegen.JavaGenerator;
import org.jooq.codegen.JavaWriter;
import org.jooq.impl.SQLDataType;
import org.jooq.meta.ColumnDefinition;
import org.jooq.meta.DataTypeDefinition;
import org.jooq.meta.Definition;
import org.jooq.meta.IndexDefinition;
import org.jooq.meta.SchemaDefinition;
import org.jooq.meta.TableDefinition;
import org.jooq.meta.TypedElementDefinition;
import org.jooq.meta.UDTDefinition;
import org.jooq.meta.UniqueKeyDefinition;
import org.jooq.tools.JooqLogger;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * 兼容 jOOQ 3.20 的 Vert.x 生成器：
 * - 不再强转 VertxJavaWriter，统一以 JavaWriter 工作；
 * - 通过 safeRef(...) 在存在 ref(String,int) 时使用该重载，否则回退到 ref(String)；
 * - 其余逻辑保持与原版 VertxGenerator 一致。
 */
public abstract class VertxJooqGenerator extends JavaGenerator {

    private static final JooqLogger logger = JooqLogger.getLogger(VertxJooqGenerator.class);

    private final boolean generateJson;
    protected VertxGeneratorStrategy vertxGeneratorStrategy;

    public VertxJooqGenerator() {
        this(true);
    }

    public VertxJooqGenerator(final boolean generateJson) {
        this.generateJson = generateJson;
        this.setGeneratePojos(true);
    }

    /* =========================
       兼容性封装：安全 ref
       ========================= */

    /** 安全调用 writer.ref(String) */
    private String safeRef(final JavaWriter out, final String fqn) {
        return out.ref(fqn);
    }

    /** 安全调用 writer.ref(String, int)，若无该重载则退化为 ref(String) */
    private String safeRef(final JavaWriter out, final String fqn, final int segments) {
        try {
            return (String) out.getClass()
                .getMethod("ref", String.class, int.class)
                .invoke(out, fqn, segments);
        } catch (final Throwable ignore) {
            return out.ref(fqn);
        }
    }

    /* =========================
       自定义扩展点（保持不变）
       ========================= */
    protected boolean handleCustomTypeFromJson(final TypedElementDefinition<?> column, final String setter, final String columnType, final String javaMemberName, final JavaWriter out) {
        return false;
    }

    protected boolean handleCustomTypeToJson(final TypedElementDefinition<?> column, final String getter, final String columnType, final String javaMemberName, final JavaWriter out) {
        return false;
    }

    protected abstract String renderFindOneType(String pType);

    protected abstract String renderFindManyType(String pType);

    protected abstract String renderExecType();

    protected abstract String renderInsertReturningType(String tType);

    protected abstract String renderQueryExecutor(String rType, String pType, String tType);

    protected abstract String renderDAOInterface(String rType, String pType, String tType);

    protected abstract void writeDAOConstructor(JavaWriter out, String className, String tableIdentifier, String rType, String pType, String tType, String schema);

    protected void writeDAOImports(final JavaWriter out) {
    }

    protected void writeDAOClassAnnotation(final JavaWriter out) {
    }

    protected void writeDAOConstructorAnnotation(final JavaWriter out) {
    }

    protected void overwriteDAOMethods(final SchemaDefinition schema, final JavaWriter out, final String className, final String tableIdentifier, final String rType, final String pType, final String tType) {
    }

    protected String renderDaoExtendsClassName() {
        return AbstractVertxDAO.class.getName();
    }

    protected String renderFQVertxName() {
        return Vertx.class.getName();
    }

    protected Collection<JavaWriter> writeExtraData(final SchemaDefinition definition, final Function<File, JavaWriter> writerGenerator) {
        return Collections.emptyList();
    }

    @Override
    protected void generatePojoMultiConstructor(final Definition tableOrUDT, final JavaWriter out) {
        super.generatePojoMultiConstructor(tableOrUDT, out);
        if (this.generateJson) {
            this.generateFromJsonConstructor(tableOrUDT, out, GeneratorStrategy.Mode.POJO);
        }
    }

    @Override
    protected void generatePojoClassFooter(final TableDefinition table, final JavaWriter out) {
        super.generatePojoClassFooter(table, out);
        if (this.generateJson) {
            if (!this.generateInterfaces()) {
                this.generateFromJson(table, out, GeneratorStrategy.Mode.POJO);
                this.generateToJson(table, out, GeneratorStrategy.Mode.POJO);
            }
        }
    }

    @Override
    protected void generateInterfaceClassFooter(final TableDefinition table, final JavaWriter out) {
        super.generateInterfaceClassFooter(table, out);
        if (this.generateJson && this.generateInterfaces()) {
            this.generateFromJson(table, out, GeneratorStrategy.Mode.INTERFACE);
            this.generateToJson(table, out, GeneratorStrategy.Mode.INTERFACE);
        }
    }

    @Override
    protected void generateRecordClassFooter(final TableDefinition table, final JavaWriter out) {
        super.generateRecordClassFooter(table, out);
        if (this.generateJson) {
            this.generateFromJsonConstructor(table, out, GeneratorStrategy.Mode.RECORD);
            if (!this.generateInterfaces()) {
                this.generateFromJson(table, out, GeneratorStrategy.Mode.RECORD);
                this.generateToJson(table, out, GeneratorStrategy.Mode.RECORD);
            }
        }
    }

    @Override
    protected JavaWriter newJavaWriter(final File file) {
        // 仍返回 VertxJavaWriter，供 writeExtraData 等路径使用；主线生成若走不到也不影响
        return new VertxJavaWriter(file, this.generateFullyQualifiedTypes(), this.targetEncoding);
    }

    @Override
    protected void printPackage(final JavaWriter out, final Definition definition, final GeneratorStrategy.Mode mode) {
        super.printPackage(out, definition, mode);
        if (mode.equals(GeneratorStrategy.Mode.DAO)) {
            out.println("import %s;", List.class.getName());
            this.writeDAOImports(out);
        } else if (this.generateJson && this.generateInterfaces() && mode.equals(GeneratorStrategy.Mode.INTERFACE)) {
            this.writeUnexpectedJsonValueTypeImport(out);
        } else if (this.generateJson && mode.equals(GeneratorStrategy.Mode.POJO)) {
            this.writeUnexpectedJsonValueTypeImport(out);
        } else if (this.generateJson && mode.equals(GeneratorStrategy.Mode.RECORD)) {
            this.writeUnexpectedJsonValueTypeImport(out);
        }
    }

    private void writeUnexpectedJsonValueTypeImport(final JavaWriter out) {
        out.println("import static %s.*;", VertxPojo.class.getName());
    }

    @Override
    protected void generateDaos(final SchemaDefinition schema) {
        super.generateDaos(schema);
        this.writeExtraData(schema);
    }

    private void writeExtraData(final SchemaDefinition definition) {
        final Collection<JavaWriter> writers = this.writeExtraData(definition, this::newJavaWriter);
        writers.forEach(this::closeJavaWriter);
    }

    private void generateFromJson(final TableDefinition table, final JavaWriter out, final GeneratorStrategy.Mode mode) {
        out.println();
        out.tab(1).override();
        final String className = this.getStrategy().getJavaClassName(table, mode);
        out.tab(1).println("public %s%s fromJson(io.vertx.core.json.JsonObject json) {", mode == GeneratorStrategy.Mode.INTERFACE ? "default " : "", className);
        for (final TypedElementDefinition<?> column : table.getColumns()) {
            final String setter = this.getStrategy().getJavaSetterName(column, GeneratorStrategy.Mode.INTERFACE);
            final String columnType = this.getJavaType(column.getType(), out);
            final String javaMemberName = this.getJsonKeyName(column);
            String jsonValueExtractor = null;
            if (this.handleCustomTypeFromJson(column, setter, columnType, javaMemberName, out)) {
                // handled by user
            } else if (this.isType(columnType, Integer.class)) {
                jsonValueExtractor = "json::getInteger";
            } else if (this.isType(columnType, Short.class)) {
                jsonValueExtractor = "key -> {Integer i = json.getInteger(key); return i==null?null:i.shortValue();}";
            } else if (this.isType(columnType, Byte.class)) {
                jsonValueExtractor = "key -> {Integer i = json.getInteger(key); return i==null?null:i.byteValue();}";
            } else if (this.isType(columnType, Long.class)) {
                jsonValueExtractor = "json::getLong";
            } else if (this.isType(columnType, Float.class)) {
                jsonValueExtractor = "json::getFloat";
            } else if (this.isType(columnType, Double.class)) {
                jsonValueExtractor = "json::getDouble";
            } else if (this.isType(columnType, Boolean.class)) {
                jsonValueExtractor = "json::getBoolean";
            } else if (this.isType(columnType, String.class)) {
                jsonValueExtractor = "json::getString";
            } else if (columnType.equals(byte.class.getName() + "[]")) {
                jsonValueExtractor = "json::getBinary";
            } else if (this.isType(columnType, Instant.class)) {
                jsonValueExtractor = "json::getInstant";
            } else if (this.isJavaTimeType(columnType)) {
                jsonValueExtractor = String.format("key -> {String s = json.getString(key); return s==null?null:%s.parse(s);}", columnType);
            } else if (this.isType(columnType, BigDecimal.class)) {
                jsonValueExtractor = String.format("key -> {String s = json.getString(key); return s==null?null:new %s(s);}", columnType);
            } else if (this.isEnum(table, column)) {
                if (column.getType().getConverter() == null) {
                    jsonValueExtractor = String.format("key -> java.util.Arrays.stream(%s.values()).filter(td -> td.getLiteral().equals(json.getString(key))).findFirst().orElse(null)", columnType);
                } else {
                    jsonValueExtractor = String.format("key -> {String s = json.getString(key); return s==null?null:%s.valueOf(s);}", columnType);
                }
            } else if ((column.getType().getConverter() != null && this.isType(column.getType().getConverter(), JsonObjectConverter.class)) ||
                (column.getType().getBinding() != null && this.isType(column.getType().getBinding(), ObjectToJsonObjectBinding.class))) {
                jsonValueExtractor = "json::getJsonObject";
            } else if ((column.getType().getConverter() != null && this.isType(column.getType().getConverter(), JsonArrayConverter.class)) ||
                (column.getType().getBinding() != null && this.isType(column.getType().getBinding(), ObjectToJsonArrayBinding.class))) {
                jsonValueExtractor = "json::getJsonArray";
            } else if (this.isType(columnType, List.class)) {
                final String genericType = columnType.substring(columnType.indexOf("<") + 1, columnType.lastIndexOf(">"));
                jsonValueExtractor = String.format("key -> {io.vertx.core.json.JsonArray arr = json.getJsonArray(key); return arr==null?null:new java.util.ArrayList<%s>(arr.getList());}", genericType);
            } else {
                logger.warn(String.format("Omitting unrecognized type %s for column %s in table %s!", columnType, column.getName(), table.getName()));
                out.tab(2).println(String.format("// Omitting unrecognized type %s for column %s!", columnType, column.getName()));
            }
            if (jsonValueExtractor != null) {
                out.tab(2).println("setOrThrow(this::%s,%s,\"%s\",\"%s\");", setter, jsonValueExtractor, javaMemberName, columnType);
            }
        }
        out.tab(2).println("return this;");
        out.tab(1).println("}");
        out.println();
    }

    public boolean isEnum(final TableDefinition table, final TypedElementDefinition<?> column) {
        return table.getDatabase().getEnum(table.getSchema(), column.getType().getUserType()) != null ||
            (column.getType().getConverter() != null && column.getType().getConverter().endsWith("EnumConverter"));
    }

    public boolean isJson(final DataTypeDefinition columnType) {
        return columnType.getType().equals(SQLDataType.JSON.getTypeName()) || columnType.getType().equals(SQLDataType.JSONB.getTypeName());
    }

    protected boolean isType(final String columnType, final Class<?> clazz) {
        final int i = columnType.indexOf("<");
        return (i == -1 ? columnType : columnType.substring(0, i)).equals(clazz.getName());
    }

    protected Class<?> tryGetPgConverterFromType(final String columnType, final String converter) {
        try {
            final Class<?> converterClazz = Class.forName(converter);
            if (PgConverter.class.isAssignableFrom(converterClazz)) {
                final PgConverter<?, ?, ?> converterInstance = (PgConverter<?, ?, ?>) converterClazz.getDeclaredConstructor().newInstance();
                return converterInstance.rowConverter().fromType();
            }
            return null;
        } catch (final ClassNotFoundException e) {
            logger.info(String.format("'%s' to map '%s' could not be accessed from code generator.", converter, columnType));
            return null;
        } catch (final IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            logger.info(String.format("'%s' to map '%s' could not be instantiated from code generator.", converter, columnType));
            return null;
        }
    }

    @Override
    public String getJavaType(final DataTypeDefinition type, final JavaWriter out) {
        return super.getJavaType(type, out);
    }

    private void generateToJson(final TableDefinition table, final JavaWriter out, final GeneratorStrategy.Mode mode) {
        out.println();
        out.tab(1).override();
        out.tab(1).println("public %sio.vertx.core.json.JsonObject toJson() {", mode == GeneratorStrategy.Mode.INTERFACE ? "default " : "");
        out.tab(2).println("io.vertx.core.json.JsonObject json = new io.vertx.core.json.JsonObject();");
        for (final TypedElementDefinition<?> column : table.getColumns()) {
            final String getter = this.getStrategy().getJavaGetterName(column, GeneratorStrategy.Mode.INTERFACE);
            final String columnType = this.getJavaType(column.getType(), out);
            if (this.handleCustomTypeToJson(column, getter, columnType, this.getJsonKeyName(column), out)) {
                // handled by user
            } else if (this.isEnum(table, column)) {
                if (column.getType().getConverter() == null) {
                    out.tab(2).println("json.put(\"%s\",%s()==null?null:%s().getLiteral());", this.getJsonKeyName(column), getter, getter);
                } else {
                    out.tab(2).println("json.put(\"%s\",%s()==null?null:%s().name());", this.getJsonKeyName(column), getter, getter);
                }
            } else if (this.isAllowedJsonType(column, columnType)) {
                out.tab(2).println("json.put(\"%s\",%s());", this.getJsonKeyName(column), getter);
            } else if (this.isJavaTimeType(columnType) || this.isType(columnType, BigDecimal.class)) {
                out.tab(2).println("json.put(\"%s\",%s()==null?null:%s().toString());", this.getJsonKeyName(column), getter, getter);
            } else if (this.isCollectionType(columnType)) {
                out.tab(2).println("json.put(\"%s\",%s()==null?null: new io.vertx.core.json.JsonArray(%s()));", this.getJsonKeyName(column), getter, getter);
            } else {
                logger.warn(String.format("Omitting unrecognized type %s for column %s in table %s!", columnType, column.getName(), table.getName()));
                out.tab(2).println(String.format("// Omitting unrecognized type %s for column %s!", columnType, column.getName()));
            }
        }
        out.tab(2).println("return json;");
        out.tab(1).println("}");
        out.println();
    }

    protected String getJsonKeyName(final TypedElementDefinition<?> column) {
        return this.vertxGeneratorStrategy.getJsonKeyName(column);
    }

    private boolean isAllowedJsonType(final TypedElementDefinition<?> column, final String columnType) {
        return this.isType(columnType, Integer.class) || this.isType(columnType, Short.class) || this.isType(columnType, Byte.class) ||
            this.isType(columnType, Long.class) || this.isType(columnType, Float.class) || this.isType(columnType, Double.class) ||
            this.isType(columnType, Boolean.class) || this.isType(columnType, String.class) || this.isType(columnType, Instant.class) ||
            columnType.equals(byte.class.getName() + "[]") || (column.getType().getConverter() != null &&
            (this.isType(column.getType().getConverter(), JsonObjectConverter.class) || this.isType(column.getType().getConverter(), JsonArrayConverter.class)))
            || (column.getType().getBinding() != null && this.isType(column.getType().getBinding(), ObjectToJsonObjectBinding.class));
    }

    private boolean isJavaTimeType(final String columnType) {
        return this.isType(columnType, LocalDateTime.class) || this.isType(columnType, LocalTime.class)
            || this.isType(columnType, ZonedDateTime.class) || this.isType(columnType, OffsetDateTime.class)
            || this.isType(columnType, LocalDate.class);
    }

    private boolean isCollectionType(final String columnType) {
        return this.isType(columnType, Collection.class) || this.isType(columnType, List.class) || this.isType(columnType, Set.class);
    }

    @Override
    public void setStrategy(final GeneratorStrategy strategy) {
        Arguments.require(strategy instanceof VertxGeneratorStrategy, "Requires instance of VertxGeneratorStrategy");
        super.setStrategy(strategy);
        this.vertxGeneratorStrategy = (VertxGeneratorStrategy) strategy;
    }

    public VertxGeneratorStrategy getVertxGeneratorStrategy() {
        return this.vertxGeneratorStrategy;
    }

    private void generateFromJsonConstructor(final Definition table, final JavaWriter out, final GeneratorStrategy.Mode mode) {
        final String className = this.getStrategy().getJavaClassName(table, mode);
        out.println();
        out.tab(1).println("public %s(io.vertx.core.json.JsonObject json) {", className);
        out.tab(2).println("this();"); // call default constructor
        out.tab(2).println("fromJson(json);");
        out.tab(1).println("}");
    }

    protected void generateFetchMethods(final TableDefinition table, final JavaWriter out) {
        final String pType = this.safeRef(out,
            this.getStrategy().getFullJavaClassName(table, GeneratorStrategy.Mode.POJO));

        final UniqueKeyDefinition primaryKey = table.getPrimaryKey();
        final ColumnDefinition firstPrimaryKeyColumn = primaryKey.getKeyColumns().get(0);
        final List<IndexDefinition> indexes = table.getIndexes();

        for (final ColumnDefinition column : table.getColumns()) {
            final String colName = column.getOutputName();
            final String colClass = this.getStrategy().getJavaClassName(column);
            final String colType = this.safeRef(out, this.getJavaType(column.getType(), out));
            final String colIdentifier = this.safeRef(
                out,
                this.getStrategy().getFullJavaIdentifier(column),
                this.colRefSegments(column)
            );

            if (!firstPrimaryKeyColumn.equals(column)) {
                this.generateFindManyByMethods(out, pType, colName, colClass, colType, colIdentifier);
                this.generateFindManyByLimitMethods(out, pType, colName, colClass, colType, colIdentifier);
            }
        }

        for (final IndexDefinition index : indexes) {
            if (index.isUnique() && index.getIndexColumns().size() == 1) {
                final ColumnDefinition column = index.getIndexColumns().get(0).getColumn();
                if (column.equals(firstPrimaryKeyColumn)) {
                    continue;
                }
                final String colName = column.getOutputName();
                final String colClass = this.getStrategy().getJavaClassName(column);
                final String colType = this.safeRef(out, this.getJavaType(column.getType(), out));
                final String colIdentifier = this.safeRef(
                    out,
                    this.getStrategy().getFullJavaIdentifier(column),
                    this.colRefSegments(column)
                );
                this.generateFindOneByMethods(out, pType, colName, colClass, colType, colIdentifier);
            }
        }
    }

    protected void generateFindOneByMethods(final JavaWriter out, final String pType, final String colName, final String colClass, final String colType, final String colIdentifier) {
        out.tab(1).javadoc("Find a unique record that has <code>%s = value</code> asynchronously", colName);
        out.tab(1).println("public %s findOneBy%s(%s value) {", this.renderFindOneType(pType), colClass, colType);
        out.tab(2).println("return findOneByCondition(%s.eq(value));", colIdentifier);
        out.tab(1).println("}");
    }

    protected void generateFindManyByMethods(final JavaWriter out, final String pType, final String colName, final String colClass, final String colType, final String colIdentifier) {
        out.tab(1).javadoc("Find records that have <code>%s IN (values)</code> asynchronously", colName);
        out.tab(1).println("public %s findManyBy%s(%s<%s> values) {", this.renderFindManyType(pType), colClass, Collection.class, colType);
        out.tab(2).println("return findManyByCondition(%s.in(values));", colIdentifier);
        out.tab(1).println("}");
    }

    protected void generateFindManyByLimitMethods(final JavaWriter out, final String pType, final String colName, final String colClass, final String colType, final String colIdentifier) {
        out.tab(1).javadoc("Find records that have <code>%s IN (values)</code> asynchronously limited by the given limit", colName);
        out.tab(1).println("public %s findManyBy%s(%s<%s> values, int limit) {", this.renderFindManyType(pType), colClass, Collection.class, colType);
        out.tab(2).println("return findManyByCondition(%s.in(values),limit);", colIdentifier);
        out.tab(1).println("}");
    }

    public String getKeyType(final UniqueKeyDefinition key, final JavaWriter out) {
        final String tType;
        final List<ColumnDefinition> keyColumns = key.getKeyColumns();

        if (keyColumns.size() == 1) {
            tType = this.getJavaType(keyColumns.get(0).getType(), out);
        } else if (keyColumns.size() <= Constants.MAX_ROW_DEGREE) {
            final StringBuilder generics = new StringBuilder();
            String separator = "";
            for (final ColumnDefinition column : keyColumns) {
                generics.append(separator).append(this.getJavaType(column.getType(), out));
                separator = ", ";
            }
            tType = Record.class.getName() + keyColumns.size() + "<" + generics + ">";
        } else {
            tType = Record.class.getName();
        }

        return tType;
    }

    private int colRefSegments(final TypedElementDefinition<?> column) {
        if (column != null && column.getContainer() instanceof UDTDefinition) {
            return 2;
        }
        if (!this.getStrategy().getInstanceFields()) {
            return 2;
        }
        return 3;
    }

    @Override
    protected void generateDao(final TableDefinition table, final JavaWriter out) {
        final UniqueKeyDefinition key = table.getPrimaryKey();
        if (key == null) {
            logger.info("Skipping DAO generation", out.file().getName());
            return;
        }
        this.generateDAO(key, table, out);
    }

    protected void generatePojoClassAnnotations(final JavaWriter out, final TableDefinition schema) {
    }

    private void generateDAO(final UniqueKeyDefinition key, final TableDefinition table, final JavaWriter out) {
        final String className = this.getStrategy().getJavaClassName(table, GeneratorStrategy.Mode.DAO);
        final List<String> interfaces = out.ref(this.getStrategy().getJavaClassImplements(table, GeneratorStrategy.Mode.DAO));
        final String tableRecord = this.safeRef(out, this.getStrategy().getFullJavaClassName(table, GeneratorStrategy.Mode.RECORD));
        final String daoImpl = this.safeRef(out, this.renderDaoExtendsClassName());
        final String tableIdentifier = this.safeRef(out, this.getStrategy().getFullJavaIdentifier(table), 2);

        String tType = "Void";
        final String pType = this.safeRef(out, this.getStrategy().getFullJavaClassName(table, GeneratorStrategy.Mode.POJO));

        final List<ColumnDefinition> keyColumns = key.getKeyColumns();

        if (keyColumns.size() == 1) {
            tType = this.getJavaType(keyColumns.get(0).getType(), out);
        } else if (keyColumns.size() <= Constants.MAX_ROW_DEGREE) {
            final StringBuilder generics = new StringBuilder();
            String separator = "";
            for (final ColumnDefinition column : keyColumns) {
                generics.append(separator).append(this.safeRef(out, this.getJavaType(column.getType(), out)));
                separator = ", ";
            }
            tType = Record.class.getName() + keyColumns.size() + "<" + generics + ">";
        } else {
            tType = Record.class.getName();
        }

        tType = this.safeRef(out, tType);
        interfaces.add(this.renderDAOInterface(tableRecord, pType, tType));

        this.printPackage(out, table, GeneratorStrategy.Mode.DAO);
        this.generateDaoClassJavadoc(table, out);
        this.printClassAnnotations(out, table.getSchema(), GeneratorStrategy.Mode.DAO);

        if (this.generateSpringAnnotations()) {
            out.println("@%s", out.ref("org.springframework.stereotype.Repository"));
        }
        this.writeDAOClassAnnotation(out);
        out.println("public class %s extends %s<%s, %s, %s, %s, %s, %s, %s>[[before= implements ][%s]] {",
            className,
            daoImpl,
            tableRecord,
            pType,
            tType,
            this.renderFindManyType(pType),
            this.renderFindOneType(pType),
            this.renderExecType(),
            this.renderInsertReturningType(tType),
            interfaces);

        if (this.generateSpringAnnotations()) {
            out.tab(1).println("@%s", out.ref("org.springframework.beans.factory.annotation.Autowired"));
        }

        this.writeDAOConstructorAnnotation(out);
        this.writeDAOConstructor(out, className, tableIdentifier, tableRecord, pType, tType, table.getSchema().getName());

        out.tab(1).overrideInherit();
        out.tab(1).println("protected %s getId(%s object) {", tType, pType);

        if (keyColumns.size() == 1) {
            out.tab(2).println("return object.%s();", this.getStrategy().getJavaGetterName(keyColumns.get(0), GeneratorStrategy.Mode.POJO));
        } else {
            final StringBuilder params = new StringBuilder();
            String separator = "";
            for (final ColumnDefinition column : keyColumns) {
                params.append(separator).append("object.").append(this.getStrategy().getJavaGetterName(column, GeneratorStrategy.Mode.POJO)).append("()");
                separator = ", ";
            }
            out.tab(2).println("return compositeKeyRecord(%s);", params.toString());
        }

        out.tab(1).println("}");
        this.generateFetchMethods(table, out);
        this.generateDaoClassFooter(table, out);
        this.overwriteDAOMethods(table.getSchema(), out, className, tableIdentifier, tableRecord, pType, tType);
        out.println("}");
    }

    @Override
    protected void printClassAnnotations(final JavaWriter out, final Definition definition, final GeneratorStrategy.Mode mode) {
        super.printClassAnnotations(out, definition, mode);
        if (mode.equals(GeneratorStrategy.Mode.POJO)) {
            this.generatePojoClassAnnotations(out, (TableDefinition) definition);
        }
    }
}
