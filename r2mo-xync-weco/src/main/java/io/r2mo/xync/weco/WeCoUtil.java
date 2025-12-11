package io.r2mo.xync.weco;

import cn.hutool.core.util.StrUtil;
import io.r2mo.base.exchange.NormProxy;
import io.r2mo.base.exchange.UniMessage;
import io.r2mo.spi.SPI;
import io.r2mo.typed.exception.web._400BadRequestException;
import io.r2mo.typed.json.JObject;
import me.chanjar.weixin.cp.config.impl.WxCpDefaultConfigImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lang : 2025-12-10
 */
public class WeCoUtil {
    /**
     * 为微信公众号 Config 应用代理
     */
    public static void applyProxy(final WxMpDefaultConfigImpl config, final NormProxy proxy) {
        if (proxy == null) {
            return;
        }
        config.setHttpProxyHost(proxy.getHost());
        config.setHttpProxyPort(proxy.getPort());
        config.setHttpProxyUsername(proxy.getUsername());
        config.setHttpProxyPassword(proxy.getPassword());
    }

    /**
     * 为企业微信 Config 应用代理
     */
    public static void applyProxy(final WxCpDefaultConfigImpl config, final NormProxy proxy) {
        if (proxy == null) {
            return;
        }
        config.setHttpProxyHost(proxy.getHost());
        config.setHttpProxyPort(proxy.getPort());
        config.setHttpProxyUsername(proxy.getUsername());
        config.setHttpProxyPassword(proxy.getPassword());
    }

    /**
     * QrCode前置方法 / 提取 expireSeconds 的专用工具类
     */
    @SuppressWarnings("all")
    public static int inputExpired(final UniMessage<?> request) {
        final String expireSecondsStr = request.header("expireSeconds");

        if (StrUtil.isBlank(expireSecondsStr)) {
            throw new _400BadRequestException("[R2MO] Header 缺少 'expireSeconds' 参数，该参数为必填项。");
        }

        final int expireSeconds;
        try {
            expireSeconds = Integer.parseInt(expireSecondsStr);
            if (expireSeconds <= 0 || expireSeconds > WeCoSession.MAX_EXPIRE_SECONDS) {
                throw new _400BadRequestException(
                    "expireSeconds 必须大于0且小于等于 " + WeCoSession.MAX_EXPIRE_SECONDS + " (30天)。"
                );
            }
        } catch (final NumberFormatException e) {
            throw new _400BadRequestException("expireSeconds 必须是一个有效的整数值。");
        }
        return expireSeconds;
    }

    public static JObject replyQr(final String uuid,
                                  final String url,
                                  final int expireSeconds) {
        // 2. 存储初始状态到 SPI
        final String sessionKey = WeCoSession.keyOf(uuid);
        final Duration storeDuration = Duration.ofSeconds(expireSeconds);

        // 调用通过 SPI 机制获取的 WeCoSession 实例
        WeCoSession.of().save(
            sessionKey,
            WeCoStatus.WAITING.name(),
            storeDuration
        );

        // 3. 封装结果返回给上层 Service
        final Map<String, Object> result = new HashMap<>();
        result.put(WeCoConstant.PARAM_UUID, uuid);
        result.put("qrUrl", url);
        result.put("expireSeconds", expireSeconds);
        result.put("actionType", WeCoActionType.APP_AUTH_QR.name());
        final JObject response = SPI.J();
        response.put(result);
        return response;
    }

    public static JObject replyStatus(final UniMessage<String> request) {
        // 读取 expireSeconds
        final int expireSeconds = inputExpired(request);
        // Payload 约定为 UUID 字符串
        final String uuid = request.payload();

        if (uuid == null) {
            throw new _400BadRequestException("[ R2MO ] 缺少 Payload 参数: UUID");
        }

        // 1. 构建缓存 Key 并查询 SPI 存储
        final String sessionKey = WeCoSession.keyOf(uuid);
        final Duration storeDuration = Duration.ofSeconds(expireSeconds);
        final String status = WeCoSession.of().get(sessionKey, storeDuration);

        final JObject result = SPI.J();

        // 2. 判断状态
        if (status == null || WeCoStatus.WAITING.name().equals(status) || WeCoStatus.EXPIRED.name().equals(status)) {
            // 状态：等待中、过期、或缓存不存在
            result.put("status", WeCoStatus.WAITING.name());
            result.put("isSuccess", false);
        } else {
            // 状态：成功 (缓存中存储的就是 OpenID)
            result.put("status", WeCoStatus.SUCCESS.name());
            result.put("isSuccess", true);
            // 缓存中的值就是 Token，返回给上层 Service
            result.put("token", status);
        }
        return result;
    }
}
