package com.touchealth.platform.processengine.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 比对常量
 */
public interface CompareConstant {

    String OP_ADD = "新增";
    String OP_UPD = "修改";
    String OP_DEL = "删除";

    /*
     * 按钮组件操作常量
     */

    String BUTTON_STYLE = "固定位置样式";
    String BUTTON_ACTION = "固定位置交互";
    String BUTTON_NAME = "固定位置名称";
    String BUTTON_COLOR = "固定位置颜色";
    String BUTTON_BG_COLOR = "固定位置背景颜色";
    String BUTTON_BG_IMG = "固定位置背景图片";

    /**
     * 轮播图组件操作常量
     */
    String BANNER_STYLE = "banner尺寸";
    String BANNER_NAME = "banner名称";
    String BANNER_DATE = "投放时段";
    String BANNER_IMG = "banner图片";

    /**
     * 登录组件操作常量
     */
    String TITLE_CHINESE = "中文标题";
    String TITLE_ENG = "英文标题";
    String SUBTITLE_CHINESE = "中文副标题";
    String SUBTITLE_ENG = "英文副标题";
    String AGREEMENT_URL_ENG = "英文用户协议";
    String AGREEMENT_URL_CHINESE = "中文用户协议";
    String PRIVACY_AGREEMENT_URL_CHINESE = "中文隐私协议";
    String PRIVACY_AGREEMENT_URL_ENG = "英文隐私协议";

    /**
     * 多图图组件操作常量
     */
    String COMBO_STYLE = "多图样式";
    String COMBO_NAME = "多图名称";
    String COMBO_IMG = "banner图片";

    /**
     * 热区组件操作常量
     */
    String HOTSPOT_NAME = "热区标题名称";
    String HOTSPOT_PARTS = "热区";
    String HOTSPOT_IMG = "热区图片";


    /**
     * 坑位组件操作常量
     */
    String NAVIGATE_STYLE = "坑位样式";
    String NAVIGATE_NAME = "坑位名称";
    String NAVIGATE_IMG = "坑位图片";

    Map<Integer, String> LAYOUT_TYPE_MAP = new HashMap<Integer, String>() {{
        put(1, "一排一");
        put(2, "一排二");
        put(3, "圆型");
    }};

    /**
     * 组合图样式
     */
    Map<Integer, String> COMBO_LAYOUT_TYPE_MAP = new HashMap<Integer, String>() {{
        put(1, "样式一");
        put(2, "样式二");
        put(3, "样式三");
        put(4, "样式四");
    }};

    /**
     * 坑位导肮样式
     */
    Map<Integer, String> NAVIGATE_LAYOUT_TYPE_MAP = new HashMap<Integer, String>() {{
        put(1, "一排四个");
        put(2, "一排五个");
    }};


    /*
     * 间隔组件操纵常量
     */

    String DELIMITER_HEIGHT = "间隔高度";

    /*
     * 链接组件操作常量
     */

    String LINK_PATH = "链接";
    String LINK_PARAM = "链接参数";

    /*
     * 图片组件操作常量
     */

    String PIC_PATH = "图片";


}
