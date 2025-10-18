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

    /**
     * 此处不使用 Fluent 的 API，主要是用于创建 DBS 专用，而 DBMany 自身并不需要链式调用
     *
     * @param name     数据库名称
     *                 - 1）如果是 application.yml 中配置，则直接就是配置名称
     *                 - 2）如果是代码编程中直接添加，则是动态模式 {@link Database#hashCode()} 的名称
     *                 - 3）如果是动态管理，则直接在数据表中使用 X_SOURCE -> NAME 进行对应，最终可统一管理
     * @param database 数据库配置
     *
     * @return 数据库实例
     */
    public DBS put(final String name, final Database database) {
        Objects.requireNonNull(database, "[ R2MO ] 数据库配置对象不可为空！");
        final FactoryDBAction db = SPI.SPI_DB;
        final DBS dbs = db.configure(database);

        if ("master".equals(name)) {
            this.master = dbs;
        }
        this.ds.put(name, dbs);
        return dbs;
    }

    public DBS get(final String name) {
        return this.ds.get(name);
    }

    public DBS get() {
        return this.master;
    }
}
