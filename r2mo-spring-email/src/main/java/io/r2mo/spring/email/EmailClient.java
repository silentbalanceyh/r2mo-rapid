package io.r2mo.spring.email;

import io.r2mo.typed.json.JObject;

import java.util.Set;

/**
 * 邮件客户端，发邮件专用
 *
 * @author lang : 2025-12-05
 */
public interface EmailClient {

    JObject send(String template, JObject params, Set<String> toSet);
}
