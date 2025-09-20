package io.r2mo.base.util;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author lang : 2025-09-20
 */
@Slf4j
class UTJvm {

    static byte[] serialize(final Object object) {
        try (final ByteArrayOutputStream bos = new ByteArrayOutputStream();
             final ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(object);
            return bos.toByteArray();
        } catch (final Exception e) {
            log.error("[ R2MO ] 序列化对象失败", e);
            return new byte[0];
        }
    }

    @SuppressWarnings("unchecked")
    static <T> T deserialize(final byte[] bytes) {
        try (final ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             final ObjectInputStream ois = new ObjectInputStream(bis)) {
            return (T) ois.readObject();
        } catch (final Exception e) {
            log.error("[ R2MO ] 反序列化对象失败", e);
            return null;
        }
    }
}
