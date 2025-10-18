package io.r2mo.typed.json;

import io.r2mo.base.io.HStore;
import io.r2mo.spi.SPI;
import io.r2mo.typed.exception.web._500ServerInternalException;

import java.util.Objects;

/**
 * @author lang : 2025-10-18
 */
public interface JElement {

    JObject toJObject();

    <T extends JElement> T fromJObject(JObject json);

    default <T extends JElement> T fromFile(final String filename) {
        final HStore store = SPI.V_STORE;
        if (Objects.isNull(store)) {
            throw new _500ServerInternalException("[ R2MO ] 检查到 HStore 存储服务不可用，无法加载 JSON 文件：" + filename);
        }
        final String filepath = store.pHome(filename);
        final JObject json = store.inJson(filepath);
        return this.fromJObject(json);
    }
}
