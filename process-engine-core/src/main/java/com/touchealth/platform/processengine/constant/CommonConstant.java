package com.touchealth.platform.processengine.constant;

import lombok.Getter;

public class CommonConstant {

    public static final String FORM_CONTENT_TYPE = "multipart/form-data";
    public static final String PRO_ENV = "prod";
    public static final String TEST_ENV = "test";
    public static final String PRE_ENV = "pre";

    public static final String APP_NAME = "process-engine";

    public static final String KEY_SPLIT = ",,,";

    public static final int IS_NOT_DELETE = 0;
    public static final int IS_DELETE = 1;

    public static final String HEADER_CHANNEL = "channelNo";

    public static final Long SC_CHANNEL_ID = 1L;

    /**
     * 势成超管账号id
     */
    public static final Long SC_ADMIN_ID = 1L;

    /**
     * 管理后台验证码短信模板ID
     */
    public static final Long VERIFY_CODE_SMS_MODULE_ID = 173L;

    /**
     * 页面或组件状态。<br>
     * 0：草稿；1：保存（下架）；2：垃圾；3：已发布（上架）；4：当前发布
     */
    public enum STATUS {
        DRAFT(0, "草稿"),
        SAVE(1, "保存"),
        TRASH(2, "垃圾箱"),
        PUBLISHED(3, "已发布"),
        RELEASE(4, "当前发布"),
        LOCK(5, "锁定状态");

        private Integer code;
        private String name;

        STATUS(Integer code, String name) {
            this.code = code;
            this.name = name;
        }

        public Integer getCode() {
            return code;
        }

        public String getName() {
            return name;
        }
    }

    /**
     * 组件分类。<br>
     * TODO 20201116 应该是一个表来维护的，这里暂时不做，用枚举类实现。
     */
    public enum MODULE_CATEGORY {
        COMMON(0L, "通用组件"),
        BUSINESS(1L, "业务组件"),
        ;

        private Long code;
        private String name;

        MODULE_CATEGORY(Long code, String name) {
            this.code = code;
            this.name = name;
        }

        public Long getCode() {
            return code;
        }

        public String getName() {
            return name;
        }
    }

    @Getter
    public enum ModuleType {

        /**
         * 组件类型
         */
        CAROUSEL(0, "轮播图"),
        NAVIGATION(1, "坑位导航"),
        HOTSPOT(2, "热区"),
        LIST_PHOTOS(3, "列表多图"),
        INTERVAL(4, "间隔"),
        BUTTON(5, "固定位置"),
        LINK(6, "链接"),
        LOGIN(7, "登录"),
        HOME_NAV(8, "首页导航"),
        PERSONAL_INFO(9, "个人信息"),
        ORDER_MANAGEMENT(10, "订单管理"),
        MY_MOD(11, "我的模块"),
        EMPTY_BUSINESS(12, "空业务组件");

        private Integer code;
        private String name;

        ModuleType(Integer code, String name) {
            this.code = code;
            this.name = name;
        }

        public static ModuleType getByCode(Integer code) {
            for (ModuleType type : ModuleType.values()) {
                if (type.getCode().equals(code)) {
                    return type;
                }
            }
            return null;
        }

        public static String getNameByCode(Integer code) {
            for (ModuleType type : ModuleType.values()) {
                if (type.getCode().equals(code)) {
                    return type.name;
                }
            }
            return null;
        }
    }
}
