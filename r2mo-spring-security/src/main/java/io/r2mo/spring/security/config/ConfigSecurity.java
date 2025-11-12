package io.r2mo.spring.security.config;

import io.r2mo.typed.common.Kv;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <pre>
 *     application.yml 中的格式如
 *     security:
 *        ignore-uris:
 *          - /api/public/**
 *        scope:
 *          app: true
 *          tenant: true
 *
 *        basic:
 *          realm: "R2MO Realm"
 *
 *        captcha:
 *          enabled: true
 *          type: circle                # 可选 circle-圆圈干扰验证码
 *          expiredAt: 30s              # 单位秒
 *          width: 150
 *          height: 50
 *          textAlpha: 0.8              # 文字透明度，0~1
 *          code:
 *            type: RANDOM              # 可选 MATH-算术 | RANDOM-随机字符
 *            length: 4
 *          font:
 *            name: Arial
 *            weight: 0
 *            size: 20
 *
 *        jwt:
 *          issuer: "R2MO Issuer"       # 签发者
 *          expiredAt: 30m              # 单位分钟
 *          refreshAt: 7d               # 单位天
 * </pre>
 *
 * @author lang : 2025-11-10
 */
@Configuration
@ConfigurationProperties(prefix = "security")
@Data
@RefreshScope
public class ConfigSecurity implements Serializable {
    private List<String> ignoreUris;
    private ConfigSecurityCaptcha captcha;
    private ConfigSecurityJwt jwt;
    private ConfigSecurityBasic basic = new ConfigSecurityBasic();  // 默认打开
    private ConfigSecurityScope scope;

    public List<Kv<String, HttpMethod>> ignoreUris() {
        // 未配置 ignore-uris 的情况下，直接返回空
        return ignoreUris(this.ignoreUris, null);
    }

    public static List<Kv<String, HttpMethod>> ignoreUris(final Collection<String> uris,
                                                          final HttpMethod defaultMethod) {
        if (Objects.isNull(uris)) {
            return new ArrayList<>();
        }
        /*
         * 配置之后格式会有两种
         * 1. /api/public/**:POST
         * 2. /api/public/**（全方法，只做路径匹配）
         */
        return uris.stream().map(item -> {
            final String[] segments = item.split(":", 2);   // 只分割一次
            final Kv<String, HttpMethod> uri;
            if (1 < segments.length) {
                final HttpMethod method = HttpMethod.valueOf(segments[1].trim());
                uri = Kv.create(segments[0].trim(), method);
            } else {
                uri = Kv.create(segments[0].trim(), defaultMethod);
            }
            return uri;
        }).collect(Collectors.toList());
    }

    // 内置配置：是否开启 Basic 认证
    public boolean isBasic() {
        return Objects.nonNull(this.basic) && this.basic.isEnabled();
    }

    // 内置配置：是否开启 JWT 认证
    public boolean isJwt() {
        return Objects.nonNull(this.jwt) && this.jwt.isEnabled();
    }

    // 内置配置：是否开启图片验证码
    public boolean isCaptcha() {
        return Objects.nonNull(this.captcha) && this.captcha.isEnabled();
    }
}
