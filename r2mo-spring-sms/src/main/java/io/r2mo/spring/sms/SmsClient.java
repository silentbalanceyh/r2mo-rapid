package io.r2mo.spring.sms;

import io.r2mo.typed.json.JObject;

import java.util.Set;

/**
 * @author lang : 2025-12-08
 */
public interface SmsClient {

    JObject send(String template, JObject params, Set<String> toSet);
}
