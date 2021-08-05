package com.touchealth.platform.processengine.pojo.dto.module.common;

import com.touchealth.platform.processengine.pojo.dto.module.BaseDto;
import lombok.Data;

/**
 * 图片或图片文件夹信息
 */
@Data
public class PicAssetDto extends BaseDto {

    /**
     * 按钮组件所在的渠道号
     */
    private String channelNo;

    /**
     * 图片或图片文件夹名
     */
    private String name;

    /**
     * 图片或图片文件夹深度
     */
    private Integer depth;

    /**
     * 图片上级图片文件夹ID。根目录改值为-1
     */
    private Long pId;

    /**
     * 图片地址
     */
    private String imgUrl;

    /**
     * 资源类型。0：图片；1：图片文件夹
     */
    private Integer type;

}
