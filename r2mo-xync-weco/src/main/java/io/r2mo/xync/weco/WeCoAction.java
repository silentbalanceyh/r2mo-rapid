package io.r2mo.xync.weco;

import io.r2mo.base.exchange.NormProxy;
import me.chanjar.weixin.cp.config.impl.WxCpDefaultConfigImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;

/**
 * 微信/企微 认证动作常量
 *
 * @author lang : 2025-12-09
 */
public interface WeCoAction {
    /**
     * 动作：获取扫码登录 URL
     */
    String GET_AUTH_URL = "GET_AUTH_URL";

    /**
     * 动作：使用 Code 换取用户信息
     */
    String LOGIN_BY_CODE = "LOGIN_BY_CODE";

    // --- Header Keys ---
    String HEADER_REDIRECT_URI = "redirectUri";
    String HEADER_STATE = "state";

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