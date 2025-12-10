package io.r2mo.spring.security.token;

import io.r2mo.jaas.enums.TypeToken;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.exception.web._404NotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/**
 * @author lang : 2025-11-12
 */
@Slf4j
public class TokenBuilderManager {

    private static final ConcurrentMap<TypeToken, Supplier<TokenBuilder>> CC_SUPPLIER = new ConcurrentHashMap<>();
    private static final Cc<String, TokenBuilder> CCT_BUILDER = Cc.openThread();
    private static TokenBuilderManager INSTANCE;

    private TokenBuilderManager() {
    }

    public static TokenBuilderManager of() {
        if (INSTANCE == null) {
            INSTANCE = new TokenBuilderManager();
        }
        return INSTANCE;
    }

    public void registry(final TypeToken token, final Supplier<TokenBuilder> constructorFn) {
        CC_SUPPLIER.putIfAbsent(token, constructorFn);
        log.info("[ R2MO ]     ----> 注册 Token 构建器：tokenType = `{}`", token);
    }

    public TokenBuilder getOrCreate(final TypeToken token) {
        final Supplier<TokenBuilder> constructorFn = CC_SUPPLIER.getOrDefault(token, null);
        if (Objects.isNull(constructorFn)) {
            throw new _404NotFoundException("[ R2MO ] 未找到对应的 Token 构建器：tokenType = " + token);
        }
        return CCT_BUILDER.pick(constructorFn, token.name());
    }
}
