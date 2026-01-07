package io.r2mo.jaas.session;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.r2mo.jaas.element.MSEmployee;
import io.r2mo.jaas.element.MSUser;
import io.r2mo.spi.SPI;
import io.r2mo.typed.json.JObject;
import io.r2mo.typed.json.JUtil;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2025-11-10
 */
@Data
@Accessors(chain = true, fluent = true)
@JsonSerialize(using = UserContext.Serializer.class)
@JsonDeserialize(using = UserContext.Deserializer.class)
class UserContextImpl implements UserContext {
    private static final JUtil UT = SPI.V_UTIL;
    @Setter(AccessLevel.NONE)
    private final UUID id;
    private final ConcurrentMap<UUID, MSEmployee> employee = new ConcurrentHashMap<>();
    private MSUser logged;

    UserContextImpl(final String id) {
        this.id = UUID.fromString(id);
    }

    UserContextImpl(final UUID id) {
        this.id = id;
    }

    @Override
    public MSEmployee employee(final UUID empId) {
        return this.employee.getOrDefault(empId, null);
    }

    @SuppressWarnings("all")
    UserContextImpl employee(final MSEmployee employee) {
        this.employee.put(employee.getId(), employee);
        return this;
    }

    @SuppressWarnings("all")
    UserContextImpl employee(final List<MSEmployee> employees) {
        employees.forEach(this::employee);
        return this;
    }

    @Override
    public boolean isOk() {
        return Objects.nonNull(this.id)
            && Objects.nonNull(this.logged);
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
        // 员工数据
        final JObject employeeJ = SPI.J();
        this.employee.forEach((id, emp) -> {
            final JObject empJ = UT.serializeJson(emp);
            employeeJ.put(id.toString(), empJ);
        });
        if (UT.isNotEmpty(employeeJ)) {
            combined.put("employees", employeeJ);
        }
        return combined;
    }
}
