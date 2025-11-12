package io.r2mo.spring.security.extension.captcha;

import java.util.Map;

/**
 * 验证码服务接口
 *
 * @author lang : 2025-11-13
 */
public interface CaptchaService {

    /**
     * 生成图形验证码
     *
     * @return { "captchaKey": "唯一标识", "image": "data:image/png;base64,..." }
     */
    Map<String, String> generate();

    /**
     * 验证用户输入
     *
     * @param captchaKey 用户提交的验证码标识
     * @param userInput  用户输入的验证码（不区分大小写）
     *
     * @return true 表示验证通过
     */
    boolean validate(String captchaKey, String userInput);

    /**
     * 主动作废验证码（如刷新时）
     */
    void invalidate(String captchaKey);
}