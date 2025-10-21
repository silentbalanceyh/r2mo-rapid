package io.r2mo.base.secure;

import io.r2mo.spi.SPI;
import io.r2mo.typed.annotation.SPID;
import lombok.extern.slf4j.Slf4j;

import java.util.Base64;
import java.util.List;
import java.util.Objects;

/**
 * @author lang : 2025-10-21
 */
@Slf4j
class EDCryptoDoctor {
    /**
     * 尝试解密数据库密码：
     * 1) password 为空/空白 —— 直接返回原文；
     * 2) 不是“像密文”的字符串（长度/格式启发式）—— 直接返回原文；
     * 3) 存在 EDCrypto 的 SPI 实现（优先带 @SPID(EDCrypto.FOR_DATABASE)）—— 尝试解密；
     * 4) 解密异常或无实现 —— 回退返回原文。
     * 说明：
     * - 这里不用环境变量，只用组件 SPI；
     * - “是否像密文”的判断使用：ENC(...) 包裹、或 Base64 形态且长度较大（RSA/SM2 常见密文很长）。
     *
     * @param password 原始配置中的密码（明文或密文）
     *
     * @return 若判断为密文且成功解密则返回明文，否则返回传入值本身
     */
    static String decryptPassword(final String password) {

        if (password == null) {
            return null;
        }
        final String raw = password.trim();
        if (raw.isEmpty()) {
            return raw;
        }

        // 先做快速判断：不像密文——直接返回
        if (!isLikelyCiphertext(raw)) {
            return raw;
        }

        try {
            final List<EDCrypto> cryptoList = SPI.findMany(EDCrypto.class);
            if (cryptoList.isEmpty()) {
                // 没有可用实现，回退
                return raw;
            }

            // 优先选择标注了 @SPID(EDCrypto.FOR_DATABASE) 的实现
            final EDCrypto crypto = cryptoList.stream()
                .filter(impl -> {
                    final SPID spid = impl.getClass().getDeclaredAnnotation(SPID.class);
                    return spid != null && Objects.equals(spid.value(), EDCrypto.FOR_DATABASE);
                })
                .findFirst()
                .orElse(cryptoList.get(0)); // 没有带标注的就退化用第一个

            // 去掉可选包装 ENC(...) 再解密
            final String material = unwrapEnc(raw);
            final String decrypted = crypto.decrypt(material);
            return (decrypted != null) ? decrypted : raw;
        } catch (final Throwable ex) {
            // 任何异常都不要影响启动，记录一下并回退
            log.warn("[Liquibase] 密码解密失败，回退原文使用：{}", ex.getMessage());
            return raw;
        }
    }

    /**
     * 是否“像密文”：满足以下任一即认为可能是密文
     * - 形如 ENC(....)
     * - Base64 形态且长度较长（>= 120），常见 RSA/SM2 密文都会很长
     */
    private static boolean isLikelyCiphertext(final String s) {
        if (s.startsWith("ENC(") && s.endsWith(")")) {
            return true;
        }
        // 简单的 Base64 形态判定（URL/标准均可）
        final String b64 = s.replace("=", "");
        final boolean base64ish = b64.matches("^[A-Za-z0-9+/_-]+$");
        if (base64ish && s.length() >= 120) {
            // 再尝试一次真实 Base64 解码，避免把普通长串误判
            try {
                Base64.getDecoder().decode(s);
                return true;
            } catch (final IllegalArgumentException ignore) {
                // 不是严格 Base64，则按“不是密文”处理
            }
        }
        return false;
    }

    /** 去掉 ENC(...) 包装（若有），否则原样返回 */
    private static String unwrapEnc(final String s) {
        if (s.startsWith("ENC(") && s.endsWith(")") && s.length() > 5) {
            return s.substring(4, s.length() - 1);
        }
        return s;
    }
}
