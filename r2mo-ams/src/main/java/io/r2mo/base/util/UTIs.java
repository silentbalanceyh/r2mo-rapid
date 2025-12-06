package io.r2mo.base.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author lang : 2025-12-06
 */
class UTIs {
    // --- 电子邮件验证 ---

    /**
     * 使用正则表达式简单验证电子邮件地址格式 (适用于中国大陆等常见格式)。
     * 注意：此正则表达式是简化的，不能保证捕获所有RFC标准下的有效或无效邮箱。
     *
     * @param email 待验证的字符串
     *
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

    // --- 手机号码验证 ---

    /**
     * 使用正则表达式验证中国大陆手机号码格式。
     * 注意：这基于常见的中国手机号段规则，可能需要根据最新的运营商号段进行调整。
     *
     * @param mobile 待验证的字符串
     *
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
