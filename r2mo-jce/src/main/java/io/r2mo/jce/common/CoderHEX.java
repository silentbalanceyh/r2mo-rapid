package io.r2mo.jce.common;

import org.bouncycastle.util.encoders.Hex;

import java.nio.charset.StandardCharsets;

/**
 * @author lang : 2025-09-19
 */
class CoderHEX implements Coder {

    @Override
    public String encode(final String data) {
        final byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        return Hex.toHexString(bytes);
    }

    @Override
    public String decode(final String data) {
        final byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        final byte[] decoded = Hex.decode(bytes);
        return new String(decoded, StandardCharsets.UTF_8);
    }
}
