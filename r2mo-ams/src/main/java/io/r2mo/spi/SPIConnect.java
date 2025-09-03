package io.r2mo.spi;

import io.r2mo.base.web.ForStatus;
import io.r2mo.typed.json.JUtil;

import java.util.concurrent.ConcurrentMap;

/**
 * 特殊接口连接点
 *
 * @author lang : 2025-08-28
 */
public interface SPIConnect {

    FactoryObject SPI_OBJECT = ProviderOfFactory.forObject();

    FactoryDBAction SPI_DB = ProviderOfFactory.forDBAction();

    FactoryIo SPI_IO = ProviderOfFactory.forIo();

    FactoryWeb SPI_WEB = ProviderOfFactory.forWeb();

    ConcurrentMap<Class<?>, Class<?>> SPI_META = ProviderOfFactory.meta();
    /* 下边是专用的接口部分用来处理特定场景下的相关内容 */
    JUtil _UTJ = SPI_OBJECT.jsonUtil();

    ForStatus STATUS = SPI_WEB.ofStatus();
}
