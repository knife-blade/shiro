package com.touchealth.platform.processengine.service.impl.module.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.touchealth.platform.processengine.constant.CommonConstant;
import com.touchealth.platform.processengine.constant.PageCenterConsts;
import com.touchealth.platform.processengine.utils.BaseHelper;
import com.touchealth.platform.processengine.utils.DateUtil;
import com.touchealth.platform.processengine.annotation.TransactionalForException;
import com.touchealth.platform.processengine.dao.module.common.BannerDao;
import com.touchealth.platform.processengine.entity.module.common.*;
import com.touchealth.platform.processengine.entity.page.PageManager;
import com.touchealth.platform.processengine.exception.CommonModuleException;
import com.touchealth.platform.processengine.exception.ParameterException;
import com.touchealth.platform.processengine.handler.ModuleHandler;
import com.touchealth.platform.processengine.pojo.bo.CompareBo;
import com.touchealth.platform.processengine.pojo.bo.WebJsonBo;
import com.touchealth.platform.processengine.pojo.bo.module.common.BannerBo;
import com.touchealth.platform.processengine.pojo.bo.module.common.BannerImgBo;
import com.touchealth.platform.processengine.pojo.dto.module.common.*;
import com.touchealth.platform.processengine.service.impl.module.BaseModuleServiceImpl;
import com.touchealth.platform.processengine.service.module.common.BannerImgService;
import com.touchealth.platform.processengine.service.module.common.BannerService;
import com.touchealth.platform.processengine.service.module.common.LinkService;
import com.touchealth.platform.processengine.service.page.PageManagerService;
import com.touchealth.platform.processengine.service.page.PlatformVersionService;
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
 * 轮播图组件表 服务实现类
 * </p>
 *
 * @author Xxx
 * @since 2020-11-23
 */
@Service
@Slf4j
public class BannerServiceImpl extends BaseModuleServiceImpl<BannerDao, Banner> implements BannerService  {


    @Resource
    private LinkService linkService;

    @Resource
    private BannerImgService bannerImgService;

    @Resource
    private PageManagerService pageManagerService;

    @Resource
    private PlatformVersionService platformVersionService;

    @Override
    @TransactionalForException
    public BannerDto save(BannerBo bo) {
        List<BannerImgBo> bannerImgBos = bo.getBannerImgs();
        if(CollectionUtils.isEmpty(bannerImgBos)){
            return null;
        }
        // 添加banner组件
        bo.setId(null);
        Banner banner = BaseHelper.r2t(bo,Banner.class);
        banner.setCategoryId(CommonConstant.MODULE_CATEGORY.COMMON.getCode());
        boolean saveBannerFlag = save(banner);
        if (!saveBannerFlag) {
            log.error("BannerServiceImpl.save banner group save fail. param: {}",bo);
            throw new CommonModuleException("添加banner组件失败");
        }
        BannerDto bannerDto = BaseHelper.r2t(banner,BannerDto.class);
        bannerDto.setBannerImgDtoList(new ArrayList<>(bannerImgBos.size()));
        // 添加图片
        List<BannerImg> bannerImgs = new ArrayList<>(bannerImgBos.size());
        bannerImgBos.forEach(img -> {
            img.setId(null);
            BannerImg bannerImg = BaseHelper.r2t(img,BannerImg.class);
            bannerImg.setChannelNo(banner.getChannelNo());
            bannerImg.setBannerId(banner.getId());
            bannerImg.setPageId(banner.getPageId());
            bannerImg.setVersion(banner.getVersion());
            bannerImg.setStatus(banner.getStatus());
            bannerImg.setUrl(img.getUrl());
            bannerImg.setShowStartTime(DateUtil.getLocalDateTime(img.getShowStartTime()));
            bannerImg.setShowEndTime(DateUtil.getLocalDateTime(img.getShowEndTime()));
            LinkDto linkDto = img.getLinkDto();
            if (linkDto != null) {
                // 添加链接
                linkDto.setChannelNo(banner.getChannelNo());
                linkDto.setVersion(banner.getVersion());
                linkDto.setPageId(banner.getPageId());
                linkDto.setStatus(banner.getStatus());
                LinkDto linkDto1 = linkService.save(linkDto);
                Long linkId = linkDto1.getId();
                bannerImg.setLinkModuleId(linkId);
            }
            bannerImgs.add(bannerImg);
            bannerDto.getBannerImgDtoList().add(BaseHelper.r2t(bannerImg, BannerImgDto.class));
        });
        boolean saveBannerImgFlag = bannerImgService.saveBatch(bannerImgs);
        if (!saveBannerImgFlag) {
            log.error("BannerServiceImpl save fail. param: {}", bo);
            throw new CommonModuleException("报存图片失败");
        }

        // 再次保存轮播组件，为了更新轮播组件中的webJson里的轮播组ID和图片ID
        WebJsonBo bannerWebJson = JSONObject.parseObject(banner.getWebJson(), WebJsonBo.class);
        if (bannerWebJson != null) {
            // 更新轮播组件的webJson字段中的bannerID
            bannerWebJson.setId(banner.getId());
            bannerWebJson.setModuleUniqueId(banner.getId());
            if (bannerWebJson.getData() != null && !CollectionUtils.isEmpty(bannerWebJson.getData().getImgList())) {
                updateWebJson(bannerImgs, bannerWebJson);
            }
            banner.setWebJson(JSONObject.toJSONString(bannerWebJson));
            saveBannerFlag = saveOrUpdate(banner);
            if (!saveBannerFlag) {
                log.error("BannerServiceImpl.save update btn group webJson fail. param: {}", bo);
                throw new CommonModuleException("添加轮播组失败");
            }
            bannerDto.setWebJson(JSONObject.toJSONString(bannerWebJson));
        }
        return bannerDto;
    }

    private void updateWebJson(List<BannerImg> bannerImgs, WebJsonBo bannerWebJson) {
        if(CollectionUtils.isEmpty(bannerImgs)){
            return;
        }
        List<WebJsonBo.WebJsonImgBo> bannerImgWebJsonList = bannerWebJson.getData().getImgList();
        for (int i = 0; i < bannerImgWebJsonList.size() && bannerImgWebJsonList.size() == bannerImgs.size(); i++) {
            // 更新图片的webJson字段中的按钮ID
            WebJsonBo.WebJsonImgBo webJsonBannerImgBo = bannerImgWebJsonList.get(i);
            BannerImg bannerImg = bannerImgs.get(i);
            webJsonBannerImgBo.setId(bannerImg.getId());
            webJsonBannerImgBo.setModuleUniqueId(bannerImg.getModuleUniqueId());
            // 更新按钮链接ID
            WebJsonBo.WebJsonLinkBo link = webJsonBannerImgBo.getLink();
            if (link != null) {
                link.setId(bannerImg.getLinkModuleId());
            }
        }
    }

    @Override
    @TransactionalForException
    public String update(BannerBo bo) {
        Long bannerId = bo.getId();
        List<BannerImgBo> bannerImgs = bo.getBannerImgs();

        // 更新按钮组
        Banner banner = getById(bannerId);
        if (banner == null) {
            throw new CommonModuleException("图片组件不存在");
        }
        BaseHelper.copyNotNullProperties(bo, banner);
        if (!updateById(banner)) {
            log.error("BannerServiceImpl.update btn group update fail. param: {}", bo);
            throw new CommonModuleException("更新轮播组失败");
        }

        List<BannerImg> addOrUpdBannerList = new ArrayList<>();
        // 更新图片
        if (!CollectionUtils.isEmpty(bannerImgs)) {
            List<Long> delBannerList = new ArrayList<>();
            Map<Long, BannerImg> updBannerMap = new HashMap<>();
            QueryWrapper<BannerImg> qw = Wrappers.<BannerImg>query()
                    .eq("banner_id", bannerId);
            List<BannerImg> bannerImgsDb = bannerImgService.baseFindList(qw);

            // 将所有需要更新的图片放到一个Map中
            for (BannerImgBo bannerImgBo : bannerImgs) {
                Long bannerImgId = bannerImgBo.getId();
                BannerImg bannerImg = BaseHelper.r2t(bannerImgBo, BannerImg.class);
                bannerImg.setChannelNo(banner.getChannelNo());
                bannerImg.setVersion(banner.getVersion());
                bannerImg.setBannerId(bannerId);
                bannerImg.setPageId(banner.getPageId());
                bannerImg.setStatus(banner.getStatus());
                bannerImg.setShowStartTime(DateUtil.getLocalDateTime(bannerImgBo.getShowStartTime()));
                bannerImg.setShowEndTime(DateUtil.getLocalDateTime(bannerImgBo.getShowEndTime()));
                LinkDto linkDto = bannerImgBo.getLinkDto();
                if (linkDto != null) {
                    // 添加|更新 链接
                    linkDto.setChannelNo(banner.getChannelNo());
                    linkDto.setPageId(banner.getPageId());
                    linkDto.setStatus(banner.getStatus());
                    linkDto.setVersion(banner.getVersion());
                    if (linkDto.getId() == null) {
                        LinkDto linkDto1 = linkService.save(linkDto);
                        Long linkId = linkDto1.getId();
                        bannerImg.setLinkModuleId(linkId);
                    } else {
                        LinkDto updLink = linkService.update(linkDto);
                        if (updLink == null) {
                            log.error("BannerServiceImpl.update banner group update link fail. param: {}", linkDto);
                        }
                    }
                }
                // 新增或修改的图片
                addOrUpdBannerList.add(bannerImg);
                if (bannerImgId != null) {
                    // 需要更新的图片
                    updBannerMap.put(bannerImgId, bannerImg);
                }
            }
            // 将数据库中存在编辑后不存在的图片删除
            bannerImgsDb.forEach(bannerImgDb -> {
                Long id = bannerImgDb.getId();
                BannerImg bannerImg = updBannerMap.get(id);
                if (bannerImg == null) {
                    delBannerList.add(bannerImgDb.getId());
                }
            });
            boolean updBannerFlag = bannerImgService.saveOrUpdateBatch(addOrUpdBannerList);
            // 修改banner到回收站状态
            boolean delBannerFlag = true;
            if(!CollectionUtils.isEmpty(delBannerList)) {
                delBannerFlag = bannerImgService.update(null,
                        Wrappers.<BannerImg>lambdaUpdate().in(BannerImg::getId, delBannerList).set(BannerImg::getUpdatedTime, LocalDateTime.now()).set(BannerImg::getDeletedFlag, 1));
            }
            if (!updBannerFlag || !delBannerFlag) {
                log.error("BannerServiceImpl.update banner update fail. param: {}", bo);
                throw new CommonModuleException("更新轮播组件失败");
            }
        }
        // 更新webJson
        WebJsonBo bannerWebJson = JSONObject.parseObject(banner.getWebJson(), WebJsonBo.class);
        updateWebJson(addOrUpdBannerList, bannerWebJson);
        banner.setWebJson(JSONObject.toJSONString(bannerWebJson));
        if (!saveOrUpdate(banner)) {
            log.error("BannerServiceImpl.save update banner webJson fail. param: {}", bo);
            throw new CommonModuleException("修改banner失败");
        }
        return JSONObject.toJSONString(bannerWebJson);
    }

    @Override
    @TransactionalForException
    public String savePageModule(String webJson, Long pageId) {
        PageManager page = pageManagerService.getById(pageId);
        Assert.notNull(page, "页面不存在");
        BannerBo bannerBo = ModuleHandler.parseBanner(page, webJson);
        BannerDto bannerDto = save(bannerBo);
        return bannerDto.getWebJson();
    }

    @TransactionalForException
    @Override
    public String clonePageModule(Long moduleId, Long pageId) {
        PageManager page = pageManagerService.getById(pageId);

        Assert.notNull(page, "页面不存在");
        Long versionId = page.getVersionId();

        Assert.notNull(platformVersionService.getById(versionId), "无效的版本号");

        Banner banner = getById(moduleId);
        Assert.notNull(banner, "轮播组件不存在");

        // 更新前端数据字符串（webJson）
        String webJson = banner.getWebJson();
        if (StringUtils.isEmpty(webJson)) {
            return "";
        }


        return savePageModule(webJson, pageId);
    }

    @TransactionalForException
    @Override
    public String updatePageModule(String webJson) {
        return update(ModuleHandler.parseBanner(null,webJson));
    }

    @TransactionalForException
    @Override
    public Boolean updateModuleStatus(Long moduleId, Integer status) {
        BannerDto bannerDto = findById(moduleId,true);
        if (bannerDto == null) {
            log.debug("BannerServiceImpl.updateModuleStatus {} not exits", moduleId);
            throw new CommonModuleException("组件不存在");
        }
        // 更新按钮组状态
        Banner updBanner = new Banner();
        updBanner.setId(bannerDto.getId());
        updBanner.setStatus(status);
        boolean updFlag = updateById(updBanner);
        if (!updFlag) {
            log.error("BannerServiceImpl.updateModuleStatus update bannerImg group status fail. {}", moduleId);
            throw new CommonModuleException("轮播组件状态更新失败");
        }

        List<BannerImgDto> bannerImgDtos = bannerDto.getBannerImgDtoList();
        if (!CollectionUtils.isEmpty(bannerImgDtos)) {
            List<Long> bannerImgIds = bannerImgDtos.stream().map(BannerImgDto::getId).filter(Objects::nonNull).collect(Collectors.toList());
            List<Long> linkIds = bannerImgDtos.stream().map(BannerImgDto::getLinkModuleId).filter(Objects::nonNull).collect(Collectors.toList());
            // 更新图片状态
            if (!CollectionUtils.isEmpty(bannerImgIds)) {
                updFlag = bannerImgService.update(null,
                        Wrappers.<BannerImg>lambdaUpdate().in(BannerImg::getId, bannerImgIds).set(BannerImg::getUpdatedTime, LocalDateTime.now()).set(BannerImg::getStatus, status));
                if (!updFlag) {
                    log.error("BannerServiceImpl.updateModuleStatus update bannerImg status fail. {}", moduleId);
                    throw new CommonModuleException("轮播组件状态更新失败");
                }
            }
            // 更新按钮链接状态
            if (!CollectionUtils.isEmpty(linkIds)) {
                updFlag = linkService.update(Wrappers.<Link>lambdaUpdate().in(Link::getId, linkIds).set(Link::getUpdatedTime, LocalDateTime.now()).set(Link::getStatus, status));
                if (!updFlag) {
                    log.error("BannerServiceImpl.updateModuleStatus update bannerImg link status fail. {}", moduleId);
                    throw new CommonModuleException("轮播组件状态更新失败");
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
        List<BannerDto> bannerDtoList = findByIdList(moduleIds,true);
        if (CollectionUtils.isEmpty(bannerDtoList)) {
            log.debug("BannerServiceImpl.batchUpdateModuleStatus {} not exits", moduleIds);
            throw new CommonModuleException("组件不存在");
        }
        // 更新Banner状态
        LambdaUpdateWrapper<Banner> bannerUpdateWrapper = Wrappers.<Banner>lambdaUpdate().in(Banner::getId, moduleIds).set(Banner::getStatus, status);
        if(null != versionId){
            bannerUpdateWrapper.set(Banner::getVersion,versionId);
        }
        boolean updFlag = update(new Banner(),bannerUpdateWrapper );
        if (!updFlag) {
            log.error("BannerServiceImpl.batchUpdateModuleStatus update banner status fail. {}", moduleIds);
            throw new CommonModuleException("banner状态更新失败");
        }

        List<BannerImgDto> bannerImgDtos = bannerDtoList.stream().flatMap(banners -> banners.getBannerImgDtoList().stream()).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(bannerImgDtos)) {
            List<Long> bannerImgIds = bannerImgDtos.stream().map(BannerImgDto::getId).filter(Objects::nonNull).collect(Collectors.toList());
            List<Long> linkIds = bannerImgDtos.stream().map(BannerImgDto::getLinkModuleId).filter(Objects::nonNull).collect(Collectors.toList());
            // 更新banner图片状态
            if (!CollectionUtils.isEmpty(bannerImgIds)) {
                LambdaUpdateWrapper<BannerImg> bannerImgUpdateWrapper = Wrappers.<BannerImg>lambdaUpdate().in(BannerImg::getId, bannerImgIds).set(BannerImg::getStatus, status);
                if(null != bannerImgUpdateWrapper){
                    bannerImgUpdateWrapper.set(BannerImg::getVersion, versionId);
                }
                updFlag = bannerImgService.update(new BannerImg(), bannerImgUpdateWrapper);
                if (!updFlag) {
                    log.error("BannerServiceImpl.batchUpdateModuleStatus update bannerImg status fail. {}", bannerImgIds);
                    throw new CommonModuleException("banner图状态更新失败");
                }
            }
            // 更新链接状态
            if (!CollectionUtils.isEmpty(linkIds)) {
                LambdaUpdateWrapper<Link> linkUpdateWrapper = Wrappers.<Link>lambdaUpdate().in(Link::getId, linkIds).set(Link::getStatus, status);
                if(null != linkUpdateWrapper){
                    linkUpdateWrapper.set(Link::getVersion, versionId);
                }
                updFlag = linkService.update(new Link(), linkUpdateWrapper);
                if (!updFlag) {
                    log.error("BannerServiceImpl.batchUpdateModuleStatus update banner link status fail. {}", linkIds);
                    throw new CommonModuleException("banner链接状态更新失败");
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

    @Override
    public String getModuleById(Long id, String... param){
        // 获取webJSON
        Banner banner = getById(id);
        Assert.notNull(banner, "组件不存在");
        WebJsonBo bannerWebJson = JSONObject.parseObject(banner.getWebJson(), WebJsonBo.class);
        List<WebJsonBo.WebJsonImgBo> imgList = bannerWebJson.getData().getImgList();
        if(!CollectionUtils.isEmpty(imgList)){
            // 过滤掉未开始和已结束的banner
            for (int i=0; i<imgList.size(); i++) {
                if (!isShowBannerTime(imgList.get(i).getPeriod(),param[0])) {
                    imgList.remove(i);
                    i--;
                }
            }
        }
        return JSONObject.toJSONString(bannerWebJson);
    }

    private Boolean isShowBannerTime(List<Date> dates,String env){
        if(!CollectionUtils.isEmpty(dates)){
            Date showStartTime = dates.get(0);
            // 如果是用户端不展示未开始的
            if(PageCenterConsts.ENV_TYPE_RELEASE .equals(env) && showStartTime.after(new Date())){
                return false;
            }
            if(dates.size()>1){
                Date showEndTime = dates.get(1);
                if(showEndTime.before(new Date())){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public List<String> findPageModuleByIdList(List<Long> ids) {
        return null;
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
    public BannerDto findById(Long id,Boolean showRecycleBin) {
        Banner banner = getById(id);
        if (banner == null) {
            return null;
        }
        BannerDto bannerDto = BaseHelper.r2t(banner, BannerDto.class);
        LambdaQueryWrapper<BannerImg> query = Wrappers.<BannerImg>lambdaQuery().eq(BannerImg::getBannerId, id).notIn(BannerImg::getStatus, Collections.singletonList(2)).orderByAsc(BannerImg::getSort);
        if(showRecycleBin){
            query = Wrappers.<BannerImg>lambdaQuery().eq(BannerImg::getBannerId, id).orderByAsc(BannerImg::getSort);
        }
        List<BannerImg> bannerImgs = bannerImgService.getBaseMapper().selectList(query);
        if (!CollectionUtils.isEmpty(bannerImgs)) {
            List<BannerImgDto> bannerImgDtos = bannerImgs.stream().map(o -> BaseHelper.r2t(o, BannerImgDto.class)).collect(Collectors.toList());
            bannerDto.setBannerImgDtoList(bannerImgDtos);
        }
        return bannerDto;
    }

    @Override
    public List<BannerDto> findByIdList(List<Long> ids,Boolean showRecycleBin) {
        List<BannerDto> bannerDtos = new ArrayList<>();
        List<Banner> banners = listByIds(ids);
        if (CollectionUtils.isEmpty(banners)) {
            return bannerDtos;
        }

        Map<Long, List<BannerImg>> bannerImgMap = new HashMap<>();
        QueryWrapper<BannerImg> query = Wrappers.<BannerImg>query().in("banner_id", ids).notIn("status", Collections.singletonList(2));
        if(showRecycleBin){
            query = Wrappers.<BannerImg>query().in("banner_id", ids);
        }
        List<BannerImg> bannerImgs = bannerImgService.baseFindList(query);
        if (!CollectionUtils.isEmpty(bannerImgs)) {
            bannerImgMap = bannerImgs.stream().collect(Collectors.toMap(
                    BannerImg::getBannerId,
                    o -> {
                        List<BannerImg> tmpArr = new ArrayList<>();
                        tmpArr.add(o);
                        return tmpArr;
                    },
                    (ov, nv) -> {
                        ov.addAll(nv);
                        return ov;
                    }));
        }
        for (Banner banner : banners) {
            BannerDto bannerDto = BaseHelper.r2t(banner, BannerDto.class);
            Long bannerId = bannerDto.getId();
            List<BannerImg> bannerImgsByBannerId = bannerImgMap.get(bannerId);
            if (!CollectionUtils.isEmpty(bannerImgsByBannerId)) {
                List<BannerImgDto> bannerImgDtos = bannerImgsByBannerId.stream().map(o -> BaseHelper.r2t(o, BannerImgDto.class)).collect(Collectors.toList());
                bannerDto.setBannerImgDtoList(bannerImgDtos);
            }
            bannerDtos.add(bannerDto);
        }

        return bannerDtos;
    }

    @TransactionalForException
    @Override
    public Boolean delete(Long id) {
        BannerDto bannerDto = findById(id,true);
        if (bannerDto == null) {
            return false;
        }
        boolean delBannerFlag = removeById(id);
        if (!delBannerFlag) {
            return false;
        }
        List<BannerImgDto> bannerImgDtos = bannerDto.getBannerImgDtoList();
        return deleteBannerAndLinks(bannerImgDtos);
    }

    @Override
    @TransactionalForException
    public Boolean delete(List<Long> ids) {
        List<BannerDto> bannerDtos = findByIdList(ids,true);
        if (CollectionUtils.isEmpty(bannerDtos)) {
            return false;
        }
        boolean delBannerFlag = removeByIds(ids);
        if (!delBannerFlag) {
            return false;
        }

        List<BannerImgDto> bannerImgs = bannerDtos.stream().flatMap(bannerDto -> bannerDto.getBannerImgDtoList().stream()).collect(Collectors.toList());
        return deleteBannerAndLinks(bannerImgs);
    }

    /**
     * 批量删除按钮和其链接
     * @param bannerImgDtos
     * @return
     */
    private Boolean deleteBannerAndLinks(List<BannerImgDto> bannerImgDtos) {
        if (!CollectionUtils.isEmpty(bannerImgDtos)) {
            List<Long> bannerImgIds = bannerImgDtos.stream().map(BannerImgDto::getId).filter(o -> o != null).distinct().collect(Collectors.toList());
            List<Long> linkIds = bannerImgDtos.stream().map(BannerImgDto::getLinkModuleId).filter(o -> o != null).distinct().collect(Collectors.toList());
            boolean delBannerImgFlag = bannerImgService.removeByIds(bannerImgIds);
            if (!delBannerImgFlag) {
                log.error("BannerServiceImpl.deleteModule banner delete fail. param: {}", bannerImgIds);
                throw new CommonModuleException("删除轮播图片失败");
            }
            if (!CollectionUtils.isEmpty(linkIds) && !linkService.delete(linkIds)) {
                log.error("BannerServiceImpl.deleteModule banner link delete fail. param: {}", linkIds);
                throw new CommonModuleException("删除轮播图片失败");
            }
        }
        return null;
    }

    @Override
    @TransactionalForException
    public List<CompareBo> compare(String webJson, Long bannerId) {
        List<CompareBo> ret = new ArrayList<>();
        if (StringUtils.isEmpty(webJson) && bannerId == null) {
            log.debug("BannerServiceImpl.compare param is null");
            return ret;
        }

        if (StringUtils.isEmpty(webJson)) { // 组件被删除
            BannerDto oldBannerDto = findById(bannerId,false);
            if (oldBannerDto == null) {
                log.debug("BannerServiceImpl.compare {} not exits", bannerId);
                throw new CommonModuleException("banner组件不存在");
            }
            String bannerWebJson = oldBannerDto.getWebJson();
            Assert.isTrue(!StringUtils.isEmpty(bannerWebJson), "组件webJson数据异常");
            WebJsonBo webJsonBo = JSONObject.parseObject(bannerWebJson, WebJsonBo.class);
            WebJsonBo.WebJsonStyleBo styleBo = webJsonBo.getStyle();
            ret.add(new CompareBo(null, CommonConstant.ModuleType.CAROUSEL, "", OP_DEL, BANNER_STYLE, styleBo.styleStr(),
                    ""));

            for (WebJsonBo.WebJsonImgBo img : webJsonBo.getData().getImgList()) {
                bannerImgOpRecord(ret, OP_DEL, null, img, null);
            }
        } else { // 组件被更新
            Assert.isTrue(!StringUtils.isEmpty(webJson), "参数不能为空");
            WebJsonBo webJsonBo = JSON.parseObject(webJson, WebJsonBo.class);
            Assert.notNull(webJsonBo.getData(), "参数不能为空");
            Assert.notEmpty(webJsonBo.getData().getImgList(), "图片列表不能为空");

            List<Long> updBannerIds = new ArrayList<>(); // 存放本次更新的bannerID集合

            WebJsonBo.WebJsonStyleBo styleBo = webJsonBo.getStyle();
            Assert.notNull(styleBo, "banner尺寸不能为空");
            String style = styleBo.styleStr();

            if (bannerId == null) { // 新增轮播图组件

                ret.add(new CompareBo(null, CommonConstant.ModuleType.CAROUSEL, "", OP_ADD,BANNER_STYLE, "", style));

                for (WebJsonBo.WebJsonImgBo img : webJsonBo.getData().getImgList()) {
                    bannerImgOpRecord(ret, OP_ADD, null, null, img);
                }

            } else { // 编辑banner组件
                BannerDto oldBannerDto = findById(bannerId,false);
                if (oldBannerDto == null) {
                    log.error("BannerServiceImpl.compare banner not exits. param: {}", bannerId);
                    throw new CommonModuleException("获取banner失败");
                }
                try {
                    WebJsonBo oldWebJson = JSONObject.parseObject(oldBannerDto.getWebJson(), WebJsonBo.class);

                    if (oldWebJson.getData() != null && !CollectionUtils.isEmpty(oldWebJson.getData().getImgList())) {

                        // 比对banner布局
                        WebJsonBo.WebJsonStyleBo oldStyleBo = oldWebJson.getStyle();
                        String oldStyle = oldStyleBo.styleStr();
                        if (!style.equals(oldStyle)) {
                            ret.add(new CompareBo(bannerId, CommonConstant.ModuleType.CAROUSEL, "", OP_UPD,BANNER_STYLE,
                                    oldStyle, style));
                        }

                        List<WebJsonBo.WebJsonImgBo> oldBannerImgListWebJson = oldWebJson.getData().getImgList();
                        Map<Long, WebJsonBo.WebJsonImgBo> oldBannerImgMap = oldBannerImgListWebJson.stream()
                                .collect(Collectors.toMap(WebJsonBo.WebJsonImgBo::getModuleUniqueId, o -> o));

                        // 比对每个banner图
                        for (WebJsonBo.WebJsonImgBo bannerImgWebJson : webJsonBo.getData().getImgList()) {
                            Long bannerImgId = bannerImgWebJson.getId();
                            Long moduleUniqueId = bannerImgWebJson.getModuleUniqueId();
                            WebJsonBo.WebJsonImgBo oldBannerImgWebJson = oldBannerImgMap.get(moduleUniqueId);
                            // 若前端传入的webJson中的按钮没有ID，那么新增按钮
                            if (bannerImgId == null || moduleUniqueId == null || oldBannerImgWebJson == null) {
                                bannerImgOpRecord(ret, OP_ADD, null, null, bannerImgWebJson);
                                continue;
                            }
                            updBannerIds.add(moduleUniqueId);

                            // 更新按钮
                            bannerImgOpRecord(ret, OP_UPD, bannerId, oldBannerImgWebJson, bannerImgWebJson);
                        }

                        // 删除按钮
                        List<BannerImgDto> delBannerImgList = oldBannerDto.getBannerImgDtoList().stream().filter(o -> !updBannerIds.contains(o.getModuleUniqueId())).collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(delBannerImgList)) {
                            for (BannerImgDto delbannerImg : delBannerImgList) {
                                WebJsonBo.WebJsonImgBo delBtnWebJson = oldBannerImgMap.get(delbannerImg.getId());
                                bannerImgOpRecord(ret, OP_DEL, null, delBtnWebJson, null);
                            }
                        }
                    }
                } catch (JSONException e) {
                    throw new ParameterException("webJson格式异常");
                } catch (Exception e) {
                    log.error("BannerServiceImpl.compare has exception", e);
                    throw new ParameterException("webJson解析异常");
                }
            }
        }
        return ret;
    }

    private void bannerImgOpRecord(List<CompareBo> ret, String opName, Long bannerId,
                                   WebJsonBo.WebJsonImgBo oldImg, WebJsonBo.WebJsonImgBo newImg) {
        String oldTitle = "", title = "";
        String oldLinkName = "", linkName = "";
        String oldLinkParam = "", linkParam = "";
        String oldBannerImg = "", bannerImg = "";
        List<Date> oldPeriod = new ArrayList<>(4), period = new ArrayList<>(4);
        if (oldImg != null) {
            oldTitle = oldImg.getTitle();
            oldBannerImg = oldImg.getImgUrl();
            oldPeriod = oldImg.getPeriod();
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
            bannerImg = newImg.getImgUrl();
            period = newImg.getPeriod();
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
            // 比对banner图名
            if (!Optional.ofNullable(title).orElse("").equals(oldTitle)) {
                recordBannerOp(ret, oldTitle, title, moduleName, OP_UPD, BANNER_NAME, bannerId);
            }
            // 比对banner图
            if (!Optional.ofNullable(bannerImg).orElse("").equals(oldBannerImg)) {
                recordBannerOp(ret, oldBannerImg, bannerImg, moduleName, OP_UPD, BANNER_IMG, bannerId);
            }
            // 比对链接
            if (!Optional.ofNullable(linkName).orElse("").equals(oldLinkName)) {
                recordBannerOp(ret, oldLinkName, linkName, moduleName, OP_UPD, LINK_PATH, bannerId);
            }
            // 比对链接参数
            if (!Optional.ofNullable(linkParam).orElse("").equals(oldLinkParam)) {
                recordBannerOp(ret, oldLinkParam, linkParam, moduleName, OP_UPD, LINK_PARAM, bannerId);
            }
            // 比对投放时间
            if (compareList(oldPeriod,period)) {
                recordBannerOp(ret, getBannerDateStr(oldPeriod), getBannerDateStr(period), moduleName, OP_UPD, BANNER_DATE, bannerId);
            }
        }else{
            // 比对banner图名
            recordBannerOp(ret, oldTitle, title, moduleName, opName, BANNER_NAME, bannerId);
            // 比对banner图
            recordBannerOp(ret, oldBannerImg, bannerImg, moduleName, opName, BANNER_IMG, bannerId);
            // 比对链接
            recordBannerOp(ret, oldLinkName, linkName, moduleName, opName, LINK_PATH, bannerId);
            // 比对链接参数
            recordBannerOp(ret, oldLinkParam, linkParam, moduleName, opName, LINK_PARAM, bannerId);
            // 比对投放时间
            recordBannerOp(ret, getBannerDateStr(oldPeriod), getBannerDateStr(period), moduleName, opName, BANNER_DATE, bannerId);
        }
    }

    private boolean compareList(List<Date> list1, List<Date> list2) {
        if(list1 == list2){
            return true;
        }

        if ((list1 == null && list2 != null && list2.size() == 0)
                || (list2 == null && list1 != null && list1.size() == 0)) {
            return true;
        }

        // 两个list元素个数不相同
        if ((!CollectionUtils.isEmpty(list1) && !CollectionUtils.isEmpty(list2)) && list1.size() != list2.size()) {
            return false;
        }

        if ((!CollectionUtils.isEmpty(list1) && !CollectionUtils.isEmpty(list2)) && list1.containsAll(list2)) {
            return false;
        }

        return true;
    }

    private String getBannerDateStr(List<Date> period) {
        if(CollectionUtils.isEmpty(period)){
            return "";
        }
        Date before = period.get(0);
        Date end = null;
        if(period.size() > 1){
            end = period.get(1);
        }
        return (null == before ? "" : DateUtil.dateTimeToStr(before,DateUtil.SQL_TIME)) + "-" + (null == end ? "" : DateUtil.dateTimeToStr(end,DateUtil.SQL_TIME));
    }

    /**
     * 记录按钮操作记录
     * @param compareList 比对结果集合
     * @param oldValue  改前的值
     * @param newValue  改后的值
     * @param opName    操作
     * @param moduleId  组件ID
     * @return
     */
    private void recordBannerOp(List<CompareBo> compareList, String oldValue, String newValue, String moduleName,
                                String opName, String opContent, Long moduleId) {
        if (!StringUtils.isEmpty(oldValue) || !StringUtils.isEmpty(newValue)) {
            // 新增链接参数操作记录
            compareList.add(new CompareBo(null, CommonConstant.ModuleType.CAROUSEL, moduleName, opName, opContent, oldValue, newValue));
        }
    }

    @Override
    public BannerDto queryBannerDetail(Long bannerId, Integer showUnStart) {
        Banner banner = getById(bannerId);
        if (banner == null) {
            return null;
        }
        BannerDto bannerDto = BaseHelper.r2t(banner,BannerDto.class);
        // 查询banner图片
        List<BannerImgDto> bannerImgDtos = bannerImgService.queryBannerImgByBannerId(bannerId,showUnStart);
        if(CollectionUtils.isEmpty(bannerImgDtos)){
            return bannerDto;
        }
        bannerDto.setBannerImgDtoList(bannerImgDtos);
        bannerImgDtos.forEach(img->{
            if(null != img.getLinkModuleId()){
                img.setLinkDto(linkService.findById(img.getLinkModuleId()));
            }
        });
        return bannerDto;
    }
}
