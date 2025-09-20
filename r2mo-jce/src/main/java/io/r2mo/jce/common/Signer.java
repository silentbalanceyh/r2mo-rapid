package io.r2mo.jce.common;

import io.r2mo.typed.cc.Cc;

import javax.crypto.SecretKey;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author lang : 2025-09-19
 */
interface Signer<PUB, PRI> {

    Cc<String, Signer<?, ?>> CCT_SIGNER = Cc.openThread();

    @SuppressWarnings("unchecked")
    static Signer<PublicKey, PrivateKey> ofAsym(final String algorithm) {
        final String cacheKey = "ASYM@" + algorithm;
        return (Signer<PublicKey, PrivateKey>) CCT_SIGNER.pick(() -> new SignerAsym(algorithm), cacheKey);
    }

    @SuppressWarnings("unchecked")
    static Signer<SecretKey, SecretKey> ofSym(final String algorithm) {
        final String cacheKey = "SYM@" + algorithm;
        return (Signer<SecretKey, SecretKey>) CCT_SIGNER.pick(() -> new SignerMac(algorithm), cacheKey);
    }

    byte[] sign(byte[] data, PRI privateKey);

    boolean verify(byte[] data, PUB publicKey, byte[] sign);
}
