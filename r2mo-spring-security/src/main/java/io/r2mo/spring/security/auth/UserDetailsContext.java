package io.r2mo.spring.security.auth;

import io.r2mo.jaas.enums.TypeID;

/**
 * @author lang : 2025-11-12
 */
public class UserDetailsContext {
    private static final ThreadLocal<TypeID> STRATEGY = new ThreadLocal<>();

    public static void setStrategy(final TypeID typeID) {
        STRATEGY.set(typeID);
    }

    public static TypeID getStrategy() {
        return STRATEGY.get();
    }

    public static void clearStrategy() {
        STRATEGY.remove();
    }
}
