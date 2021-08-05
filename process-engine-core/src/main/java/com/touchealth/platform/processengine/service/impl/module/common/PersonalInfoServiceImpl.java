package com.touchealth.platform.processengine.service.impl.module.common;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.touchealth.platform.processengine.annotation.TransactionalForException;
import com.touchealth.platform.processengine.constant.CommonConstant;
import com.touchealth.platform.processengine.dao.module.common.PersonalInfoDao;
import com.touchealth.platform.processengine.entity.module.common.PersonalInfo;
import com.touchealth.platform.processengine.entity.module.common.PersonalInfoImg;
import com.touchealth.platform.processengine.entity.page.PageManager;
import com.touchealth.platform.processengine.exception.CommonModuleException;
import com.touchealth.platform.processengine.handler.ModuleHandler;
import com.touchealth.platform.processengine.pojo.bo.WebJsonBo;
import com.touchealth.platform.processengine.pojo.bo.module.common.PersonalInfoBo;
import com.touchealth.platform.processengine.pojo.bo.module.common.PersonalInfoImgBo;
import com.touchealth.platform.processengine.pojo.dto.module.common.LinkDto;
import com.touchealth.platform.processengine.service.impl.module.BaseModuleServiceImpl;
import com.touchealth.platform.processengine.service.module.common.LinkService;
import com.touchealth.platform.processengine.service.module.common.PersonalInfoImgService;
import com.touchealth.platform.processengine.service.module.common.PersonalInfoService;
import com.touchealth.platform.processengine.service.page.PageManagerService;
import com.touchealth.platform.processengine.service.page.PlatformVersionService;
import com.touchealth.platform.processengine.utils.BaseHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 个人信息组件表 服务实现类
 * </p>
 *
 * @author lvx
 * @since 2021-01-14
 */
@Service
@Slf4j
public class PersonalInfoServiceImpl extends BaseModuleServiceImpl<PersonalInfoDao, PersonalInfo> implements PersonalInfoService {

    @Resource
    private PageManagerService pageManagerService;

    @Resource
    private PlatformVersionService platformVersionService;

    @Resource
    private LinkService linkService;

    @Resource
    private PersonalInfoImgService personalInfoImgService;

    @Override
    @TransactionalForException
    public String savePageModule(String webJson, Long pageId) {
        PageManager page = pageManagerService.getById(pageId);
        Assert.notNull(page, "页面不存在");
        PersonalInfoBo personalInfoBo = ModuleHandler.parsePersonalInfo(page, webJson);

        return save(personalInfoBo);
    }

    public String save(PersonalInfoBo bo) {
        // 添加个人信息组件
        bo.setId(null);
        PersonalInfo personalInfo = BaseHelper.r2t(bo, PersonalInfo.class);
        personalInfo.setCategoryId(CommonConstant.MODULE_CATEGORY.COMMON.getCode());
        boolean saveFlag = save(personalInfo);
        if (!saveFlag) {
            log.error("PersonalInfoServiceImpl.save personal info fail. param: {}", bo);
            throw new CommonModuleException("添加个人信息组件失败");
        }

        List<PersonalInfoImgBo> personalInfoImgBos = bo.getPersonalInfoImgs();
        // 添加图片
        List<PersonalInfoImg> personalInfoImgs = new ArrayList<>();
        if (!CollectionUtils.isEmpty(personalInfoImgBos)) {
            personalInfoImgBos.forEach(img -> {
                img.setId(null);
                PersonalInfoImg personalInfoImg = BaseHelper.r2t(img, PersonalInfoImg.class);
                personalInfoImg.setChannelNo(personalInfo.getChannelNo());
                personalInfoImg.setPersonalInfoId(personalInfo.getId());
                personalInfoImg.setPageId(personalInfo.getPageId());
                personalInfoImg.setVersion(personalInfo.getVersion());
                personalInfoImg.setStatus(personalInfo.getStatus());
                personalInfoImg.setUrl(img.getUrl());
                LinkDto linkDto = img.getLinkDto();
                if (linkDto != null) {
                    // 添加链接
                    linkDto.setChannelNo(personalInfo.getChannelNo());
                    linkDto.setVersion(personalInfo.getVersion());
                    linkDto.setPageId(personalInfo.getPageId());
                    linkDto.setStatus(personalInfo.getStatus());
                    LinkDto linkDto1 = linkService.save(linkDto);
                    Long linkId = linkDto1.getId();
                    personalInfoImg.setLinkModuleId(linkId);
                }
                personalInfoImgs.add(personalInfoImg);
            });
            boolean saveImgFlag = personalInfoImgService.saveBatch(personalInfoImgs);
            if (!saveImgFlag) {
                log.error("PersonalInfoServiceImpl save images fail. param: {}", bo);
                throw new CommonModuleException("保存图片失败");
            }
        }

        // 更新个人信息组件中的webJson里的图片ID
        WebJsonBo webJson = JSONObject.parseObject(personalInfo.getWebJson(), WebJsonBo.class);
        if (webJson != null) {
            webJson.setId(personalInfo.getId());
            webJson.setModuleUniqueId(personalInfo.getModuleUniqueId());
            updateWebJson(personalInfoImgs, webJson);
            personalInfo.setWebJson(JSONObject.toJSONString(webJson));
            saveFlag = saveOrUpdate(personalInfo);
            if (!saveFlag) {
                log.error("PersonalInfoServiceImpl.save update personal info webJson fail. param: {}", bo);
                throw new CommonModuleException("添加个人信息组件失败");
            }

            return JSONObject.toJSONString(webJson);
        }

        return null;
    }

    @Override
    @TransactionalForException
    public String clonePageModule(Long moduleId, Long pageId) {
        PageManager page = pageManagerService.getById(pageId);

        Assert.notNull(page, "页面不存在");
        Long versionId = page.getVersionId();

        Assert.notNull(platformVersionService.getById(versionId), "无效的版本号");

        PersonalInfo personalInfo = getById(moduleId);
        Assert.notNull(personalInfo, "个人信息组件不存在");

        // 更新前端数据字符串（webJson）
        String webJson = personalInfo.getWebJson();
        if (StringUtils.isEmpty(webJson)) {
            return "";
        }

        return savePageModule(webJson, pageId);
    }

    @Override
    @TransactionalForException
    public String updatePageModule(String webJson) {
        PersonalInfoBo bo = ModuleHandler.parsePersonalInfo(null, webJson);
        Long personalInfoId = bo.getId();
        List<PersonalInfoImgBo> imgBos = bo.getPersonalInfoImgs();

        // 更新个人信息组件
        PersonalInfo personalInfo = getById(personalInfoId);
        if (personalInfo == null) {
            throw new CommonModuleException("个人信息组件不存在");
        }
        BaseHelper.copyNotNullProperties(bo, personalInfo);
        if (!updateById(personalInfo)) {
            log.error("PersonalInfoServiceImpl.updatePageModule update personal info fail. param: {}", bo);
            throw new CommonModuleException("更新个人信息组件失败");
        }

        List<PersonalInfoImg> addOrUpdImgList = new ArrayList<>();
        // 更新图片
        if (!CollectionUtils.isEmpty(imgBos)) {
            List<Long> delImgList = new ArrayList<>();
            Map<Long, PersonalInfoImg> updPersonalImgMap = new HashMap<>();
            QueryWrapper<PersonalInfoImg> qw = Wrappers.<PersonalInfoImg>query()
                    .eq("personal_info_id", personalInfoId);
            List<PersonalInfoImg> personalInfoImgs = personalInfoImgService.baseFindList(qw);

            // 将所有需要更新的图片放到一个Map中
            for (PersonalInfoImgBo personalInfoImgBo : imgBos) {
                Long imgId = personalInfoImgBo.getId();
                PersonalInfoImg personalInfoImg = BaseHelper.r2t(personalInfoImgBo, PersonalInfoImg.class);
                personalInfoImg.setChannelNo(personalInfo.getChannelNo());
                personalInfoImg.setVersion(personalInfo.getVersion());
                personalInfoImg.setPersonalInfoId(personalInfoId);
                personalInfoImg.setPageId(personalInfo.getPageId());
                personalInfoImg.setStatus(personalInfo.getStatus());
                LinkDto linkDto = personalInfoImgBo.getLinkDto();
                if (linkDto != null) {
                    // 添加|更新 链接
                    linkDto.setChannelNo(personalInfo.getChannelNo());
                    linkDto.setPageId(personalInfo.getPageId());
                    linkDto.setStatus(personalInfo.getStatus());
                    linkDto.setVersion(personalInfo.getVersion());
                    if (linkDto.getId() == null) {
                        LinkDto linkDto1 = linkService.save(linkDto);
                        Long linkId = linkDto1.getId();
                        personalInfoImg.setLinkModuleId(linkId);
                    } else {
                        LinkDto updLink = linkService.update(linkDto);
                        if (updLink == null) {
                            log.error("PersonalInfoServiceImpl.updatePageModule update link fail. param: {}", linkDto);
                        }
                    }
                }
                // 新增或修改的图片
                addOrUpdImgList.add(personalInfoImg);
                if (imgId != null) {
                    // 需要更新的图片
                    updPersonalImgMap.put(imgId, personalInfoImg);
                }
            }
            // 将数据库中存在编辑后不存在的图片删除
            personalInfoImgs.forEach(personalInfoImg -> {
                Long id = personalInfoImg.getId();
                PersonalInfoImg infoImg = updPersonalImgMap.get(id);
                if (infoImg == null) {
                    delImgList.add(personalInfoImg.getId());
                }
            });
            boolean updImgFlag = personalInfoImgService.saveOrUpdateBatch(addOrUpdImgList);
            // 修改到回收站状态
            boolean delImgFlag = true;
            if (!CollectionUtils.isEmpty(delImgList)) {
                delImgFlag = personalInfoImgService.update(null,
                        Wrappers.<PersonalInfoImg>lambdaUpdate().in(PersonalInfoImg::getId, delImgList).set(PersonalInfoImg::getUpdatedTime, LocalDateTime.now()).set(PersonalInfoImg::getDeletedFlag, 1));
            }
            if (!updImgFlag || !delImgFlag) {
                log.error("PersonalInfoServiceImpl.updatePageModule fail. param: {}", bo);
                throw new CommonModuleException("更新个人信息组件失败");
            }
        }
        // 更新webJson
        WebJsonBo updateWebJson = JSONObject.parseObject(personalInfo.getWebJson(), WebJsonBo.class);
        updateWebJson(addOrUpdImgList, updateWebJson);
        personalInfo.setWebJson(JSONObject.toJSONString(updateWebJson));
        if (!saveOrUpdate(personalInfo)) {
            log.error("PersonalInfoServiceImpl.updatePageModule update webJson fail. param: {}", bo);
            throw new CommonModuleException("修改个人信息组件失败");
        }

        return JSONObject.toJSONString(updateWebJson);
    }

    private void updateWebJson(List<PersonalInfoImg> personalInfoImgs, WebJsonBo webJson) {
        if (!CollectionUtils.isEmpty(personalInfoImgs)) {
            List<WebJsonBo.WebJsonImgBo> imgWebJsonList = webJson.getData().getImgList();
            for (int i = 0; i < imgWebJsonList.size() && imgWebJsonList.size() == personalInfoImgs.size(); i++) {
                // 更新图片的webJson字段中的按钮ID
                WebJsonBo.WebJsonImgBo webJsonImgBo = imgWebJsonList.get(i);
                PersonalInfoImg personalInfoImg = personalInfoImgs.get(i);
                webJsonImgBo.setId(personalInfoImg.getId());
                webJsonImgBo.setModuleUniqueId(personalInfoImg.getModuleUniqueId());
                // 更新按钮链接ID
                WebJsonBo.WebJsonLinkBo link = webJsonImgBo.getLink();
                if (link != null) {
                    link.setId(personalInfoImg.getLinkModuleId());
                }
            }
        }

        //更新虚拟按钮id
        List<WebJsonBo.WebJsonVtButtonBo> vtButtonList = webJson.getData().getVtButtonList();
        if(!CollectionUtils.isEmpty(vtButtonList)){
            vtButtonList.forEach(e->{
                long id = IdWorker.getId();
                if(e.getId() == null){
                    e.setId(id);
                    e.setModuleUniqueId(id);
                } else {
                    e.setId(id);
                    if(e.getModuleUniqueId() == null){
                        e.setModuleUniqueId(id);
                    }
                }
            });
        }
    }
}
