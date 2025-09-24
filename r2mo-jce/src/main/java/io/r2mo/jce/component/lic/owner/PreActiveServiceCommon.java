package io.r2mo.jce.component.lic.owner;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import io.r2mo.base.io.HStore;
import io.r2mo.jce.common.HED;
import io.r2mo.jce.component.lic.domain.LicenseConfiguration;
import io.r2mo.jce.component.lic.domain.LicenseData;
import io.r2mo.spi.SPI;
import io.r2mo.typed.domain.builder.BuilderOf;
import io.r2mo.typed.exception.web._401UnauthorizedException;
import io.r2mo.typed.json.JObject;
import io.r2mo.typed.json.JUtil;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Base64;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Objects;

/**
 * 激活码服务（默认实现）
 * <pre>
 * 📌 职责：
 *   1. 生成激活码（Generate）
 *      - 将 {@link LicenseData} 转换为 {@link Activation}
 *      - 使用私钥对序列化后的 JSON 进行签名
 *      - 签名结果存储到 Activation.signature 字段
 *
 *   2. 验证激活码（Verify）
 *      - 提取签名（Base64 格式）
 *      - 使用公钥对激活码进行验签
 *      - 验签成功表示激活码未被篡改
 *
 * ⚠️ 注意：
 *   - 此处仅验证签名合法性，不验证时间有效性、硬件指纹等业务逻辑
 *   - 业务层需在外部做额外校验（例如 expiredAt、signFinger 等）
 *   - 如果缺失公钥/私钥文件，将抛出 {@link _401UnauthorizedException}
 *   - 激活码在序列化时需排除 signature 字段，否则会导致验签失败
 *
 * 📂 数据流：
 *   LicenseData -> Activation -> JSON -> 签名 -> Activation(signature)
 *
 * @author lang
 * @since 2025-09-21
 */
@Slf4j
class PreActiveServiceCommon implements PreActiveService {
    private static final JUtil UT = SPI.V_UTIL;
    protected final HStore store;

    PreActiveServiceCommon(final HStore store) {
        this.store = store;
    }

    /**
     * ✍️ 生成激活码
     * <pre>
     * Step 1 | 构造激活码对象
     *   - 从 {@link LicenseData} 创建 {@link Activation}
     *
     * Step 2 | 序列化
     *   - 将 Activation 转换为 JSON 格式
     *
     * Step 3 | 签名
     *   - 使用私钥对 JSON 数据进行签名
     *   - 结果存储在 Activation.signature（Base64 编码）
     *
     * Step 4 | 追加信息
     *   - 可在外层扩展 Features/Notes 等自定义字段
     * </pre>
     *
     * ⚠️ 异常点：
     * - 如果私钥缺失，将抛出 {@link _401UnauthorizedException}
     *
     * @param licenseData   License 核心数据
     * @param configuration 配置信息（含私钥路径、签名算法）
     *
     * @return 含签名的激活码对象
     */
    @Override
    public Activation generate(final LicenseData licenseData, final LicenseConfiguration configuration) {
        // Step 1 | 构造激活码对象
        final Activation activation = BuilderOf.of(BuilderOfActivation::new).create(licenseData);

        // Step 2 | 序列化激活码
        final JObject serialized = UT.serializeJson(activation);

        // Step 3 | 签名
        final String path = this.store.pHome(configuration.ioPrivate());
        final PrivateKey privateKey = this.store.inPrivate(path);
        if (Objects.isNull(privateKey)) {
            throw new _401UnauthorizedException("[ R2MO ] 私钥不存在，无法生成激活码签名！");
        }
        log.info("[ R2MO ] 激活码签名数据：{}", serialized.encode());
        final byte[] data = serialized.encode().getBytes(StandardCharsets.UTF_8);
        final byte[] signature = HED.sign(data, privateKey, configuration.algSign().value());
        final String signBase64 = Base64.toBase64String(signature);
        activation.setSignature(signBase64);

        // Step 4 | 返回结果
        return activation;
    }

    /**
     * 🔍 验证激活码
     * <pre>
     * Step 1 | 提取签名
     *   - 从 Activation.signature 获取 Base64 签名内容
     *
     * Step 2 | 读取公钥
     *   - 从配置加载公钥
     *   - 用于后续验签
     *
     * Step 3 | 重新序列化（去掉签名字段）
     *   - 将 Activation 复制一份，清空 signature 字段
     *   - 序列化为 JSON
     *
     * Step 4 | 验签
     *   - 使用公钥和签名算法对 JSON 数据进行验证
     *   - 返回验签结果（true/false）
     * </pre>
     *
     * ⚠️ 异常点：
     * - 签名为空 -> 抛出 {@link _401UnauthorizedException}
     * - 公钥缺失 -> 抛出 {@link _401UnauthorizedException}
     * - 验签失败 -> 抛出 {@link _401UnauthorizedException}
     *
     * @param code          激活码对象（含签名）
     * @param configuration 配置信息（含公钥路径、签名算法）
     *
     * @return 验签是否通过
     */
    @Override
    public boolean verify(final Activation code, final LicenseConfiguration configuration) {
        // Step 1 | 提取签名
        final String signBase64 = code.getSignature();
        if (StrUtil.isEmpty(signBase64)) {
            throw new _401UnauthorizedException("[ R2MO ] 激活码签名不能为空，无法验证合法性！");
        }
        final byte[] signature = Base64.decode(signBase64);

        // Step 2 | 加载公钥
        final String path = this.store.pHome(configuration.ioPublic());
        final PublicKey publicKey = this.store.inPublic(path);
        if (Objects.isNull(publicKey)) {
            throw new _401UnauthorizedException("[ R2MO ] 公钥不存在，无法验证激活码合法性！");
        }

        // Step 3 | 序列化去掉签名后的数据
        final Activation unsignedCode = new Activation();
        BeanUtil.copyProperties(code, unsignedCode);
        unsignedCode.setSignature(null);
        final JObject serialized = UT.serializeJson(unsignedCode);
        log.info("[ R2MO ] 激活码验证数据：{}", serialized.encode());
        final byte[] data = serialized.encode().getBytes(StandardCharsets.UTF_8);

        // Step 4 | 验签
        final boolean verified = HED.verify(data, signature, publicKey, configuration.algSign().value());
        if (!verified) {
            throw new _401UnauthorizedException("[ R2MO ] 激活码签名验证失败，文件可能被篡改！");
        }
        return verified;
    }
}
