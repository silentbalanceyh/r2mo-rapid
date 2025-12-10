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
    // 参数键定义 (Keys)
    // ==========================================

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