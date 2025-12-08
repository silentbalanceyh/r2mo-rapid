package io.r2mo.spring.security.email;

import java.util.Set;

/**
 * @author lang : 2025-12-07
 */
public interface EmailService {

    boolean sendCaptcha(Set<String> toSet);
}
