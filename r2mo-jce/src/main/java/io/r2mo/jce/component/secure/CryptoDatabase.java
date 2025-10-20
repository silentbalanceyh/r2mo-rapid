package io.r2mo.jce.component.secure;

import io.r2mo.base.secure.EDCrypto;
import io.r2mo.base.secure.EDPair;
import io.r2mo.typed.annotation.SPID;

/**
 * <pre>
 *     1. 私钥加密
 *     2. 公钥加密
 *     3. 固定密钥
 * </pre>
 *
 * @author lang : 2025-10-20
 */
@SPID(EDCrypto.FOR_DATABASE)
public class CryptoDatabase extends CryptoByPrivate {

    @Override
    protected EDPair data() {
        return EDDefault.loadRSA();
    }

    @Override
    protected ED executor() {
        return ED.encryptOfPrivate(AlgNorm.RSA);
    }
}
