package io.r2mo.dbe.common.enums;

/**
 * @author lang : 2025-08-29
 */
public enum DatabaseType {
    MYSQL_5,
    MYSQL_8,
    MYSQL_9,
    PGSQL,
    SQLITE_3,
    H2;

    public static boolean isMySQL(final DatabaseType type) {
        return type == MYSQL_5 || type == MYSQL_8 || type == MYSQL_9;
    }
}
