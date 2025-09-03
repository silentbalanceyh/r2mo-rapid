package io.r2mo.spi;

import io.r2mo.base.io.HStore;
import io.r2mo.base.web.ForAbort;
import io.r2mo.base.web.ForLocale;
import io.r2mo.base.web.ForStatus;
import io.r2mo.typed.json.JUtil;

import java.util.concurrent.ConcurrentMap;

/**
 * 特殊接口连接点
 *
 * @author lang : 2025-08-28
 */
public interface SPI {

    FactoryObject SPI_OBJECT = ProviderOfFactory.forObject();

    FactoryDBAction SPI_DB = ProviderOfFactory.forDBAction();

    FactoryIo SPI_IO = ProviderOfFactory.forIo();

    FactoryWeb SPI_WEB = ProviderOfFactory.forWeb();

    ConcurrentMap<Class<?>, Class<?>> SPI_META = ProviderOfFactory.meta();
    /* 下边是专用的接口部分用来处理特定场景下的相关内容，一般是直接组件 */
    JUtil V_UTIL = SPI_OBJECT.jsonUtil();

    HStore V_STORE = SPI_IO.ioAction();

    ForStatus V_STATUS = SPI_WEB.ofStatus();

    ForLocale V_LOCALE = SPI_WEB.ofLocale();

    ForAbort V_ABORT = SPI_WEB.ofAbort();
}
