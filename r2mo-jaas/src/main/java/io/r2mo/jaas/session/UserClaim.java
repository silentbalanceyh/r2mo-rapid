package io.r2mo.jaas.session;

import io.r2mo.jaas.element.MSUser;

import java.util.Map;

/**
 * SPI 用户令牌，用于扩展用户认证中的基本信息
 *
 * @author lang : 2025-11-14
 */
public interface UserClaim {

    Map<String, Object> token(MSUser user);
}
