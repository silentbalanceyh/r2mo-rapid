package io.r2mo.jce.common;

import org.bouncycastle.util.encoders.Base64;

import java.nio.charset.StandardCharsets;

/**
 * @author lang : 2025-09-19
 */
class CoderBase64 implements Coder {
    @Override
    public String encode(final String data) {
        final byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        final byte[] encoded = Base64.encode(bytes);
        return Base64.toBase64String(encoded);
    }

    @Override
    public String decode(final String data) {
        final byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        final byte[] decoded = Base64.decode(bytes);
        return new String(decoded, StandardCharsets.UTF_8);
    }
}
