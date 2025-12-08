package io.r2mo.spring.security.sms;

import java.util.Set;

/**
 * @author lang : 2025-12-08
 */
public interface SmsService {

    default boolean sendCaptcha(final Set<String> toSet) {
        toSet.forEach(this::sendCaptcha);
        return true;
    }

    boolean sendCaptcha(String to);
}
