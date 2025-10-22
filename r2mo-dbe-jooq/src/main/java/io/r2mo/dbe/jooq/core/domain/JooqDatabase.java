package io.r2mo.dbe.jooq.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.r2mo.base.dbe.Database;
import io.r2mo.typed.enums.DatabaseType;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Configuration;
import org.jooq.ConnectionProvider;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;

import javax.sql.DataSource;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 多出来的部分就是 Jooq 的专有配置
 *
 * @author lang : 2025-10-18
 */
@Slf4j
public class JooqDatabase extends Database {


    @JsonIgnore
    private Configuration configuration;

    @JsonIgnore
    private DSLContext context;

    public Configuration configure(final DataSource dataSource) {
        if (Objects.nonNull(this.configuration)) {
            return this.configuration;
        }
        final Configuration configuration = new DefaultConfiguration();
        final SQLDialect dialect = DIALECT.getOrDefault(this.getType(), SQLDialect.DEFAULT);
        configuration.set(dialect);
        /*
         * 🔧 jOOQ 在幕后使用了一个 ConnectionProvider，并且在执行语句前总会调用 **acquire()**，
         *    执行后会调用 **release()**。是否会关闭 JDBC 连接，取决于你的 ConnectionProvider 配置。
         *    例如：
         *    • 传入独立 Connection（DefaultConnectionProvider）➡️ 连接 **不会被关闭**。
         *    • 传入 DataSource（DataSourceConnectionProvider）➡️ 连接 **会被关闭**。
         *
         * 🧩 旧代码：
         *   final Connection connection = FnZero.getJvm(() -> pool.getDataSource().getConnection());
         *   final ConnectionProvider provider = new DefaultConnectionProvider(connection);
         *
         * 🚀 新写法：
         *   final ConnectionProvider provider = new DataSourceConnectionProvider(pool.getDataSource());
         *
         * ✅ 含义：改用 `DataSourceConnectionProvider`，
         *    由 jOOQ 的后台流程通过 **acquire()/release()** 生命周期管理连接
         *    （在 DataSource 场景下一般在 **release()** 时关闭连接）。🔁
         */
        final ConnectionProvider provider = new DataSourceConnectionProvider(dataSource);
        configuration.set(provider);
        this.configuration = configuration;
        return configuration;
    }

    public DSLContext getContext() {
        if (Objects.isNull(this.configuration)) {
            log.warn("[ R2MO ] （获取上下文）数据库配置还未初始化，请先调用 configure 初始化数据库！");
            return null;
        }
        if (Objects.isNull(this.context)) {
            this.context = DSL.using(this.configuration);
        }
        return this.context;
    }

    public Configuration getConfiguration() {
        if (Objects.isNull(this.configuration)) {
            log.warn("[ R2MO ] 数据库配置未初始化，请先调用 configure 初始化数据库！");
            return null;
        }
        return this.configuration;
    }

    @SuppressWarnings("all")
    public static ConcurrentMap<DatabaseType, SQLDialect> DIALECT = new ConcurrentHashMap<>() {
        {
            // Jooq Supported Default
            // MySQL
            this.put(DatabaseType.MYSQL_8, SQLDialect.MYSQL);
            this.put(DatabaseType.MYSQL_5, SQLDialect.MYSQL);
            this.put(DatabaseType.TIDB, SQLDialect.MYSQL);

            // PgSQL
            this.put(DatabaseType.PGSQL, SQLDialect.POSTGRES);
            this.put(DatabaseType.COCKROACHDB, SQLDialect.POSTGRES);

            // Other
            this.put(DatabaseType.MARIADB, SQLDialect.MARIADB);
            this.put(DatabaseType.SQLLITE_3, SQLDialect.SQLITE);
            this.put(DatabaseType.TRINO, SQLDialect.TRINO);
            this.put(DatabaseType.YUGABYTEDB, SQLDialect.YUGABYTEDB);
            this.put(DatabaseType.DERBY, SQLDialect.DERBY);
            this.put(DatabaseType.FIREBIRD, SQLDialect.FIREBIRD);
            this.put(DatabaseType.H2, SQLDialect.H2);
            this.put(DatabaseType.HSQLDB, SQLDialect.HSQLDB);

            // Experimental / Deprecated
            this.put(DatabaseType.DUCKDB, SQLDialect.DUCKDB);
            this.put(DatabaseType.CUBRID, SQLDialect.CUBRID);
            this.put(DatabaseType.IGNITE, SQLDialect.IGNITE);

            // Other will use DEFAULT instead for future
            for (DatabaseType category : DatabaseType.values()) {
                if (!this.containsKey(category)) {
                    this.put(category, SQLDialect.DEFAULT);
                }
            }
        }
    };
}
