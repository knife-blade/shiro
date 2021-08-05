package com.touchealth.platform.processengine.constant;

/**
 * Description:redis key
 *
 * @author admin
 * @date 2020/8/23
 */
public class RedisConstant {

    /**
     * 缓存时间相关key
     */
    public static final String REDIS_CACHE_30_SECOND = "redis_cache_30_second";
    public static final String REDIS_CACHE_10_MINUTE = "redis_cache_10_minute";
    public static final String REDIS_CACHE_30_MINUTE = "redis_cache_30_minute";
    public static final String REDIS_CACHE_2_HOUR = "redis_cache_2_hour";
    public static final String REDIS_CACHE_12_HOUR = "redis_cache_12_hour";
    public static final String REDIS_CACHE_24_HOUR = "redis_cache_24_hour";

    public static final Long REDIS_CACHE_EXPIRE_TIME_24_HOUR = 24 * 3600L;
    public static final Long REDIS_CACHE_EXPIRE_TIME_10_MINUTES = 10 * 60L;
    public static final Long REDIS_CACHE_EXPIRE_TIME_1_MINUTES = 60L;

    /**
     * 部门缓存
     */
    public static final String REDIS_CACHE_DEPT = "redis_cache_dept";
    /**
     * 岗位缓存
     */
    public static final String REDIS_CACHE_POST_JOB = "redis_cache_postjob";

    /**
     * 用户权限缓存
     */
    public static final String REDIS_CACHE_USER_PERMS = "redis_cache_user_perms";

    /**
     * 验证码
     */
    public static final String SMS_CODE = "sms:code:";

    /**
     * 一分钟的限制
     */
    public static final String SMS_CODE_LIMIT_MINUTE = SMS_CODE + "limit:minute:";

    /**
     * 一天的限制
     */
    public static final String SMS_CODE_LIMIT_DAY_HASH = SMS_CODE + "limit:day";

    /**
     * 一天的限制
     */
    public static final String SMS_CODE_LIMIT_IP_HASH = SMS_CODE + "limit:ip";

    /**
     * 一天限制的次数
     */
    public static final String SMS_CODE_LIMIT_DAY_TIMES = SMS_CODE + "limit:day:times";

    /**
     * 一天同一IP限制的次数
     */
    public static final String SMS_CODE_LIMIT_IP_TIMES = SMS_CODE + "limit:ip:times";

    public static String getSmsCodeKey(String mobile) {
        return SMS_CODE + mobile;
    }

    public static String getSmsLimitMinuteKey(String mobile) {
        return SMS_CODE_LIMIT_MINUTE + mobile;
    }

    /**
     * 页面中心常量
     */
    public static final class PageCenter {

        /** 页面锁定时间 */
        public static final Long PAGE_CENTER_PAGE_LOCK_TIME = 2 * 3600L;
        /** 页面预览时间 */
        public static final Long PAGE_CENTER_PAGE_PREVIEW_TIME = 30 * 60L;

        /** 页面对应的锁定用户 */
        private static final String PAGE_CENTER_PAGE_LOCK_PAGE = "page_center:page_lock:page:";
        /** 用户锁定的页面 */
        private static final String PAGE_CENTER_PAGE_LOCK_USER = "page_center:page_lock:user:";
        /** 页面预览 */
        private static final String PAGE_CENTER_PAGE_PREVIEW = "page_center:page_preview:";

        public static String getPageLockKeyByUserId(Long userId) {
            return PAGE_CENTER_PAGE_LOCK_PAGE + userId;
        }

        public static String getPageLockKeyByPageId(Long pageId) {
            return PAGE_CENTER_PAGE_LOCK_USER + pageId;
        }

        public static String getPagePreviewKey(String channelNo, Long versionId) {
            return PAGE_CENTER_PAGE_PREVIEW + channelNo + ":" + versionId;
        }
    }

    /**
     * 页面数据统计key(总)
     * 栗子：data:statistics:page:click:times
     */
    public static final String DATA_STATISTICS_PAGE_CLICK_TIMES = "data:statistics:page:click:times";

    /**
     * 页面数据统计key，每天页面访问次数
     * 栗子：data:statistics:page:click:times:{yyyyMMdd}
     * data:statistics:page:click:times:20201210
     */
    public static final String DATA_STATISTICS_PAGE_EVERY_DAY_CLICK_TIMES = "data:statistics:page:every:day:click:times:%s";

    /**
     * 页面总停留时长
     *  栗子：data:statistics:page:visit:times
     *       data:statistics:page:visit:times
     */
    public static final String DATA_STATISTICS_PAGE_VISIT_TIMES = "data:statistics:page:visit:times";

    /**
     * 页面每日浏览时长
     *  栗子：data:statistics:page:every:day:visit:times:{yyyyMMdd}
     *       data:statistics:page:every:day:visit:times:20201210
     */
    public static final String DATA_STATISTICS_PAGE_EVERY_DAY_VISIT_TIMES = "data:statistics:page:every:day:visit:times:%s";

    /**
     * 渠道总停留时长
     *  栗子：data:statistics:platform:channel:visit:times
     *  hset data:statistics:platform:channel:visit:times channelNo 100000L
     */
    public static final String DATA_STATISTICS_PLATFORM_CHANNEL_VISIT_TIMES = "data:statistics:platform:channel:visit:times";

    /**
     * 渠道每日浏览时长
     *  栗子：data:statistics:platform:channel:every:day:visit:times:{yyyyMMdd}
     *       data:statistics:page:every:day:visit:times:20201210
     */
    public static final String DATA_STATISTICS_PLATFORM_CHANNEL_EVERY_DAY_VISIT_TIMES = "data:statistics:platform:channel:every:day:visit:times:%s";

    /**
     * 页面点击人数统计key(总)
     * 栗子：data:statistics:page:person:click:times:{pageUniqueId}
     * 栗子：data:statistics:page:person:click:times:13345332
     */
    public static final String DATA_STATISTICS_PAGE_PERSON_CLICK_TIMES = "data:statistics:page:person:click:times:%s";

    /**
     * 页面点击人数数据统计key，每天页面访问次数
     * 栗子：data:statistics:page:person:every:day:click:times:{pageUniqueId}:{yyyyMMdd}
     * data:statistics:page:person:every:day:click:times:20201210:11223
     */
    public static final String DATA_STATISTICS_PAGE_PERSON_EVERY_DAY_CLICK_TIMES = "data:statistics:page:person:every:day:click:times:%s:%s";

    /**
     * 渠道总访问人数
     * 栗子：data:statistics:platform:channel:every:times:{channelNo}
     * 栗子：data:statistics:platform:channel:person:times:{NO00001}
     */
    public static final String DATA_STATISTICS_PLATFORM_CHANNEL_PERSON_TIMES = "data:statistics:platform:channel:person:times:%s";

    /**
     * 渠道每日访问人数
     * 栗子：data:statistics:platform:channel:every:day:person:times:{channelNo}:{yyyyMMdd}
     * 栗子：data:statistics:platform:channel:every:day:person:times:{NO00001}:{20201229}
     */
    public static final String DATA_STATISTICS_PLATFORM_CHANNEL_EVERY_DAY_PERSON_TIMES = "data:statistics:platform:channel:every:day:person:times:%s:%s";

    /**
     * 数据埋点-点击次数（总数据）
     * 组件类型，页面uniqueId，
     * 栗子：data:statistics:click:times:{banner}
     */
    public static final String DATA_STATISTICS_CLICK_TIMES = "data:statistics:click:times:%s";

    /**
     * 数据埋点-点击次数（每日）
     * 组件类型，页面uniqueId，
     * 栗子：data:statistics:every:day:click:times:{banner}:{yyyyMMdd}
     * 栗子：data:statistics:every:day:click:times:{banner}:20201212
     */
    public static final String DATA_STATISTICS_EVERY_DAY_CLICK_TIMES = "data:statistics:every:day:click:times:%s:%s";

    /**
     * 数据埋点-点击人数（总数据）
     * 组件类型，页面uniqueId，
     * 栗子：data:statistics:person:click:times:{moduleType}:{uniqueId}
     * 栗子：data:statistics:person:click:times:banner:1233111
     */
    public static final String DATA_STATISTICS_PERSON_CLICK_TIMES = "data:statistics:person:click:times:%s:%s";

    /**
     * 数据埋点-点击人数（每日）
     * 组件类型，页面uniqueId，
     * 栗子：data:statistics:person:click:times:{moduleType}:{yyyyMMdd}:{uniqueId}
     * 栗子：data:statistics:person:click:times:banner:20201212:112331
     */
    public static final String DATA_STATISTICS_EVERY_DAY_PERSON_CLICK_TIMES = "data:statistics:every:day:person:click:times:%s:%s:%s";

    /**
     * 页面访问用户
     * 栗子：data:statistics:page:visit:person:{pageUniqueId}
     */
    public static final String DATA_STATISTICS_PAGE_VISIT_PERSON = "data:statistics:page:visit:person:%s";

    /**
     * 每日页面新增用户数量
     * 栗子：data:statistics:page:new:visit:person:{yyyyDDmm}
     * 栗子：data:statistics:page:new:visit:person:20201224
     */
    public static final String DATA_STATISTICS_PAGE_NEW_VISIT_PERSON = "data:statistics:page:new:visit:person:%s";

    /**
     * 查询数据埋点方式 true|实时数据 false|昨日数据
     */
    public static final String DATA_STATISTICS_FIND_DATA_TYPE = "data:statistics:find:data:type";

    /**
     * 渠道访问用户
     * 栗子：data:statistics:platform:channel:visit:person:{channelNo}
     */
    public static final String DATA_STATISTICS_PLATFORM_CHANNEL_VISIT_PERSON = "data:statistics:platform:channel:visit:person:%s";

    /**
     * 渠道每日新增用户数
     * 栗子：data:statistics:platform:channel:new:visit:person:{yyyyDDmm}
     * 栗子：data:statistics:platform:channel:new:visit:person:20201224
     */
    public static final String DATA_STATISTICS_PLATFORM_CHANNEL_NEW_VISIT_PERSON = "data:statistics:platform:channel:new:visit:person:%s";

    /**
     * 登录组件通用模板
     */
    public static final String LOGIN_WEB_JSON_TEMPLATE = "login:web:json:template";

    /**
     * 用户互通渠道用户列表
     */
    public static final String USER_BIND_CHANNEL_USER_LIST = "user_bind_channel_user_list:";

    /**
     * 空模块唯一标识
     */
    public static final String BLANK_UNIQUEID = "%s-%s-%s";

    /**
     * 资源权限列表
     */
    public static final String RESOURCE_PERMS_LIST = "resource_perms";
}
