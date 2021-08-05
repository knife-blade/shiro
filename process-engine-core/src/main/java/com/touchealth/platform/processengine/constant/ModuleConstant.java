package com.touchealth.platform.processengine.constant;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 组件会用到的常量
 */
public class ModuleConstant {

    /**
     * 链接类型：站内链接
     */
    public static final Integer LINK_TYPE_INSIDE = 0;
    /**
     * 链接类型：站外链接
     */
    public static final Integer LINK_TYPE_OUTSIDE = 1;
    /**
     * 图片资源类型：图片
     */
    public static final Integer PIC_TYPE = 0;
    /**
     * 图片资源类型：图片文件夹
     */
    public static final Integer PIC_FOLDER_TYPE = 1;
    /**
     * 图片资源类型：只读图片
     */
    public static final Integer PIC_READONLY_TYPE = 2;
    /**
     * 图片资源类型：只读图片文件夹
     */
    public static final Integer PIC_READONLY_FOLDER_TYPE = 3;

    /**
     * 链接组件的站内链接类型。0:页面；1：文章；2：视频；3：体检套餐；4：医院；5：商品
     * @deprecated
     */
    public enum INSIDE_LINK_TYPE {
        PAGE(0, "页面"),
        ARTICLE(1, "文章"),
        VIDEO(2, "视频"),
        HEALTH_SET_MEAL(3, "体检套餐"),
        HEALTH_HOSPITAL(4, "医院"),
        GOODS(5, "商品"),
        ;

        private Integer code;
        private String name;

        INSIDE_LINK_TYPE(Integer code, String name) {
            this.code = code;
            this.name = name;
        }

        public Integer getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        public static Map<Integer, INSIDE_LINK_TYPE> CODE_MAP;
        static {
            CODE_MAP = Arrays.stream(INSIDE_LINK_TYPE.values()).collect(Collectors.toMap(INSIDE_LINK_TYPE::getCode, o -> o));
        }
    }

}
