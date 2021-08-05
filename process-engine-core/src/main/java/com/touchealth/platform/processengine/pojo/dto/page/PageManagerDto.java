package com.touchealth.platform.processengine.pojo.dto.page;

import com.touchealth.platform.processengine.constant.PageCenterConsts;
import lombok.Data;

import java.util.List;

/**
 * @author liufengqiang
 * @date 2020-11-16 19:24:11
 */
@Data
public class PageManagerDto {

    private Long id;
    /** 页面唯一id */
    private String pageUniqueId;
    /** 是否是文件夹 */
    private Boolean isFolder;
    /** 页面名称 */
    private String pageName;
    /** 页面标识 */
    private List<String> pageTags;
    /** 子页面*/
    private List<PageManagerDto> children;
    /** 路由名称 */
    private String routerName;
    /** 业务类型
     * @see PageCenterConsts.BusinessType */
    private String businessType;
    /** 改动状态 0.新增 1.改动 2.未改动（发布后） 3.删除 */
    private Integer changeStatus;
}
