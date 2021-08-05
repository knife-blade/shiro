package com.touchealth.platform.processengine.pojo.dto.page;

import lombok.Data;

import java.util.List;

/**
 * @author liufengqiang
 * @date 2020-11-17 15:43:04
 */
@Data
public class PageDetailsDto {

    private Long id;
    /** 页面名称 */
    private String pageName;
    /** 路由名称 */
    private String routerName;
    /** 业务类型 */
    private String businessType;
    /** 组件列表 */
    private List<PageModuleDto> modules;

    /** 不可编辑原因 */
    private String notEditReason;
    /** 编辑人姓名 */
    private String realName;
    /** 编辑人头像 */
    private String avatar;
    /** 页面标识 相同页面不同版本使用同一个值 */
    private String pageUniqueId;
    /** 平台名称 */
    private String channelName;
    /** 异常状态 0.预览过期 1.下架 2.回收站 */
    private Integer exceptionStatus;
    /** 模板配置信息 */
    private List<PageTemplateDto> pageTemplates;
}
