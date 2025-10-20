package io.r2mo.jce.component.secure;

import io.r2mo.base.secure.EDPair;
import io.r2mo.function.Fn;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

/**
 * 加解密基类
 *
 * @author lang : 2025-10-20
 */
public abstract class EDBase implements ED {
    private final AlgNorm algorithm;

    public EDBase(final String algorithm) {
        this.algorithm = AlgNorm.from(algorithm);
    }

    protected EDBase(final AlgNorm algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    public EDPair generate(final int size) {
        return Fn.jvmOr(() -> {
            final KeyPairGenerator generator = KeyPairGenerator.getInstance(this.algorithm.name());
            generator.initialize(size);
            final KeyPair pair = generator.generateKeyPair();
            // Base64 encoding
            final String publicKey = this.encodeBase64(pair.getPublic().getEncoded());
            final String privateKey = this.encodeBase64(pair.getPrivate().getEncoded());
            return new EDPair(publicKey, privateKey);
        });
    }

    protected String encodeBase64(final byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    protected String runEncrypt(final String source, final Key key) {
        return Fn.jvmOr(() -> {
            final Cipher cipher = Cipher.getInstance(this.algorithm.jcaName());
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.getEncoder().encodeToString(cipher.doFinal(source.getBytes()));
        });
    }

    /*
     * RSA Fix:
     * javax.crypto.IllegalBlockSizeException: Data must not be longer than 128 bytes
     * at java.base/com.sun.crypto.provider.RSACipher.doFinal(RSACipher.java:348)
     */
    protected String runDecrypt(final String source, final Key key) {
        return Fn.jvmOr(() -> {
            // RSA Decrypt
            final Cipher cipher = Cipher.getInstance(this.algorithm.jcaName());
            cipher.init(Cipher.DECRYPT_MODE, key);

            final byte[] inputBytes = Base64.getDecoder().decode(source.getBytes(StandardCharsets.UTF_8));
            final int inputLength = inputBytes.length;
            // The Max Block Bytes of decrypt
            final int MAX_ENCRYPT_BLOCK = 128;
            int offSet = 0;
            byte[] resultBytes = {};
            byte[] cache = {};
            while (inputLength - offSet > 0) {
                if (inputLength - offSet > MAX_ENCRYPT_BLOCK) {
                    cache = cipher.doFinal(inputBytes, offSet, MAX_ENCRYPT_BLOCK);
                    offSet += MAX_ENCRYPT_BLOCK;
                } else {
                    cache = cipher.doFinal(inputBytes, offSet, inputLength - offSet);
                    offSet = inputLength;
                }
                resultBytes = Arrays.copyOf(resultBytes, resultBytes.length + cache.length);
                System.arraycopy(cache, 0, resultBytes, resultBytes.length - cache.length, cache.length);
            }
            return new String(resultBytes);
        });
    }

    protected PublicKey x509(final String keyContent) {
        // Generate Public Key Object
        return Fn.jvmOr(() -> {
            final byte[] buffer = Base64.getDecoder().decode(keyContent);
            final KeyFactory keyFactory = KeyFactory.getInstance(this.algorithm.jcaName());
            final X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            return keyFactory.generatePublic(keySpec);
        });
    }

    protected PrivateKey pKCS8(final String keyContent) {
        // Generate Private Key Object
        return Fn.jvmOr(() -> {
            final byte[] buffer = Base64.getDecoder().decode(keyContent);
            final KeyFactory keyFactory = KeyFactory.getInstance(this.algorithm.jcaName());
            final PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
            return keyFactory.generatePrivate(keySpec);
        });
    }
}
