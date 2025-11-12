package io.r2mo.base.util;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author lang : 2025-10-19
 */
class _UtilDate extends _UtilArray {
    protected _UtilDate() {
    }

    // =============== 时间工具集

    /**
     * 检查两个日期是否相等
     *
     * @param left  左边日期
     * @param right 右边日期
     *
     * @return 是否相等
     */
    public static boolean isSame(final Date left, final Date right) {
        return UTDate.isSame(left, right);
    }

    /**
     * 检查对象是否是一个合法时间格式，或可转换成时间的格式
     *
     * @param value 对象
     *
     * @return 是否是一个合法时间格式
     */
    public static boolean isDate(final Object value) {
        return UTDate.isDate(value);
    }

    /**
     * 检查字符串是否是否位于某个区间内，从开始时间到结束时间
     *
     * @param current 当前时间
     * @param start   开始时间
     * @param end     结束时间
     *
     * @return 是否位于某个区间内
     */
    public static boolean isDuration(final LocalDateTime current, final LocalDateTime start, final LocalDateTime end) {
        return UTDate.isDuration(current, start, end);
    }

    /**
     * 针对 LocalDate 类型的日期执行格式化
     *
     * @param date    LocalDate
     * @param pattern 时间格式模式
     *
     * @return 格式化后的字符串
     */
    public static String fromDate(final LocalDate date, final String pattern) {
        return UTDate.fromPattern(date, pattern);
    }

    /**
     * 针对 LocalDateTime 类型的日期执行格式化
     *
     * @param datetime LocalDateTime
     * @param pattern  时间格式模式
     *
     * @return 格式化后的字符串
     */
    public static String fromDate(final LocalDateTime datetime, final String pattern) {
        return UTDate.fromPattern(datetime, pattern);
    }

    /**
     * 针对 LocalTime 类型的日期执行格式化
     *
     * @param time    LocalTime
     * @param pattern 时间格式模式
     *
     * @return 格式化后的字符串
     */
    public static String fromDate(final LocalTime time, final String pattern) {
        return UTDate.fromPattern(time, pattern);
    }

    /**
     * 针对 Date 类型的日期执行格式化
     *
     * @param date    Date
     * @param pattern 时间格式模式
     *
     * @return 格式化后的字符串
     */
    public static String fromDate(final Date date, final String pattern) {
        return UTDate.fromPattern(UTDate.toDateTime(date), pattern);
    }

    /**
     * 针对 Instant 类型的日期执行格式化
     *
     * @param instant Instant
     * @param pattern 时间格式模式
     *
     * @return 格式化后的字符串
     */
    public static String fromDate(final Instant instant, final String pattern) {
        return UTDate.fromPattern(UTDate.toDateTime(instant), pattern);
    }


    /**
     * 合法时间格式中字符串转成月
     *
     * @param literal 时间字符串
     *
     * @return 月份
     */
    public static int toMonth(final String literal) {
        return UTDate.toMonth(literal);
    }

    /**
     * 提取时间中的月份
     *
     * @param date 时间
     *
     * @return 月份
     */
    public static int toMonth(final Date date) {
        return UTDate.toMonth(date);
    }

    /**
     * 合法时间格式中字符串转成年
     *
     * @param literal 时间字符串
     *
     * @return 年份
     */
    public static int toYear(final String literal) {
        return UTDate.toYear(literal);
    }

    /**
     * 提取时间中的年份
     *
     * @param date 时间
     *
     * @return 年份
     */
    public static int toYear(final Date date) {
        return UTDate.toYear(date);
    }

    /**
     * 合法时间格式中字符串转成LocalDateTime
     *
     * @param literal 时间字符串
     *
     * @return LocalDateTime
     */

    public static LocalDateTime toDateTime(final String literal) {
        return UTDate.toDateTime(literal);
    }

    /**
     * Date转换成LocalDateTime
     *
     * @param date Date
     *
     * @return LocalDateTime
     */
    public static LocalDateTime toDateTime(final Date date) {
        return UTDate.toDateTime(date);
    }

    /**
     * Instant转换成LocalDateTime
     *
     * @param date Instant
     *
     * @return LocalDateTime
     */
    public static LocalDateTime toDateTime(final Instant date) {
        return UTDate.toDateTime(date);
    }

    /**
     * 合法时间格式中字符串转成LocalDate
     *
     * @param literal 时间字符串
     *
     * @return LocalDate
     */
    public static LocalDate toDate(final String literal) {
        return UTDate.toDate(literal);
    }

    /**
     * 合法时间格式中字符串转成LocalTime
     *
     * @param literal 时间字符串
     *
     * @return LocalTime
     */
    public static LocalTime toTime(final String literal) {
        return UTDate.toTime(literal);
    }

    /**
     * 毫秒值转换成 LocalDateTime
     *
     * @param millSeconds 毫秒值
     *
     * @return LocalDateTime
     */
    public static LocalDateTime toDuration(final long millSeconds) {
        return UTDate.toDuration(millSeconds);
    }

    /**
     * Date类型的时间转换成 LocalDate，Java 8+
     *
     * @param date Date
     *
     * @return LocalDate
     */
    public static LocalDate toDate(final Date date) {
        return UTDate.toDate(date);
    }

    /**
     * Date类型的时间转换成 LocalTime，Java 8+
     *
     * @param date Date
     *
     * @return LocalTime
     */
    public static LocalTime toTime(final Date date) {
        return UTDate.toTime(date);
    }

    /**
     * Instant类型的时间转换成 LocalDate，Java 8+
     *
     * @param date Instant
     *
     * @return LocalDate
     */
    public static LocalDate toDate(final Instant date) {
        return UTDate.toDate(date);
    }

    /**
     * Instant类型的时间转换成 LocalTime，Java 8+
     *
     * @param date Instant
     *
     * @return LocalTime
     */
    public static LocalTime toTime(final Instant date) {
        return UTDate.toTime(date);
    }

    /**
     * 将字符串literal转换成 Duration类型
     *
     * @param literal 字符串
     *
     * @return Duration
     */
    public static Duration toDuration(final String literal) {
        return UTDuration.parseToDuration(literal);
    }

    /**
     * 将字符串literal转换成 Date类型（和JDK老版本对接）
     *
     * @param literal 字符串
     *
     * @return Date
     */
    public static Date parse(final String literal) {
        return UTDate.parse(literal);
    }

    /**
     * 将LocalTime转换成 Date类型（和JDK老版本对接）
     *
     * @param time LocalTime
     *
     * @return Date
     */
    public static Date parse(final LocalTime time) {
        return UTDate.parse(time);
    }

    /**
     * 将LocalDateTime转换成 Date类型（和JDK老版本对接）
     *
     * @param datetime LocalDateTime
     *
     * @return Date
     */
    public static Date parse(final LocalDateTime datetime) {
        return UTDate.parse(datetime);
    }

    /**
     * 将LocalDate转换成 Date类型（和JDK老版本对接）
     *
     * @param date LocalDate
     *
     * @return Date
     */
    public static Date parse(final LocalDate date) {
        return UTDate.parse(date);
    }

    /**
     * 将字符串literal转换成 Date类型（和JDK老版本对接）
     *
     * @param literal 字符串
     *
     * @return Date
     */
    public static Date parseFull(final String literal) {
        return UTDate.parseFull(literal);
    }

    /**
     * （计算过时区）获取当前时间，转换成 Date 值
     *
     * @return Date
     */
    public static Date valueNow() {
        return UTDate.parse(LocalDateTime.now());
    }

    /**
     * 按天遍历日期区间
     *
     * @param from  开始日期
     * @param to    结束日期
     * @param dayFn 每一天的执行函数
     */
    public static void itDay(final String from, final String to, final Consumer<Date> dayFn) {
        final LocalDateTime begin = UTDate.toDateTime(Objects.requireNonNull(UTDate.parseFull(from)));
        final LocalDateTime end = UTDate.toDateTime(Objects.requireNonNull(UTDate.parseFull(to)));
        UTDate.itDay(begin.toLocalDate(), end.toLocalDate(), dayFn);
    }

    /**
     * 按天遍历日期区间
     *
     * @param from  开始日期
     * @param to    结束日期
     * @param dayFn 每一天的执行函数
     */
    public static void itDay(final LocalDateTime from, final LocalDateTime to, final Consumer<Date> dayFn) {
        UTDate.itDay(from.toLocalDate(), to.toLocalDate(), dayFn);
    }


    /**
     * 按天遍历日期区间
     *
     * @param from  开始日期
     * @param to    结束日期
     * @param dayFn 每一天的执行函数
     */
    public static void itDay(final LocalDate from, final LocalDate to, final Consumer<Date> dayFn) {
        UTDate.itDay(from, to, dayFn);
    }

    /**
     * 按周遍历日期区间
     *
     * @param from  开始日期
     * @param to    结束日期
     * @param dayFn 每一天的执行函数
     */
    public static void itWeek(final LocalDateTime from, final LocalDateTime to, final Consumer<Date> dayFn) {
        UTDate.itWeek(from.toLocalDate(), to.toLocalDate(), dayFn);
    }

    /**
     * 按周遍历日期区间
     *
     * @param from  开始日期
     * @param to    结束日期
     * @param dayFn 每一天的执行函数
     */
    public static void itWeek(final LocalDate from, final LocalDate to, final Consumer<Date> dayFn) {
        UTDate.itWeek(from, to, dayFn);
    }

    /**
     * 按周遍历日期区间
     *
     * @param from  开始日期
     * @param to    结束日期
     * @param dayFn 每一天的执行函数
     */
    public static void itWeek(final String from, final String to, final Consumer<Date> dayFn) {
        final LocalDate begin = UTDate.toDate(UTDate.parseFull(from));
        final LocalDate end = UTDate.toDate(UTDate.parseFull(to));
        UTDate.itWeek(begin, end, dayFn);
    }
}
