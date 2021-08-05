package com.touchealth.platform.processengine.pojo.dto.module.common;

import com.touchealth.platform.processengine.pojo.dto.module.BaseDto;
import lombok.Data;
import com.touchealth.platform.processengine.constant.*;

/**
 * 链接组件信息
 */
@Data
public class LinkDto extends BaseDto {

    /**
     * 链接组件所在的渠道号
     */
    private String channelNo;

    /**
     * 组件分类ID
     */
    private Long categoryId;

    /**
     * 链接组件所在的页面ID
     */
    private Long pageId;

    /**
     * 链接名
     */
    private String name;

    /**
     * 链接类型。<br>
     * <ur>
     *     <li>0：站内链接{@link ModuleConstant#LINK_TYPE_INSIDE}</li>
     *     <li>1：站外链接{@link ModuleConstant#LINK_TYPE_OUTSIDE}</li>
     * </ur>
     */
    private Integer type;

    /**
     * 链接跳转地址，仅当type为1时有用。
     */
    private String linkUrl;

    /**
     * 链接跳转类型，
     * @see PageCenterConsts.BusinessType
     */
    private Integer linkType;

    /**
     * 链接跳转的组件或页面版本唯一ID，仅当type为0时有用。
     */
    private String linkToId;

    /**
     * 链接跳转的页面路由。
     */
    private String linkPath;

    /**
     * 链接跳转后需要传递的JSON参数字符串
     */
    private String params;

    /**
     * 状态。0：草稿；1：保存（下架）；2：垃圾；3：已发布（上架）；4：当前发布
     * @see CommonConstant.STATUS
     */
    private Integer status;

    /**
     * 前端页面配置信息，JSON格式。该值来源于前端，原样返回给前端。每次更新时的对比记录操作日志也使用该字段。
     */
    private String webJson;

    /**
     * 版本号ID
     */
    private Long version;


}
