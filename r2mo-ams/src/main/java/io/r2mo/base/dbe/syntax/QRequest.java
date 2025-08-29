package io.r2mo.base.dbe.syntax;

import io.r2mo.spi.SPIConnect;
import io.r2mo.typed.json.JBase;

/**
 * @author lang : 2025-08-28
 */
public interface QRequest extends SPIConnect {

    String field();

    <T extends JBase> T data();

    boolean isOk();

    <R> R item();
}
