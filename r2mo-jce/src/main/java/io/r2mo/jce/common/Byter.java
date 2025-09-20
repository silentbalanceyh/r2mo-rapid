package io.r2mo.jce.common;

import io.r2mo.jce.component.lic.AlgLicenseSpec;
import io.r2mo.typed.cc.Cc;

import javax.crypto.SecretKey;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author lang : 2025-09-19
 */
interface Byter<T extends Key> {

    Cc<String, Byter<?>> CCT_BYTER = Cc.openThread();

    @SuppressWarnings("unchecked")
    static <T extends PublicKey> Byter<T> ofPub(final AlgLicenseSpec spec) {
        final String cacheKey = "PUB@" + spec.alg() + "@" + spec.length();
        return (Byter<T>) CCT_BYTER.pick(() -> new ByterPublic(spec), cacheKey);
    }

    @SuppressWarnings("unchecked")
    static <T extends PrivateKey> Byter<T> ofPri(final AlgLicenseSpec spec) {
        final String cacheKey = "PRI@" + spec.alg() + "@" + spec.length();
        return (Byter<T>) CCT_BYTER.pick(() -> new ByterPrivate(spec), cacheKey);
    }

    @SuppressWarnings("unchecked")
    static <T extends SecretKey> Byter<T> ofSec(final AlgLicenseSpec spec) {
        final String cacheKey = "SEC@" + spec.alg() + "@" + spec.length();
        return (Byter<T>) CCT_BYTER.pick(() -> new ByterSecret(spec), cacheKey);
    }

    byte[] encode(T value);

    T decode(byte[] bytes);
}
