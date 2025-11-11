package io.r2mo.spring.security.auth.executor;

import io.r2mo.jaas.enums.UserIDType;

/**
 * @author lang : 2025-11-12
 */
public class UserDetailsContext {
    private static final ThreadLocal<UserIDType> STRATEGY = new ThreadLocal<>();

    public static void setStrategy(final UserIDType idType) {
        STRATEGY.set(idType);
    }

    public static UserIDType getStrategy() {
        return STRATEGY.get();
    }

    public static void clearStrategy() {
        STRATEGY.remove();
    }
}
