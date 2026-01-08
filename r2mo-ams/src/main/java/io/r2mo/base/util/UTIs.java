package io.r2mo.base.util;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author lang : 2025-12-06
 */
@Slf4j
class UTIs {
    // --- 电子邮件验证 ---

    /**
     * 语言级空值黑名单 (全部转为小写匹配)
     */
    private static final Set<String> NULL_KEYWORDS = Set.of(
        // --- JavaScript / JSON / ApiFox / Postman ---
        "undefined",        // 变量未定义
        "null",             // 空对象
        "nan",              // Not a Number
        "[object object]",  // 对象强转字符串错误

        // --- Python ---
        "none",             // Python 的 None

        // --- Go / Lua / Ruby / Objective-C ---
        "nil",              // 指针/对象为空

        // --- C / C++ / SQL ---
        "nullptr",          // 空指针

        // --- Swagger / OpenAPI 默认值 ---
        "string",           // Swagger UI 如果不填参数，默认发 "string"

        // --- 其他常见无效占位 ---
        "void",             // Java/C void
        "unknown",          // 未知
        "default"           // 默认
    );

    /**
     * 使用正则表达式简单验证电子邮件地址格式 (适用于中国大陆等常见格式)。
     * 注意：此正则表达式是简化的，不能保证捕获所有RFC标准下的有效或无效邮箱。
     *
     * @param email 待验证的字符串
     * @return 如果字符串符合基本电子邮件格式，则返回 true；否则返回 false。
     */
    static boolean isEmail(final String email) {
        if (email == null || email.isEmpty()) {
            return false; // 空或null不是有效的邮箱
        }

        // 一个相对宽松但常用的邮箱正则表达式
        // ^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$
        // 解释:
        // ^ : 字符串开始
        // [a-zA-Z0-9._%+-]+ : 用户名部分，包含字母、数字、点、下划线、百分号、加号、减号，至少一个
        // @ : 必须包含@符号
        // [a-zA-Z0-9.-]+ : 域名部分，包含字母、数字、点、减号，至少一个
        // \. : 必须包含点号
        // [a-zA-Z]{2,} : 顶级域名，至少两个字母
        // $ : 字符串结束
        final String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

        final Pattern pattern = Pattern.compile(emailRegex);
        final Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * 判定字符串是否为“垃圾值”或“语言级空值”
     *
     * @param value 待检查的 Token 字符串
     * @return true = 无效 (是垃圾值); false = 有效 (可能是正常 Token)
     */
    static boolean isInvalid(final String value) {
        // 1. 基础判空 (Java 层面)
        if (StrUtil.isEmpty(value)) {
            return true;
        }

        // 2. 预处理：去空格 + 转小写
        final String raw = value.trim();
        final String normalized = raw.toLowerCase();

        // 3. 拦截：语言级关键字 (O(1) 查找)
        if (NULL_KEYWORDS.contains(normalized)) {
            return true;
        }

        // 4. 拦截：模板变量未解析 (针对 ApiFox/Postman/Mustache)
        // 例如: "{{TOKEN}}", "{{undefined}}", "${token}"
        if (raw.startsWith("{{") || raw.startsWith("${")) {
            return true;
        }

        // 5. 拦截：HTML/XML 标签或泛型占位 (针对 Swagger/文档)
        // 例如: "<string>", "<token>", "&lt;string&gt;"
        if (raw.startsWith("<") || raw.startsWith("&lt;")) {
            return true;
        }

        // 6. 拦截：常见的替换占位符
        // 例如: "your_token_here", "token_value"
        if (normalized.contains("your_") || normalized.contains("_here")) {
            return true;
        }

        log.debug("[ R2MO ] Token 垃圾值检测未命中，视为有效值: {}", raw);
        return false;
    }

    // --- 手机号码验证 ---

    /**
     * 使用正则表达式验证中国大陆手机号码格式。
     * 注意：这基于常见的中国手机号段规则，可能需要根据最新的运营商号段进行调整。
     *
     * @param mobile 待验证的字符串
     * @return 如果字符串符合中国大陆手机号码格式，则返回 true；否则返回 false。
     */
    static boolean isMobile(final String mobile) {
        if (mobile == null || mobile.isEmpty()) {
            return false; // 空或null不是有效的手机号
        }

        // 中国大陆手机号码正则表达式 (简化版，匹配11位数字，以1开头，第二位为3-9)
        // ^1([3-9])\d{9}$
        // 解释:
        // ^ : 字符串开始
        // 1 : 必须以1开头
        // ([3-9]) : 第二位必须是3到9之间的数字 (代表不同的运营商号段前缀)
        // \d{9} : 后面跟着9个数字 (\d 是数字的简写, {9} 表示重复9次)
        // $ : 字符串结束
        final String mobileRegex = "^1[3-9]\\d{9}$";

        final Pattern pattern = Pattern.compile(mobileRegex);
        final Matcher matcher = pattern.matcher(mobile);
        return matcher.matches();
    }
}
