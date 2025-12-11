package io.r2mo.jce.component.secure;

import java.util.Locale;

public enum AlgNorm {
    RSA("RSA"),
    DSA("DSA"),
    EC("EC"),
    ED25519("Ed25519"),
    ED448("Ed448"),
    X25519("X25519"),
    X448("X448");

    private final String jcaName;

    AlgNorm(final String jcaName) {
        this.jcaName = jcaName;
    }

    /** 宽松解析：支持枚举名或 JCA 名称，大小写不敏感 */
    public static AlgNorm from(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("[ R2MO ] 传入名称不可为 null");
        }
        final String n = name.trim();
        // 先按枚举名匹配（RSA/EC/ED25519等）
        try {
            return AlgNorm.valueOf(n.toUpperCase(Locale.ROOT));
        } catch (final IllegalArgumentException ignored) {
            // 再按 JCA 名称匹配（"Ed25519"、"X25519" 等）
            for (final AlgNorm a : values()) {
                if (a.jcaName.equalsIgnoreCase(n)) {
                    return a;
                }
            }
            throw new IllegalArgumentException("[ R2MO ] 算法不支持：" + name);
        }
    }

    /** 返回对应的 JCA 算法名（如 "RSA"、"Ed25519"） */
    public String jcaName() {
        return this.jcaName;
    }
}
