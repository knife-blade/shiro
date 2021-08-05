package com.touchealth.platform.processengine.constant.datastatistics;

/**
 * @program: process-engine
 * @author: xianghy
 * @create: 2020/12/2
 **/
public enum DataStatisticsModuleTypeEnum {

    BANNER("banner", "banner"),
    NAVIGATE("navigate", "navigate"),
    COMBO_IMG("combo_img", "combo_img"),
    HOTSPOT("hotspot", "热区"),
    BUTTON("button", "button"),
    INPUT("input", "input"),
    SELECT("select", "select"),
    LOGIN("login", "login"),
    SLIDE("slide", "slide"),
    PERSONAL_INFO("personal_info", "personal_info"),
    ORDER_MANAGEMENT("order_management", "order_management"),
    MY_MOD("my_mod", "my_mod"),
    HOME_NAV("navigation", "navigation"),
    BLANK("blank", "blank")

    ;

    private String type;

    private String msg;

    DataStatisticsModuleTypeEnum(String type, String msg) {
        this.type = type;
        this.msg = msg;
    }

    public String type() {
        return type;
    }

    public String msg() {
        return msg;
    }
}
