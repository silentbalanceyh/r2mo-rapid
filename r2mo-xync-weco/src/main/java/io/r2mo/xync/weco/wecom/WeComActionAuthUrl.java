package io.r2mo.xync.weco.wecom;

import cn.hutool.core.util.StrUtil;
import io.r2mo.base.exchange.UniMessage;
import io.r2mo.base.exchange.UniResponse;
import io.r2mo.typed.exception.web._400BadRequestException;
import io.r2mo.xync.weco.WeCoAction;
import io.r2mo.xync.weco.WeCoConstant;
import me.chanjar.weixin.cp.api.WxCpService;

/**
 * 动作：获取企业微信 OAuth2 授权跳转 URL (WX_AUTH_URL)
 * <p>适用于 PC 端扫码登录或移动端 H5 授权。</p>
 *
 * @author lang : 2025-12-10
 */
public class WeComActionAuthUrl extends WeComAction implements WeCoAction<Void> {

    public WeComActionAuthUrl(final WxCpService service) {
        super(service);
    }

    /**
     * 执行获取企业微信 OAuth2 授权跳转 URL 的命令。
     * <pre>
     * 输入格式 ({@link UniMessage})
     * Header:
     * - redirectUri: String. 授权成功后，企业微信回调的完整地址。**必需**。
     * - state: String. 业务自定义状态参数，原样返回。**必需**。
     * Payload:
     * null (Void)
     * 输出格式 ({@link UniResponse})
     * Payload:
     * String. 构造完成的企业微信授权跳转 URL (<a href="https://open.work.weixin.qq.com/wwopen/sso/qrConnect...">...</a>)
     * </pre>
     *
     * @param request 封装了回调地址和状态参数的 UniMessage 请求。
     *
     * @return 包含授权跳转 URL 字符串的 UniResponse。
     * @throws Exception 企业微信API调用失败或参数缺失。
     */
    @Override
    public UniResponse execute(final UniMessage<Void> request) throws Exception {
        final String redirectUri = request.header(WeCoConstant.HEADER_REDIRECT_URI);
        final String state = request.header(WeCoConstant.HEADER_STATE);

        if (StrUtil.isBlank(redirectUri) || StrUtil.isBlank(state)) {
            throw new _400BadRequestException("[ R2MO ] 缺少必要 Header: redirectUri 或 state");
        }

        // 构造扫码 URL (企微不需要 scope 参数，方法签名不同)
        final String url = this.service().buildQrConnectUrl(redirectUri, state);
        return UniResponse.success(url);
    }
}