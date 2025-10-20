package io.r2mo.base.secure;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * 公私密钥对
 *
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
@Data
public class EDPair implements Serializable {
    private String publicKey;
    private String privateKey;

    public EDPair(final String publicKey, final String privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public boolean isOk() {
        return StrUtil.isNotEmpty(this.privateKey) && StrUtil.isNotEmpty(this.publicKey);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final EDPair EDPair = (EDPair) o;
        return this.publicKey.equals(EDPair.publicKey) && this.privateKey.equals(EDPair.privateKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.publicKey, this.privateKey);
    }

    @Override
    public String toString() {
        return "PublicKey =\n" + this.publicKey + '\n' +
            "PrivateKey =\n" + this.privateKey;
    }
}
