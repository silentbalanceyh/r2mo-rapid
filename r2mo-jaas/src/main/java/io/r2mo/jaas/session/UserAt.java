package io.r2mo.jaas.session;

import io.r2mo.jaas.element.MSEmployee;
import io.r2mo.jaas.element.MSUser;
import io.r2mo.typed.json.JObject;

import java.util.UUID;

/**
 * @author lang : 2025-11-10
 */
public interface UserAt {

    UUID id();

    MSUser logged();

    MSEmployee employee();

    JObject data();

    @SuppressWarnings("all")
    boolean isOk();
}
