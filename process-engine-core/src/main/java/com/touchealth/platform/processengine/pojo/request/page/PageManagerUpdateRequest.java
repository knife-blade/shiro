package com.touchealth.platform.processengine.pojo.request.page;

import lombok.Data;

/**
 * @author liufengqiang
 * @date 2020-11-20 10:03:41
 */
@Data
public class PageManagerUpdateRequest {

    /** 页面名称 */
    private String pageName;
    /** 上个页面id */
    private Long lastId;
    /** 所属文件夹 拖动到文件夹时传 */
    private Long folderId;
}
