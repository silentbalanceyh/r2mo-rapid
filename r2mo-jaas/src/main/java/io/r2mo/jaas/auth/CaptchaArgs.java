package io.r2mo.jaas.auth;

import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.enums.TypeLogin;

import java.io.Serializable;
import java.time.Duration;
import java.util.Objects;

/**
 * 验证码专用参数处理，此处参数通常只有唯一的处理模型，一般从配置中提取，也就是说所有请求可共享此参数的详细信息
 * <pre>
 *     1. 包含了 {@link TypeLogin}，常用的如下
 *        - 图片验证码
 *        - 邮件验证码
 *        - 短信验证码
 *     2. 可根据实际需求进行扩展，其次是 Duration 有效时间，通常配置之后就不会再有新的，所以由当前类统一管理
 * </pre>
 *
 * @author lang : 2025-12-08
 */
public class CaptchaArgs implements Serializable {

    private final Duration duration;
    private final TypeLogin type;

    private CaptchaArgs(final TypeLogin type,
                        final Duration duration) {
        this.type = type;
        // 默认使用 60 秒
        this.duration = Objects.isNull(duration) ? Duration.ofSeconds(60) : duration;
    }

    private static final Cc<String, CaptchaArgs> CC_CAPTCHA = Cc.open();

    public static CaptchaArgs of(final TypeLogin type,
                                 final Duration duration) {
        final String cacheKey = CaptchaArgs.class.getName() + "@" + type.name() + "@" + duration.hashCode();
        return CC_CAPTCHA.pick(() -> new CaptchaArgs(type, duration), cacheKey);
    }

    public Duration duration() {
        return this.duration;
    }

    public TypeLogin type() {
        return this.type;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final CaptchaArgs that = (CaptchaArgs) o;
        return Objects.equals(this.duration, that.duration) && this.type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.duration, this.type);
    }
}
