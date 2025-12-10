package io.r2mo.xync.weco;

import io.r2mo.base.exchange.NormProxy;
import me.chanjar.weixin.cp.config.impl.WxCpDefaultConfigImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;

/**
 * 微信/企微 认证动作常量
 *
 * @author lang : 2025-12-09
 */
public interface WeCoConstant {
    // ==========================================
    // 动作定义 (Actions)
    // ==========================================

    // 这两个动作对应：微信内部打开应用 -> 扫码授权 -> 进入应用
    /**
     * 动作：获取扫码登录 URL
     */
    String WX_AUTH_URL = "WX_AUTH_URL";

    /**
     * 动作：使用 Code 换取用户信息
     */
    String WX_LOGIN_BY = "WX_LOGIN_BY";

    // 这两个动作对应：应用中点开微信二维码 -> 扫码 -> 进入应用
    // ==========================================
    // 参数键定义 (Keys)
    // ==========================================
    /**
     * 动作：获取扫码登录的二维码 (PC 端展示用)
     * <br>对应实现：生成 UUID -> 调微信接口换图片 -> 存入缓存
     */
    String APP_AUTH_QR = "APP_AUTH_QR";
    /**
     * 动作：检查扫码状态 (PC 端轮询用)
     * <br>对应实现：查缓存看 UUID 是否关联了 OpenID
     */
    String APP_STATUS = "APP_STATUS";

    // --- Header Keys ---
    String HEADER_REDIRECT_URI = "redirectUri";
    String HEADER_STATE = "state";
    /** 扫码会话 ID/参数的 Key (用于 APP_AUTH_QR) */
    String PARAM_UUID = "uuid";

    /**
     * 内部帮助类：统一处理配置注入（代理等）
     */
    class Helper {
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
    }
}