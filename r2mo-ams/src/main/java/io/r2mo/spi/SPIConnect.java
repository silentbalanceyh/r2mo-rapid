package io.r2mo.spi;

import io.r2mo.typed.json.JUtil;

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

    JUtil _UTJ = SPI_OBJECT.jsonUtil();
}
