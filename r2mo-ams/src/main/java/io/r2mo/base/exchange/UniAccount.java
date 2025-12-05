package io.r2mo.base.exchange;

/**
 * 发送者账号契约
 * 区分了核心技术身份（Identity）和业务展示属性（Profile）。
 *
 * @author lang : 2025-12-05
 */
public interface UniAccount {

    // --- Core Identity (核心技术属性) ---

    /**
     * 业务ID (e.g. "user_1001", "bot_aliyun")
     */
    String id();

    /**
     * 发送签名 / 身份标识
     * 短信的【签名】，微信的【公众号名】，邮件的【From Address】
     */
    String signature();

    /**
     * 技术凭证 (多态入口)
     */
    UniCredential credential();


    // --- Business Profile (业务扩展/展示属性) ---
    // 使用 get 前缀，表明这些是“附加”的、“可能从别处获取”的信息

    /**
     * 发送者昵称 (用于 UI 显示或日志)
     */
    String getName();

    /**
     * 发送者头像 (用于 UI 显示)
     */
    String getAvatar();
}