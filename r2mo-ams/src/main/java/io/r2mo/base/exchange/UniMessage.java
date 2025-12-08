package io.r2mo.base.exchange;

import java.util.Map;
import java.util.Set;

/**
 * 消息体契约
 *
 * @author lang : 2025-12-05
 */
public interface UniMessage<T> {

    String id();                    // 消息ID

    String subject();               // 消息主题

    /**
     * 接收人列表 (Recipients)
     * <p>
     * 统一抽象发送目标，例如：
     * <ul>
     *     <li>短信：手机号 (e.g. "13800138000")</li>
     *     <li>邮件：邮箱地址 (e.g. "user@example.com")</li>
     *     <li>微信：OpenID (e.g. "oW12345...")</li>
     * </ul>
     * </p>
     *
     * @return 接收人集合
     */
    Set<String> to();

    void addTo(String... toList);

    T payload();                    // 消息内容

    Map<String, Object> header();   // 消息头

    <H> H header(String key);          // 获取指定头信息
}
