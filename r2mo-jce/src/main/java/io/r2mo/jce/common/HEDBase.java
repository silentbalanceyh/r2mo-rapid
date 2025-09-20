package io.r2mo.jce.common;

import io.r2mo.jce.component.lic.AlgLicenseSpec;
import io.r2mo.jce.constant.AlgHash;

import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author lang : 2025-09-20
 */
class HEDBase {

    protected HEDBase() {
    }

    public static byte[] sign(final byte[] data, final PrivateKey privateKey, final String algorithm) {
        return Signer.ofAsym(algorithm).sign(data, privateKey);
    }

    public static byte[] sign(final byte[] data, final PrivateKey privateKey, final AlgLicenseSpec spec) {
        return sign(data, privateKey, spec.algSign());
    }

    public static boolean verify(final byte[] data, final byte[] signature,
                                 final PublicKey publicKey, final String algorithm) {
        return Signer.ofAsym(algorithm).verify(data, publicKey, signature);
    }

    public static boolean verify(final byte[] data, final byte[] signature,
                                 final PublicKey publicKey, final AlgLicenseSpec spec) {
        return verify(data, signature, publicKey, spec.algSign());
    }

    public static byte[] sign(final byte[] data, final SecretKey secretKey, final String algorithm) {
        return Signer.ofSym(algorithm).sign(data, secretKey);
    }

    public static byte[] sign(final byte[] data, final SecretKey secretKey, final AlgLicenseSpec spec) {
        return sign(data, secretKey, spec.algSign());
    }

    public static boolean verify(final byte[] data, final byte[] signature,
                                 final SecretKey secretKey, final String algorithm) {
        return Signer.ofSym(algorithm).verify(data, secretKey, signature);
    }

    public static boolean verify(final byte[] data, final byte[] signature,
                                 final SecretKey secretKey, final AlgLicenseSpec spec) {
        return verify(data, signature, secretKey, spec.algSign());
    }

    public static String encrypt(final String data, final AlgHash algorithm) {
        return EDHasher.encrypt(data, algorithm);
    }

    public static byte[] encodePrivate(final PrivateKey privateKey, final AlgLicenseSpec spec) {
        return Byter.ofPri(spec).encode(privateKey);
    }

    public static byte[] encodePublic(final PublicKey publicKey, final AlgLicenseSpec spec) {
        return Byter.ofPub(spec).encode(publicKey);
    }

    public static byte[] encodeSecretKey(final SecretKey secretKey, final AlgLicenseSpec spec) {
        return Byter.ofSec(spec).encode(secretKey);
    }

    public static PrivateKey decodePrivate(final byte[] bytes, final AlgLicenseSpec spec) {
        return Byter.ofPri(spec).decode(bytes);
    }

    public static PublicKey decodePublic(final byte[] bytes, final AlgLicenseSpec spec) {
        return Byter.ofPub(spec).decode(bytes);
    }

    public static SecretKey decodeSecretKey(final byte[] bytes, final AlgLicenseSpec spec) {
        return Byter.ofSec(spec).decode(bytes);
    }

    public static KeyPair generate(final AlgLicenseSpec spec) {
        return JceProvider.ofKeyPair(spec);
    }

    public static byte[] encrypt(final byte[] data, final PrivateKey privateKey, final String algorithm) {
        return EDExecutor.of(algorithm).encrypt(data, privateKey);
    }

    public static byte[] encrypt(final byte[] data, final PrivateKey privateKey, final AlgLicenseSpec spec) {
        return encrypt(data, privateKey, spec.algCipher());
    }

    public static byte[] decrypt(final byte[] data, final PublicKey publicKey, final AlgLicenseSpec spec) {
        return decrypt(data, publicKey, spec.algCipher());
    }

    public static byte[] decrypt(final byte[] data, final PublicKey publicKey, final String algorithm) {
        return EDExecutor.of(algorithm).decrypt(data, publicKey);
    }

    public static byte[] encrypt(final byte[] data, final SecretKey secretKey, final String algorithm) {
        return EDExecutor.of(algorithm).encrypt(data, secretKey);
    }

    public static byte[] encrypt(final byte[] data, final SecretKey secretKey, final AlgLicenseSpec spec) {
        return encrypt(data, secretKey, spec.algCipher());
    }

    public static byte[] decrypt(final byte[] data, final SecretKey secretKey, final String algorithm) {
        return EDExecutor.of(algorithm).decrypt(data, secretKey);
    }

    public static byte[] decrypt(final byte[] data, final SecretKey secretKey, final AlgLicenseSpec spec) {
        return decrypt(data, secretKey, spec.algCipher());
    }
}
