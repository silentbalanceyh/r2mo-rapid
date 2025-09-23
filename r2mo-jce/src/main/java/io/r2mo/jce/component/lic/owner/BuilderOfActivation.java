package io.r2mo.jce.component.lic.owner;

import cn.hutool.core.util.RandomUtil;
import io.r2mo.jce.component.lic.domain.LicenseData;
import io.r2mo.typed.domain.extension.AbstractBuilder;

import java.util.UUID;

/**
 * @author lang : 2025-09-21
 */
class BuilderOfActivation extends AbstractBuilder<Activation> {

    /**
     * 此处提取的信息要纳入到签名范畴，而且保证签名可校验，所以不签 createdAt、updatedAt 等字段
     */
    @Override
    public <R> Activation create(final R source) {
        if (source instanceof final LicenseData licenseData) {
            // Step 1: 构造激活码基本信息
            final Activation activation = new Activation();
            activation.setAppId(licenseData.getAppId());
            activation.setTenantId(licenseData.getTenantId());

            // Step 2: 构造基础激活码数据
            activation.setId(UUID.randomUUID());
            activation.setLicenseId(licenseData.getLicenseId());
            activation.setIssuedAt(licenseData.getIssuedAt());
            activation.setExpiredAt(licenseData.getExpireAt());
            activation.setSignFinger(licenseData.getSignFinger());
            activation.setNonce(RandomUtil.randomString(32));

            return activation;
        }
        return null;
    }
}
