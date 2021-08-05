package com.touchealth.platform.processengine.constant;

import com.touchealth.platform.processengine.pojo.dto.page.BusinessTypeDto;
import lombok.Getter;

/**
 * 页面中心常量
 *
 * @author liufengqiang
 * @date 2020-11-30 10:45:02
 */
public class PageCenterConsts {

    /**
     * 客户端类型-PC端
     */
    public static final String CLIENT_TYPE_PC = "0";
    /**
     * 客户端类型-移动端
     */
    public static final String CLIENT_TYPE_MOBILE = "1";
    /**
     * 环境类型-线上
     */
    public static final String ENV_TYPE_RELEASE = "0";
    /**
     * 环境类型-预览
     */
    public static final String ENV_TYPE_PREVIEW = "1";
    /**
     * 移动端标识
     */
    public static final String MOBILE_URI_TAG = "/mobile";
    /**
     * 查询渠道列表
     */
    public static final String GET_CHANNEL_URI = "/platform-channel";

    /**
     * 页面标记
     */
    public static final String PAGE_TAG_NEW = "新";
    public static final String PAGE_TAG_EMPTY = "空";
    public static final String PAGE_TAG_ALERT = "改";
    public static final String PAGE_TAG_RESTORE = "复";
    public static final String PAGE_TAG_BUSINESS = "业";

    /**
     * 前端路由名
     */
    public static final String ROUTER_NAME_ENTRY = "entry";
    public static final String ROUTER_NAME_CHECKUP = "checkup";
    public static final String ROUTER_NAME_MALL = "mall";
    public static final String ROUTER_NAME_LOGIN = "login";
    public static final String ROUTER_NAME_HOME = "home";
    public static final String ROUTER_NAME_PERSONAL = "personal";

    /**
     * 日志组合类型
     */
    @Getter
    public enum CombinationType {

        /**
         * 页面类型A
         */
        TYPE_PAGE_A(0),
        /**
         * 页面类型B
         */
        TYPE_PAGE_B(1),
        /**
         * 文件夹类型A
         */
        TYPE_FOLDER_A(2),
        /**
         * 文件夹类型
         */
        TYPE_FOLDER_B(3),
        /**
         * 发布类型A
         */
        TYPE_RELEASE_A(4),
        /**
         * 组件型A
         */
        TYPE_COMPONENT_A(5),
        /**
         * 组件型A
         */
        TYPE_COMPONENT_B(6);

        private Integer code;

        CombinationType(Integer code) {
            this.code = code;
        }
    }

    @Getter
    public enum LogOperate {

        /**
         * 日志操作
         */
        ADD("新增"),
        UPDATE("修改"),
        DELETE("删除"),
        RESTORE("恢复"),
        RELEASE("发布");

        private String value;

        LogOperate(String value) {
            this.value = value;
        }
    }

    @Getter
    public enum BusinessType {

        /**
         * 页面业务类型
         */
        COMMON(0, "common", "通用页面"),
        CHECKUP(1, "examination", "体检业务"),
        MALL(2, "mall", "商城业务"),
        HEALTH_MANAGE(3, "health-manage", "健管业务");

        private Integer code;
        private String name;
        private String desc;

        BusinessType(Integer code, String name, String desc) {
            this.code = code;
            this.name = name;
            this.desc = desc;
        }

        public static String getNameByCode(Integer code) {
            for (BusinessType type : BusinessType.values()) {
                if (type.code.equals(code)) {
                    return type.name;
                }
            }
            return null;
        }

        public static String getDescByCode(Integer code) {
            for (BusinessType type : BusinessType.values()) {
                if (type.code.equals(code)) {
                    return type.desc;
                }
            }
            return null;
        }

        public static String getDescByName(String name) {
            for (BusinessType type : BusinessType.values()) {
                if (type.name.equals(name)) {
                    return type.desc;
                }
            }
            return null;
        }

        public static Integer getCodeByName(String name) {
            for (BusinessType type : BusinessType.values()) {
                if (type.name.equals(name)) {
                    return type.code;
                }
            }
            return null;
        }

        public static BusinessTypeDto getDtoByCode(Integer code) {
            for (BusinessType type : BusinessType.values()) {
                if (type.code.equals(code)) {
                    return new BusinessTypeDto(type.getCode(), type.getName(), type.getDesc());
                }
            }
            return null;
        }
    }

    /**
     * 页面改动状态
     */
    @Getter
    public enum ChangerStatus {

        /**
         * 新增
         */
        ADD,
        /**
         * 改动
         */
        ALERT,
        /**
         * 未改动（发布后）
         */
        NOT_ALERT,
        /**
         * 删除
         */
        DELETE;
    }

    /**
     * 版本状态
     */
    @Getter
    public enum VersionStatus {
        /**
         * 草稿
         */
        DRAFT(0),
        /**
         * 审批锁定
         */
        LOCK(1),
        /**
         * 已发布
         */
        RELEASE(3);

        private Integer code;

        VersionStatus(Integer code) {
            this.code = code;
        }
    }
}
