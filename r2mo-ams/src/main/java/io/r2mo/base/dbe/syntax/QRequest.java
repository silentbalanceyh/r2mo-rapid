package io.r2mo.base.dbe.syntax;

import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JBase;

/**
 * @author lang : 2025-08-28
 */
public interface QRequest extends SPI {

    String field();

    <T extends JBase> T data();

    boolean isOk();

    <R> R item();
}
