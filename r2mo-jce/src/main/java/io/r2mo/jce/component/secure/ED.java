package io.r2mo.jce.component.secure;

import io.r2mo.base.secure.EDPair;
import io.r2mo.typed.cc.Cc;

/**
 * @author lang : 2025-10-20
 */
public interface ED {
    Cc<String, ED> CC_ED = Cc.openThread();

    static ED encryptOfPublic(final AlgNorm algorithm) {
        return CC_ED.pick(() -> new EPublicDPrivate(algorithm), algorithm.jcaName());
    }

    static ED encryptOfPrivate(final AlgNorm algorithm) {
        return CC_ED.pick(() -> new EPrivateDPublic(algorithm), algorithm.jcaName());
    }

    // 根据算法生成公私钥对
    EDPair generate(int size);

    // 加密（使用指定密钥内容）
    String encrypt(String plainText, String keyContent);

    // 解密（使用指定密钥内容）
    String decrypt(String cipherText, String keyContent);
}
