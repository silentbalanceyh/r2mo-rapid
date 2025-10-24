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
 * Created by jklingsporn on 17.10.16.
 * Extension of the jOOQ's <code>JavaGenerator</code>.
 * By default, it generates POJO's that have a <code>#fromJson</code> and a <code>#toJson</code>-method which takes/generates a <code>JsonObject</code> out of the generated POJO.
 * When you've enabled Interface-generation, these methods are added to the generated Interface as default-methods.
 * Besides these method there is also a constructor generated which takes a <code>JsonObject</code>.
 * It also generates DAOs which implement <code>VertxDAO</code> and allow you to execute CRUD-operations asynchronously.
 */
public abstract class VertxGenerator extends JavaGenerator {

    private static final JooqLogger logger = JooqLogger.getLogger(VertxGenerator.class);

    private final boolean generateJson;
    protected VertxGeneratorStrategy vertxGeneratorStrategy;

    public VertxGenerator() {
        this(true);
    }

    public VertxGenerator(final boolean generateJson) {
        this.generateJson = generateJson;
        this.setGeneratePojos(true);
    }

    /* ------------------------
     * 辅助：ref / 标识符处理
     * ------------------------ */

    /** 兼容地调用 writer.ref(String,int)，失败则退化到 ref(String) */
    private String safeRef(final JavaWriter out, final String fqn, final int segments) {
        try {
            return (String) out.getClass()
                .getMethod("ref", String.class, int.class)
                .invoke(out, fqn, segments);
        } catch (final Throwable ignore) {
            return out.ref(fqn);
        }
    }

    /** 标识符安全截断：从右往左截取 segments 段，不触发 import（用于 XXX.YYY 常量路径） */
    private String safeId(final String fqn, final int segments) {
        if (fqn == null || fqn.isEmpty()) {
            return fqn;
        }
        final String[] parts = fqn.split("\\.");
        if (segments <= 0 || segments >= parts.length) {
            return fqn;
        }
        final StringBuilder sb = new StringBuilder();
        for (int i = Math.max(0, parts.length - segments); i < parts.length; i++) {
            if (sb.length() > 0) {
                sb.append('.');
            }
            sb.append(parts[i]);
        }
        return sb.toString();
    }

    /**
     * Overwrite this method to handle your custom type. This is needed especially when you have custom converters.
     *
     * @param column         the column definition
     * @param setter         the setter name
     * @param columnType     the type of the column
     * @param javaMemberName the java member name
     * @param out            the JavaWriter
     *
     * @return <code>true</code> if the column was handled.
     * @see #generateFromJson(TableDefinition, JavaWriter, org.jooq.codegen.GeneratorStrategy.Mode)
     */
    protected boolean handleCustomTypeFromJson(final TypedElementDefinition<?> column, final String setter, final String columnType, final String javaMemberName, final JavaWriter out) {
        return false;
    }

    /**
     * Overwrite this method to handle your custom type. This is needed especially when you have custom converters.
     *
     * @param column         the column definition
     * @param getter         the getter name
     * @param columnType     the type of the column
     * @param javaMemberName the java member name
     * @param out            the JavaWriter
     *
     * @return <code>true</code> if the column was handled.
     * @see #generateToJson(TableDefinition, JavaWriter, org.jooq.codegen.GeneratorStrategy.Mode)
     */
    protected boolean handleCustomTypeToJson(final TypedElementDefinition<?> column, final String getter, final String columnType, final String javaMemberName, final JavaWriter out) {
        return false;
    }


    /**
     * @param pType the POJO type
     *
     * @return the type returned by {@code QueryExecutor#insertReturningPrimary}.
     */
    protected abstract String renderFindOneType(String pType);

    /**
     *
     * @param pType the POJO type
     *
     * @return the type returned by {@code QueryExecutor#findMany}.
     */
    protected abstract String renderFindManyType(String pType);

    /**
     * @return the type returned by {@code QueryExecutor#execute}.
     */
    protected abstract String renderExecType();

    /**
     * @param tType the primary key type
     *
     * @return the type returned by {@code QueryExecutor#insertReturningPrimary}.
     */
    protected abstract String renderInsertReturningType(String tType);

    /**
     *
     * @param rType the record type
     * @param pType the POJO type
     * @param tType the primary key type
     *
     * @return the {@code QueryExecutor} used for query execution.
     */
    protected abstract String renderQueryExecutor(String rType, String pType, String tType);

    /**
     * @param rType the record type
     * @param pType the POJO type
     * @param tType the primary key type
     *
     * @return the interface implemented by the generated DAO.
     */
    protected abstract String renderDAOInterface(String rType, String pType, String tType);

    /**
     * Write the DAO constructor.
     *
     * @param out             the JavaWriter
     * @param className       the class name of the generated DAO
     * @param tableIdentifier the table identifier
     * @param rType           the record type
     * @param pType           the POJO type
     * @param tType           the primary key type
     * @param schema
     */
    protected abstract void writeDAOConstructor(JavaWriter out, String className, String tableIdentifier, String rType, String pType, String tType, String schema);

    /**
     * Write imports in the DAO.
     *
     * @param out the JavaWriter
     */
    protected void writeDAOImports(final JavaWriter out) {
    }

    /**
     * Write annotations on the DAOs class signature.
     *
     * @param out the JavaWriter
     */
    protected void writeDAOClassAnnotation(final JavaWriter out) {
    }

    /**
     * Write annotations on the DAOs constructor.
     *
     * @param out the JavaWriter
     */
    protected void writeDAOConstructorAnnotation(final JavaWriter out) {
    }

    /**
     * Can be used to overwrite certain methods, e.g. AsyncXYZ-strategies shouldn't
     * allow insertReturning for non-numeric or compound primary keys due to limitations
     * of the AsyncMySQL/Postgres client.
     *
     * @param schema
     * @param out             the JavaWriter
     * @param className       the class name of the generated DAO
     * @param tableIdentifier the table identifier
     * @param rType           the record type
     * @param pType           the POJO type
     * @param tType           the primary key type
     */
    protected void overwriteDAOMethods(final SchemaDefinition schema, final JavaWriter out, final String className, final String tableIdentifier, final String rType, final String pType, final String tType) {
    }

    /**
     * @return the fully qualified class name of the DAO's extension
     */
    protected String renderDaoExtendsClassName() {
        return AbstractVertxDAO.class.getName();
    }

    /**
     * @return the fully qualified class name of the vertx instance
     */
    protected String renderFQVertxName() {
        return Vertx.class.getName();
    }

    /**
     * Write some extra data during code generation
     *
     * @param definition      the schema
     * @param writerGenerator a Function that returns a new JavaWriter based on a File.
     *
     * @return a Collection of JavaWriters with data init.
     */
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
            //converters are handled in ComponentBasedVertxGenerator
            if (this.handleCustomTypeFromJson(column, setter, columnType, javaMemberName, out)) {
                //handled by user
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
                //if this is an enum from the database (no converter) it has getLiteral defined
                if (column.getType().getConverter() == null) {
                    jsonValueExtractor = String.format("key -> java.util.Arrays.stream(%s.values()).filter(td -> td.getLiteral().equals(json.getString(key))).findFirst().orElse(null)", columnType);
                    //otherwise just use valueOf
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
                //handled by user
            } else if (this.isEnum(table, column)) {
                //if enum is handled by custom type, try getLiteral() is not available
                if (column.getType().getConverter() == null) {
                    out.tab(2).println("json.types(\"%s\",%s()==null?null:%s().getLiteral());", this.getJsonKeyName(column), getter, getter);
                } else {
                    out.tab(2).println("json.types(\"%s\",%s()==null?null:%s().name());", this.getJsonKeyName(column), getter, getter);
                }
            } else if (this.isAllowedJsonType(column, columnType)) {
                out.tab(2).println("json.types(\"%s\",%s());", this.getJsonKeyName(column), getter);
            } else if (this.isJavaTimeType(columnType) || this.isType(columnType, BigDecimal.class)) {
                out.tab(2).println("json.types(\"%s\",%s()==null?null:%s().toString());", this.getJsonKeyName(column), getter, getter);
            } else if (this.isCollectionType(columnType)) {
                out.tab(2).println("json.types(\"%s\",%s()==null?null: new io.vertx.core.json.JsonArray(%s()));", this.getJsonKeyName(column), getter, getter);
            } else {
                logger.warn(String.format("Omitting unrecognized type %s for column %s in table %s!", columnType, column.getName(), table.getName()));
                out.tab(2).println(String.format("// Omitting unrecognized type %s for column %s!", columnType, column.getName()));
            }
        }
        out.tab(2).println("return json;");
        out.tab(1).println("}");
        out.println();
    }

    /**
     * @param column
     *
     * @return the JSON-key name of this column. Starting from version 2.4.0
     * this defaults to the name of that database column. There are different ways to change this behaviour:<br>
     * - subclass and override this method<br>
     * - subclass and override <code>VertxGeneratorStrategy#getJsonKeyName</code><br>
     * - plug-in a custom GeneratorStrategy into the <code>VertxGeneratorStrategy</code> that returns a strategy of
     * your choice for <code>GeneratorStrategy#getJavaMemberName(column, DefaultGeneratorStrategy.Mode.POJO)</code>
     */
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

    /**
     * @return the VertxGeneratorStrategy used. Unfortunately {@code getStrategy()} cannot be used because every
     * {@code GeneratorStrategy} is wrapped into a package local jOOQ-class, so casting doesn't work.
     */
    public VertxGeneratorStrategy getVertxGeneratorStrategy() {
        return this.vertxGeneratorStrategy;
    }

    private void generateFromJsonConstructor(final Definition table, final JavaWriter out, final GeneratorStrategy.Mode mode) {
        final String className = this.getStrategy().getJavaClassName(table, mode);
        out.println();
        out.tab(1).println("public %s(io.vertx.core.json.JsonObject json) {", className);
        out.tab(2).println("this();"); //call default constructor
        out.tab(2).println("fromJson(json);");
        out.tab(1).println("}");
    }

    /**
     * Copied (more ore less) from JavaGenerator.
     * Generates fetchByCYZ- and fetchOneByCYZ-methods
     *
     * @param table
     * @param out
     */
    protected void generateFetchMethods(final TableDefinition table, final JavaWriter out) {
        // 触发表类 import，保证后续能用 FBank.F_BANK（不改变行为）
        out.ref(this.getStrategy().getFullJavaClassName(table, GeneratorStrategy.Mode.DEFAULT));

        if (out instanceof VertxJavaWriter) {
            final VertxJavaWriter vOut = (VertxJavaWriter) out;
            final String pType = vOut.ref(this.getStrategy().getFullJavaClassName(table, GeneratorStrategy.Mode.POJO));
            final UniqueKeyDefinition primaryKey = table.getPrimaryKey();
            final ColumnDefinition firstPrimaryKeyColumn = primaryKey.getKeyColumns().get(0);
            final List<IndexDefinition> indexes = table.getIndexes();

            for (final ColumnDefinition column : table.getColumns()) {
                final String colName = column.getOutputName();
                final String colClass = this.getStrategy().getJavaClassName(column);
                final String colType = vOut.ref(this.getJavaType(column.getType(), out));
                final String colIdentifier = vOut.ref(this.getStrategy().getFullJavaIdentifier(column), this.colRefSegments(column));

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
                    final String colType = vOut.ref(this.getJavaType(column.getType(), out));
                    final String colIdentifier = vOut.ref(this.getStrategy().getFullJavaIdentifier(column), this.colRefSegments(column));
                    this.generateFindOneByMethods(out, pType, colName, colClass, colType, colIdentifier);
                }
            }
        } else {
            // 退化路径：普通 JavaWriter
            final String pType = out.ref(this.getStrategy().getFullJavaClassName(table, GeneratorStrategy.Mode.POJO));
            final UniqueKeyDefinition primaryKey = table.getPrimaryKey();
            final ColumnDefinition firstPrimaryKeyColumn = primaryKey.getKeyColumns().get(0);
            final List<IndexDefinition> indexes = table.getIndexes();

            for (final ColumnDefinition column : table.getColumns()) {
                final String colName = column.getOutputName();
                final String colClass = this.getStrategy().getJavaClassName(column);
                final String colType = out.ref(this.getJavaType(column.getType(), out));
                final String colIdentifier = this.safeId(this.getStrategy().getFullJavaIdentifier(column), this.colRefSegments(column));

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
                    final String colType = out.ref(this.getJavaType(column.getType(), out));
                    final String colIdentifier = this.safeId(this.getStrategy().getFullJavaIdentifier(column), this.colRefSegments(column));
                    this.generateFindOneByMethods(out, pType, colName, colClass, colType, colIdentifier);
                }
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

    /**
     * Copied from JavaGenerator
     *
     * @param key
     *
     * @return
     */
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

    /**
     * Copied from JavaGenerator
     *
     * @param column
     *
     * @return
     */
    private int colRefSegments(final TypedElementDefinition<?> column) {
        if (column != null && column.getContainer() instanceof UDTDefinition) {
            return 2;
        }

        if (!this.getStrategy().getInstanceFields()) {
            return 2;
        }

        return 3;
    }

    /**
     * copied from jOOQ's JavaGenerator
     *
     * @param table
     * @param out1
     */
    @Override
    protected void generateDao(final TableDefinition table, final JavaWriter out1) {
        final UniqueKeyDefinition key = table.getPrimaryKey();
        if (key == null) {
            logger.info("Skipping DAO generation", out1.file().getName());
            return;
        }
        if (out1 instanceof VertxJavaWriter) {
            final VertxJavaWriter out = (VertxJavaWriter) out1;
            this.generateDAO(key, table, out);
        } else {
            this.generateDAO(key, table, out1); // 退化路径
        }
    }

    protected void generatePojoClassAnnotations(final JavaWriter out, final TableDefinition schema) {
    }

    /** 原有签名保留：VertxJavaWriter 专用路径 */
    private void generateDAO(final UniqueKeyDefinition key, final TableDefinition table, final VertxJavaWriter out) {
        final String className = this.getStrategy().getJavaClassName(table, GeneratorStrategy.Mode.DAO);
        final List<String> interfaces = out.ref(this.getStrategy().getJavaClassImplements(table, GeneratorStrategy.Mode.DAO));
        final String tableRecord = out.ref(this.getStrategy().getFullJavaClassName(table, GeneratorStrategy.Mode.RECORD));
        final String daoImpl = out.ref(this.renderDaoExtendsClassName());
        final String tableIdentifier = out.ref(this.getStrategy().getFullJavaIdentifier(table), 2);

        String tType = "Void";
        final String pType = out.ref(this.getStrategy().getFullJavaClassName(table, GeneratorStrategy.Mode.POJO));

        final List<ColumnDefinition> keyColumns = key.getKeyColumns();

        if (keyColumns.size() == 1) {
            tType = this.getJavaType(keyColumns.get(0).getType(), out);
        } else if (keyColumns.size() <= Constants.MAX_ROW_DEGREE) {
            final StringBuilder generics = new StringBuilder();
            String separator = "";

            for (final ColumnDefinition column : keyColumns) {
                generics.append(separator).append(out.ref(this.getJavaType(column.getType(), out)));
                separator = ", ";
            }

            tType = Record.class.getName() + keyColumns.size() + "<" + generics + ">";
        } else {
            tType = Record.class.getName();
        }

        tType = out.ref(tType);
        interfaces.add(this.renderDAOInterface(tableRecord, pType, tType)); //let DAO implement the right DAO-interface

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

        // Template method implementations
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

    /** 新增：普通 JavaWriter 退化路径（方法名不同，但仅为私有重载，不影响原有对外签名） */
    private void generateDAO(final UniqueKeyDefinition key, final TableDefinition table, final JavaWriter out) {
        // 触发表类 import（仅导入类型 FBank）
        out.ref(this.getStrategy().getFullJavaClassName(table, GeneratorStrategy.Mode.DEFAULT));

        final String className = this.getStrategy().getJavaClassName(table, GeneratorStrategy.Mode.DAO);
        final List<String> interfaces = out.ref(this.getStrategy().getJavaClassImplements(table, GeneratorStrategy.Mode.DAO));
        final String tableRecord = out.ref(this.getStrategy().getFullJavaClassName(table, GeneratorStrategy.Mode.RECORD));
        final String daoImpl = out.ref(this.renderDaoExtendsClassName());
        final String tableIdentifier = this.safeId(this.getStrategy().getFullJavaIdentifier(table), 2);

        String tType = "Void";
        final String pType = out.ref(this.getStrategy().getFullJavaClassName(table, GeneratorStrategy.Mode.POJO));

        final List<ColumnDefinition> keyColumns = key.getKeyColumns();

        if (keyColumns.size() == 1) {
            tType = this.getJavaType(keyColumns.get(0).getType(), out);
        } else if (keyColumns.size() <= Constants.MAX_ROW_DEGREE) {
            final StringBuilder generics = new StringBuilder();
            String separator = "";

            for (final ColumnDefinition column : keyColumns) {
                generics.append(separator).append(out.ref(this.getJavaType(column.getType(), out)));
                separator = ", ";
            }

            tType = Record.class.getName() + keyColumns.size() + "<" + generics + ">";
        } else {
            tType = Record.class.getName();
        }

        tType = out.ref(tType);
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
