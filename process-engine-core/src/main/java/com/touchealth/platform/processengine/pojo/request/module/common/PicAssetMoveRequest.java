package com.touchealth.platform.processengine.pojo.request.module.common;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 图片资源移动请求参数
 * @author SunYang
 */
@Data
public class PicAssetMoveRequest {

    /**
     * 移动的图片或图片文件夹ID
     */
    @NotNull(message = "目标ID不能为空")
    private Long id;
    /**
     * 目标图片文件夹ID
     */
    @NotNull(message = "文件夹ID不能为空")
    private Long folderId;

}
