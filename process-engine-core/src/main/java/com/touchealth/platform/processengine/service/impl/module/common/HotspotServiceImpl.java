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
import com.touchealth.platform.processengine.dao.module.common.HotspotDao;
import com.touchealth.platform.processengine.entity.module.common.*;
import com.touchealth.platform.processengine.entity.page.PageManager;
import com.touchealth.platform.processengine.exception.CommonModuleException;
import com.touchealth.platform.processengine.exception.ParameterException;
import com.touchealth.platform.processengine.handler.ModuleHandler;
import com.touchealth.platform.processengine.pojo.bo.CompareBo;
import com.touchealth.platform.processengine.pojo.bo.WebJsonBo;
import com.touchealth.platform.processengine.pojo.bo.module.common.HotspotBo;
import com.touchealth.platform.processengine.pojo.bo.module.common.HotspotPartsBo;
import com.touchealth.platform.processengine.pojo.dto.module.common.*;
import com.touchealth.platform.processengine.service.impl.module.BaseModuleServiceImpl;
import com.touchealth.platform.processengine.service.module.common.HotspotPartsService;
import com.touchealth.platform.processengine.service.module.common.HotspotService;
import com.touchealth.platform.processengine.service.module.common.LinkService;
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
import java.util.*;
import java.util.stream.Collectors;

import static com.touchealth.platform.processengine.constant.CompareConstant.*;
import static com.touchealth.platform.processengine.constant.WebJsonConstant.WEB_LINK_TYPE_OUTSIDE;

/**
 * <p>
 * 热区组件表 服务实现类
 * </p>
 *
 * @author LJH
 * @since 2020-11-25
 */
@Service
@Slf4j
public class HotspotServiceImpl extends BaseModuleServiceImpl<HotspotDao, Hotspot> implements HotspotService {

    @Resource
    private LinkService linkService;

    @Resource
    private HotspotPartsService hotspotPartsService;

    @Resource
    private PageManagerService pageManagerService;

    @Resource
    private PlatformVersionService platformVersionService;

    @TransactionalForException
    @Override
    public HotspotDto save(HotspotBo bo) {
        List<HotspotPartsBo> hotspotPartsBos = bo.getHotspotPartsBos();
        // 添加热区组件
        bo.setId(null);
        Hotspot hotspot = BaseHelper.r2t(bo,Hotspot.class);
        hotspot.setCategoryId(CommonConstant.MODULE_CATEGORY.COMMON.getCode());
        boolean saveHotspotFlag = save(hotspot);
        if (!saveHotspotFlag) {
            log.error("HotspotServiceImpl.save hotspot group save fail. param: {}",bo);
            throw new CommonModuleException("添加热区组件失败");
        }
        HotspotDto hotspotDto = BaseHelper.r2t(hotspot,HotspotDto.class);
        List<HotspotParts> hotspotPartsList = null;
        if(!CollectionUtils.isEmpty(hotspotPartsBos)) {
            hotspotDto.setHotspotPartsDtos(new ArrayList<>(hotspotPartsBos.size()));
            // 添加热区
            hotspotPartsList = new ArrayList<>(hotspotPartsBos.size());
            for (HotspotPartsBo hot : hotspotPartsBos) {
                hot.setId(null);
                HotspotParts hotspotParts = BaseHelper.r2t(hot, HotspotParts.class);
                hotspotParts.setChannelNo(hotspot.getChannelNo());
                hotspotParts.setHotspotId(hotspot.getId());
                hotspotParts.setPageId(hotspot.getPageId());
                hotspotParts.setVersion(hotspot.getVersion());
                hotspotParts.setStatus(hotspot.getStatus());
                LinkDto linkDto = hot.getLinkDto();
                if (linkDto != null) {
                    // 添加链接
                    linkDto.setChannelNo(hotspot.getChannelNo());
                    linkDto.setPageId(hotspot.getPageId());
                    linkDto.setStatus(hotspot.getStatus());
                    linkDto.setVersion(hotspot.getVersion());
                    LinkDto linkDto1 = linkService.save(linkDto);
                    Long linkId = linkDto1.getId();
                    hotspotParts.setLinkModuleId(linkId);
                }
                hotspotPartsList.add(hotspotParts);
                hotspotDto.getHotspotPartsDtos().add(BaseHelper.r2t(hotspotParts, HotspotPartsDto.class));
            }
            boolean saveHotImgFlag = hotspotPartsService.saveBatch(hotspotPartsList);
            if (!saveHotImgFlag) {
                log.error("HotspotServiceImpl save fail. param: {}", bo);
                throw new CommonModuleException("报存热区失败");
            }
        }
        // 再次保存热区组件，为了更新热区组件中的webJson里的热区组ID和图片ID
        WebJsonBo hotspotWebJson = JSONObject.parseObject(hotspot.getWebJson(), WebJsonBo.class);
        if (hotspotWebJson != null) {
            // 更新热区组件的webJson字段中的按钮组ID
            hotspotWebJson.setId(hotspot.getId());
            hotspotWebJson.setModuleUniqueId(hotspot.getModuleUniqueId());
            if (hotspotWebJson.getData() != null && !CollectionUtils.isEmpty(hotspotWebJson.getData().getHotspotList())) {
                updateWebJson(hotspotPartsList, hotspotWebJson);
            }
            hotspot.setWebJson(JSONObject.toJSONString(hotspotWebJson));
            if (!saveOrUpdate(hotspot)) {
                log.error("HotspotServiceImpl.save update hotspot webJson fail. param: {}", bo);
                throw new CommonModuleException("添加多图组件失败");
            }
            hotspotDto.setWebJson(JSONObject.toJSONString(hotspotWebJson));
        }
        return hotspotDto;
    }

    private void updateWebJson(List<HotspotParts> hotspotPartsList, WebJsonBo hotspotWebJson) {
        if(CollectionUtils.isEmpty(hotspotPartsList)){
            return;
        }
        List<WebJsonBo.HotspotPartsBo> hotspotPartsWebJsonList = hotspotWebJson.getData().getHotspotList();
        for (int i = 0; i < hotspotPartsWebJsonList.size() && hotspotPartsWebJsonList.size() == hotspotPartsList.size(); i++) {
            // 更新图片的webJson字段中的按钮ID
            WebJsonBo.HotspotPartsBo webJsonPartsBo = hotspotPartsWebJsonList.get(i);
            HotspotParts hotspotParts = hotspotPartsList.get(i);
            webJsonPartsBo.setId(hotspotParts.getId());
            webJsonPartsBo.setModuleUniqueId(hotspotParts.getModuleUniqueId());
            // 更新按钮链接ID
            WebJsonBo.WebJsonLinkBo link = webJsonPartsBo.getLink();
            if (link != null) {
                link.setId(hotspotParts.getLinkModuleId());
            }
        }
    }

    @TransactionalForException
    @Override
    public String update(HotspotBo bo) {
        Long hotspotId = bo.getId();
        List<HotspotPartsBo> hotspotPartsBos = bo.getHotspotPartsBos();

        // 更新热区组
        Hotspot hotspot = getById(hotspotId);
        if (hotspot == null) {
            throw new CommonModuleException("热区组件不存在");
        }
        BaseHelper.copyNotNullProperties(bo, hotspot);
        if (!updateById(hotspot)) {
            log.error("HotspotServiceImpl.update hot group update fail. param: {}", bo);
            throw new CommonModuleException("更新热区失败");
        }

        List<HotspotParts> addOrUpdHotImgList = new ArrayList<>();
        // 更新图片
        if (!CollectionUtils.isEmpty(hotspotPartsBos)) {
            List<Long> delHotList = new ArrayList<>();
            Map<Long, HotspotParts> updHotImgMap = new HashMap<>();
            QueryWrapper<HotspotParts> qw = Wrappers.<HotspotParts>query()
                    .eq("hotspot_id", hotspotId);
            List<HotspotParts> hotspotPartsDbs = hotspotPartsService.baseFindList(qw);

            // 将所有需要更新的按钮放到一个Map中
            for (HotspotPartsBo hotspotPartsBo : hotspotPartsBos) {
                Long hotspotPartsId = hotspotPartsBo.getId();
                HotspotParts hotspotParts = BaseHelper.r2t(hotspotPartsBo, HotspotParts.class);
                hotspotParts.setChannelNo(hotspot.getChannelNo());
                hotspotParts.setVersion(hotspot.getVersion());
                hotspotParts.setHotspotId(hotspotId);
                hotspotParts.setPageId(hotspot.getPageId());
                hotspotParts.setStatus(hotspot.getStatus());

                LinkDto linkDto = hotspotPartsBo.getLinkDto();
                if (linkDto != null) {
                    // 添加|更新 链接
                    linkDto.setChannelNo(hotspot.getChannelNo());
                    linkDto.setPageId(hotspot.getPageId());
                    linkDto.setStatus(hotspot.getStatus());
                    linkDto.setVersion(hotspot.getVersion());
                    if (linkDto.getId() == null) {
                        LinkDto linkDto1 = linkService.save(linkDto);
                        Long linkId = linkDto1.getId();
                        hotspotParts.setLinkModuleId(linkId);
                    } else {
                        LinkDto updLink = linkService.update(linkDto);
                        if (updLink == null) {
                            log.error("HotspotServiceImpl.update hot group update link fail. param: {}", linkDto);
                        }
                    }
                }
                // 新增或修改的图片
                addOrUpdHotImgList.add(hotspotParts);
                if (hotspotPartsId != null) {
                    // 需要更新的图片
                    updHotImgMap.put(hotspotPartsId, hotspotParts);
                }
            }
            // 将数据库中存在编辑后不存在的图片删除
            hotspotPartsDbs.forEach(hotspotPartsDb -> {
                Long id = hotspotPartsDb.getId();
                HotspotParts hotspotParts = updHotImgMap.get(id);
                if (hotspotParts == null) {
                    delHotList.add(hotspotPartsDb.getId());
                }
            });
            boolean updHotFlag = hotspotPartsService.saveOrUpdateBatch(addOrUpdHotImgList);
            // 更新为回收站状态
            boolean delHotFlag = true;
            if(!CollectionUtils.isEmpty(delHotList)){
                delHotFlag = hotspotPartsService.update(null,
                        Wrappers.<HotspotParts>lambdaUpdate().in(HotspotParts::getId, delHotList).set(HotspotParts::getUpdatedTime, LocalDateTime.now()).set(HotspotParts::getDeletedFlag, 1));
            }
            if (!updHotFlag || !delHotFlag) {
                log.error("HotspotServiceImpl.update hot update fail. param: {}", bo);
                throw new CommonModuleException("更新热区组件失败");
            }
        }

        // 更新webJson
        WebJsonBo hotspotWebJson = JSONObject.parseObject(hotspot.getWebJson(), WebJsonBo.class);
        updateWebJson(addOrUpdHotImgList, hotspotWebJson);
        hotspot.setWebJson(JSONObject.toJSONString(hotspotWebJson));
        if (!saveOrUpdate(hotspot)) {
            log.error("HotspotServiceImpl.save update hotspot webJson fail. param: {}", bo);
            throw new CommonModuleException("更新热区组件失败");
        }
        return JSONObject.toJSONString(hotspotWebJson);
    }

    @Override
    public HotspotDto findById(Long id,Boolean showRecycleBin) {
        Hotspot hotspot = getById(id);
        if (hotspot == null) {
            return null;
        }
        HotspotDto hotspotDto = BaseHelper.r2t(hotspot, HotspotDto.class);
        LambdaQueryWrapper<HotspotParts> query = Wrappers.<HotspotParts>lambdaQuery().eq(HotspotParts::getHotspotId, id).eq(HotspotParts::getDeletedFlag,0).notIn(HotspotParts::getStatus, Collections.singletonList(2)).orderByAsc(HotspotParts::getSort);
        if(showRecycleBin){
            query = Wrappers.<HotspotParts>lambdaQuery().eq(HotspotParts::getHotspotId, id).eq(HotspotParts::getDeletedFlag,0).orderByAsc(HotspotParts::getSort);
        }
        List<HotspotParts> hotspotParts = hotspotPartsService.getBaseMapper().selectList(query);
        if(CollectionUtils.isEmpty(hotspotParts)){
            return hotspotDto;
        }
        Map<Long, WebJsonBo.HotspotPartsBo> hotspotStyleMap = getLongHotspotStyleMap(hotspot.getWebJson());
        List<HotspotPartsDto> hotspotPartsDtos = hotspotParts.stream().map(o -> BaseHelper.r2t(o, HotspotPartsDto.class)).collect(Collectors.toList());
        hotspotDto.setHotspotPartsDtos(hotspotPartsDtos);
        // 获取热区位置和链接
        hotspotPartsDtos.forEach(p -> {
            WebJsonBo.HotspotPartsBo style = hotspotStyleMap.get(p.getId());
            if(null != style){
                p.setStyle(style);
            }
        });
        return hotspotDto;
    }

    private Map<Long, WebJsonBo.HotspotPartsBo> getLongHotspotStyleMap(String webJson) {
        WebJsonBo webJsonBo = JSONObject.parseObject(webJson, WebJsonBo.class);
        List<WebJsonBo.HotspotPartsBo> hotspotStyles = webJsonBo.getData().getHotspotList();
        return hotspotStyles.stream().collect(Collectors.toMap(WebJsonBo.HotspotPartsBo::getId, style -> style));
    }

    @Override
    public List<HotspotDto> findByIdList(List<Long> ids,Boolean showRecycleBin) {
        List<HotspotDto> hotspotDtos = new ArrayList<>();
        List<Hotspot> hotspots = listByIds(ids);
        if (CollectionUtils.isEmpty(hotspots)) {
            return hotspotDtos;
        }

        Map<Long, List<HotspotParts>> hotspotPartsMap = new HashMap<>();
        QueryWrapper<HotspotParts> query = Wrappers.<HotspotParts>query().in("hotspot_id", ids).notIn("status", Collections.singletonList(2));
        if(showRecycleBin){
            query = Wrappers.<HotspotParts>query().in("hotspot_id", ids);
        }
        List<HotspotParts> hotspotPartsList = hotspotPartsService.baseFindList(query);
        if (!CollectionUtils.isEmpty(hotspotPartsList)) {
            hotspotPartsMap = hotspotPartsList.stream().collect(Collectors.toMap(
                    HotspotParts::getHotspotId,
                    o -> {
                        List<HotspotParts> tmpArr = new ArrayList<>();
                        tmpArr.add(o);
                        return tmpArr;
                    },
                    (ov, nv) -> {
                        ov.addAll(nv);
                        return ov;
                    }));
        }
        for (Hotspot hotspot : hotspots) {
            HotspotDto hotspotDto = BaseHelper.r2t(hotspot, HotspotDto.class);
            Map<Long, WebJsonBo.HotspotPartsBo> hotspotStyleMap = getLongHotspotStyleMap(hotspotDto.getWebJson());
            Long hotspotDtoId = hotspotDto.getId();
            List<HotspotParts> hotspotPartsById = hotspotPartsMap.get(hotspotDtoId);
            if(CollectionUtils.isEmpty(hotspotPartsById)){
                continue;
            }
            List<HotspotPartsDto> hotspotPartsDtos = hotspotPartsById.stream().map(o -> BaseHelper.r2t(o, HotspotPartsDto.class)).collect(Collectors.toList());
            hotspotDto.setHotspotPartsDtos(hotspotPartsDtos);
            // 设置样式
            hotspotPartsDtos.forEach(p -> {
                WebJsonBo.HotspotPartsBo style = hotspotStyleMap.get(p.getId());
                if(null != style){
                    p.setStyle(style);
                }
            });
            hotspotDtos.add(hotspotDto);
        }

        return hotspotDtos;
    }

    @TransactionalForException
    @Override
    public Boolean delete(Long id) {
        HotspotDto hotspotDto = findById(id,true);
        if (hotspotDto == null) {
            return false;
        }
        boolean delHotspotFlag = removeById(id);
        if (!delHotspotFlag) {
            return false;
        }
        List<HotspotPartsDto> hotspotPartsDtos = hotspotDto.getHotspotPartsDtos();
        return deleteHotspotPartsDetailAndLink(hotspotPartsDtos);
    }

    @TransactionalForException
    @Override
    public Boolean delete(List<Long> ids) {
        List<HotspotDto> hotspotDtos = findByIdList(ids,true);
        if (CollectionUtils.isEmpty(hotspotDtos)) {
            return false;
        }
        boolean delHotspotDtoFlag = removeByIds(ids);
        if (!delHotspotDtoFlag) {
            return false;
        }
        List<HotspotPartsDto> hotspotPartsDtos = hotspotDtos.stream().flatMap(hotspotDto -> hotspotDto.getHotspotPartsDtos().stream()).collect(Collectors.toList());
        return deleteHotspotPartsDetailAndLink(hotspotPartsDtos);
    }

    private Boolean deleteHotspotPartsDetailAndLink(List<HotspotPartsDto> hotspotPartsDtos) {
        if (!CollectionUtils.isEmpty(hotspotPartsDtos)) {
            List<Long> hotspotPartsIds = hotspotPartsDtos.stream().map(HotspotPartsDto::getId).filter(o -> o != null).distinct().collect(Collectors.toList());
            List<Long> linkIds = hotspotPartsDtos.stream().map(HotspotPartsDto::getLinkModuleId).filter(o -> o != null).distinct().collect(Collectors.toList());
            boolean delFlag = hotspotPartsService.removeByIds(hotspotPartsIds);
            if (!delFlag) {
                log.error("HotspotServiceImpl.deleteModule hotspot delete fail. param: {}", hotspotPartsIds);
                throw new CommonModuleException("删除热区失败");
            }
            if (!CollectionUtils.isEmpty(linkIds) && !linkService.delete(linkIds)) {
                log.error("HotspotServiceImpl.deleteModule hotspot link delete fail. param: {}", linkIds);
                throw new CommonModuleException("删除热区失败");
            }
        }
        return null;
    }

    @TransactionalForException
    @Override
    public String savePageModule(String webJson, Long pageId) {
        PageManager page = pageManagerService.getById(pageId);
        Assert.notNull(page, "页面不存在");
        HotspotBo comboImgBo = ModuleHandler.parseHotspot(page, webJson);
        HotspotDto comboImgDto = save(comboImgBo);
        return comboImgDto.getWebJson();
    }

    @TransactionalForException
    @Override
    public String clonePageModule(Long moduleId, Long pageId) {
        PageManager page = pageManagerService.getById(pageId);

        Assert.notNull(page, "页面不存在");
        Long versionId = page.getVersionId();

        Assert.notNull(platformVersionService.getById(versionId), "无效的版本号");

        Hotspot hotspot = getById(moduleId);
        Assert.notNull(hotspot, "多图组件不存在");

        // 更新前端数据字符串（webJson）
        String webJson = hotspot.getWebJson();
        if (StringUtils.isEmpty(webJson)) {
            return "";
        }


        return savePageModule(webJson, pageId);
    }

    @TransactionalForException
    @Override
    public String updatePageModule(String webJson) {
        return update(ModuleHandler.parseHotspot(null,webJson));
    }

    @TransactionalForException
    @Override
    public Boolean updateModuleStatus(Long moduleId, Integer status) {
        HotspotDto hotspotDto = findById(moduleId,true);
        if (hotspotDto == null) {
            log.debug("HotspotServiceImpl.updateModuleStatus {} not exits", moduleId);
            throw new CommonModuleException("组件不存在");
        }
        // 更新按钮组状态
        Hotspot updHotspot = new Hotspot();
        updHotspot.setId(hotspotDto.getId());
        updHotspot.setStatus(status);
        boolean updFlag = updateById(updHotspot);
        if (!updFlag) {
            log.error("HotspotServiceImpl.updateModuleStatus update comboImg group status fail. {}", moduleId);
            throw new CommonModuleException("热区组件状态更新失败");
        }

        List<HotspotPartsDto> hotspotPartsDtos = hotspotDto.getHotspotPartsDtos();
        if (!CollectionUtils.isEmpty(hotspotPartsDtos)) {
            List<Long> hotspotPartsIds = hotspotPartsDtos.stream().map(HotspotPartsDto::getId).collect(Collectors.toList());
            List<Long> linkIds = hotspotPartsDtos.stream().map(HotspotPartsDto::getLinkModuleId).filter(Objects::nonNull).collect(Collectors.toList());
            // 更新热区状态
            if (!CollectionUtils.isEmpty(hotspotPartsIds)) {
                updFlag = hotspotPartsService.update(null,
                        Wrappers.<HotspotParts>lambdaUpdate().in(HotspotParts::getId, hotspotPartsIds).set(HotspotParts::getUpdatedTime, LocalDateTime.now()).set(HotspotParts::getStatus, status));
                if (!updFlag) {

                    log.error("HotspotServiceImpl.updateModuleStatus update hotspot status fail. {}", moduleId);
                    throw new CommonModuleException("热区组件状态更新失败");
                }
            }
            // 更新按钮链接状态
            if (!CollectionUtils.isEmpty(linkIds)) {
                updFlag = linkService.update(null,
                        Wrappers.<Link>lambdaUpdate().in(Link::getId, linkIds).set(Link::getUpdatedTime, LocalDateTime.now()).set(Link::getStatus, status));
                if (!updFlag) {
                    log.error("HotspotServiceImpl.updateModuleStatus update hotspot link status fail. {}", moduleId);
                    throw new CommonModuleException("热区组件状态更新失败");
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
        List<HotspotDto> hotspotDtoList = findByIdList(moduleIds,true);
        if (CollectionUtils.isEmpty(hotspotDtoList)) {
            log.debug("HotspotServiceImpl.batchUpdateModuleStatus {} not exits", moduleIds);
            throw new CommonModuleException("组件不存在");
        }
        // 更新热区状态
        LambdaUpdateWrapper<Hotspot> hotspotUpdateWrapper = Wrappers.<Hotspot>lambdaUpdate().in(Hotspot::getId, moduleIds).set(Hotspot::getStatus, status);
        if(null != versionId){
            hotspotUpdateWrapper.set(Hotspot::getVersion, versionId);
        }
        boolean updFlag = update(new Hotspot(),hotspotUpdateWrapper );
        if (!updFlag) {
            log.error("HotspotServiceImpl.batchUpdateModuleStatus update hotspot status fail. {}", moduleIds);
            throw new CommonModuleException("多图状态更新失败");
        }

        List<HotspotPartsDto> hotspotPartsDtos = hotspotDtoList.stream().flatMap(hotspotDtos -> hotspotDtos.getHotspotPartsDtos().stream()).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(hotspotPartsDtos)) {
            List<Long> hotspotPartsIds = hotspotPartsDtos.stream().map(HotspotPartsDto::getId).filter(Objects::nonNull).collect(Collectors.toList());
            List<Long> linkIds = hotspotPartsDtos.stream().map(HotspotPartsDto::getLinkModuleId).filter(Objects::nonNull).collect(Collectors.toList());
            // 更新热区状态
            if (!CollectionUtils.isEmpty(hotspotPartsIds)) {
                LambdaUpdateWrapper<HotspotParts> hotspotPartsUpdateWrapper = Wrappers.<HotspotParts>lambdaUpdate().in(HotspotParts::getId, hotspotPartsIds).set(HotspotParts::getStatus, status);
                if(null != versionId){
                    hotspotPartsUpdateWrapper.set(HotspotParts::getVersion,versionId);
                }
                updFlag = hotspotPartsService.update(new HotspotParts(), hotspotPartsUpdateWrapper);
                if (!updFlag) {
                    log.error("HotspotServiceImpl.batchUpdateModuleStatus update hotspot status fail. {}", hotspotPartsIds);
                    throw new CommonModuleException("多图状态更新失败");
                }
            }
            // 更新按钮链接状态
            if (!CollectionUtils.isEmpty(linkIds)) {
                LambdaUpdateWrapper<Link> linkUpdateWrapper = Wrappers.<Link>lambdaUpdate().in(Link::getId, linkIds).set(Link::getStatus, status);
                if(null != versionId){
                    linkUpdateWrapper.set(Link::getVersion,versionId);
                }
                updFlag = linkService.update(new Link(),linkUpdateWrapper );
                if (!updFlag) {
                    log.error("HotspotServiceImpl.batchUpdateModuleStatus update hotspot link status fail. {}", linkIds);
                    throw new CommonModuleException("多图状态更新失败");
                }
            }
        }

        return true;
    }

    @TransactionalForException
    @Override
    public Boolean deletePageModule(List<Long> ids) {
        return delete(ids);
    }

    public Boolean recycleBinModule(Set<Long> ids) {
        return batchUpdateModuleStatus(new ArrayList<>(ids), CommonConstant.STATUS.DRAFT.getCode());
    }

    @Override
    public Boolean restoreModule(List<Long> ids) {
        return batchUpdateModuleStatus(new ArrayList<>(ids), CommonConstant.STATUS.DRAFT.getCode());
    }

    @Override
    public Boolean restoreModule(Collection<Long> ids,Long versionId) {
        return batchUpdateModuleStatusAndVersion(new ArrayList<>(ids), CommonConstant.STATUS.DRAFT.getCode(),versionId);
    }


    @Override
    public String findPageModuleById(Long id) {
        HotspotDto hotspotDto = findById(id,false);
        return hotspotDto == null || CollectionUtils.isEmpty(hotspotDto.getHotspotPartsDtos())? "" : hotspotDto.getWebJson();
    }

    @Override
    public List<String> findPageModuleByIdList(List<Long> ids) {
        List<HotspotDto> hotspotDtos = findByIdList(ids,false);
        return CollectionUtils.isEmpty(hotspotDtos) ? new ArrayList<>() :
                hotspotDtos.stream().map(HotspotDto::getWebJson).collect(Collectors.toList());
    }

    @TransactionalForException
    @Override
    public List<CompareBo> compare(String webJson, Long hotspotId) {
        List<CompareBo> ret = new ArrayList<>();
        if (StringUtils.isEmpty(webJson) && hotspotId == null) {
            log.debug("HotspotServiceImpl.compare param is null");
            return ret;
        }

        if (StringUtils.isEmpty(webJson)) { // 组件被删除
            HotspotDto oldHotspotDto = findById(hotspotId,false);
            if (oldHotspotDto == null) {
                log.debug("HotspotServiceImpl.compare {} not exits", hotspotId);
                throw new CommonModuleException("热区组件不存在");
            }
            String hotspotWebJson = oldHotspotDto.getWebJson();
            Assert.isTrue(!StringUtils.isEmpty(hotspotWebJson), "组件webJson数据异常");

            WebJsonBo webJsonBo = JSONObject.parseObject(hotspotWebJson, WebJsonBo.class);
            for (WebJsonBo.HotspotPartsBo hotspotPartsBo : webJsonBo.getData().getHotspotList()) {
                hotspotOpRecord(ret, OP_DEL, null, hotspotPartsBo, null);
            }
        } else { // 组件被更新
            Assert.isTrue(!StringUtils.isEmpty(webJson), "参数不能为空");
            WebJsonBo webJsonBo = JSON.parseObject(webJson, WebJsonBo.class);
            Assert.notNull(webJsonBo.getData(), "参数不能为空");

            List<Long> updHotspotPartsIds = new ArrayList<>(); // 存放本次更新的热区集合

            WebJsonBo.WebJsonDataBo hotspot = webJsonBo.getData();
            if (hotspotId == null) { // 新增热区
                recordHotspotOp(ret, "", hotspot.getTitle(), hotspot.getTitle(), OP_ADD,HOTSPOT_NAME, null);
                recordHotspotOp(ret, "", hotspot.getImgUrl(), hotspot.getTitle(), OP_ADD,HOTSPOT_IMG, null);
                for (WebJsonBo.HotspotPartsBo hotspotPartsBo : hotspot.getHotspotList()) {
                    hotspotOpRecord(ret, OP_ADD, null, null, hotspotPartsBo);
                }
            } else { // 编辑热区组件
                HotspotDto oldHotspotDto = findById(hotspotId,false);
                if (oldHotspotDto == null) {
                    log.error("HotspotServiceImpl.compare hotspot not exits. param: {}", hotspotId);
                    throw new CommonModuleException("获取热区失败");
                }
                try {
                    WebJsonBo oldWebJson = JSONObject.parseObject(oldHotspotDto.getWebJson(), WebJsonBo.class);

                    if (oldWebJson.getData() != null && !CollectionUtils.isEmpty(oldWebJson.getData().getHotspotList())) {
                        // 对比热区标题和图片
                        if (!Optional.ofNullable(hotspot.getTitle()).orElse("").equals(oldWebJson.getData().getTitle())) {
                            recordHotspotOp(ret, oldWebJson.getData().getTitle(), hotspot.getTitle(), hotspot.getTitle(), OP_UPD, HOTSPOT_NAME, oldWebJson.getId());
                        }
                        if (!Optional.ofNullable(hotspot.getImgUrl()).orElse("").equals(oldWebJson.getData().getImgUrl())) {
                            recordHotspotOp(ret, oldWebJson.getData().getImgUrl(), hotspot.getImgUrl(), hotspot.getTitle(), OP_UPD, HOTSPOT_IMG, oldWebJson.getId());
                        }
                        List<WebJsonBo.HotspotPartsBo> oldPartListWebJson = oldWebJson.getData().getHotspotList();
                        Map<Long, WebJsonBo.HotspotPartsBo> oldComboImgDetailMap = oldPartListWebJson.stream()
                                .collect(Collectors.toMap(WebJsonBo.HotspotPartsBo::getModuleUniqueId, o -> o));

                        // 比对每个热区
                        for (WebJsonBo.HotspotPartsBo hotspotPartsBo : webJsonBo.getData().getHotspotList()) {
                            Long hotspotPartsId = hotspotPartsBo.getId();
                            Long moduleUniqueId = hotspotPartsBo.getModuleUniqueId();
                            WebJsonBo.HotspotPartsBo oldHotspotPartsBo = oldComboImgDetailMap.get(moduleUniqueId);
                            // 若前端传入的webJson中的按钮没有ID，那么新增按钮
                            if (hotspotPartsId == null || moduleUniqueId == null || oldHotspotPartsBo == null) {
                                hotspotOpRecord(ret, OP_ADD, null, null, hotspotPartsBo);
                                continue;
                            }
                            updHotspotPartsIds.add(moduleUniqueId);

                            // 更新标题
                            hotspotOpRecord(ret, OP_UPD, hotspotId, oldHotspotPartsBo, hotspotPartsBo);
                        }

                        // 删除热区
                        List<HotspotPartsDto> delHotspotPartsList = oldHotspotDto.getHotspotPartsDtos().stream().filter(o -> !updHotspotPartsIds.contains(o.getModuleUniqueId())).collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(delHotspotPartsList)) {
                            for (HotspotPartsDto delHotspotPartsDto : delHotspotPartsList) {
                                WebJsonBo.HotspotPartsBo delHotspotPartWebJson = oldComboImgDetailMap.get(delHotspotPartsDto.getId());
                                hotspotOpRecord(ret, OP_DEL, hotspotId, delHotspotPartWebJson, null);
                            }
                        }
                    }else if(CollectionUtils.isEmpty(oldWebJson.getData().getHotspotList())){
                        // 新增热区
                        // 比对每个热区
                        for (WebJsonBo.HotspotPartsBo hotspotPartsBo : webJsonBo.getData().getHotspotList()) {
                            hotspotOpRecord(ret, OP_ADD, null, null, hotspotPartsBo);
                        }
                    }
                } catch (JSONException e) {
                    throw new ParameterException("webJson格式异常");
                } catch (Exception e) {
                    log.error("HotspotServiceImpl.compare has exception", e);
                    throw new ParameterException("webJson解析异常");
                }
            }
        }
        return ret;
    }

    /**
     * 添加多图操作记录
     * @param ret
     * @param opName
     * @param hotspotId
     * @param oldHotspotPartsBo
     * @param newHotspotPartsBo
     */
    private void hotspotOpRecord(List<CompareBo> ret, String opName, Long hotspotId,
                               WebJsonBo.HotspotPartsBo oldHotspotPartsBo, WebJsonBo.HotspotPartsBo newHotspotPartsBo) {
        String oldTitle = "", title = "";
        String oldLinkName = "", linkName = "";
        String oldLinkParam = "", linkParam = "";
        if (oldHotspotPartsBo != null) {
            oldTitle = oldHotspotPartsBo.getTitle();
            // 老的链接属性
            WebJsonBo.WebJsonLinkBo oldLink = oldHotspotPartsBo.getLink();
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
        if (newHotspotPartsBo != null) {
            title = newHotspotPartsBo.getTitle();
            // 老的链接属性
            WebJsonBo.WebJsonLinkBo oldLink = newHotspotPartsBo.getLink();
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
                recordHotspotOp(ret, oldTitle, title, moduleName, OP_UPD, HOTSPOT_NAME, hotspotId);
            }
            // 比对链接
            if (!Optional.ofNullable(linkName).orElse("").equals(oldLinkName)) {
                recordHotspotOp(ret, oldLinkName, linkName, moduleName, OP_UPD, LINK_PATH, hotspotId);
            }
            // 比对链接参数
            if (!Optional.ofNullable(linkParam).orElse("").equals(oldLinkParam)) {
                recordHotspotOp(ret, oldLinkParam, linkParam, moduleName, OP_UPD, LINK_PARAM, hotspotId);
            }
        }else{
            // 比对图名
            recordHotspotOp(ret, oldTitle, title, moduleName, opName, HOTSPOT_NAME, hotspotId);
            // 比对链接
            recordHotspotOp(ret, oldLinkName, linkName, moduleName, opName, LINK_PATH, hotspotId);
            // 比对链接参数
            recordHotspotOp(ret, oldLinkParam, linkParam, moduleName, opName, LINK_PARAM, hotspotId);
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
    private void recordHotspotOp(List<CompareBo> compareList, String oldValue, String newValue, String moduleName,
                                 String opName,String opContent, Long moduleId) {
        if (!StringUtils.isEmpty(oldValue) || !StringUtils.isEmpty(newValue)) {
            // 新增链接参数操作记录
            compareList.add(new CompareBo(null, CommonConstant.ModuleType.HOTSPOT, moduleName, opName,opContent, oldValue, newValue));
        }
    }
}
