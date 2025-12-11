package io.r2mo.io.local.operation;

import cn.hutool.core.io.IoUtil;
import io.r2mo.function.Fn;
import io.r2mo.typed.constant.DefaultConstantValue;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * @author lang : 2025-09-19
 */
@Slf4j
class SecurityIn {
    // Fix: java.security.NoSuchProviderException: no such provider: BC
    static {
        Provider p = Security.getProvider(DefaultConstantValue.DEFAULT_SEC_PROVIDER);
        if (Objects.isNull(p)) {
            p = new BouncyCastleProvider();
            Security.addProvider(p);
            log.info("[ R2MO ] 使用安全提供者: {}, 版本: {}", p.getName(), p.getVersionStr());
        }
    }
    // ======================== 写 PEM ========================

    static InputStream inPublic(final PublicKey publicKey) {
        // 提取算法并作为 PEM type
        return inPem(publicKey.getAlgorithm() + " PUBLIC KEY", publicKey.getEncoded());
    }

    static InputStream inPrivate(final PrivateKey privateKey) {
        // 提取算法并作为 PEM type
        return inPem(privateKey.getAlgorithm() + " PRIVATE KEY", privateKey.getEncoded());
    }

    static InputStream inSecret(final SecretKey secretKey) {
        final String base64Key = Base64.toBase64String(secretKey.getEncoded());
        final String pemContent = "-----BEGIN SECRET KEY-----\n" +
            base64Key + "\n" +
            "-----END SECRET KEY-----\n";
        return new ByteArrayInputStream(pemContent.getBytes(StandardCharsets.UTF_8));
    }

    private static InputStream inPem(final String title, final byte[] encoded) {
        return Fn.jvmOr(() -> {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final PemObject pemObject = new PemObject(title, encoded);
            try (final PemWriter pemWriter = new PemWriter(new OutputStreamWriter(baos, StandardCharsets.UTF_8))) {
                pemWriter.writeObject(pemObject);
            }
            return new ByteArrayInputStream(baos.toByteArray());
        });
    }

    // ======================== 读 PEM ========================

    static PublicKey inPublic(final InputStream in) {
        final PemObject pemObject = inPem(in);
        final String type = pemObject.getType(); // e.g. "RSA PUBLIC KEY"
        final byte[] content = pemObject.getContent();

        final String algorithm = resolveAlgorithm(type);
        final KeyFactory factory = Fn.jvmOr(() -> KeyFactory.getInstance(algorithm, DefaultConstantValue.DEFAULT_SEC_PROVIDER));
        final X509EncodedKeySpec keySpec = new X509EncodedKeySpec(content);
        return Fn.jvmOr(() -> factory.generatePublic(keySpec));
    }

    static PrivateKey inPrivate(final InputStream in) {
        final PemObject pemObject = inPem(in);
        final String type = pemObject.getType(); // e.g. "RSA PRIVATE KEY"
        final byte[] content = pemObject.getContent();

        final String algorithm = resolveAlgorithm(type);
        final KeyFactory factory = Fn.jvmOr(() -> KeyFactory.getInstance(algorithm, DefaultConstantValue.DEFAULT_SEC_PROVIDER));
        final PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(content);
        return Fn.jvmOr(() -> factory.generatePrivate(keySpec));
    }

    static SecretKey inSecret(final InputStream in) {
        if (in == null) {
            throw new IllegalArgumentException("[ R2MO ] SecretKey 处理中输入流为空");
        }
        return Fn.jvmOr(() -> {
            byte[] keyBytes = IoUtil.readBytes(in);
            final String keyString = new String(keyBytes, StandardCharsets.UTF_8);
            if (keyString.contains("-----BEGIN")) {
                final String base64Key = keyString
                    .replace("-----BEGIN SECRET KEY-----", "")
                    .replace("-----END SECRET KEY-----", "")
                    .replaceAll("\\s", "");
                keyBytes = Base64.decode(base64Key);
            }
            final String algorithm = switch (keyBytes.length * 8) {
                case 128 -> "AES";
                case 168 -> "DESede";
                case 192 -> "AES";
                case 256 -> "AES";
                default -> "AES";
            };
            return new SecretKeySpec(keyBytes, algorithm);
        });
    }

    private static PemObject inPem(final InputStream in) {
        if (Objects.isNull(in)) {
            throw new IllegalArgumentException("[ R2MO ] 输入流为 null");
        }
        return Fn.jvmOr(() -> {
            try (final PemReader reader = new PemReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                final PemObject pemObject = reader.readPemObject();
                if (Objects.isNull(pemObject)) {
                    throw new IllegalArgumentException("[ R2MO ] 读取失败，PEM 对象为 null");
                }
                return pemObject;
            }
        });
    }

    /**
     * 从 PEM type 中解析算法名称
     * e.g. "RSA PRIVATE KEY" -> "RSA"
     */
    private static String resolveAlgorithm(final String type) {
        if (type == null) {
            throw new IllegalArgumentException("[ R2MO ] PEM 类型为空");
        }

        final String upperType = type.toUpperCase(Locale.ROOT);

        for (final Map.Entry<String, String> entry : SecurityMap.ALG_MAP.entrySet()) {
            if (upperType.contains(entry.getKey())) {
                log.info("[ R2MO ] 解析 PEM 类型: {} / 侦测算法类型：{}", upperType, entry.getValue());
                return entry.getValue();
            }
        }
        throw new IllegalArgumentException("[ R2MO ] 无法识别的 PEM 算法类型: " + upperType);
    }
}
