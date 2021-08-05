package com.touchealth.platform.processengine.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 日期类型转换类
 */
public class DateUtil {
    //数据库格式的日期
    public static final String SQL_MONTH = "yyyy-MM";
    public static final String SQL_DATE = "yyyy-MM-dd";
    public static final String SQL_TIME = "yyyy-MM-dd HH:mm:ss";
    public static final String SQL_TIMESTAMP = "yyyy-MM-dd HH:mm:ss.SS";

    //斜杠格式的日期
    public static final String DATE = "yyyy/MM/dd";
    public static final String TIMESTAMP = "yyyy/MM/dd HH:mm:ss.SS";
    public static final String TIMESTAMP_SHORT = "yyyy/MM/dd HH:mm";
    public static final String TIME = "HH:mm:ss";
    public static final String TIME_SHORT = "HH:mm";

    //不常用日期格式
    public static final String CHINESEDATE = "yyyy年MM月dd日";
    public static final String DATE_TIME = "yyyyMMddHHmmss";
    public static final String DATE_TIME_DETAIL = "yyyyMMddHHmmssSS";
    public static final String DATE_DAY = "yyyyMMdd";
    public static final String DATE_HOUR = "yyyyMMddHH";
    public static final String DATE_MONTH = "yyyyMM";
    public static final String DATE_MONTH_DAY = "MM/dd";


    /**
     * 防止被实例化
     */
    private DateUtil() {

    }

    /**
     * Date转LocalDateTime
     * 使用系统时区
     *
     * @param date
     * @return
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        Instant instant = date.toInstant();
        return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * LocalDateTime转Date
     * 使用系统时区
     *
     * @param localDateTime
     * @return
     */
    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        ZonedDateTime zdt = localDateTime.atZone(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }

    /**
     * 日期转字符串
     *
     * @param localDateTime
     * @param pattern
     * @return
     */
    public static String dateTimeToStr(LocalDateTime localDateTime, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return localDateTime.format(formatter);
    }

    /**
     * 日期转字符串
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String dateTimeToStr(Date date, String pattern) {
        if (null == date) {
            return null;
        }
        return dateTimeToStr(dateToLocalDateTime(date), pattern);
    }

    /**
     * 将字符串日期解析为java.time.LocalDateTime
     *
     * @param dateTimeStr
     * @param pattern
     * @return
     */
    public static LocalDateTime strToLocalDateTime(String dateTimeStr, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.parse(dateTimeStr, formatter);
    }

    /**
     * 将字符串日期解析为java.time.strToLocalDate
     *
     * @param dateStr
     * @param pattern
     * @return
     */
    public static LocalDate strToLocalDate(String dateStr, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDate.parse(dateStr, formatter);
    }

    /**
     * 开始日期，补齐" 00:00:00"
     *
     * @param localDateTime
     * @return
     */
    public static LocalDateTime getStartDateTimeWithHMS(LocalDateTime localDateTime) {
        return LocalDateTime.of(localDateTime.toLocalDate(), LocalTime.MIN);
    }

    /**
     * 结束日期，补齐" 23:59:59"
     *
     * @param localDateTime
     * @return
     */
    public static LocalDateTime getEndDateWithHMS(LocalDateTime localDateTime) {
        return LocalDateTime.of(localDateTime.toLocalDate(), LocalTime.MAX);
    }


    public static LocalDateTime getAfterYears(LocalDateTime localDateTime, int count) {
        return localDateTime.plusYears(count);
    }

    public static LocalDateTime getAfterMonths(LocalDateTime localDateTime, int count) {
        return localDateTime.plusMonths(count);
    }

    public static LocalDateTime getAfterDays(LocalDateTime localDateTime, int count) {
        return localDateTime.plusDays(count);
    }

    public static LocalDateTime getAfterMinutes(LocalDateTime localDateTime, int count) {
        return localDateTime.plusMinutes(count);
    }

    public static LocalDateTime getAfterSeconds(LocalDateTime localDateTime, int count) {
        return localDateTime.plusSeconds(count);
    }


    /**
     * 获得当前年的第一天
     *
     * @param
     * @return
     */
    public static LocalDateTime getYearFirstDay(LocalDateTime localDateTime) {
        return localDateTime.with(TemporalAdjusters.firstDayOfYear());
    }

    /**
     * 获得当前年的最后一天
     *
     * @param
     * @return
     */
    public static LocalDateTime getYearLastDay(LocalDateTime localDateTime) {
        return localDateTime.with(TemporalAdjusters.lastDayOfYear());
    }


    /**
     * 获得当前月的第一天
     *
     * @param
     * @return
     */
    public static LocalDateTime getMonthFirstDay(LocalDateTime localDateTime) {
        return localDateTime.with(TemporalAdjusters.firstDayOfMonth());
    }

    /**
     * 获得当前月的最后一天
     *
     * @param
     * @return
     */
    public static LocalDateTime getMonthLastDay(LocalDateTime localDateTime) {
        return localDateTime.with(TemporalAdjusters.lastDayOfMonth());
    }


    /**
     * 获得当前星期的第一天
     *
     * @param localDateTime
     * @param locale        默认Locale.CHINA 周日为一周的第一天
     * @return
     */
    public static LocalDateTime getWeekFirstDay(LocalDateTime localDateTime, Locale locale) {
        return localDateTime.with(WeekFields.of(locale == null ? Locale.CHINA : locale).dayOfWeek(), 1);
    }

    /**
     * 获得当前星期的最后一天
     *
     * @param localDateTime
     * @param locale        默认默认Locale.CHINA 周日为一周的第一天
     * @return
     */
    public static LocalDateTime getWeekLastDay(LocalDateTime localDateTime, Locale locale) {
        return localDateTime.with(WeekFields.of(locale == null ? Locale.CHINA : locale).dayOfWeek(), 7);
    }


    /**
     * 计算两个日期之间相差年数
     *
     * @param smallDateTime 较小的时间
     * @param bigDateTime   较大的时间
     * @return 相差年数
     */
    public static int getYearDiff(LocalDateTime smallDateTime, LocalDateTime bigDateTime) {
        return (int) smallDateTime.until(bigDateTime, ChronoUnit.YEARS);
    }

    /**
     * 计算两个日期之间相差月数
     *
     * @param smallDateTime 较小的时间
     * @param bigDateTime   较大的时间
     * @return 相差月数
     */
    public static int getMonthDiff(LocalDateTime smallDateTime, LocalDateTime bigDateTime) {
        return (int) smallDateTime.until(bigDateTime, ChronoUnit.MONTHS);
    }

    /**
     * 计算两个日期之间相差的天数
     *
     * @param smallDateTime 较小的时间
     * @param bigDateTime   较大的时间
     * @return 相差天数
     */
    public static int getDayDiff(LocalDateTime smallDateTime, LocalDateTime bigDateTime) {
        return (int) smallDateTime.until(bigDateTime, ChronoUnit.DAYS);
    }

    /**
     * 计算两个日期之间相差小时数
     *
     * @param smallDateTime 较小的时间
     * @param bigDateTime   较大的时间
     * @return 相差小时数
     */
    public static int getHourDiff(LocalDateTime smallDateTime, LocalDateTime bigDateTime) {
        return (int) smallDateTime.until(bigDateTime, ChronoUnit.HOURS);
    }

    /**
     * 计算两个日期之间相差分钟数
     *
     * @param smallDateTime
     * @param bigDateTime
     * @return 相差分钟数
     */
    public static int getMinutesDiff(LocalDateTime smallDateTime, LocalDateTime bigDateTime) {
        return (int) smallDateTime.until(bigDateTime, ChronoUnit.MINUTES);
    }

    /**
     * 计算两个日期之间相差秒数
     *
     * @param smallDateTime
     * @param bigDateTime
     * @return 相差秒数
     */
    public static int getSecondsDiff(LocalDateTime smallDateTime, LocalDateTime bigDateTime) {
        return (int) smallDateTime.until(bigDateTime, ChronoUnit.SECONDS);
    }

    /**
     * 获取服务器时间的毫秒数
     *
     * @param
     * @return 返回服务器时间的毫秒数
     * @author zhangpingan
     * @author zhangpingan mrd Beidou2.0.0
     */
    public static long getTimeInMillionSeconds() {
        Calendar c = Calendar.getInstance();
        return c.getTimeInMillis();
    }

    /**
     * 获取年龄
     *
     * @param birthDay
     * @return
     */
    public static Integer getAge(LocalDateTime birthDay) {
        LocalDateTime now = LocalDateTime.now();
        if (birthDay != null) {
            return now.getYear() - birthDay.getYear();
        } else {
            return 0;
        }

    }

    /**
     * date转localDateTime
     *
     * @param date
     * @return
     */
    public static LocalDateTime getLocalDateTime(Date date) {
        if (null == date) {
            return null;
        }
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        return instant.atZone(zoneId).toLocalDateTime();
    }

    /**
     * 日期加减
     *
     * @param date   需要加减的日期
     * @param amount 对应的数值 + 表示加几天 - 表示减几天
     * @return 对应的时间
     */
    public static Date add(Date date, int amount) {
        if (null == date) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, amount);
        return calendar.getTime();
    }

    /**
     * 毫秒数转为分秒，10:30
     * @param currentTime 对应的毫秒
     * @return
     */
    public static String formatLongToTimeStr(long currentTime) {
        if (0L >= currentTime) {
            return "00:00";
        }

        int second = (int) (currentTime / 1000);
        if (second > 60) {
            int minute = second / 60;
            second = second % 60;
            return (minute >= 10 ? minute : "0" + minute) + ":" + (second >= 10 ? second : "0" + second);
        }

        if (second == 60) {
            return "01:00";
        }

        return "00:" + (second >= 10 ? second : "0" + second);
    }

    public static void main(String[] args) {

        System.out.println(formatLongToTimeStr(10000L));
        System.out.println(formatLongToTimeStr(61000L));
        System.out.println(formatLongToTimeStr(60000L * 2));
        System.out.println(formatLongToTimeStr(60000L * 10));
    }

}
