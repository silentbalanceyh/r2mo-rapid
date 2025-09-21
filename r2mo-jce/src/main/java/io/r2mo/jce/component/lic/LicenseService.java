package io.r2mo.jce.component.lic;

import io.r2mo.base.io.HStore;
import io.r2mo.jce.component.lic.domain.LicenseData;
import io.r2mo.jce.component.lic.domain.LicenseFile;
import io.r2mo.jce.constant.AlgLicense;
import io.r2mo.typed.cc.Cc;

import javax.crypto.SecretKey;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/**
 * @author lang : 2025-09-19
 */
public interface LicenseService {

    Cc<String, LicenseService> CCT_LIC_SERVICE = Cc.openThread();

    static LicenseService of(final AlgLicense license) {
        final Supplier<LicenseService> constructor = __.SUPPLIER.get(license);
        return CCT_LIC_SERVICE.pick(constructor, license.name());
    }

    boolean generate(String directory, HStore store);

    /**
     * 核心生成流程以及方法
     * <pre>
     *     1. 一个带 {@link SecretKey} 参数，一个不带，如果带了就做加密处理
     *     2. {@link LicenseData} 中拥有 License 的核心数据
     *     3. 使用 {@link PrivateKey} 对 LicenseData 进行签名
     * </pre>
     *
     * @param data       License数据
     * @param privateKey 私钥
     *
     * @return License文件对象
     */
    LicenseFile encrypt(LicenseData data, PrivateKey privateKey);

    LicenseFile encrypt(LicenseData data, PrivateKey privateKey, SecretKey secretKey);

    LicenseData decrypt(LicenseFile file, PublicKey publicKey);
}

/**
 * 必须在包内可见，防止外部调用
 */
interface __ {

    ConcurrentMap<AlgLicense, Supplier<LicenseService>> SUPPLIER =
        new ConcurrentHashMap<>() {
            {
                this.put(AlgLicense.RSA, LicenseServiceRSA::new);
                this.put(AlgLicense.SM2, LicenseServiceSM2::new);
                this.put(AlgLicense.ECC, LicenseServiceECC::new);
                this.put(AlgLicense.ED25519, LicenseServiceEd25519::new);
            }
        };
}
