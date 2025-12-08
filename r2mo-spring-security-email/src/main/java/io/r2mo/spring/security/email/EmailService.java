package io.r2mo.spring.security.email;

import java.util.Set;

/**
 * @author lang : 2025-12-07
 */
public interface EmailService {

    default boolean sendCaptcha(final Set<String> toSet) {
        toSet.forEach(this::sendCaptcha);
        return true;
    }

    boolean sendCaptcha(String to);
}
