package io.r2mo.typed.common;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author lang : 2025-09-03
 */
public class GdbSigner {
    private final String appKey;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GdbSigner(final String appKey) {
        this.appKey = appKey;
    }

    public String appKey() {
        return this.appKey;
    }

    public String sign(final Map<String, Object> payload) {
        try {
            // 1. 对 value 为 Map 结构的字段进行 JSON 编码
            final Map<String, Object> m = new TreeMap<>();
            for (final var e : payload.entrySet()) {
                final Object v = e.getValue();
                if (v instanceof Map<?, ?>) {
                    m.put(e.getKey(), this.objectMapper.writeValueAsString(v));
                } else {
                    m.put(e.getKey(), v);
                }
            }
            // 2. 拼 query string
            final String qs = m.entrySet().stream()
                .map(en -> en.getKey() + "=" + en.getValue())
                .reduce((a, b) -> a + "&" + b)
                .orElse("");

            // 3. 拼接 appKey
            final String s = this.appKey + "&" + qs + "&" + this.appKey;
            final MessageDigest md5 = MessageDigest.getInstance("MD5");
            final byte[] digest = md5.digest(s.getBytes(StandardCharsets.UTF_8));
            final StringBuilder hex = new StringBuilder();
            for (final byte b : digest) hex.append(String.format("%02x", b & 0xFF));
            return hex.toString();
        } catch (final Exception e) {
            throw new RuntimeException("[ R2MO ] 签名失败", e);
        }
    }
}
