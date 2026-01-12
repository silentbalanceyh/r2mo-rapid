package io.r2mo.vertx.jooq.generate;

import io.r2mo.base.dbe.Database;
import io.r2mo.typed.enums.DatabaseType;
import org.jooq.meta.clickhouse.ClickHouseDatabase;
import org.jooq.meta.derby.DerbyDatabase;
import org.jooq.meta.duckdb.DuckDBDatabase;
import org.jooq.meta.firebird.FirebirdDatabase;
import org.jooq.meta.h2.H2Database;
import org.jooq.meta.hsqldb.HSQLDBDatabase;
import org.jooq.meta.jaxb.*;
import org.jooq.meta.mariadb.MariaDBDatabase;
import org.jooq.meta.mysql.MySQLDatabase;
import org.jooq.meta.postgres.PostgresDatabase;
import org.jooq.meta.sqlite.SQLiteDatabase;
import org.jooq.meta.trino.TrinoDatabase;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * @author lang : 2025-10-20
 */
public class JooqSourceConfigurer {

    private static final JooqSourceConfigurer INSTANCE = new JooqSourceConfigurer();
    private static final ConcurrentMap<DatabaseType, String> DATABASE = new ConcurrentHashMap<>() {
        {
            // MySQL 系列
            this.put(DatabaseType.MYSQL_5, MySQLDatabase.class.getName());
            this.put(DatabaseType.MYSQL_8, MySQLDatabase.class.getName());
            this.put(DatabaseType.MYSQL_9, MySQLDatabase.class.getName());
            this.put(DatabaseType.TIDB, MySQLDatabase.class.getName());

            // PostgreSQL 系列
            this.put(DatabaseType.PGSQL, PostgresDatabase.class.getName());
            this.put(DatabaseType.COCKROACHDB, PostgresDatabase.class.getName());

            // H2
            this.put(DatabaseType.H2, H2Database.class.getName());

            // HSQLDB
            this.put(DatabaseType.HSQLDB, HSQLDBDatabase.class.getName());

            // Derby
            this.put(DatabaseType.DERBY, DerbyDatabase.class.getName());

            // Firebird
            this.put(DatabaseType.FIREBIRD, FirebirdDatabase.class.getName());

            // SQLite
            this.put(DatabaseType.SQLLITE_3, SQLiteDatabase.class.getName());

            // MariaDB
            this.put(DatabaseType.MARIADB, MariaDBDatabase.class.getName());

            // 其他数据库
            this.put(DatabaseType.YUGABYTEDB, PostgresDatabase.class.getName()); // YugabyteDB 兼容 PostgreSQL
            this.put(DatabaseType.TRINO, TrinoDatabase.class.getName()); // Trino 可能需要自定义实现
            this.put(DatabaseType.DUCKDB, DuckDBDatabase.class.getName()); // DuckDB 可能需要自定义实现
            this.put(DatabaseType.REDSHIFT, PostgresDatabase.class.getName()); // Redshift 兼容 PostgreSQL
            this.put(DatabaseType.CLICKHOUSE, ClickHouseDatabase.class.getName()); // ClickHouse 可能需要自定义实现
        }
    };

    private JooqSourceConfigurer() {
    }

    public static JooqSourceConfigurer of() {
        return INSTANCE;
    }

    private String resolveProperty(final String value, final Function<String, String> resolver) {
        if (value == null || resolver == null) {
            return value;
        }
        return resolver.apply(value);
    }

    public Configuration configure(final JooqSourceConfiguration inputConfiguration) {
        final Database database = inputConfiguration.database();
        final Function<String, String> resolver = inputConfiguration.resolver();
        final String databaseClass = DATABASE.get(database.getType());
        return new Configuration()
            // JDBC 连接配置
            .withJdbc(new Jdbc()
                .withDriver(database.getDriverClassName())
                .withUrl(database.getUrl())
                .withUser(this.resolveProperty(database.getUsername(), resolver))
                .withPassword(this.resolveProperty(database.getPassword(), resolver))
                // ✅ 直接用 Property，可变参数形式
                .withProperties(
                    new Property().withKey("serverTimezone").withValue("Asia/Shanghai"),
                    new Property().withKey("useUnicode").withValue("true"),
                    new Property().withKey("characterEncoding").withValue("UTF-8"),
                    new Property().withKey("autoReconnect").withValue("true"),
                    new Property().withKey("failOverReadOnly").withValue("false"),
                    new Property().withKey("useSSL").withValue("false"),
                    new Property().withKey("allowPublicKeyRetrieval").withValue("true")
                )
            )
            .withGenerator(new Generator()
                .withName(Objects.requireNonNull(inputConfiguration.classGenerator(),
                    "[ PLUG ] 代码生成器类不可为 null.").getName())
                .withDatabase(new org.jooq.meta.jaxb.Database()
                    .withName(databaseClass)
                    .withInputSchema(database.getInstance())
                    .withOutputSchema("ZDB")
                    .withIncludes(inputConfiguration.databaseIncludes())
                    .withExcludes(inputConfiguration.databaseExcludes())
                    .withUnsignedTypes(false)
                    .withSyntheticPrimaryKeys("public\\..*\\.id") // 为所有 public schema 中的 id 字段生成假主键
                    .withOverridePrimaryKeys("override_primmary_key") // 假主键名称
                )
                .withGenerate(new Generate()
                    .withDaos(true)
                    .withPojos(true)
                    .withJavaTimeTypes(true)
                    .withInterfaces(true)
                    .withFluentSetters(true)
                )
                .withTarget(new Target()
                    .withPackageName(Objects.requireNonNull(inputConfiguration.classPackage(),
                        "[ PLUG ] 目标包名不可为 null.").getName() + ".domain")
                    .withDirectory(inputConfiguration.directory())
                )
                .withStrategy(new Strategy()
                    .withName(Objects.requireNonNull(inputConfiguration.classStrategy(),
                        "[ PLUG ] 代码生成策略类不可为 null.").getName())
                )
            )
            ;
        // Generator 配置
    }
}
