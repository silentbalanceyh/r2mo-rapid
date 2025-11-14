package io.r2mo.spring.security.oauth2.token;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import io.r2mo.spring.security.oauth2.config.ConfigSecurityOAuth2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.InputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

/**
 * JWK 密钥源管理器
 *
 * 支持：
 * 1. 自动生成 RSA 密钥对（内存模式）
 * 2. 从 KeyStore 加载密钥对（文件模式）
 * 3. 自定义密钥大小
 *
 * @author lang : 2025-11-13
 */
@Slf4j
public class OAuth2JwkSourceManager {

    private final ConfigSecurityOAuth2 config;
    private final ResourceLoader resourceLoader;

    public OAuth2JwkSourceManager(final ConfigSecurityOAuth2 config, final ResourceLoader resourceLoader) {
        this.config = config;
        this.resourceLoader = resourceLoader;
    }

    /**
     * 创建 JWK Source
     *
     * @return JWK Source
     */
    public JWKSource<SecurityContext> createJwkSource() {
        final KeyPair keyPair;

        // 检查是否配置了 KeyStore
        if (this.config.getJwk() != null && this.config.getJwk().getKeyStore() != null) {
            log.info("[ R2MO ] 从 KeyStore 加载 JWK 密钥对");
            keyPair = this.loadKeyPairFromKeyStore();
        } else {
            log.info("[ R2MO ] 生成新的 RSA 密钥对（内存模式）");
            keyPair = this.generateRsaKeyPair();
        }

        final RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        final RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        final String keyId = this.config.getJwk() != null && this.config.getJwk().getKeyId() != null
            ? this.config.getJwk().getKeyId()
            : UUID.randomUUID().toString();

        final RSAKey rsaKey = new RSAKey.Builder(publicKey)
            .privateKey(privateKey)
            .keyID(keyId)
            .build();

        final JWKSet jwkSet = new JWKSet(rsaKey);

        log.info("[ R2MO ] JWK Source 创建成功：keyId = {}, algorithm = RS256", keyId);

        return new ImmutableJWKSet<>(jwkSet);
    }

    /**
     * 生成 RSA 密钥对
     */
    private KeyPair generateRsaKeyPair() {
        try {
            final int keySize = this.config.getJwk() != null && this.config.getJwk().getKeySize() > 0
                ? this.config.getJwk().getKeySize()
                : 2048;  // 默认 2048 位

            final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(keySize);

            log.info("[ R2MO ] 生成 RSA 密钥对：keySize = {}", keySize);

            return keyPairGenerator.generateKeyPair();
        } catch (final Exception ex) {
            throw new IllegalStateException("[ R2MO ] 生成 RSA 密钥对失败", ex);
        }
    }

    /**
     * 从 KeyStore 加载密钥对
     */
    private KeyPair loadKeyPairFromKeyStore() {
        try {
            final ConfigSecurityOAuth2.JwkKeyStore keyStoreConfig = this.config.getJwk().getKeyStore();

            // 加载 KeyStore 文件
            final Resource resource = this.resourceLoader.getResource(keyStoreConfig.getLocation());
            final KeyStore keyStore = KeyStore.getInstance(keyStoreConfig.getType());

            try (final InputStream inputStream = resource.getInputStream()) {
                keyStore.load(inputStream, keyStoreConfig.getPassword().toCharArray());
            }

            // 获取密钥对
            final String alias = keyStoreConfig.getAlias();
            final PrivateKey privateKey = (PrivateKey) keyStore.getKey(
                alias,
                keyStoreConfig.getKeyPassword().toCharArray()
            );
            final PublicKey publicKey = keyStore.getCertificate(alias).getPublicKey();

            if (privateKey == null || publicKey == null) {
                throw new IllegalStateException("[ R2MO ] KeyStore 中未找到密钥对：alias = " + alias);
            }

            log.info("[ R2MO ] 从 KeyStore 加载密钥对成功：location = {}, alias = {}",
                keyStoreConfig.getLocation(),
                alias);

            return new KeyPair(publicKey, privateKey);
        } catch (final Exception ex) {
            throw new IllegalStateException("[ R2MO ] 从 KeyStore 加载密钥对失败", ex);
        }
    }
}

