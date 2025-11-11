package io.r2mo.jaas.session;

import io.r2mo.jaas.element.MSEmployee;
import io.r2mo.jaas.element.MSUser;

import java.util.UUID;

/**
 * @author lang : 2025-11-10
 */
public interface UserContext {

    UUID id();

    MSUser logged();

    MSEmployee employee(UUID empId);
}
