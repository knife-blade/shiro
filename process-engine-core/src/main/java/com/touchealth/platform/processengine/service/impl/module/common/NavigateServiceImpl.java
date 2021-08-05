package com.touchealth.platform.processengine.service.impl.module.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.touchealth.platform.processengine.annotation.TransactionalForException;
import com.touchealth.platform.processengine.constant.CommonConstant;
import com.touchealth.platform.processengine.dao.module.common.NavigateDao;
import com.touchealth.platform.processengine.entity.module.common.*;
import com.touchealth.platform.processengine.entity.page.PageManager;
import com.touchealth.platform.processengine.exception.CommonModuleException;
import com.touchealth.platform.processengine.exception.ParameterException;
import com.touchealth.platform.processengine.handler.ModuleHandler;
import com.touchealth.platform.processengine.pojo.bo.CompareBo;
import com.touchealth.platform.processengine.pojo.bo.WebJsonBo;
import com.touchealth.platform.processengine.pojo.bo.module.common.NavigateBo;
import com.touchealth.platform.processengine.pojo.bo.module.common.NavigateImgBo;
import com.touchealth.platform.processengine.pojo.dto.module.common.*;
import com.touchealth.platform.processengine.service.impl.module.BaseModuleServiceImpl;
import com.touchealth.platform.processengine.service.module.common.LinkService;
import com.touchealth.platform.processengine.service.module.common.NavigateImgService;
import com.touchealth.platform.processengine.service.module.common.NavigateService;
import com.touchealth.platform.processengine.service.page.PageManagerService;
import com.touchealth.platform.processengine.service.page.PlatformVersionService;
import com.touchealth.platform.processengine.utils.BaseHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.touchealth.platform.processengine.constant.CompareConstant.*;
import static com.touchealth.platform.processengine.constant.WebJsonConstant.WEB_LINK_TYPE_OUTSIDE;

/**
 * <p>
 * 坑位导航组件表 服务实现类
 * </p>
 *
 * @author LJH
 * @since 2020-11-26
 */
@Service
@Slf4j
public class NavigateServiceImpl extends BaseModuleServiceImpl<NavigateDao, Navigate> implements NavigateService {

    @Autowired
    private LinkService linkService;

    @Autowired
    private NavigateImgService navigateImgService;

    @Resource
    private PageManagerService pageManagerService;

    @Resource
    private PlatformVersionService platformVersionService;

    @TransactionalForException
    @Override
    public NavigateDto save(NavigateBo bo) {
        List<NavigateImgBo> navigateImgBos = bo.getNavigateImgBos();
        if(CollectionUtils.isEmpty(navigateImgBos)){
            return null;
        }
        // 添加坑位组件
        bo.setId(null);
        Navigate navigate = BaseHelper.r2t(bo,Navigate.class);
        navigate.setCategoryId(CommonConstant.MODULE_CATEGORY.COMMON.getCode());
        boolean saveNavigateFlag = save(navigate);
        if (!saveNavigateFlag) {
            log.error("NavigateServiceImpl.save navigate group save fail. param: {}",bo);
            throw new CommonModuleException("添加坑位组件失败");
        }
        NavigateDto navigateDto = BaseHelper.r2t(navigate,NavigateDto.class);
        navigateDto.setNavigateImgDtos(new ArrayList<>(navigateImgBos.size()));
        // 添加图片
        List<NavigateImg> navigateImgs = new ArrayList<>(navigateImgBos.size());
        navigateImgBos.forEach(img -> {
            img.setId(null);
            NavigateImg navigateImg = BaseHelper.r2t(img,NavigateImg.class);
            navigateImg.setChannelNo(navigate.getChannelNo());
            navigateImg.setNavigateId(navigate.getId());
            navigateImg.setPageId(navigate.getPageId());
            navigateImg.setVersion(navigate.getVersion());
            navigateImg.setStatus(navigate.getStatus());
            navigateImg.setUrl(img.getUrl());
            LinkDto linkDto = img.getLinkDto();
            if (linkDto != null) {
                // 添加链接
                linkDto.setVersion(navigate.getVersion());
                linkDto.setChannelNo(navigate.getChannelNo());
                linkDto.setPageId(navigate.getPageId());
                linkDto.setStatus(navigate.getStatus());
                LinkDto linkDto1 = linkService.save(linkDto);
                Long linkId = linkDto1.getId();
                navigateImg.setLinkModuleId(linkId);
            }
            navigateImgs.add(navigateImg);
            navigateDto.getNavigateImgDtos().add(BaseHelper.r2t(navigateImg, NavigateImgDto.class));
        });
        boolean saveBannerImgFlag = navigateImgService.saveBatch(navigateImgs);
        if (!saveBannerImgFlag) {
            log.error("NavigateServiceImpl save fail. param: {}", bo);
            throw new CommonModuleException("报存图片失败");
        }
        // 再次保存轮播组件，为了更新轮播组件中的webJson里的轮播组ID和图片ID
        WebJsonBo navigateWebJson = JSONObject.parseObject(navigate.getWebJson(), WebJsonBo.class);
        if (navigateWebJson != null) {
            // 更新坑位组件的webJson字段中的按钮组ID
            navigateWebJson.setId(navigate.getId());
            navigateWebJson.setModuleUniqueId(navigate.getModuleUniqueId());
            if (navigateWebJson.getData() != null && !CollectionUtils.isEmpty(navigateWebJson.getData().getImgList())) {
                updateWebJson(navigateImgs, navigateWebJson);
            }
            navigate.setWebJson(JSONObject.toJSONString(navigateWebJson));
            if (!saveOrUpdate(navigate)) {
                log.error("NavigateServiceImpl.save update navigate webJson fail. param: {}", bo);
                throw new CommonModuleException("添加热区组件失败");
            }
            navigateDto.setWebJson(JSONObject.toJSONString(navigateWebJson));
        }
        return navigateDto;
    }

    private void updateWebJson(List<NavigateImg> navigateImgs, WebJsonBo navigateWebJson) {
        if(CollectionUtils.isEmpty(navigateImgs)){
            return;
        }
        List<WebJsonBo.WebJsonImgBo> nativateImgWebJsonList = navigateWebJson.getData().getImgList();
        for (int i = 0; i < nativateImgWebJsonList.size() && nativateImgWebJsonList.size() == navigateImgs.size(); i++) {
            // 更新图片的webJson字段中的肯为ID
            WebJsonBo.WebJsonImgBo webJsonImgBo = nativateImgWebJsonList.get(i);
            NavigateImg navigateImg = navigateImgs.get(i);
            webJsonImgBo.setId(navigateImg.getId());
            webJsonImgBo.setModuleUniqueId(navigateImg.getModuleUniqueId());
            // 更新按钮链接ID
            WebJsonBo.WebJsonLinkBo link = webJsonImgBo.getLink();
            if (link != null) {
                link.setId(navigateImg.getLinkModuleId());
            }
        }
    }

    @TransactionalForException
    @Override
    public String update(NavigateBo bo) {
        Long navigateId = bo.getId();
        List<NavigateImgBo> navigateImgBos = bo.getNavigateImgBos();

        // 更新按钮组
        Navigate navigate = getById(navigateId);
        if (navigate == null) {
            throw new CommonModuleException("坑位组件不存在");
        }
        BaseHelper.copyNotNullProperties(bo, navigate);
        if (!updateById(navigate)) {
            log.error("NavigateServiceImpl.update navigate group update fail. param: {}", bo);
            throw new CommonModuleException("更新坑位组失败");
        }

        List<NavigateImg> addOrUpdNavigateImgList = new ArrayList<>();
        // 更新图片
        if (!CollectionUtils.isEmpty(navigateImgBos)) {
            List<Long> delNavigateImgList = new ArrayList<>();
            Map<Long, NavigateImg> updNavigateImgMap = new HashMap<>();
            QueryWrapper<NavigateImg> qw = Wrappers.<NavigateImg>query()
                    .eq("navigate_id", navigateId);
            List<NavigateImg> navigateImgs = navigateImgService.baseFindList(qw);

            // 将所有需要更新的按钮放到一个Map中
            for (NavigateImgBo navigateImgBo : navigateImgBos) {
                Long navigateImgId = navigateImgBo.getId();
                NavigateImg navigateImg = BaseHelper.r2t(navigateImgBo, NavigateImg.class);
                navigateImg.setChannelNo(navigate.getChannelNo());
                navigateImg.setVersion(navigate.getVersion());
                navigateImg.setNavigateId(navigateId);
                navigateImg.setPageId(navigate.getPageId());
                navigateImg.setStatus(navigate.getStatus());

                LinkDto linkDto = navigateImgBo.getLinkDto();
                if (linkDto != null) {
                    // 添加|更新 链接
                    linkDto.setChannelNo(navigate.getChannelNo());
                    linkDto.setPageId(navigate.getPageId());
                    linkDto.setVersion(navigate.getVersion());
                    linkDto.setStatus(navigate.getStatus());
                    if (linkDto.getId() == null) {
                        LinkDto linkDto1 = linkService.save(linkDto);
                        Long linkId = linkDto1.getId();
                        navigateImg.setLinkModuleId(linkId);
                    } else {
                        LinkDto updLink = linkService.update(linkDto);
                        if (updLink == null) {
                            log.error("NavigateServiceImpl.update banner group update link fail. param: {}", linkDto);
                        }
                    }
                }
                // 新增或修改的图片
                addOrUpdNavigateImgList.add(navigateImg);
                if (navigateImgId != null) {
                    // 需要更新的图片
                    updNavigateImgMap.put(navigateImgId, navigateImg);
                }
            }
            // 将数据库中存在编辑后不存在的图片删除
            navigateImgs.forEach(navigateImgDb -> {
                NavigateImg navigateImg = updNavigateImgMap.get(navigateImgDb.getId());
                if (navigateImg == null) {
                    delNavigateImgList.add(navigateImgDb.getId());
                }
            });
            boolean updBannerFlag = navigateImgService.saveOrUpdateBatch(addOrUpdNavigateImgList);
            // 更新为回收站状态
            boolean delBannerFlag = true;
            if(!CollectionUtils.isEmpty(delNavigateImgList)){
                delBannerFlag = navigateImgService.update(null,
                        Wrappers.<NavigateImg>lambdaUpdate().in(NavigateImg::getId, delNavigateImgList).set(NavigateImg::getUpdatedTime, LocalDateTime.now()).set(NavigateImg::getDeletedFlag, 1));
            }
            if (!updBannerFlag || !delBannerFlag) {
                log.error("NavigateServiceImpl.update navigate update fail. param: {}", bo);
                throw new CommonModuleException("更新坑位组件失败");
            }
        }
        // 更新webJson
        WebJsonBo hotspotWebJson = JSONObject.parseObject(navigate.getWebJson(), WebJsonBo.class);
        updateWebJson(addOrUpdNavigateImgList, hotspotWebJson);
        navigate.setWebJson(JSONObject.toJSONString(hotspotWebJson));
        if (!saveOrUpdate(navigate)) {
            log.error("NavigateServiceImpl.save update navigate webJson fail. param: {}", bo);
            throw new CommonModuleException("更新坑位组件失败");
        }
        return JSONObject.toJSONString(hotspotWebJson);
    }

    @Override
    public NavigateDto findById(Long id,Boolean showRecycleBin) {
        Navigate navigate = getById(id);
        if (navigate == null) {
            return null;
        }
        NavigateDto navigateDto = BaseHelper.r2t(navigate, NavigateDto.class);
        LambdaQueryWrapper<NavigateImg> query = Wrappers.<NavigateImg>lambdaQuery().eq(NavigateImg::getNavigateId, id).eq(NavigateImg::getDeletedFlag,0).notIn(NavigateImg::getStatus, Collections.singletonList(2)).orderByAsc(NavigateImg::getSort);
        if(showRecycleBin){
            query = Wrappers.<NavigateImg>lambdaQuery().eq(NavigateImg::getNavigateId, id).eq(NavigateImg::getDeletedFlag,0).orderByAsc(NavigateImg::getSort);
        }
        List<NavigateImg> navigateImgs = navigateImgService.getBaseMapper().selectList(query);

        if (CollectionUtils.isEmpty(navigateImgs)) {
            return navigateDto;
        }
        List<NavigateImgDto> navigateImgDtos = navigateImgs.stream().map(o -> BaseHelper.r2t(o, NavigateImgDto.class)).collect(Collectors.toList());
        navigateDto.setNavigateImgDtos(navigateImgDtos);
        return navigateDto;
    }

    @Override
    public List<NavigateDto> findByIdList(List<Long> ids,Boolean showRecycleBin) {
        List<NavigateDto> navigateDtos = new ArrayList<>();
        List<Navigate> navigates = listByIds(ids);
        if (CollectionUtils.isEmpty(navigates)) {
            return navigateDtos;
        }

        Map<Long, List<NavigateImg>> navigateImgMap = new HashMap<>();
        QueryWrapper<NavigateImg> query = Wrappers.<NavigateImg>query().in("navigate_id", ids).notIn("status", Collections.singletonList(2));
        if(showRecycleBin){
            query = Wrappers.<NavigateImg>query().in("navigate_id", ids);
        }
        List<NavigateImg> navigateImgs = navigateImgService.baseFindList(query);
        if (!CollectionUtils.isEmpty(navigateImgs)) {
            navigateImgMap = navigateImgs.stream().collect(Collectors.toMap(
                    NavigateImg::getNavigateId,
                    o -> {
                        List<NavigateImg> tmpArr = new ArrayList<>();
                        tmpArr.add(o);
                        return tmpArr;
                    },
                    (ov, nv) -> {
                        ov.addAll(nv);
                        return ov;
                    }));
        }
        for (Navigate navigate : navigates) {
            NavigateDto navigateDto = BaseHelper.r2t(navigate, NavigateDto.class);
            Long navigateId = navigateDto.getId();
            List<NavigateImg> navigteImgsById = navigateImgMap.get(navigateId);
            if (!CollectionUtils.isEmpty(navigteImgsById)) {
                List<NavigateImgDto> navigateImgDtos = navigteImgsById.stream().map(o -> BaseHelper.r2t(o, NavigateImgDto.class)).collect(Collectors.toList());
                navigateDto.setNavigateImgDtos(navigateImgDtos);
            }
            navigateDtos.add(navigateDto);
        }

        return navigateDtos;
    }

    @TransactionalForException
    @Override
    public Boolean delete(Long id) {
        NavigateDto navigateDto = findById(id,true);
        if (navigateDto == null) {
            return false;
        }
        boolean delNavigateFlag = removeById(id);
        if (!delNavigateFlag) {
            return false;
        }
        List<NavigateImgDto> navigateImgDtos = navigateDto.getNavigateImgDtos();
        return deleteNavigateImgDetailAndLink(navigateImgDtos);
    }

    @TransactionalForException
    @Override
    public Boolean delete(List<Long> ids){
        List<NavigateDto> navigateDtos = findByIdList(ids,true);
        if (CollectionUtils.isEmpty(navigateDtos)) {
            return false;
        }
        boolean delFlag = removeByIds(ids);
        if (!delFlag) {
            return false;
        }
        List<NavigateImgDto> navigateImgDtoDtos = navigateDtos.stream().flatMap(navigateDto -> navigateDto.getNavigateImgDtos().stream()).collect(Collectors.toList());
        return deleteNavigateImgDetailAndLink(navigateImgDtoDtos);
    }

    private Boolean deleteNavigateImgDetailAndLink(List<NavigateImgDto> navigateImgDtos) {
        if (!CollectionUtils.isEmpty(navigateImgDtos)) {
            List<Long> navigateImgIds = navigateImgDtos.stream().map(NavigateImgDto::getId).filter(o -> o != null).distinct().collect(Collectors.toList());
            List<Long> linkIds = navigateImgDtos.stream().map(NavigateImgDto::getLinkModuleId).filter(o -> o != null).distinct().collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(navigateImgIds) && !navigateImgService.removeByIds(navigateImgIds)) {
                log.error("BannerServiceImpl.deleteModule navigate delete fail. param: {}", navigateImgIds);
                throw new CommonModuleException("删除banner图片失败");
            }
            if (!CollectionUtils.isEmpty(linkIds) && !linkService.removeByIds(linkIds)) {
                log.error("NavigateServiceImpl.deleteModule navigate link delete fail. param: {}", linkIds);
                throw new CommonModuleException("删除坑位链接失败");
            }
        }
        return true;
    }


    @TransactionalForException
    @Override
    public String savePageModule(String webJson, Long pageId) {
        PageManager page = pageManagerService.getById(pageId);
        Assert.notNull(page, "页面不存在");
        NavigateBo navigateBo = ModuleHandler.parseNavigate(page, webJson);
        NavigateDto navigateDto = save(navigateBo);
        return navigateDto.getWebJson();
    }

    @TransactionalForException
    @Override
    public String clonePageModule(Long moduleId, Long pageId) {
        PageManager page = pageManagerService.getById(pageId);

        Assert.notNull(page, "页面不存在");
        Long versionId = page.getVersionId();

        Assert.notNull(platformVersionService.getById(versionId), "无效的版本号");

        Navigate navigate = getById(moduleId);
        Assert.notNull(navigate, "热区组件不存在");

        // 更新前端数据字符串（webJson）
        String webJson = navigate.getWebJson();
        if (StringUtils.isEmpty(webJson)) {
            return "";
        }


        return savePageModule(webJson, pageId);
    }

    @TransactionalForException
    @Override
    public String updatePageModule(String webJson) {
        return update(ModuleHandler.parseNavigate(null,webJson));
    }

    @TransactionalForException
    @Override
    public Boolean deletePageModule(List<Long> ids) {
        return delete(ids);
    }

    @Override
    public Boolean restoreModule(List<Long> ids) {
        return null;
    }

    @TransactionalForException
    @Override
    public Boolean updateModuleStatus(Long moduleId, Integer status) {
        NavigateDto navigateDto = findById(moduleId,true);
        if (navigateDto == null) {
            log.debug("NavigateServiceImpl.updateModuleStatus {} not exits", moduleId);
            throw new CommonModuleException("组件不存在");
        }
        // 更新状态
        Navigate updNavigate = new Navigate();
        updNavigate.setId(navigateDto.getId());
        updNavigate.setStatus(status);
        boolean updFlag = updateById(updNavigate);
        if (!updFlag) {
            log.error("NavigateServiceImpl.updateModuleStatus update navigate group status fail. {}", moduleId);
            throw new CommonModuleException("坑位组件状态更新失败");
        }

        List<NavigateImgDto> navigateImgDtos = navigateDto.getNavigateImgDtos();
        if (!CollectionUtils.isEmpty(navigateImgDtos)) {
            List<Long> navigateImgIds = navigateImgDtos.stream().map(NavigateImgDto::getId).filter(Objects::nonNull).collect(Collectors.toList());
            List<Long> linkIds = navigateImgDtos.stream().map(NavigateImgDto::getLinkModuleId).filter(Objects::nonNull).collect(Collectors.toList());
            // 更新图片状态
            if (!CollectionUtils.isEmpty(navigateImgIds)) {
                updFlag = navigateImgService.update(null,
                        Wrappers.<NavigateImg>lambdaUpdate().in(NavigateImg::getId, navigateImgIds).set(NavigateImg::getUpdatedTime, LocalDateTime.now()).set(NavigateImg::getStatus, status));
                if (!updFlag) {

                    log.error("NavigateServiceImpl.updateModuleStatus update navigateImg status fail. {}", moduleId);
                    throw new CommonModuleException("坑位组件状态更新失败");
                }
            }
            // 更新按钮链接状态
            if (!CollectionUtils.isEmpty(linkIds)) {
                updFlag = linkService.update(null,
                        Wrappers.<Link>lambdaUpdate().in(Link::getId, linkIds).set(Link::getUpdatedTime, LocalDateTime.now()).set(Link::getStatus, status));
                if (!updFlag) {
                    log.error("NavigateServiceImpl.updateModuleStatus update navigateImg link status fail. {}", moduleId);
                    throw new CommonModuleException("坑位组件状态更新失败");
                }
            }
        }

        return true;
    }

    /**
     * 批量更新组件状态
     *
     * @param moduleIds 组件id
     * @param status   版本状态
     * @return
     */
    @Override
    public Boolean batchUpdateModuleStatus(List<Long> moduleIds, Integer status){
        return batchUpdateModuleStatusAndVersion(moduleIds,status,null);
    }

    /**
     * 批量更新组件状态
     *
     * @param moduleIds 组件id
     * @param status   版本状态
     * @return
     */
    @Override
    public Boolean batchUpdateModuleStatusAndVersion(List<Long> moduleIds, Integer status,Long versionId){
        List<NavigateDto> navigateDtoList = findByIdList(moduleIds,true);
        if (CollectionUtils.isEmpty(navigateDtoList)) {
            log.debug("NavigateServiceImpl.batchUpdateModuleStatus {} not exits", moduleIds);
            throw new CommonModuleException("组件不存在");
        }
        // 更新坑位状态
        LambdaUpdateWrapper<Navigate> navigateUpdateWrapper = Wrappers.<Navigate>lambdaUpdate().in(Navigate::getId, moduleIds).set(Navigate::getStatus, status);
        if(null == versionId){
            navigateUpdateWrapper.set(Navigate::getVersion, versionId);
        }
        boolean updFlag = update(new Navigate(), navigateUpdateWrapper);
        if (!updFlag) {
            log.error("NavigateServiceImpl.batchUpdateModuleStatus update navigate status fail. {}", moduleIds);
            throw new CommonModuleException("坑位状态更新失败");
        }

        List<NavigateImgDto> navigateImgDtos = navigateDtoList.stream().flatMap(navigateDtos -> navigateDtos.getNavigateImgDtos().stream()).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(navigateImgDtos)) {
            List<Long> navigateImgIds = navigateImgDtos.stream().map(NavigateImgDto::getId).filter(Objects::nonNull).collect(Collectors.toList());
            List<Long> linkIds = navigateImgDtos.stream().map(NavigateImgDto::getLinkModuleId).filter(Objects::nonNull).collect(Collectors.toList());
            // 更新combo图片状态
            if (!CollectionUtils.isEmpty(navigateImgIds)) {
                LambdaUpdateWrapper<NavigateImg> navigateImgUpdateWrapper = Wrappers.<NavigateImg>lambdaUpdate().in(NavigateImg::getId, navigateImgIds).set(NavigateImg::getStatus, status);
                if(null != versionId){
                    navigateImgUpdateWrapper.set(NavigateImg::getVersion,versionId);
                }
                updFlag = navigateImgService.update(new NavigateImg(), navigateImgUpdateWrapper);
                if (!updFlag) {
                    log.error("NavigateServiceImpl.batchUpdateModuleStatus update navigate status fail. {}", navigateImgIds);
                    throw new CommonModuleException("坑位状态更新失败");
                }
            }
            // 更新按钮链接状态
            if (!CollectionUtils.isEmpty(linkIds)) {
                LambdaUpdateWrapper<Link> linkUpdateWrapper = Wrappers.<Link>lambdaUpdate().in(Link::getId, linkIds).set(Link::getStatus, status);
                if(null != linkUpdateWrapper){
                    linkUpdateWrapper.set(Link::getVersion, versionId);
                }
                updFlag = linkService.update(new Link(), linkUpdateWrapper);
                if (!updFlag) {
                    log.error("NavigateServiceImpl.batchUpdateModuleStatus update navigate link status fail. {}", linkIds);
                    throw new CommonModuleException("坑位状态更新失败");
                }
            }
        }

        return true;
    }

    @Override
    public Boolean restoreModule(Collection<Long> ids,Long versionId) {
        return batchUpdateModuleStatusAndVersion(new ArrayList<>(ids), CommonConstant.STATUS.DRAFT.getCode(),versionId);
    }

    @Override
    public String findPageModuleById(Long id) {
        NavigateDto navigateDto = findById(id,false);
        return navigateDto == null || CollectionUtils.isEmpty(navigateDto.getNavigateImgDtos())? "" : navigateDto.getWebJson();
    }

    @Override
    public List<String> findPageModuleByIdList(List<Long> ids) {
        List<NavigateDto> navigateDtos = findByIdList(ids,false);
        return CollectionUtils.isEmpty(navigateDtos) ? new ArrayList<>() :
                navigateDtos.stream().map(NavigateDto::getWebJson).collect(Collectors.toList());
    }

    @TransactionalForException
    @Override
    public List<CompareBo> compare(String webJson, Long navigateId) {
        List<CompareBo> ret = new ArrayList<>();
        if (StringUtils.isEmpty(webJson) && navigateId == null) {
            log.debug("NavigateServiceImpl.compare param is null");
            return ret;
        }

        if (StringUtils.isEmpty(webJson)) { // 组件被删除
            NavigateDto oldNavigateDto = findById(navigateId,false);
            if (oldNavigateDto == null) {
                log.debug("NavigateServiceImpl.compare {} not exits", navigateId);
                throw new CommonModuleException("坑位组件不存在");
            }
            String navigateWebJson = oldNavigateDto.getWebJson();
            Assert.isTrue(!StringUtils.isEmpty(navigateWebJson), "组件webJson数据异常");

            WebJsonBo webJsonBo = JSONObject.parseObject(navigateWebJson, WebJsonBo.class);
            ret.add(new CompareBo(null, CommonConstant.ModuleType.NAVIGATION, "", OP_DEL, NAVIGATE_STYLE, "",
                    NAVIGATE_LAYOUT_TYPE_MAP.getOrDefault(webJsonBo.getLayoutType(), "")));

            for (WebJsonBo.WebJsonImgBo img : webJsonBo.getData().getImgList()) {
                navigateOpRecord(ret, OP_DEL, null, img, null);
            }
        } else { // 组件被更新
            Assert.isTrue(!StringUtils.isEmpty(webJson), "参数不能为空");
            WebJsonBo webJsonBo = JSON.parseObject(webJson, WebJsonBo.class);
            Assert.notNull(webJsonBo.getData(), "参数不能为空");
            Assert.notEmpty(webJsonBo.getData().getImgList(), "图片列表不能为空");

            List<Long> updNavigateImgIds = new ArrayList<>(); // 存放本次更新的图片集合

            Integer layoutType = webJsonBo.getLayoutType();

            if (navigateId == null) { // 新增轮播图组件

                ret.add(new CompareBo(null, CommonConstant.ModuleType.NAVIGATION, "", OP_ADD,NAVIGATE_STYLE, "",
                        NAVIGATE_LAYOUT_TYPE_MAP.getOrDefault(layoutType, "")));

                for (WebJsonBo.WebJsonImgBo img : webJsonBo.getData().getImgList()) {
                    navigateOpRecord(ret, OP_ADD, null, null, img);
                }

            } else { // 编辑banner组件
                NavigateDto oldNavigateDto = findById(navigateId,false);
                if (oldNavigateDto == null) {
                    log.error("NavigateServiceImpl.compare navigate not exits. param: {}", navigateId);
                    throw new CommonModuleException("获取坑位导航失败");
                }
                try {
                    WebJsonBo oldWebJson = JSONObject.parseObject(oldNavigateDto.getWebJson(), WebJsonBo.class);

                    if (oldWebJson.getData() != null && !CollectionUtils.isEmpty(oldWebJson.getData().getImgList())) {

                        // 比对按钮组布局
                        Integer oldLayoutType = oldWebJson.getLayoutType();
                        if (!layoutType.equals(oldLayoutType)) {
                            ret.add(new CompareBo(navigateId, CommonConstant.ModuleType.NAVIGATION, "", OP_UPD,NAVIGATE_STYLE,
                                    NAVIGATE_LAYOUT_TYPE_MAP.getOrDefault(oldLayoutType, ""),
                                    NAVIGATE_LAYOUT_TYPE_MAP.getOrDefault(layoutType, "")));
                        }

                        List<WebJsonBo.WebJsonImgBo> oldNavigateImgListWebJson = oldWebJson.getData().getImgList();
                        Map<Long, WebJsonBo.WebJsonImgBo> oldNavigateImgMap = oldNavigateImgListWebJson.stream()
                                .collect(Collectors.toMap(WebJsonBo.WebJsonImgBo::getModuleUniqueId, o -> o));

                        // 比对每个图片
                        for (WebJsonBo.WebJsonImgBo navigateImgWebJson : webJsonBo.getData().getImgList()) {
                            Long navigateImgId = navigateImgWebJson.getId();
                            Long moduleUniqueId = navigateImgWebJson.getModuleUniqueId();
                            WebJsonBo.WebJsonImgBo oldNavigateWebJson = oldNavigateImgMap.get(moduleUniqueId);
                            // 若前端传入的webJson中的按钮没有ID，那么新增按钮
                            if (navigateImgId == null || moduleUniqueId == null || oldNavigateWebJson == null) {
                                navigateOpRecord(ret, OP_ADD,null, null, navigateImgWebJson);
                                continue;
                            }
                            updNavigateImgIds.add(moduleUniqueId);

                            // 更新按钮
                            navigateOpRecord(ret, OP_UPD,navigateId, oldNavigateWebJson, navigateImgWebJson);
                        }

                        // 删除按钮
                        List<NavigateImgDto> delNavigateImgList = oldNavigateDto.getNavigateImgDtos().stream().filter(o -> !updNavigateImgIds.contains(o.getModuleUniqueId())).collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(delNavigateImgList)) {
                            for (NavigateImgDto delNavigateImgDto : delNavigateImgList) {
                                WebJsonBo.WebJsonImgBo delNavigateImgWebJson = oldNavigateImgMap.get(delNavigateImgDto.getId());
                                navigateOpRecord(ret, OP_DEL,null, delNavigateImgWebJson, null);
                            }
                        }
                    }
                } catch (JSONException e) {
                    throw new ParameterException("webJson格式异常");
                } catch (Exception e) {
                    log.error("NavigateServiceImpl.compare has exception", e);
                    throw new ParameterException("webJson解析异常");
                }
            }
        }
        return ret;
    }

    /**
     * 添加坑位操作记录
     * @param ret
     * @param opName
     * @param navigateId
     * @param oldImg
     * @param newImg
     */
    private void navigateOpRecord(List<CompareBo> ret, String opName, Long navigateId,
                                     WebJsonBo.WebJsonImgBo oldImg, WebJsonBo.WebJsonImgBo newImg) {
        String oldTitle = "", title = "";
        String oldLinkName = "", linkName = "";
        String oldLinkParam = "", linkParam = "";
        String oldNavigateImg = "", navigateImg = "";
        if (oldImg != null) {
            oldTitle = oldImg.getTitle();
            oldNavigateImg = oldImg.getImgUrl();
            // 老的链接属性
            WebJsonBo.WebJsonLinkBo oldLink = oldImg.getLink();
            if (oldLink != null) {
                Integer linkType = oldLink.getLinkType();
                if (WEB_LINK_TYPE_OUTSIDE.equals(linkType)) { // 站外链接
                    oldLinkName = oldLink.getPageUrl();
                } else { // 站内链接
                    oldLinkName = oldLink.getPageName();
                    oldLinkParam = Optional.ofNullable(oldLink.getParams()).orElse(new HashMap<>()).toString()
                            .replaceAll("\\{", "").replaceAll("}", "");
                }
            }
        }
        if (newImg != null) {
            title = newImg.getTitle();
            navigateImg = newImg.getImgUrl();
            // 老的链接属性
            WebJsonBo.WebJsonLinkBo oldLink = newImg.getLink();
            if (oldLink != null) {
                Integer linkType = oldLink.getLinkType();
                if (WEB_LINK_TYPE_OUTSIDE.equals(linkType)) { // 站外链接
                    linkName = oldLink.getPageUrl();
                } else { // 站内链接
                    linkName = oldLink.getPageName();
                    linkParam = Optional.ofNullable(oldLink.getParams()).orElse(new HashMap<>()).toString()
                            .replaceAll("\\{", "").replaceAll("}", "");
                }
            }
        }

        // 删除组件时，组件名称取旧的标题，更新和添加时取新的标题
        String moduleName = OP_DEL.equals(opName) ? oldTitle : title;

        if (OP_UPD.equals(opName)) {
            // 比对图名
            if (!Optional.ofNullable(title).orElse("").equals(oldTitle)) {
                recordNavigateOp(ret, oldTitle, title, moduleName, OP_UPD, NAVIGATE_NAME, navigateId);
            }
            // 比对图
            if (!Optional.ofNullable(navigateImg).orElse("").equals(oldNavigateImg)) {
                recordNavigateOp(ret, oldTitle, title, moduleName, OP_UPD, NAVIGATE_NAME, navigateId);
            }
            // 比对链接
            if (!Optional.ofNullable(linkName).orElse("").equals(oldLinkName)) {
                recordNavigateOp(ret, oldLinkName, linkName, moduleName, OP_UPD, LINK_PATH, navigateId);
            }
            // 比对链接参数
            if (!Optional.ofNullable(linkParam).orElse("").equals(oldLinkParam)) {
                recordNavigateOp(ret, oldLinkParam, linkParam, moduleName, OP_UPD, LINK_PARAM, navigateId);
            }
        }else{
            // 比对图名
            recordNavigateOp(ret, oldTitle, title, moduleName, opName, NAVIGATE_NAME, navigateId);
            // 比对图
            recordNavigateOp(ret, oldNavigateImg, navigateImg, moduleName, opName, NAVIGATE_IMG, navigateId);
            // 比对链接
            recordNavigateOp(ret, oldLinkName, linkName, moduleName, opName, LINK_PATH, navigateId);
            // 比对链接参数
            recordNavigateOp(ret, oldLinkParam, linkParam, moduleName, opName, LINK_PARAM, navigateId);
        }
    }

    /**
     * 记录多图操作记录
     * @param compareList 比对结果集合
     * @param oldValue  改前的值
     * @param newValue  改后的值
     * @param opName    操作
     * @param moduleId  组件ID
     * @return
     */
    private void recordNavigateOp(List<CompareBo> compareList, String oldValue, String newValue, String moduleName,
                                  String opName, String opContent, Long moduleId) {
        if (!StringUtils.isEmpty(oldValue) || !StringUtils.isEmpty(newValue)) {
            // 新增链接参数操作记录
            compareList.add(new CompareBo(null, CommonConstant.ModuleType.NAVIGATION, moduleName, opName,opContent, oldValue, newValue));
        }
    }
}
