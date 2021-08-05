package com.touchealth.platform.processengine.service.impl.module.common;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchealth.platform.processengine.constant.CommonConstant;
import com.touchealth.platform.processengine.constant.ModuleConstant;
import com.touchealth.platform.processengine.utils.BaseHelper;
import com.touchealth.platform.processengine.dao.module.common.PicAssetDao;
import com.touchealth.platform.processengine.pojo.dto.PageData;
import com.touchealth.platform.processengine.pojo.dto.module.common.PicAssetDto;
import com.touchealth.platform.processengine.pojo.dto.module.common.PicAssetOssDto;
import com.touchealth.platform.processengine.pojo.request.module.common.PicAssetPageRequest;
import com.touchealth.platform.processengine.entity.module.common.PicAsset;
import com.touchealth.platform.processengine.exception.CommonModuleException;
import com.touchealth.platform.processengine.service.impl.BaseServiceImpl;
import com.touchealth.platform.processengine.service.module.common.PicAssetService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * 图片资源 服务实现类
 * </p>
 *
 * @author SunYang
 * @since 2020-11-16
 */
@Service
@Slf4j
public class PicAssetServiceImpl extends BaseServiceImpl<PicAssetDao, PicAsset> implements PicAssetService {

    @Value("${spring.profiles.active}")
    private String active;

    @Override
    public PicAssetDto save(PicAssetOssDto dto) throws CommonModuleException {
        Long pId = dto.getPId();
        PicAsset picAsset = BaseHelper.r2t(dto, PicAsset.class);

        if (pId == null || pId == -1L) { // 当前位置在根目录
            picAsset.setPId(-1L);
            picAsset.setDepth(0);
        } else { // 当前位置非根目录，在某个文件夹下
            PicAsset folder = getById(pId);
            if (folder == null) {
                throw new CommonModuleException("图片文件夹不存在");
            }
            picAsset.setDepth(Optional.ofNullable(folder.getDepth()).orElse(0) + 1);
        }

        boolean saveFlag = save(picAsset);
        if (!saveFlag) {
            log.error("PicAssetServiceImpl.save picture asset save fail. param: {}", dto);
            throw new CommonModuleException("图片或文件夹添加失败");
        }
        dto.setId(picAsset.getId());
        PicAssetDto picAssetDto = BaseHelper.r2t(dto, PicAssetDto.class);
        picAssetDto.setImgUrl(dto.getUrl());
        return picAssetDto;
    }

    @Override
    public Boolean move(Long id, Long folderId) {
        PicAsset pic = getById(id);
        if (pic == null) {
            throw new CommonModuleException("图片或文件夹不存在");
        }
        PicAsset folder = getById(folderId);
        if (folder == null) {
            throw new CommonModuleException("目标文件夹不存在");
        }
        pic.setPId(folderId);
        pic.setDepth(Optional.ofNullable(folder.getDepth()).orElse(0) + 1);
        return updateById(pic);
    }

    @Override
    public Boolean updateName(Long id, String name) {
        PicAsset pic = getById(id);
        if (pic == null) {
            throw new CommonModuleException("图片或文件夹不存在");
        }
        pic.setName(name);
        return updateById(pic);
    }

    @Override
    public PageData<PicAssetDto> pageList(PicAssetPageRequest query) {
        String channelNo = query.getChannelNo();
        Long folderId = Optional.ofNullable(query.getFolderId()).orElse(-1L);
        Long pageNo = Optional.ofNullable(query.getPageNo()).orElse(1L);
        Long pageSize = Optional.ofNullable(query.getPageSize()).orElse(10L);

        LambdaQueryWrapper<PicAsset> qw = Wrappers.<PicAsset>lambdaQuery()
                .nested(i -> i.eq(PicAsset::getChannelNo, channelNo).eq(PicAsset::getPId, folderId))
                .or()
                .nested(i -> i.in(PicAsset::getType, Arrays.asList(ModuleConstant.PIC_READONLY_TYPE, ModuleConstant.PIC_READONLY_FOLDER_TYPE))
                        .eq(PicAsset::getPId, folderId))
                .or()
                .orderByDesc(PicAsset::getType)
                .orderByDesc(PicAsset::getCreatedTime);
        Page<PicAsset> page = page(new Page<>(pageNo, pageSize), qw);

        List<PicAsset> records = page.getRecords();
        if (!CollectionUtils.isEmpty(records)) {
            List<PicAssetDto> picAssetDtos = records.stream().map(o -> {
                PicAssetDto picAssetDto = BaseHelper.r2t(o, PicAssetDto.class);
                String url = o.getUrl();
                // 生产环境将图片地址http转为https
                if (CommonConstant.PRO_ENV.equals(active) && StringUtils.isNotEmpty(url)) {
                    url = url.replaceFirst("http:", "https:");
                }
                picAssetDto.setImgUrl(url);
                return picAssetDto;
            }).collect(Collectors.toList());
            return new PageData<>(pageNo.intValue(), pageSize.intValue(), (int)page.getTotal(), (int)page.getPages(), picAssetDtos);
        }
        return new PageData<>(pageNo.intValue(), pageSize.intValue(), 0, new ArrayList<>());
    }

}
