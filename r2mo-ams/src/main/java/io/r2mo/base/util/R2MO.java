package io.r2mo.base.util;

/**
 * Ams 工具类 Tool
 *
 * @author lang : 2025-09-20
 */
public class R2MO {

    public static byte[] serialize(final Object object) {
        return UTJvm.serialize(object);
    }

    public static <T> T deserialize(final byte[] bytes) {
        return UTJvm.deserialize(bytes);
    }
}
