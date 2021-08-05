package com.touchealth.platform.processengine.pojo.request.page;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author liufengqiang
 * @date 2020-11-16 19:01:20
 */
@Data
public class PageManagerRequest {

    /** 页面名称 */
    private String pageName;
    /** 所属文件夹 */
    private Long folderId;
    /** 是否是文件夹 */
    @NotNull(message = "是否是文件夹不能为空")
    private Boolean isFolder;
    /** 版本号 */
    @NotNull(message = "版本号不能为空")
    private Long versionId;
}
