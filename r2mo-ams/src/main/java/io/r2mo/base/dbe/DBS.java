package io.r2mo.base.dbe;

import io.r2mo.function.Fn;

import javax.sql.DataSource;
import javax.sql.XADataSource;
import java.sql.Connection;
import java.util.Set;

/**
 * 数据源的管理器，用于处理当前底层对接的连接池操作，DBS 用于连接池，接入 {@link DBMany} 进行统一管理
 *
 * @author lang : 2025-10-18
 */
public interface DBS {

    /**
     * 获取连接池定义
     *
     * @return 返回数据库定义
     */
    Database getDatabase();

    DataSource getDs();

    /**
     * 获取连接
     *
     * @return 返回SQL的原生连接
     */
    default Connection getConnection() {
        return Fn.jvmOr(this.getDs()::getConnection);
    }

    /**
     * @author lang : 2025-10-18
     */
    interface CPFactory {

        Set<String> NAMES = Set.of(
            "hikari",
            "tomcat",
            "dbcp2",
            "user-cp"
        );

        DataSource createDatasource(Database database);

        default XADataSource createXADatasource(final Database database) {
            return null;
        }
    }
}
