package io.r2mo.jce.common;

import io.r2mo.jce.constant.AlgLicenseSpec;

import java.security.Key;

/**
 * 抽象密钥编解码器基类
 *
 * <p>设计要点：
 * <pre>
 * 1. 绑定 {@link AlgLicenseSpec}，保证算法和密钥长度的统一来源；
 * 2. 提供通用的算法校验与长度校验逻辑；
 * 3. 作为 {@link Byter} 的基类，简化具体实现类的重复代码；
 * </pre>
 *
 * @author lang
 * @since 2025-09-20
 */
abstract class AbstractByter<T extends Key> implements Byter<T> {

    /** 算法规范（统一来源于枚举） */
    protected final AlgLicenseSpec spec;

    /**
     * 构造函数 - 必须指定算法枚举
     *
     * @param spec 算法规范
     */
    protected AbstractByter(final AlgLicenseSpec spec) {
        this.spec = spec;
    }

    /**
     * 验证密钥算法是否匹配
     *
     * @param key 输入的密钥对象
     */
    protected void validateAlgorithm(final T key) {
        if (!this.spec.alg().equalsIgnoreCase(key.getAlgorithm())) {
            throw new IllegalArgumentException(
                "[ R2MO ] 密钥算法不匹配: 期望 " + this.spec.alg() + ", 实际 " + key.getAlgorithm()
            );
        }
    }

    /**
     * 验证密钥长度是否匹配
     *
     * @param keyBytes 密钥字节数组
     */
    protected void validateKeyLength(final byte[] keyBytes) {
        final int expectedBits = this.spec.length();
        if (expectedBits > 0 && keyBytes.length * 8 != expectedBits) {
            throw new IllegalArgumentException(
                "[ R2MO ] 密钥长度不匹配: 期望 " + expectedBits + " 位, 实际 " + (keyBytes.length * 8) + " 位"
            );
        }
    }
}
