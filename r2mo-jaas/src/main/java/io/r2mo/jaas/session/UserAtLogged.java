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
    private static final JUtil UT = SPI.V_UTIL;
    // 下边是旧代码，Jackson 序列化必须带有无参构造，而且属性可读写
    @Setter(AccessLevel.NONE)
    private UUID id;
    private MSUser logged;
    private MSEmployee employee;

    UserAtLogged(final String id) {
        this.id = UUID.fromString(id);
    }

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
        final JObject userJ = UT.serializeJson(this.logged);
        combined.put(userJ);
        // 员工
        if (Objects.nonNull(this.employee)) {
            final JObject employeeJ = UT.serializeJson(this.employee);
            combined.put(employeeJ);
        }
        /*
         * - id / userId
         * - employeeId
         */
        combined.put(ID_USER, this.id.toString());
        if (Objects.nonNull(this.employee)) {
            // Fix: Cannot invoke "java.util.UUID.toString()" because the return value of "io.r2mo.jaas.element.MSEmployee.getId()" is null
            final UUID employeeId = this.employee.getId();
            combined.put(ID_EMPLOYEE, Objects.isNull(employeeId) ? null : this.employee.getId().toString());
        }
        return combined;
    }

    @Override
    public boolean isOk() {
        return Objects.nonNull(this.logged);
    }
}
