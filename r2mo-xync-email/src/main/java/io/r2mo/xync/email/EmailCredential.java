package io.r2mo.xync.email;

import io.r2mo.base.exchange.UniCredential;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 邮箱凭证
 *
 * @author lang : 2025-12-05
 */
@Data
@Accessors(fluent = true, chain = true)
@AllArgsConstructor
public class EmailCredential implements UniCredential {
    @Setter(AccessLevel.NONE)
    private String username;
    @Setter(AccessLevel.NONE)
    private String password;
}
