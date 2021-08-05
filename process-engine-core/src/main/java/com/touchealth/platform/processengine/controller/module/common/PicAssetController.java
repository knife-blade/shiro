package com.touchealth.platform.processengine.controller.module.common;


import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.touchealth.platform.processengine.constant.ModuleConstant;
import com.touchealth.platform.processengine.config.AliYunOssConfig;
import com.touchealth.platform.processengine.controller.BaseController;
import com.touchealth.platform.processengine.entity.module.common.PicAsset;
import com.touchealth.platform.processengine.pojo.dto.PageData;
import com.touchealth.platform.processengine.pojo.dto.Response;
import com.touchealth.platform.processengine.pojo.dto.module.common.PicAssetDto;
import com.touchealth.platform.processengine.pojo.dto.module.common.PicDto;
import com.touchealth.platform.processengine.pojo.request.module.common.PicAssetFolderAddRequest;
import com.touchealth.platform.processengine.pojo.request.module.common.PicAssetMoveRequest;
import com.touchealth.platform.processengine.pojo.dto.module.common.PicAssetOssDto;
import com.touchealth.platform.processengine.pojo.request.module.common.PicAssetPageRequest;
import com.touchealth.platform.processengine.service.AliyunOssService;
import com.touchealth.platform.processengine.service.module.common.PicAssetService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 组件分类表 前端控制器
 * </p>
 *
 * @author SunYang
 * @since 2020-11-16
 */
@RestController
@RequestMapping("/pic")
@Slf4j
public class PicAssetController extends BaseController {

    @Autowired
    private PicAssetService picAssetService;
    @Autowired
    private AliyunOssService aliyunOssService;
    @Autowired
    private AliYunOssConfig aliYunOssConfig;

    /**
     * 文件名最大长度
     */
    private static final Integer MAX_FILE_NAME_LEN = 100;
    /**
     * 文件大小限制
     */
    private static final Long MAX_FILE_SIZE = 1024L * 1024 * 3;

    /**
     * 上传图片到OSS，本地不存储
     * @param file
     * @param isPrivate 是否私有
     * @return
     */
    @PostMapping("/upload")
    public Response<PicDto> uploadImg(@RequestParam MultipartFile file, @RequestParam(required = false, defaultValue = "false") Boolean isPrivate,
                                      @RequestParam(required = false) String filename) {
        if (file == null || file.getSize() <= 0) {
            return Response.error("很抱歉，请选择要上传的图片！");
        }

        if (isPrivate) {
            // TODO 20201221 私有图片暂时不处理，直接返回
            return Response.ok(null);
        }

        String imgUrl = "", ossFileName = StringUtils.isEmpty(filename) ? DateFormatUtils.format(new Date(), "yyyyMMddHHmmss") + IdWorker.get32UUID() : filename;

        try {
            imgUrl = aliyunOssService.upload(file, "/" + ossFileName);
        } catch (IOException e) {
            log.error("PicAssetController.uploadImg upload fail", e);
            return Response.error("很抱歉，上传图片失败！");
        }

        return Response.ok(new PicDto(null, imgUrl, filename));
    }

    /**
     * 添加图片
     * @param file
     * @param folderId  当前所在文件夹ID。根目录不传或传-1
     * @param name  图片名
     * @param channelNo 渠道号
     * @return
     */
    @PostMapping("/add")
    public Response<PicAssetDto> addImg(@RequestParam MultipartFile file,
                                        @RequestParam(defaultValue = "-1") Long folderId,
                                        @RequestParam(required = false) String name,
                                        @RequestHeader String channelNo) {
        if (file == null || file.getSize() <= 0) {
            return Response.error("很抱歉，请选择要上传的图片！");
        }

        // 验证文件夹是否只读
        if (checkPicAssetReadOnly(folderId)) {
            return Response.error("很抱歉，该文件夹为只读！");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            return Response.error("您上传的图片尺寸超过3M，请压缩图片后重新上传");
        }

        name = StringUtils.isEmpty(name) ? file.getOriginalFilename() : name;
        int length = name.length();
        if (length > MAX_FILE_NAME_LEN) {
            name = name.substring(length - MAX_FILE_NAME_LEN);
        }

        // 上传图片到阿里云OSS
        String ossPath = "", imgUrl = "", ossFileName = DateFormatUtils.format(new Date(), "yyyyMMddHHmmss") + IdWorker.get32UUID();

        try {
            // 处理路径信息,默认前缀，区分环境
            String prefix = aliYunOssConfig.getAliyunPrefix();
            ossPath = prefix + "/" + ossFileName;
            imgUrl = aliyunOssService.upload(file, "/" + ossFileName);
        } catch (IOException e) {
            log.error("PicAssetController.addImg upload fail", e);
            return Response.error("很抱歉，上传图片失败！");
        }

        // 保存图片
        PicAssetOssDto saveDto = new PicAssetOssDto();
        saveDto.setChannelNo(channelNo);
        saveDto.setType(ModuleConstant.PIC_TYPE);
        saveDto.setName(name);
        saveDto.setPId(folderId);
        saveDto.setUrl(imgUrl);
        saveDto.setOssPath(ossPath);
        saveDto.setOssFilename(ossFileName);
        PicAssetDto save = picAssetService.save(saveDto);
        return Response.ok(save);
    }

    /**
     * 批量添加图片
     * @param files
     * @param folderId  当前所在文件夹ID。根目录不传或传-1
     * @param names  图片名
     * @param channelNo 渠道号
     * @return
     */
    @PostMapping("/batchAdd")
    public Response<List<PicAssetDto>> batchAddImg(@RequestParam List<MultipartFile> files,
                                                   @RequestParam(defaultValue = "-1") Long folderId,
                                                   @RequestParam List<String> names,
                                                   @RequestHeader String channelNo) {
        if (CollectionUtils.isEmpty(files) || CollectionUtils.isEmpty(names)) {
            return Response.error("很抱歉，请选择要上传的图片！");
        }
        if (files.size() != names.size()) {
            return Response.error("很抱歉，图片数量和图片名数量不一致！");
        }

        // 验证文件夹是否只读
        if (checkPicAssetReadOnly(folderId)) {
            return Response.error("很抱歉，该文件夹为只读！");
        }

        List<PicAssetDto> ret = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            String name = names.get(i);
            name = StringUtils.isEmpty(name) ? file.getOriginalFilename() : name;
            int length = name.length();
            if (length > MAX_FILE_NAME_LEN) {
                name = name.substring(length - MAX_FILE_NAME_LEN);
            }

            // 上传图片到阿里云OSS
            String ossPath = "", imgUrl = "", ossFileName = DateFormatUtils.format(new Date(), "yyyyMMddHHmmss") + IdWorker.get32UUID();

            try {
                // 处理路径信息,默认前缀，区分环境
                String prefix = aliYunOssConfig.getAliyunPrefix();
                ossPath = prefix + "/" + ossFileName;
                imgUrl = aliyunOssService.upload(file, "/" + ossFileName);
            } catch (IOException e) {
                log.error("PicAssetController.addImg upload fail", e);
                return Response.error("很抱歉，上传图片失败！");
            }

            // 保存图片
            PicAssetOssDto saveDto = new PicAssetOssDto();
            saveDto.setChannelNo(channelNo);
            saveDto.setType(ModuleConstant.PIC_TYPE);
            saveDto.setName(name);
            saveDto.setPId(folderId);
            saveDto.setUrl(imgUrl);
            saveDto.setOssPath(ossPath);
            saveDto.setOssFilename(ossFileName);
            PicAssetDto save = picAssetService.save(saveDto);
            ret.add(save);
        }
        return Response.ok(ret);
    }

    /**
     * 添加图片文件夹
     * @param request
     * @param channelNo 渠道号
     * @return
     */
    @PostMapping("/addfolder")
    public Response<PicAssetDto> addImgFolder(@RequestBody @Valid PicAssetFolderAddRequest request, @RequestHeader String channelNo) {
        Long folderId = Optional.ofNullable(request.getFolderId()).orElse(-1L);

        // 验证文件夹是否只读
        if (checkPicAssetReadOnly(folderId)) {
            return Response.error("很抱歉，该文件夹为只读！");
        }

        String name = request.getName();
        // 保存图片文件夹
        PicAssetOssDto saveDto = new PicAssetOssDto();
        saveDto.setChannelNo(channelNo);
        saveDto.setType(ModuleConstant.PIC_FOLDER_TYPE);
        saveDto.setName(name);
        saveDto.setPId(folderId);
        PicAssetDto save = picAssetService.save(saveDto);
        return Response.ok(save);
    }

    /**
     * 分页查询图片资源列表
     * @param query
     * @param channelNo
     * @return
     */
    @RequestMapping("/list")
    public Response<PageData<PicAssetDto>> pageList(PicAssetPageRequest query, @RequestHeader String channelNo) {
        query.setChannelNo(channelNo);
        PageData<PicAssetDto> pageData = picAssetService.pageList(query);
        return Response.ok(pageData);
    }

    /**
     * 将图片或图片文件夹放到某个文件夹下
     * @param moveDto
     * @return
     */
    @PutMapping("/move")
    public Response<Boolean> move(@RequestBody PicAssetMoveRequest moveDto) {
        Long id = moveDto.getId();
        Long folderId = moveDto.getFolderId();

        // 验证文件是否只读
        if (checkPicAssetReadOnly(id)) {
            return Response.error("很抱歉，该文件为只读！");
        }
        // 验证文件夹是否只读
        if (checkPicAssetReadOnly(folderId)) {
            return Response.error("很抱歉，该文件夹为只读！");
        }

        return Response.ok(picAssetService.move(id, folderId));
    }

    /**
     * 修改图片或图片文件夹名
     * @param dto
     * @return
     */
    @PutMapping("/rename")
    public Response<Boolean> rename(@RequestBody PicAssetDto dto) {
        Long id = dto.getId();
        String name = dto.getName();

        // 验证文件或文件夹是否只读
        if (checkPicAssetReadOnly(id)) {
            return Response.error("很抱歉，该文件或文件夹为只读！");
        }

        return Response.ok(picAssetService.updateName(id, name));
    }

    /**
     * 修改图片或图片文件夹名
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public Response<Boolean> delete(@PathVariable Long id) {

        // 验证文件或文件夹是否只读
        if (checkPicAssetReadOnly(id)) {
            return Response.error("很抱歉，该文件或文件夹为只读！");
        }

        return Response.ok(picAssetService.removeById(id));
    }

    /**
     * 检查图片文件或文件夹是否只读
     * @param assetId
     * @return
     */
    private boolean checkPicAssetReadOnly(Long assetId) {
        // -1 表示根目录
        if (assetId == -1) {
            return false;
        }
        // 验证文件夹是否只读
        PicAsset asset = picAssetService.getById(assetId);
        Assert.notNull(asset, "无效的文件或文件夹");
        if (ModuleConstant.PIC_READONLY_FOLDER_TYPE.equals(asset.getType()) ||
                ModuleConstant.PIC_READONLY_TYPE.equals(asset.getType())) {
            return true;
        }
        return false;
    }

}
