package com.touchealth.platform.processengine.constant;

public interface WebJsonConstant {

    /**
     * 前端传入的链接类型：站内链接
     */
    Integer WEB_LINK_TYPE_INSIDE = 0;
    /**
     * 前端传入的链接类型：站外链接
     */
    Integer WEB_LINK_TYPE_OUTSIDE = 1;
    /*
     * 前端传入的链接组件的站内链接类型。0:页面；1：文章；2：视频；3：体检套餐；4：医院；5：商品
     */
    Integer WEB_INSIDE_LINK_TYPE_PAGE = 0;
    Integer WEB_INSIDE_LINK_TYPE_ARTICLE = 1;
    Integer WEB_INSIDE_LINK_TYPE_VIDEO = 2;
    Integer WEB_INSIDE_LINK_TYPE_HEALTH_SET_MEAL = 3;
    Integer WEB_INSIDE_LINK_TYPE_HEALTH_HOSPITAL = 4;
    Integer WEB_INSIDE_LINK_TYPE_GOODS = 5;

}
