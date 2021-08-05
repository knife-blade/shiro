package com.touchealth.platform.processengine.pojo.dto.module.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图片或图片文件夹信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PicDto {

    private String id;
    private String url;
    private String filename;

}
