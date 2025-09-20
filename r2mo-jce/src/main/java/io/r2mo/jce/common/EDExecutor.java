package io.r2mo.jce.common;

import io.r2mo.typed.cc.Cc;

import javax.crypto.SecretKey;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * 加密接口
 *
 * @author lang : 2025-09-19
 */
interface EDExecutor {

    Cc<String, EDExecutor> CCT_SYMMETRIC = Cc.openThread();

    static EDExecutor of(final String algorithm) {
        return CCT_SYMMETRIC.pick(() -> new EDExecutorCommon(algorithm), algorithm);
    }

    byte[] encrypt(byte[] data, PrivateKey secretKey);

    byte[] decrypt(byte[] data, PublicKey privateKey);

    byte[] decrypt(byte[] data, SecretKey secretKey);

    byte[] encrypt(byte[] data, SecretKey secretKey);
}
