package io.r2mo.base.exchange;

/**
 * @author lang : 2025-12-05
 */
public interface UniContext {
    // --- 标准技术键定义 (Standard Keys) ---
    // 定义这些常量是为了让所有插件（微信、邮件、短信）尽可能复用标准 Key，而不是各自造词

    String KEY_HOST = "host";           // e.g. "smtp.qq.com", "api.weixin.qq.com"
    String KEY_PORT = "port";           // e.g. 465, 80, 443
    String KEY_PROTOCOL = "protocol";   // e.g. "https", "smtp", "pop3"
    String KEY_SSL = "ssl";             // e.g. true/false
    String KEY_TIMEOUT = "timeout";     // e.g. 5000 (ms)

    // --- 核心方法 ---

    /**
     * 设置参数 (支持链式)
     */
    UniContext set(String key, Object value);

    /**
     * 获取参数 (强制类型转换，调用方保证类型安全)
     */
    <T> T get(String key);

    /**
     * 获取参数 (带默认值)
     */
    <T> T getOrDefault(String key, T defaultValue);

    // 新增：方便判断是否存在配置
    default boolean has(final String key) {
        return this.get(key) != null;
    }

    // 快捷获取 Host 的默认实现（因为太常用了）
    default String getHost() {
        return this.get(KEY_HOST);
    }

    default Integer getPort() {
        return this.get(KEY_PORT);
    }
}
