package io.r2mo.spring.security.auth;

import io.r2mo.typed.enums.TypeLogin;

/**
 * @author lang : 2025-11-12
 */
public class UserDetailsContext {
    private static final ThreadLocal<TypeLogin> STRATEGY = new ThreadLocal<>();

    public static void setStrategy(final TypeLogin typeID) {
        STRATEGY.set(typeID);
    }

    public static TypeLogin getStrategy() {
        return STRATEGY.get();
    }

    public static void clearStrategy() {
        STRATEGY.remove();
    }
}
