package io.r2mo.dbe.common.hikari;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.r2mo.base.dbe.DBS;
import io.r2mo.base.dbe.Database;
import io.r2mo.typed.annotation.SPID;
import io.r2mo.typed.cc.Cc;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.util.Objects;

/**
 *
 * @author lang : 2025-10-18
 */
@Slf4j
@SPID(HikariName.HIKARI_SPID)
public class HikariCPFactory implements DBS.CPFactory {
    private static final Cc<Integer, HikariDataSource> CC_SOURCE = Cc.open();

    @Override
    public DataSource createDatasource(final Database database) {
        Objects.requireNonNull(database, "[ R2MO ] 创建数据源时，数据库配置不能为空！");
        return CC_SOURCE.pick(() -> {
            // 数据库基本配置
            final HikariConfig hikariConfig = new HikariConfig();

            // 初始化数据库配置
            HikariBuilder.of().initialize(hikariConfig, database);

            // 构造数据源
            try {
                return new HikariDataSource(hikariConfig);
            } catch (final Exception ex) {
                log.error(ex.getMessage(), ex);
                return null;
            }
        }, database.hashCode());
    }
}
