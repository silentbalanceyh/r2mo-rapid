package io.r2mo.spring.security.oauth2.defaults;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import io.r2mo.function.Fn;
import io.r2mo.spi.SPI;
import io.r2mo.spring.common.config.WebApp;
import io.r2mo.spring.security.oauth2.OAuth2Endpoint;
import io.r2mo.spring.security.oauth2.config.ConfigOAuth2;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.exception.web._400BadRequestException;
import io.r2mo.typed.exception.web._404NotFoundException;
import io.r2mo.typed.exception.web._500ServerInternalException;
import io.r2mo.typed.json.JObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Map;
import java.util.Objects;

/**
 * OAuth2 授权页面回调处理逻辑 (纯客户端版本)
 *
 * @author lang
 */
@Slf4j
public class OAuth2ClientAuthorized {

    private final static Cc<String, OAuth2ClientAuthorized> CC_SKELETON = Cc.openThread();
    private static final String STATE_SEPARATOR = "_VC_";
    private static ConfigOAuth2 CONFIG;
    private static WebApp app;
    private final RestTemplate restTemplate = new RestTemplate();

    private OAuth2ClientAuthorized() {
    }

    // ... config() 和 app() 方法保持不变 ...
    private static WebApp app() {
        if (Objects.isNull(app)) {
            app = SpringUtil.getBean(WebApp.class);
        }
        return app;
    }

    private static ConfigOAuth2 config() {
        if (Objects.isNull(CONFIG)) {
            CONFIG = SpringUtil.getBean(ConfigOAuth2.class);
        }
        return CONFIG;
    }

    public static OAuth2ClientAuthorized of() {
        return CC_SKELETON.pick(OAuth2ClientAuthorized::new);
    }

    public JObject handleToken(final String registrationId,
                               final String code,
                               final String state,
                               final String error) {

        Fn.jvmKo(StrUtil.isNotEmpty(error), _404NotFoundException.class, error);
        Fn.jvmKo(StrUtil.isEmpty(code), _400BadRequestException.class, "[ R2MO ] 授权码 code 不能为空");

        try {
            // ❌ 【删除】 绝对不要在 Client 端查 Server 的数据库！
            // final RegisteredClientRepository clientRepo = ...
            // final RegisteredClient client = ...

            // ✅ 【新增】 直接从本地配置获取 Client ID 和 Secret (明文)
            // 假设 registrationId 就是 clientId，或者你可以从 app() 里拿
            final String clientId = app().getClientId();
            final String clientSecret = app().getClientSecret(); // 这是 YAML 里的明文

            // 1. 智能匹配 Redirect URI (保持原有逻辑)
            final String matchedRedirectUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/oauth2/authorized/" + registrationId)
                .toUriString();

            // 2. 提取 PKCE Verifier
            String codeVerifier = null;
            if (StrUtil.isNotEmpty(state) && state.contains(STATE_SEPARATOR)) {
                final String[] parts = state.split(STATE_SEPARATOR);
                if (parts.length > 1) {
                    codeVerifier = parts[parts.length - 1];
                }
            }

            // 3. 构建 Token URL
            final String tokenEndpointUrl = this.resolveTokenUrl();
            log.info("[ R2MO ] Token Url: {}", tokenEndpointUrl);

            // 4.1 准备 Header
            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // 4.2 准备 Body
            final MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
            request.add("grant_type", AuthorizationGrantType.AUTHORIZATION_CODE.getValue());
            request.add("code", code);
            request.add("redirect_uri", matchedRedirectUri);

            if (StrUtil.isNotEmpty(codeVerifier)) {
                request.add("code_verifier", codeVerifier);
            }

            // 5. 客户端认证 (关键修正)
            if (StrUtil.isNotEmpty(clientSecret)) {
                // ✅ 必须用 setBasicAuth，因为服务端配置的是 client_secret_basic
                // 这里的 clientId 和 clientSecret 必须都是【明文】
                headers.setBasicAuth(clientId, clientSecret);
            } else {
                // Public Client (无 Secret)
                request.add("client_id", clientId);
            }

            // 6. 发送请求
            final HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(request, headers);

            @SuppressWarnings("all") final ResponseEntity<Map> response =
                this.restTemplate.postForEntity(tokenEndpointUrl, requestEntity, Map.class);

            @SuppressWarnings("unchecked") final Map<String, Object> responseBody = response.getBody();

            Fn.jvmKo(Objects.isNull(responseBody), _500ServerInternalException.class, "[ R2MO ] Token 响应体为空");

            final JObject responseJ = SPI.J();
            responseJ.put(responseBody);

            // 7. 回调扩展
            final OAuth2ClientCallback callback = OAuth2ClientCallback.of(registrationId);
            if (Objects.nonNull(callback)) {
                // 注意：这里传 null 或者传 clientId，因为 RegisteredClient 对象已经拿不到了
                // callback 接口可能需要调整签名
                return callback.handleCallback(responseJ, null);
            }

            return responseJ;
        } catch (final Exception ex) {
            log.error("[ R2MO ] OAuth2 Token Exchange Failed", ex);
            throw new _500ServerInternalException(ex.getMessage());
        }
    }

    // resolveTokenUrl 保持不变
    private String resolveTokenUrl() {
        final ConfigOAuth2.ServerSettings settings = OAuth2ClientAuthorized.config().getServerSettings();
        String uriToken = settings != null ? settings.getTokenEndpoint() : null;
        if (uriToken == null) {
            uriToken = OAuth2Endpoint.TOKEN();
        }
        return ServletUriComponentsBuilder.fromCurrentContextPath()
            .path(uriToken)
            .build().toUriString();
    }
}