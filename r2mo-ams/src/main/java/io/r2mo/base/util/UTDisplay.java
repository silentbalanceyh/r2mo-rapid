package io.r2mo.base.util;

import io.r2mo.base.web.ForLocale;
import io.r2mo.spi.SPI;

import java.util.concurrent.TimeUnit;

/**
 * @author lang : 2025-12-08
 */
class UTDisplay {

    // 资源文件名
    private static final String I18N_FILE = "MessageCommon";

    // 时间常量定义 (秒)
    private static final long SEC_MINUTE = 60;
    private static final long SEC_HOUR = 60 * SEC_MINUTE;
    private static final long SEC_DAY = 24 * SEC_HOUR;
    private static final long SEC_MONTH = 30 * SEC_DAY; // 近似 30 天
    private static final long SEC_YEAR = 365 * SEC_DAY; // 近似 365 天

    /**
     * 获取国际化组件
     * 注意：为了被 static uiDate 调用，此处改为 static，或者直接在 uiDate 中调用 SPI
     */
    private static ForLocale locale() {
        return SPI.SPI_WEB.ofLocale();
    }

    /**
     * 将时间转换为人性化显示 (年 月 天 时 分 秒)
     */
    static String uiDate(final long integer, final TimeUnit unit) {
        if (integer < 0) {
            throw new IllegalArgumentException("[ R2MO ] Duration 值不可以为负数！");
        }

        // 1. 转换为总秒数
        final long totalSeconds = unit.toSeconds(integer);

        // 获取本地化接口实例
        final ForLocale fl = locale();

        // 0秒特殊处理
        if (totalSeconds == 0) {
            // 调用三参方法，args 为空即可
            return "0" + fl.formatI18n(I18N_FILE, "time.unit.second");
        }

        // 2. 计算各个单位
        final long years = totalSeconds / SEC_YEAR;
        long rem = totalSeconds % SEC_YEAR;

        final long months = rem / SEC_MONTH;
        rem = rem % SEC_MONTH;

        final long days = rem / SEC_DAY;
        rem = rem % SEC_DAY;

        final long hours = rem / SEC_HOUR;
        final long minutes = (rem % SEC_HOUR) / SEC_MINUTE;
        final long seconds = rem % SEC_MINUTE;

        // 3. 拼接字符串
        final StringBuilder sb = new StringBuilder();

        if (years > 0) {
            sb.append(years).append(fl.formatI18n(I18N_FILE, "time.unit.year"));
        }
        if (months > 0) {
            sb.append(months).append(fl.formatI18n(I18N_FILE, "time.unit.month"));
        }
        if (days > 0) {
            sb.append(days).append(fl.formatI18n(I18N_FILE, "time.unit.day"));
        }
        if (hours > 0) {
            sb.append(hours).append(fl.formatI18n(I18N_FILE, "time.unit.hour"));
        }
        if (minutes > 0) {
            sb.append(minutes).append(fl.formatI18n(I18N_FILE, "time.unit.minute"));
        }
        if (seconds > 0) {
            sb.append(seconds).append(fl.formatI18n(I18N_FILE, "time.unit.second"));
        }

        return sb.toString().trim();
    }
}