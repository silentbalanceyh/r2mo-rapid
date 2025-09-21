package io.r2mo.io.local.operation;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lang : 2025-09-20
 */
class SecurityMap {
    static final Map<String, String> ALG_MAP = new HashMap<>();

    static {
        // RSA 系列
        ALG_MAP.put("RSA", "RSA");

        // ECC 系列
        ALG_MAP.put("EC", "EC");

        // EdDSA 系列
        ALG_MAP.put("EDDSA", "EdDSA");
        ALG_MAP.put("ED25519", "EdDSA");
        ALG_MAP.put("ED448", "EdDSA");

        // XDH 系列
        ALG_MAP.put("X25519", "XDH");
        ALG_MAP.put("X448", "XDH");
        ALG_MAP.put("XDH", "XDH");

        // 国密系列
        ALG_MAP.put("SM2", "SM2");
    }
}
