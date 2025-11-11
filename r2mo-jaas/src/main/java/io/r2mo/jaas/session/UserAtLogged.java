package io.r2mo.jaas.session;

import io.r2mo.jaas.element.MSEmployee;
import io.r2mo.jaas.element.MSUser;
import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JObject;
import io.r2mo.typed.json.JUtil;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Objects;
import java.util.UUID;

/**
 * @author lang : 2025-11-10
 */
@Data
@Accessors(fluent = true, chain = true)
class UserAtLogged implements UserAt {
    private final JUtil V = SPI.V_UTIL;
    @Setter(AccessLevel.NONE)
    private final UUID id;
    private MSUser logged;
    private MSEmployee employee;

    UserAtLogged(final UUID id) {
        Objects.requireNonNull(id, "[ R2MO ] 用户 id 不可为空！");
        this.id = id;
    }

    @Override
    public JObject data() {
        final JObject combined = SPI.J();
        if (!this.isOk()) {
            return combined;
        }
        // 账号
        final JObject userJ = this.V.serializeJson(this.logged);
        combined.put(userJ);
        // 员工
        final JObject employeeJ = this.V.serializeJson(this.employee);
        combined.put(employeeJ);
        /*
         * - id / userId
         * - employeeId
         */
        combined.put("id", this.logged.getId());
        combined.put("employeeId", this.employee.getId());
        return combined;
    }

    @Override
    public boolean isOk() {
        return Objects.nonNull(this.logged)
            && Objects.nonNull(this.employee);
    }
}
