package io.r2mo.jce.common;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author lang : 2025-09-19
 */
class CoderURL implements Coder {
    @Override
    public String encode(final String data) {
        return URLEncoder.encode(data, StandardCharsets.UTF_8);
    }

    @Override
    public String decode(final String data) {
        return URLDecoder.decode(data, StandardCharsets.UTF_8);
    }
}
