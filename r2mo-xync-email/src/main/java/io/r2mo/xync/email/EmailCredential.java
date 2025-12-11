package io.r2mo.xync.email;

import io.r2mo.base.exchange.UniCredential;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 邮箱凭证
 *
 * @author lang : 2025-12-05
 */
@Data
@Accessors(fluent = true, chain = true)
@EqualsAndHashCode(callSuper = false)
public class EmailCredential implements UniCredential {
    private String username;
    private String password;
}
