package io.r2mo.xync.sms;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import io.r2mo.base.exchange.UniAccount;
import io.r2mo.base.exchange.UniContext;
import io.r2mo.base.exchange.UniCredential;
import io.r2mo.base.exchange.UniMessage;
import io.r2mo.base.exchange.UniProvider;
import io.r2mo.function.Supplier;
import io.r2mo.spi.SPI;
import io.r2mo.typed.annotation.SPID;
import io.r2mo.typed.cc.Cc;
import io.r2mo.typed.exception.web._500ServerInternalException;
import io.r2mo.typed.json.JObject;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lang : 2025-12-08
 */
@SPID("UNI_SMS")
@Slf4j
public class SmsProvider implements UniProvider {
    private static final Cc<String, IAcsClient> CC_CLIENT = Cc.open();

    @Override
    public String channel() {
        return "SMS";
    }

    @Override
    public Class<? extends UniCredential> credentialType() {
        return SmsCredential.class;
    }

    @Override
    public String send(final UniAccount account, final UniMessage<?> message, final UniContext context) {
        // 1. 凭证校验与转换
        if (!(account.credential() instanceof final SmsCredential cred)) {
            throw new IllegalArgumentException("[ R2MO ] 凭证类型不匹配，需要 SmsCredential");
        }
        if (!(context instanceof final SmsContext ctx)) {
            throw new IllegalArgumentException("[ R2MO ] 上下文类型不匹配，需要 SmsContext");
        }

        // 2. 初始化客户端
        final IAcsClient client = this.getClient(cred, ctx);

        // 3. 发送短信
        final SendSmsRequest request = this.getRequest((SmsAccount) account, message, ctx);
        return this.handleResponse(() -> client.getAcsResponse(request), message.to().iterator().next());
    }

    private String handleResponse(final Supplier<SendSmsResponse> responseFn, final String to) {
        try {
            final SendSmsResponse response = responseFn.get();
            log.info("[ R2MO ] 短信发送成功：{}, {}/{}, 响应：{}/{}", to,
                response.getRequestId(), response.getBizId(),
                response.getCode(), response.getMessage());
            return response.getBizId();
        } catch (final ClientException ex) {
            log.error("[ R2MO ] 短信发送失败：{}", ex.getMessage());
            throw new _500ServerInternalException(ex.getMessage());
        } catch (final Throwable ex) {
            log.error("[ R2MO ] 短信发送失败（其他异常）：{}", ex.getMessage());
            throw new _500ServerInternalException(ex.getMessage());
        }
    }

    private SendSmsRequest getRequest(final SmsAccount account,
                                      final UniMessage<?> message, final SmsContext context) {
        final SendSmsRequest request = new SendSmsRequest();
        final String to = message.to().iterator().next();
        request.setPhoneNumbers(to);
        request.setSignName(account.signature());
        final JObject params = SPI.J();
        params.put(message.params());
        final String template = params.getString("template");
        request.setTemplateCode(template);
        request.setTemplateParam(params.encode());
        return request;
    }

    private IAcsClient getClient(final SmsCredential cred, final SmsContext ctx) {

        System.setProperty("sun.net.client.defaultConnectTimeout", String.valueOf(ctx.getTimeoutConnect()));
        System.setProperty("sun.net.client.defaultReadTimeout", String.valueOf(ctx.getTimeoutRead()));

        final String cacheKey = ctx.hashCode() + "@" + cred.hashCode();
        return CC_CLIENT.pick(() -> {
            log.info("[ R2MO ] 初始化新的客户端：{}", cacheKey);
            final String region = ctx.getRegion();
            final String product = ctx.getProduct();
            try {
                DefaultProfile.addEndpoint(region, product, ctx.getHost());
            } catch (final Throwable ex) {
                throw new _500ServerInternalException(ex.getMessage());
            }
            // AscClient initialized
            final IClientProfile profile = DefaultProfile.getProfile(region, cred.accessId(), cred.accessSecret());
            return new DefaultAcsClient(profile);
        }, cacheKey);
    }
}
