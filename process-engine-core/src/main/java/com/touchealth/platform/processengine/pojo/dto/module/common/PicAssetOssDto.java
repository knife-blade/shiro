package com.touchealth.platform.processengine.pojo.dto.module.common;

import lombok.Data;

/**
 * 图片或图片文件夹信息
 */
@Data
public class PicAssetOssDto {

    /**
     * 图片资源ID
     */
    private Long id;

    /**
     * 版本间的唯一组件标识。同一个组件，不同版本该值不变。
     */
    private Long moduleUniqueId;

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
    private String url;

    /**
     * 资源类型。0：图片；1：图片文件夹
     */
    private Integer type;

    /**
     * 图片OSS路径
     */
    private String ossPath;

    /**
     * 图片OSS名称
     */
    private String ossFilename;

}
