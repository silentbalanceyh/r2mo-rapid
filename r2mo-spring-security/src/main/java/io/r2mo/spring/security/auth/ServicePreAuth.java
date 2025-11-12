package io.r2mo.spring.security.auth;

import io.r2mo.typed.common.Kv;

/**
 * 前置服务，可直接查找前置服务
 *
 * @author lang : 2025-11-11
 */
public interface ServicePreAuth {

    Kv<String, String> authorize(String identifier);
}
