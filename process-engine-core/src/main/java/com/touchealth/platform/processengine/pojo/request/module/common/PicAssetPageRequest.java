package com.touchealth.platform.processengine.pojo.request.module.common;

import com.touchealth.platform.processengine.pojo.request.PageDataRequest;
import lombok.Data;

/**
 * 查询图片资源参数
 */
@Data
public class PicAssetPageRequest extends PageDataRequest {

    /**
     * 渠道号
     */
    String channelNo;
    /**
     * 图片文件夹ID。获取根目录可传空值或-1
     */
    Long folderId;

}
