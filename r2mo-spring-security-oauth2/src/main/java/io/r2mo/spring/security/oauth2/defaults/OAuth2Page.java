package io.r2mo.spring.security.oauth2.defaults;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import io.r2mo.function.Fn;
import io.r2mo.spi.SPI;
import io.r2mo.spring.security.oauth2.OAuth2Endpoint;
import io.r2mo.spring.security.oauth2.config.ConfigOAuth2;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.exception.web._400BadRequestException;
import io.r2mo.typed.exception.web._404NotFoundException;
import io.r2mo.typed.exception.web._500ServerInternalException;
import io.r2mo.typed.json.JObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author lang : 2025-11-28
 */
@Slf4j
public class OAuth2Page {

    private final static Cc<String, OAuth2Page> CC_SKELETON = Cc.openThread();
    private static ConfigOAuth2 CONFIG;
    // State 参数的分隔符，格式：{业务状态}_VC_{CODE_VERIFIER}
    private static final String STATE_SEPARATOR = "_VC_";

    private final RestTemplate restTemplate = new RestTemplate();

    private OAuth2Page() {
    }

    private static ConfigOAuth2 config() {
        if (Objects.isNull(CONFIG)) {
            CONFIG = SpringUtil.getBean(ConfigOAuth2.class);
        }
        return CONFIG;
    }

    public static OAuth2Page of() {
        return CC_SKELETON.pick(OAuth2Page::new);
    }

    public JObject handleToken(final String registrationId,
                               final String code,
                               final String state,
                               final String error) {
        // 这里可以处理授权回调逻辑
        Fn.jvmKo(StrUtil.isNotEmpty(error), _404NotFoundException.class, error);

        Fn.jvmKo(StrUtil.isEmpty(code), _400BadRequestException.class, "[ R2MO ] 授权码 code 不能为空");

        try {
            /*
             * 从 Repository (数据库) 获取客户端信息
             * 假设 registrationId 就是 clientId
             */
            final RegisteredClientRepository clientRepo = SpringUtil.getBean(RegisteredClientRepository.class);
            final RegisteredClient client = clientRepo.findByClientId(registrationId);
            Fn.jvmKo(Objects.isNull(client), _404NotFoundException.class, "[ R2MO ] 未找到客户端信息");


            /*
             * 智能匹配 Redirect URI
             * 数据库中可能配了多个 URI，此处需要找到匹配当前回调路径的 URI
             * - 默认地址：/authorized/{registrationId}
             * 验证配置的 URI 是否包含当前的内容
             */
            final String uriCallback = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/authorized/" + registrationId)
                .toUriString();

            assert client != null;
            final Set<String> validUris = client.getRedirectUris();
            // 尝试精确匹配，匹配不到则使用当前构建的
            final String matchedRedirectUri = validUris.stream()
                .filter(uri -> uri.equals(uriCallback))
                .findFirst()
                .orElse(uriCallback);

            /*
             * 4. 提取 PKCE Verifier
             * 严格校验：如果没有传 Verifier，则无法完成 PKCE 流程，直接抛出异常
             */
            String codeVerifier = null;
            if (StrUtil.isNotEmpty(state) && state.contains(STATE_SEPARATOR)) {
                final String[] parts = state.split(STATE_SEPARATOR);
                // 取最后一部分作为 Verifier，防止业务状态中也包含分隔符导致切割错误
                if (parts.length > 1) {
                    codeVerifier = parts[parts.length - 1];
                }
            }

            Fn.jvmKo(StrUtil.isEmpty(codeVerifier), _400BadRequestException.class, "[ R2MO ] PKCE Code Verifier 不能为空");

            /*
             * 5. 构建 /oauth2/token 请求
             * - 构造 URL
             * - 构造参数信息
             * - 发送请求并获取对应响应
             */
            final String tokenEndpointUrl = this.resolveTokenUrl();
            log.info("[ R2MO ] ( Callback ) OAuth2 Token 端点 URL: {}", tokenEndpointUrl);

            // 5.1 准备 Header (application/x-www-form-urlencoded)
            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // 5.2 准备 Body 参数
            final MultiValueMap<String, String> request = new LinkedMultiValueMap<>();
            request.add("grant_type", AuthorizationGrantType.AUTHORIZATION_CODE.getValue());
            request.add("code", code);
            request.add("client_id", client.getClientId());
            request.add("redirect_uri", matchedRedirectUri);
            request.add("code_verifier", codeVerifier);

            final HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(request, headers);

            @SuppressWarnings("all") final ResponseEntity<Map> response =
                this.restTemplate.postForEntity(tokenEndpointUrl, requestEntity, Map.class);

            @SuppressWarnings("unchecked") final Map<String, Object> responseBody = response.getBody();

            Fn.jvmKo(Objects.isNull(responseBody), _500ServerInternalException.class, "[ R2MO ] Token 响应体为空");

            final JObject responseJ = SPI.J();
            responseJ.put(responseBody);
            return responseJ;
        } catch (final Exception ex) {
            throw new _500ServerInternalException(ex.getMessage());
        }
    }

    /**
     * 🟢【新增】解析 Token 端点路径
     * 优先使用配置中的路径，如果未配置则使用默认路径
     */
    private String resolveTokenUrl() {
        final ConfigOAuth2.ServerSettings settings = OAuth2Page.config().getServerSettings();
        String uriToken = settings != null ? settings.getTokenEndpoint() : null;
        if (uriToken == null) {
            // OAuth2Endpoint.TOKEN() 应该是你项目中定义的常量，如 "/oauth2/token"
            uriToken = OAuth2Endpoint.TOKEN();
        }
        return ServletUriComponentsBuilder.fromCurrentContextPath()
            .path(uriToken)
            .build().toUriString();
    }

    public String handleLoginHtml(final Resource page, final String error) {
        try {
            // 1. 读取文件内容为字符串
            final String html = StreamUtils.copyToString(
                page.getInputStream(),
                StandardCharsets.UTF_8
            );

            // 2. 简单的动态处理：如果有 error 参数，替换占位符显示错误框
            String errorHtml = "";
            if (error != null) {
                // 这里可以根据需要调整 CSS 样式类名
                errorHtml = "<div class='error-message' style='color:red;text-align:center;margin-bottom:10px;'>账号或密码错误，请重试</div>";
            }

            // 3. 🟢 修改点 2：指定要替换的 HTML 占位符
            // 你的 HTML 文件里必须包含 这个字符串
            return html.replace("", errorHtml);

        } catch (final IOException e) {
            return "Error loading login page: " + e.getMessage();
        }
    }
}
