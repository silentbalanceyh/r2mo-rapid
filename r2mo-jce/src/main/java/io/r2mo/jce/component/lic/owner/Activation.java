package io.r2mo.jce.component.lic.owner;

import io.r2mo.jce.common.HED;
import io.r2mo.spi.SPI;
import io.r2mo.typed.domain.extension.AbstractNormObject;
import io.r2mo.typed.json.JBase;
import io.r2mo.typed.json.JObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bouncycastle.util.encoders.Base64;

import javax.crypto.SecretKey;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * <pre>
 *     继承属性
 *     - id
 *     - appId
 *     - tenantId
 *     - createdAt
 *     - createdBy
 *     - updatedAt
 *     - updatedBy
 * </pre>
 * 激活码载荷（Activation Payload）
 * <pre>
 * 📌 核心作用：
 *   - 表示某一个 License 在某个设备上的一次激活信息
 *   - 由发行端生成，客户端加载并验证签名
 *
 * ⚠️ 注意：
 *   - 此对象会被序列化为 JSON，然后再进行签名/加密
 *   - 不应包含敏感信息（除非采用加密型激活码）
 * </pre>
 *
 * 字段说明：
 * <pre>
 * - licenseId      对应的 License 唯一标识
 * - issuedAt       激活码签发时间（ISO-8601 格式）
 * - expiredAt      激活码到期时间（ISO-8601 格式，可选）
 * - signFinger     设备硬件指纹（绑定设备时必填）
 * - nonce          随机字符串（用于防重放）
 * - features       功能约束（例如最大用户数、模块开关）
 * - notes          备注信息（非必须）
 * </pre>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Activation extends AbstractNormObject implements Serializable {

    /** 对应的 License 唯一标识 */
    private String licenseId;

    /** 签发时间 */
    private LocalDateTime issuedAt;

    /** 过期时间（可选） */
    private LocalDateTime expiredAt;

    /** 设备硬件指纹（可选，用于硬件绑定） */
    private String signFinger;

    /** 随机字符串（用于防重放） */
    private String nonce;

    /** 功能约束（例如：最大用户数、功能开关） */
    private Map<String, Object> features;

    /** 备注信息（非必须） */
    private String notes;

    /** 🔑 激活码签名（Base64） */
    private String signature;


    public static String toString(final Activation activation, final SecretKey secretKey) {
        final JObject serialized = SPI.V_UTIL.serializeJson(activation);
        final byte[] data = serialized.encode().getBytes(StandardCharsets.UTF_8);
        final byte[] hash = HED.encrypt(data, secretKey);
        return Base64.toBase64String(hash);
    }

    public static Activation toActivation(final String base64, final SecretKey secretKey) {
        final byte[] data = Base64.decode(base64);
        final byte[] decrypted = HED.decrypt(data, secretKey);
        final String json = new String(decrypted, StandardCharsets.UTF_8);
        final JObject deserialized = JBase.parse(json);
        return SPI.V_UTIL.deserializeJson(deserialized, Activation.class);
    }
}
