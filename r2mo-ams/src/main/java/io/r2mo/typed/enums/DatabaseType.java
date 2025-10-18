package io.r2mo.typed.enums;

import jdk.jfr.Experimental;

/**
 * @author lang : 2025-08-29
 */
public enum DatabaseType {
    MARIADB,    // MariaDB

    SQLLITE_3,    // SQLite
    TRINO,      // Trino
    YUGABYTEDB, // YugabyteDB

    DERBY,      // Derby 10.16.1.1   Embedded / Remote
    FIREBIRD,   // Friebird Database > 4
    H2,         // H2 Database > 2
    HSQLDB,     // HSQLDB

    @Experimental
    DUCKDB,    // Duck DB
    @Deprecated
    CUBRID,     // CUBRID
    @Deprecated
    IGNITE,     // Ignite

    // MySQL
    MYSQL_5,     // MySQL 5.x
    MYSQL_8,     // MySQL 8.x
    MYSQL_9,     // MySQL 9.x
    TIDB,        // MySQL

    // PgSQL
    PGSQL,   // PgSQL
    COCKROACHDB,// CockroachDB 42.6.0

    // --------------------- Jooq exclude
    ORACLE12,   // > 12
    ORACLE,     // < 12 Order Old


    // Created based on JDBC protocol
    REDSHIFT,   // Amazon Redshift 2.1.0.18
    CASSANDRA,  // Apache Cassandra 1.4
    HIVE,       // Apache Hive 3.1.2
    SPARK,      // Apache Spark 1.2.2
    AURORA,     // Aurora MySQL 1.1.9
    CLICKHOUSE, // ClickHouse 0.4.2
    COUCHBASE,  // Couchbase 0.6

    // SQL Server
    MSSQL2000,  // SQLServer
    MSSQL,      // SQLServer ( New Version )
    AZURE_SQL,  // Azure SQL 12.2.0
    AZURE_SA    // Azure Synapse Analytics 12.2.0
    ;

    public static boolean isMySQL(final DatabaseType type) {
        return type == MYSQL_5 || type == MYSQL_8 || type == MYSQL_9;
    }
}
