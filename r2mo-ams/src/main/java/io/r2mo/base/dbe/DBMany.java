package io.r2mo.base.dbe;

import io.r2mo.spi.FactoryDBAction;
import io.r2mo.spi.SPI;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 动态数据源管理器，用于管理多个 {@link Database} 来执行序列化和反序列化的相关操作
 *
 * @author lang : 2025-10-18
 */
@Slf4j
public class DBMany {
    private static DBMany INSTANCE = new DBMany();
    private final ConcurrentMap<String, DBS> ds = new ConcurrentHashMap<>();
    private DBS master;

    private DBMany() {
    }

    public static DBMany of() {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new DBMany();
        }
        return INSTANCE;
    }

    public DBMany put(final String name, final Database database) {
        Objects.requireNonNull(database, "[ R2MO ] 数据库配置对象不可为空！");
        final FactoryDBAction db = SPI.SPI_DB;
        final DBS dbs = db.configure(database);
        
        if ("master".equals(name)) {
            this.master = dbs;
        }
        this.ds.put(name, dbs);
        return this;
    }

    public DBS get(final String name) {
        return this.ds.get(name);
    }

    public DBS get() {
        return this.master;
    }
}
