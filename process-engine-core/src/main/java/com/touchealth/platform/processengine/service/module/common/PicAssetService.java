package com.touchealth.platform.processengine.service.module.common;

import com.touchealth.platform.processengine.pojo.dto.PageData;
import com.touchealth.platform.processengine.pojo.dto.module.common.PicAssetDto;
import com.touchealth.platform.processengine.pojo.dto.module.common.PicAssetOssDto;
import com.touchealth.platform.processengine.pojo.request.module.common.PicAssetPageRequest;
import com.touchealth.platform.processengine.entity.module.common.PicAsset;
import com.touchealth.platform.processengine.service.BaseService;

/**
 * <p>
 * 图片或图片文件夹 服务类
 * </p>
 *
 * @author SunYang
 * @since 2020-11-16
 */
public interface PicAssetService extends BaseService<PicAsset> {

    /**
     * 添加图片或图片文件夹
     * @param dto
     * @return
     */
    PicAssetDto save(PicAssetOssDto dto);

    /**
     * 将图片放到某个文件夹下
     * @param id    图片ID
     * @param folderId  图片文件夹ID
     * @return
     */
    Boolean move(Long id, Long folderId);

    /**
     * 更新图片或图片文件夹名
     * @param id
     * @param name
     * @return
     */
    Boolean updateName(Long id, String name);

    /**
     * 分页查询图片或图片资源列表
     * @param query
     * @return
     */
    PageData<PicAssetDto> pageList(PicAssetPageRequest query);

}
