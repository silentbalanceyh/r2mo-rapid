package io.r2mo.spring.security.oauth2.defaults;

import io.r2mo.spi.SPI;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.json.JObject;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

/**
 * 基于 SPI 的 OAuth2 的回调接口扩展，可以根据不同的生命周期返回不同的 {@link JObject} 对象，用于前端做更细粒度的处理，特别是
 * 不同终端的定制化需求集合，由于页面级不用处理，所以此处不考虑 /login 的扩展，/login 扩展仅操作页面即可。
 * <pre>
 *      此 SPI 方案仅用于不定制 Controller 的场景中，如果要定制 Controller 则此此接口可以忽略
 * </pre>
 *
 * @author lang : 2025-12-05
 */
public interface OAuth2ClientCallback {
    Cc<String, OAuth2ClientCallback> CC_SKELETON = Cc.openThread();

    static OAuth2ClientCallback of(final String registrationId) {
        final String id = "CLIENT/" + registrationId;
        return CC_SKELETON.pick(() -> SPI.findOne(OAuth2ClientCallback.class, id), id);
    }

    JObject handleCallback(JObject tokenJ, RegisteredClient client);
}
