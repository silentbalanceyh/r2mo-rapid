package io.r2mo.dbe.jooq.core.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import io.r2mo.base.dbe.DBS;
import io.r2mo.base.dbe.Database;
import io.r2mo.spi.SPI;
import io.r2mo.typed.cc.Cc;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.util.Objects;

/**
 * @author lang : 2025-10-18
 */
@Slf4j
public class JooqDBS implements DBS {

    private static final Cc<String, DBS> CC_DBS = Cc.open();
    private static final Cc<String, DBS.CPFactory> CC_CPF = Cc.open();

    private final Database database;
    private final DataSource dataSource;

    private JooqDBS(final Database database) {
        // 创建数据源
        final DBS.CPFactory factory = this.lookupCPFactory(database);
        final DataSource datasource = factory.createDatasource(database);


        // 初始化 JooqDatabase
        final JooqDatabase databaseJq = new JooqDatabase();
        BeanUtil.copyProperties(database, databaseJq,
            CopyOptions.create().ignoreError().ignoreNullValue()
        );
        databaseJq.configure(datasource);

        this.database = databaseJq;
        this.dataSource = datasource;
    }

    private CPFactory lookupCPFactory(final Database database) {
        // 查找唯一的 DBCP 名称
        final String found = database.findNameOfDBCP();
        // 先根据 Database 构造 DataSource
        return CC_CPF.pick(() -> {
            final CPFactory factory = SPI.findOne(CPFactory.class, found);
            if (Objects.isNull(factory)) {
                log.error("[ R2MO ] 未配置连接池工厂：{}，请检查相关配置！", found);
            }
            return factory;
        }, found);
    }

    public static DBS getOrCreate(final Database database) {
        final String cacheKey = database.getUrl();
        return CC_DBS.pick(() -> new JooqDBS(database), cacheKey);
    }

    @Override
    public Database getDatabase() {
        return this.database;
    }

    @Override
    public DataSource getDs() {
        return this.dataSource;
    }
}
