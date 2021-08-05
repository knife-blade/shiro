package com.touchealth.platform.processengine.pojo.request.module.common;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 添加图片文件夹参数
 * @author SunYang
 */
@Data
public class PicAssetFolderAddRequest {

    /**
     * 当前所在文件夹ID。根目录不传或传-1
     */
    @NotNull(message = "文件夹ID不能为空")
    private Long folderId;
    /**
     * 文件夹名
     */
    @NotBlank(message = "文件夹名不能为空")
    private String name;

}
