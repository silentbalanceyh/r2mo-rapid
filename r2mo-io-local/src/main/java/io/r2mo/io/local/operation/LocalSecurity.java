package io.r2mo.io.local.operation;

import cn.hutool.core.io.IoUtil;
import io.r2mo.function.Fn;
import io.r2mo.typed.constant.DefaultConstantValue;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Objects;

/**
 * @author lang : 2025-09-19
 */
class LocalSecurity {

    static InputStream inPublic(final PublicKey publicKey) {
        return inPem("PUBLIC KEY", publicKey.getEncoded());
    }

    static InputStream inPrivate(final PrivateKey privateKey) {
        return inPem("PRIVATE KEY", privateKey.getEncoded());
    }

    static InputStream inSecret(final SecretKey secretKey) {
        // 将密钥编码为Base64格式
        final String base64Key = Base64.toBase64String(secretKey.getEncoded());

        // 创建PEM格式内容
        final String pemContent = "-----BEGIN SECRET KEY-----\n" +
            base64Key + "\n" +
            "-----END SECRET KEY-----\n";

        // 写入文件
        return new ByteArrayInputStream(pemContent.getBytes(StandardCharsets.UTF_8));
    }

    static PublicKey inPublic(final InputStream in) {
        final PemObject pemObject = inPem(in);
        final String type = pemObject.getType();
        final byte[] content = pemObject.getContent();

        final KeyFactory factory = Fn.jvmOr(() -> KeyFactory.getInstance(type, DefaultConstantValue.DEFAULT_SEC_PROVIDER));
        final X509EncodedKeySpec keySpec = new X509EncodedKeySpec(content);
        return Fn.jvmOr(() -> factory.generatePublic(keySpec));
    }

    private static InputStream inPem(final String title, final byte[] encoded) {
        return Fn.jvmOr(() -> {
            // 将公钥编码为PEM格式并写入文件
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final PemObject pemObject = new PemObject(title, encoded);
            final PemWriter pemWriter = new PemWriter(new OutputStreamWriter(baos, StandardCharsets.UTF_8));
            pemWriter.writeObject(pemObject);
            pemWriter.close();

            // 写入文件
            return new ByteArrayInputStream(baos.toByteArray());
        });
    }

    private static PemObject inPem(final InputStream in) {
        if (Objects.isNull(in)) {
            throw new IllegalArgumentException("[ R2MO ] 输入流为 null");
        }
        return Fn.jvmOr(() -> {
            // 读取 PEM 格式的公钥
            final PemReader reader = new PemReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            final PemObject pemObject = reader.readPemObject();
            reader.close();

            if (Objects.isNull(pemObject)) {
                throw new IllegalArgumentException("[ R2MO ] 读取失败，PEM 对象为 null");
            }
            return pemObject;
        });
    }

    static PrivateKey inPrivate(final InputStream in) {
        final PemObject pemObject = inPem(in);
        final String type = pemObject.getType();
        final byte[] content = pemObject.getContent();

        final KeyFactory factory = Fn.jvmOr(() -> KeyFactory.getInstance(type, DefaultConstantValue.DEFAULT_SEC_PROVIDER));
        final PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(content);
        return Fn.jvmOr(() -> factory.generatePrivate(keySpec));
    }

    static SecretKey inSecret(final InputStream in) {
        if (in == null) {
            throw new IllegalArgumentException("[ R2MO ] SecretKey 处理中输入流为空");
        }
        return Fn.jvmOr(() -> {
            // 读取密钥数据
            byte[] keyBytes = IoUtil.readBytes(in);

            // 尝试解析PEM格式
            final String keyString = new String(keyBytes, StandardCharsets.UTF_8);
            if (keyString.contains("-----BEGIN")) {
                // 提取PEM内容
                final String base64Key = keyString
                    .replace("-----BEGIN SECRET KEY-----", "")
                    .replace("-----END SECRET KEY-----", "")
                    .replaceAll("\\s", "");
                keyBytes = Base64.decode(base64Key);
            } else if (keyString.startsWith("{")) {
                // JSON格式密钥
                // 这里可以解析JSON格式的密钥信息
                // 简化处理，直接使用Base64解码
                keyBytes = Base64.decode(keyString);
            }

            // 根据密钥长度推断算法
            final String algorithm = switch (keyBytes.length * 8) {
                case 128 -> "AES"; // 或 SM4
                case 168 -> "DESede";
                case 192 -> "AES";
                case 256 -> "AES"; // 或 ChaCha20
                default -> "AES"; // 默认使用AES
            };

            return new SecretKeySpec(keyBytes, algorithm);
        });
    }
}
