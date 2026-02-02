package io.r2mo.typed.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * @author lang : 2025-11-10
 */
public enum TypeID {
    // 已完成的部分
    WECHAT,     // 微信
    WECOM,      // 企微
    LDAP,       // LDAP
    // ----------- 待开发
    ALIPAY,     // 支付宝
    TAOBAO,     // 淘宝
    WEIBO,      // 微博
    GOOGLE,     // 谷歌
    QQ,         // QQ
    FACEBOOK,   // 脸书 ( Meta )
    GITHUB,     // GitHub
    LINKEDIN,   // 领英
    TWITTER,    // 推特 ( X )
    DOUBAN,     // 豆瓣
    DOUYIN,     // 抖音
    APPLE,      // 苹果 (Apple ID)
    // 补充常用的
    MICROSOFT,      // 微软 (Microsoft Account)
    WECHAT_WORK,    // 企业微信
    DINGTALK,       // 钉钉
    LARK,           // 飞书
    SLACK,          // Slack
    LINE,           // LINE
    KAKAOTALK,      // KakaoTalk
    BAIDU,          // 百度
    TENCENT,        // 腾讯 (通用)
    JD,             // 京东
    PDD,            // 拼多多
    MEITUAN,        // 美团
    ELEME,          // 饿了么
    XIAOMI,         // 小米
    HUAWEI,         // 华为
    OPPO,           // OPPO
    VIVO,           // VIVO
    BYTEDANCE,      // 字节跳动 (通用)
    NETEASE,        // 网易 (如网易云音乐)
    XUEQIU,         // 雪球
    ZHIHU,          // 知乎
    BILIBILI,       // 哔哩哔哩 (B站)
    TIKTOK,         // TikTok (抖音国际版)
    INSTAGRAM,      // Instagram
    YOUTUBE,        // YouTube
    SNAPCHAT,       // Snapchat
    DISCORD,        // Discord
    STEAM,          // Steam (游戏平台)
    PAYPAL,         // PayPal (支付)
    ALIPAY_HK,      // 支付宝香港
    ALIBABA,        // 阿里巴巴 (1688/国际站)
    AMAZON,         // 亚马逊
    APPLE_PAY,      // Apple Pay (支付相关)
    WECHAT_PAY,     // 微信支付 (支付相关)
    UNIPASS,        // 统一通行证 (游戏/应用通用)
    GAME_CENTER,    // Game Center (苹果游戏中心)
    PLAY_GAME,      // Google Play Games
    // 🔥 【新增】占位符：用于承载被污染的脏数据 Key
    // 当反序列化遇到 "@class" 或未知 Key 时，返回此枚举，而不是 null
    // 配合业务代码过滤掉此 Key，可避免 ConcurrentHashMap 报错
    _IGNORE_UNKNOWN;

    // -------------------------------------------------------------
    //  序列化与反序列化逻辑
    // -------------------------------------------------------------

    /**
     * 1. 作用于：作为【对象属性值】反序列化时（如 MSUser.type = "WECHAT"）
     */
    @JsonCreator
    public static TypeID fromValue(final String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return TypeID.valueOf(value.toUpperCase());
        } catch (final IllegalArgumentException e) {
            // 容错：遇到未知枚举值不报错，返回 null
            return null;
        }
    }

    /**
     * 2. 作用于：作为【对象属性值】序列化时
     */
    @JsonValue
    public String toValue() {
        return this.name();
    }

    // -------------------------------------------------------------
    //  自定义序列化器 (作用于 Map Key)
    // -------------------------------------------------------------

    /**
     * 3. 作用于：作为【Map Key】序列化时 (idMap 的 Key)
     */
    public static class Serializer extends JsonSerializer<TypeID> {
        @Override
        public void serialize(final TypeID value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
            // 如果是占位符，不进行序列化（或者序列化为特殊字符，看需求）
            if (value == _IGNORE_UNKNOWN) {
                return; // 跳过不写，或者 gen.writeFieldName("_IGNORE");
            }
            gen.writeFieldName(value.name());
        }
    }

    /**
     * 4. 作用于：作为【Map Key】反序列化时 (idMap 的 Key)
     * 🔥 核心修复逻辑在这里
     */
    public static class Deserializer extends KeyDeserializer {
        @Override
        public Object deserializeKey(final String key, final DeserializationContext ctxt) throws IOException {
            // 1. 拦截 Jackson 注入的类型标识符 "@class"
            if (key == null || "@class".equals(key) || key.startsWith("@")) {
                // 如果 idMap 是 HashMap，返回 null 是安全的（该 Entry 会变成 null=JSONObject）
                // 如果 idMap 是 ConcurrentHashMap，返回 null 会崩！建议返回 _IGNORE_UNKNOWN
                return _IGNORE_UNKNOWN;
            }

            // 2. 正常解析
            try {
                return TypeID.valueOf(key.toUpperCase());
            } catch (final IllegalArgumentException e) {
                // 3. 遇到未知 Key (可能是脏数据或其他版本枚举)，返回占位符，防止崩溃
                return _IGNORE_UNKNOWN;
            }
        }
    }
}
