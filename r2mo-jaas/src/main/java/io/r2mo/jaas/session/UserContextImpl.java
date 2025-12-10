package io.r2mo.jaas.session;

import io.r2mo.jaas.element.MSEmployee;
import io.r2mo.jaas.element.MSUser;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author lang : 2025-11-10
 */
@Data
@Accessors(chain = true, fluent = true)
class UserContextImpl implements UserContext {
    @Setter(AccessLevel.NONE)
    private final UUID id;
    private final ConcurrentMap<UUID, MSEmployee> employee = new ConcurrentHashMap<>();
    private MSUser logged;

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
}
