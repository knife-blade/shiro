package com.touchealth.platform.processengine.service.impl.module.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.touchealth.platform.processengine.constant.CommonConstant;
import com.touchealth.platform.processengine.utils.BaseHelper;
import com.touchealth.platform.processengine.annotation.TransactionalForException;
import com.touchealth.platform.processengine.entity.module.common.*;
import com.touchealth.platform.processengine.dao.module.common.ComboImgDao;
import com.touchealth.platform.processengine.entity.page.PageManager;
import com.touchealth.platform.processengine.exception.CommonModuleException;
import com.touchealth.platform.processengine.exception.ParameterException;
import com.touchealth.platform.processengine.handler.ModuleHandler;
import com.touchealth.platform.processengine.pojo.bo.CompareBo;
import com.touchealth.platform.processengine.pojo.bo.WebJsonBo;
import com.touchealth.platform.processengine.pojo.bo.module.common.ComboImgBo;
import com.touchealth.platform.processengine.pojo.bo.module.common.ComboImgDetailBo;
import com.touchealth.platform.processengine.pojo.dto.module.common.*;
import com.touchealth.platform.processengine.service.impl.module.BaseModuleServiceImpl;
import com.touchealth.platform.processengine.service.module.common.ComboImgDetailService;
import com.touchealth.platform.processengine.service.module.common.ComboImgService;
import com.touchealth.platform.processengine.service.module.common.LinkService;
import com.touchealth.platform.processengine.service.page.PageManagerService;
import com.touchealth.platform.processengine.service.page.PlatformVersionService;
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
import java.util.stream.IntStream;

import static com.touchealth.platform.processengine.constant.CompareConstant.*;
import static com.touchealth.platform.processengine.constant.WebJsonConstant.WEB_LINK_TYPE_OUTSIDE;

/**
 * <p>
 * 组合图通用组件表 服务实现类
 * </p>
 *
 * @author LJH
 * @since 2020-11-30
 */
@Service
@Slf4j
public class ComboImgServiceImpl extends BaseModuleServiceImpl<ComboImgDao, ComboImg> implements ComboImgService {

    @Autowired
    private LinkService linkService;

    @Autowired
    private ComboImgDetailService comboImgDetailService;

    @Resource
    private PageManagerService pageManagerService;

    @Resource
    private PlatformVersionService platformVersionService;

    @TransactionalForException
    @Override
    public String savePageModule(String webJson, Long pageId) {
        PageManager page = pageManagerService.getById(pageId);
        Assert.notNull(page, "页面不存在");
        ComboImgBo comboImgBo = ModuleHandler.parseComboImg(page, webJson);
        ComboImgDto comboImgDto = save(comboImgBo);
        return comboImgDto.getWebJson();
    }

    @TransactionalForException
    @Override
    public String clonePageModule(Long moduleId, Long pageId) {
        PageManager page = pageManagerService.getById(pageId);

        Assert.notNull(page, "页面不存在");
        Long versionId = page.getVersionId();

        Assert.notNull(platformVersionService.getById(versionId), "无效的版本号");

        ComboImg comboImg = getById(moduleId);
        Assert.notNull(comboImg, "多图组件不存在");

        // 更新前端数据字符串（webJson）
        String webJson = comboImg.getWebJson();
        if (StringUtils.isEmpty(webJson)) {
            return "";
        }


        return savePageModule(webJson, pageId);
    }

    @TransactionalForException
    @Override
    public String updatePageModule(String webJson) {
        return update(ModuleHandler.parseComboImg(null,webJson));
    }

    @TransactionalForException
    @Override
    public Boolean updateModuleStatus(Long moduleId, Integer status) {
        ComboImgDto comboImgDto = findById(moduleId,true);
        if (comboImgDto == null) {
            log.debug("ComboImgServiceImpl.updateModuleStatus {} not exits", moduleId);
            throw new CommonModuleException("组件不存在");
        }
        // 更新按钮组状态
        ComboImg updComboImg = new ComboImg();
        updComboImg.setId(comboImgDto.getId());
        updComboImg.setStatus(status);
        boolean updFlag = updateById(updComboImg);
        if (!updFlag) {
            log.error("ComboImgServiceImpl.updateModuleStatus update comboImg group status fail. {}", moduleId);
            throw new CommonModuleException("多图组件状态更新失败");
        }

        List<ComboImgDetailDto> comboImgDetailDtos = comboImgDto.getComboImgDetailDtos();
        if (!CollectionUtils.isEmpty(comboImgDetailDtos)) {
            List<Long> comboImgDetailIds = comboImgDetailDtos.stream().map(ComboImgDetailDto::getId).filter(Objects::nonNull).collect(Collectors.toList());
            List<Long> linkIds = comboImgDetailDtos.stream().map(ComboImgDetailDto::getLinkModuleId).filter(Objects::nonNull).collect(Collectors.toList());
            // 更新图片状态
            if (!CollectionUtils.isEmpty(comboImgDetailIds)) {
                updFlag = comboImgDetailService.update(null,
                        Wrappers.<ComboImgDetail>lambdaUpdate().in(ComboImgDetail::getId, comboImgDetailIds).set(ComboImgDetail::getUpdatedTime, LocalDateTime.now()).set(ComboImgDetail::getStatus, status));
                if (!updFlag) {

                    log.error("ComboImgServiceImpl.updateModuleStatus update comboImgDetail status fail. {}", moduleId);
                    throw new CommonModuleException("多图组件状态更新失败");
                }
            }
            // 更新按钮链接状态
            if (!CollectionUtils.isEmpty(linkIds)) {
                updFlag = linkService.update(null,
                        Wrappers.<Link>lambdaUpdate().in(Link::getId, linkIds).set(Link::getUpdatedTime, LocalDateTime.now()).set(Link::getStatus, status));
                if (!updFlag) {
                    log.error("ComboImgServiceImpl.updateModuleStatus update comboImgDetail link status fail. {}", moduleId);
                    throw new CommonModuleException("多图组件状态更新失败");
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
     * @param versionId   版本状态
     * @return
     */
    @Override
    public Boolean batchUpdateModuleStatusAndVersion(List<Long> moduleIds, Integer status,Long versionId){
        List<ComboImgDto> comboImgDtos = findByIdList(moduleIds,true);
        if (CollectionUtils.isEmpty(comboImgDtos)) {
            log.debug("ComboImgServiceImpl.batchUpdateModuleStatus {} not exits", moduleIds);
            throw new CommonModuleException("组件不存在");
        }
        // 更新按钮组状态
        LambdaUpdateWrapper<ComboImg> comboImgUpdateWrapper = Wrappers.<ComboImg>lambdaUpdate().in(ComboImg::getId, moduleIds).set(ComboImg::getStatus, status);
        if(null != versionId){
            comboImgUpdateWrapper.set(ComboImg::getVersion, versionId);
        }
        boolean updFlag = update(new ComboImg(), comboImgUpdateWrapper);
        if (!updFlag) {
            log.error("ComboImgServiceImpl.batchUpdateModuleStatus update comboImg status fail. {}", moduleIds);
            throw new CommonModuleException("多图状态更新失败");
        }

        List<ComboImgDetailDto> comboImgDetailDtos = comboImgDtos.stream().flatMap(comboImgDtoList -> comboImgDtoList.getComboImgDetailDtos().stream()).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(comboImgDetailDtos)) {
            List<Long> comboImgDetailIds = comboImgDetailDtos.stream().map(ComboImgDetailDto::getId).filter(Objects::nonNull).collect(Collectors.toList());
            List<Long> linkIds = comboImgDetailDtos.stream().map(ComboImgDetailDto::getLinkModuleId).filter(Objects::nonNull).collect(Collectors.toList());
            // 更新combo图片状态
            if (!CollectionUtils.isEmpty(comboImgDetailIds)) {
                LambdaUpdateWrapper<ComboImgDetail> comboImgDetailUpdateWrapper = Wrappers.<ComboImgDetail>lambdaUpdate().in(ComboImgDetail::getId, comboImgDetailIds).set(ComboImgDetail::getStatus, status);
                if (null != versionId){
                    comboImgDetailUpdateWrapper.set(ComboImgDetail::getVersion, versionId);
                }
                updFlag = comboImgDetailService.update(new ComboImgDetail(), comboImgDetailUpdateWrapper);
                if (!updFlag) {
                    log.error("ComboImgServiceImpl.batchUpdateModuleStatus update comboImg status fail. {}", comboImgDetailIds);
                    throw new CommonModuleException("多图状态更新失败");
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
                    log.error("ComboImgServiceImpl.batchUpdateModuleStatus update comboImg link status fail. {}", linkIds);
                    throw new CommonModuleException("多图状态更新失败");
                }
            }
        }

        return true;
    }

    @Override
    public Boolean restoreModule(Collection<Long> ids, Long versionId) {
        return batchUpdateModuleStatusAndVersion(new ArrayList<>(ids), CommonConstant.STATUS.DRAFT.getCode(),versionId);
    }

    @Override
    public String findPageModuleById(Long id) {
        ComboImgDto comboImgDto = findById(id,false);
        return comboImgDto == null || CollectionUtils.isEmpty(comboImgDto.getComboImgDetailDtos())? "" : comboImgDto.getWebJson();
    }

    @Override
    public List<String> findPageModuleByIdList(List<Long> ids) {
        List<ComboImgDto> comboImgDtos = findByIdList(ids,false);
        return CollectionUtils.isEmpty(comboImgDtos) ? new ArrayList<>() :
                comboImgDtos.stream().map(ComboImgDto::getWebJson).collect(Collectors.toList());
    }

    @TransactionalForException
    @Override
    public List<CompareBo> compare(String webJson, Long comboId) {
        List<CompareBo> ret = new ArrayList<>();
        if (StringUtils.isEmpty(webJson) && comboId == null) {
            log.debug("ComboImgServiceImpl.compare param is null");
            return ret;
        }

        if (StringUtils.isEmpty(webJson)) { // 组件被删除
            ComboImgDto oldComboImgDto = findById(comboId,false);
            if (oldComboImgDto == null) {
                log.debug("ComboImgServiceImpl.compare {} not exits", comboId);
                throw new CommonModuleException("多图组件不存在");
            }
            String comboWebJson = oldComboImgDto.getWebJson();
            Assert.isTrue(!StringUtils.isEmpty(comboWebJson), "组件webJson数据异常");

            WebJsonBo webJsonBo = JSONObject.parseObject(comboWebJson, WebJsonBo.class);
            ret.add(new CompareBo(null, CommonConstant.ModuleType.LIST_PHOTOS, "", OP_DEL, COMBO_STYLE, "",
                    COMBO_LAYOUT_TYPE_MAP.getOrDefault(webJsonBo.getLayoutType(), "")));

            for (WebJsonBo.WebJsonImgBo img : webJsonBo.getData().getImgList()) {
                comboOpRecord(ret, OP_DEL, null, img, null);
            }
        } else { // 组件被更新
            Assert.isTrue(!StringUtils.isEmpty(webJson), "参数不能为空");
            WebJsonBo webJsonBo = JSON.parseObject(webJson, WebJsonBo.class);
            Assert.notNull(webJsonBo.getData(), "参数不能为空");
            Assert.notEmpty(webJsonBo.getData().getImgList(), "图片列表不能为空");

            List<Long> updComboDetailIds = new ArrayList<>(); // 存放本次更新的combo集合

            Integer layoutType = webJsonBo.getLayoutType();

            if (comboId == null) { // 新增轮播图组件

                ret.add(new CompareBo(null, CommonConstant.ModuleType.LIST_PHOTOS, "", OP_ADD,COMBO_STYLE, "",
                        COMBO_LAYOUT_TYPE_MAP.getOrDefault(layoutType, "")));

                for (WebJsonBo.WebJsonImgBo img : webJsonBo.getData().getImgList()) {
                    comboOpRecord(ret, OP_ADD, null, null, img);
                }

            } else { // 编辑banner组件
                ComboImgDto oldComboImgDto = findById(comboId,false);
                if (oldComboImgDto == null) {
                    log.error("ComboImgServiceImpl.compare banner not exits. param: {}", comboId);
                    throw new CommonModuleException("获取组合图失败");
                }
                try {
                    WebJsonBo oldWebJson = JSONObject.parseObject(oldComboImgDto.getWebJson(), WebJsonBo.class);

                    if (oldWebJson.getData() != null && !CollectionUtils.isEmpty(oldWebJson.getData().getImgList())) {

                        // 比对按钮组布局
                        Integer oldLayoutType = oldWebJson.getLayoutType();
                        if (!layoutType.equals(oldLayoutType)) {
                            ret.add(new CompareBo(comboId, CommonConstant.ModuleType.LIST_PHOTOS, "", OP_UPD,COMBO_STYLE,
                                    COMBO_LAYOUT_TYPE_MAP.getOrDefault(oldLayoutType, ""),
                                    COMBO_LAYOUT_TYPE_MAP.getOrDefault(layoutType, "")));
                        }

                        List<WebJsonBo.WebJsonImgBo> oldComboImgDrtailListWebJson = oldWebJson.getData().getImgList();
                        Map<Long, WebJsonBo.WebJsonImgBo> oldComboImgDetailMap = oldComboImgDrtailListWebJson.stream()
                                .collect(Collectors.toMap(WebJsonBo.WebJsonImgBo::getModuleUniqueId, o -> o));

                        // 比对每个banner图
                        for (WebJsonBo.WebJsonImgBo comboImgDetailWebJson : webJsonBo.getData().getImgList()) {
                            Long comboImgDetailId = comboImgDetailWebJson.getId();
                            Long moduleUniqueId = comboImgDetailWebJson.getModuleUniqueId();
                            WebJsonBo.WebJsonImgBo oldComboImgDetailWebJson = oldComboImgDetailMap.get(moduleUniqueId);
                            // 若前端传入的webJson中的按钮没有ID，那么新增按钮
                            if (comboImgDetailId == null || moduleUniqueId == null || oldComboImgDetailWebJson == null) {
                                comboOpRecord(ret, OP_ADD,null, null, comboImgDetailWebJson);
                                continue;
                            }
                            updComboDetailIds.add(moduleUniqueId);

                            // 更新按钮
                            comboOpRecord(ret, OP_UPD,comboId, oldComboImgDetailWebJson, comboImgDetailWebJson);
                        }

                        // 删除按钮
                        List<ComboImgDetailDto> delComboImgDetailList = oldComboImgDto.getComboImgDetailDtos().stream().filter(o -> !updComboDetailIds.contains(o.getModuleUniqueId())).collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(delComboImgDetailList)) {
                            for (ComboImgDetailDto delcomboImgDetailDto : delComboImgDetailList) {
                                WebJsonBo.WebJsonImgBo delComboImgDetailWebJson = oldComboImgDetailMap.get(delcomboImgDetailDto.getId());
                                comboOpRecord(ret, OP_DEL,null, delComboImgDetailWebJson, null);
                            }
                        }
                    }
                } catch (JSONException e) {
                    throw new ParameterException("webJson格式异常");
                } catch (Exception e) {
                    log.error("ComboImgServiceImpl.compare has exception", e);
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
     * @param comboId
     * @param oldImg
     * @param newImg
     */
    private void comboOpRecord(List<CompareBo> ret, String opName, Long comboId,
                               WebJsonBo.WebJsonImgBo oldImg, WebJsonBo.WebJsonImgBo newImg) {
        String oldTitle = "", title = "";
        String oldLinkName = "", linkName = "";
        String oldLinkParam = "", linkParam = "";
        String oldComboImg = "", comboImg = "";
        if (oldImg != null) {
            oldTitle = oldImg.getTitle();
            oldComboImg = oldImg.getImgUrl();
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
            comboImg = newImg.getImgUrl();
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
                recordComboOp(ret, oldTitle, title, moduleName, OP_UPD, COMBO_NAME, comboId);
            }
            // 比对图
            if (!Optional.ofNullable(comboImg).orElse("").equals(oldComboImg)) {
                recordComboOp(ret, oldTitle, title, moduleName, OP_UPD, COMBO_IMG, comboId);
            }
            // 比对链接
            if (!Optional.ofNullable(linkName).orElse("").equals(oldLinkName)) {
                recordComboOp(ret, oldLinkName, linkName, moduleName, OP_UPD, LINK_PATH, comboId);
            }
            // 比对链接参数
            if (!Optional.ofNullable(linkParam).orElse("").equals(oldLinkParam)) {
                recordComboOp(ret, oldLinkParam, linkParam, moduleName, OP_UPD, LINK_PARAM, comboId);
            }
        }else{
            // 比对图名
            recordComboOp(ret, oldTitle, title, moduleName, opName, COMBO_NAME, comboId);
            // 比对图
            recordComboOp(ret, oldComboImg, comboImg, moduleName, opName, COMBO_IMG, comboId);
            // 比对链接
            recordComboOp(ret, oldLinkName, linkName, moduleName, opName, LINK_PATH, comboId);
            // 比对链接参数
            recordComboOp(ret, oldLinkParam, linkParam, moduleName, opName, LINK_PARAM, comboId);
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
    private void recordComboOp(List<CompareBo> compareList, String oldValue, String newValue, String moduleName,
                               String opName, String opContent, Long moduleId) {
        if (!StringUtils.isEmpty(oldValue) || !StringUtils.isEmpty(newValue)) {
            // 新增链接参数操作记录
            compareList.add(new CompareBo(null, CommonConstant.ModuleType.LIST_PHOTOS, moduleName, opName,opContent, oldValue, newValue));
        }
    }

    @TransactionalForException
    @Override
    public ComboImgDto save(ComboImgBo bo) {
        List<ComboImgDetailBo> comboImgDetailBos = bo.getComboImgDetailBos();
        if(CollectionUtils.isEmpty(comboImgDetailBos)){
            return null;
        }
        // 添加坑位组件
        bo.setId(null);
        ComboImg comboImg = BaseHelper.r2t(bo,ComboImg.class);
        comboImg.setCategoryId(CommonConstant.MODULE_CATEGORY.COMMON.getCode());
        boolean saveComboFlag = save(comboImg);
        if (!saveComboFlag) {
            log.error("ComboImgServiceImpl.save comboImg group save fail. param: {}",bo);
            throw new CommonModuleException("添加多图组件失败");
        }
        ComboImgDto comboImgDto = BaseHelper.r2t(comboImg,ComboImgDto.class);
        comboImgDto.setComboImgDetailDtos(new ArrayList<>(comboImgDetailBos.size()));
        // 添加图片
        List<ComboImgDetail> comboImgDetails = new ArrayList<>(comboImgDetailBos.size());
        comboImgDetailBos.forEach(detailBo -> {
            detailBo.setId(null);
            ComboImgDetail comboImgDetail = BaseHelper.r2t(detailBo,ComboImgDetail.class);
            comboImgDetail.setChannelNo(comboImg.getChannelNo());
            comboImgDetail.setComboImgId(comboImg.getId());
            comboImgDetail.setPageId(comboImg.getPageId());
            comboImgDetail.setVersion(comboImg.getVersion());
            comboImgDetail.setStatus(comboImg.getStatus());
            comboImgDetail.setUrl(detailBo.getUrl());
            LinkDto linkDto = detailBo.getLinkDto();
            if (linkDto != null) {
                // 添加链接
                linkDto.setChannelNo(comboImg.getChannelNo());
                linkDto.setVersion(comboImg.getVersion());
                linkDto.setStatus(comboImg.getStatus());
                linkDto.setPageId(comboImg.getPageId());
                LinkDto linkDto1 = linkService.save(linkDto);
                Long linkId = linkDto1.getId();
                comboImgDetail.setLinkModuleId(linkId);
            }
            comboImgDetails.add(comboImgDetail);
            comboImgDto.getComboImgDetailDtos().add(BaseHelper.r2t(comboImgDetail, ComboImgDetailDto.class));
        });
        boolean saveComboImgFlag = comboImgDetailService.saveBatch(comboImgDetails);
        if (!saveComboImgFlag) {
            log.error("ComboImgServiceImpl save fail. param: {}", bo);
            throw new CommonModuleException("报存图片失败");
        }
        // 再次保存轮播组件，为了更新轮播组件中的webJson里的轮播组ID和图片ID
        WebJsonBo comboWebJson = JSONObject.parseObject(comboImg.getWebJson(), WebJsonBo.class);
        if (comboWebJson != null) {
            // 更新多图组件的webJson字段中的按钮组ID
            comboWebJson.setId(comboImg.getId());
            comboWebJson.setModuleUniqueId(comboImg.getModuleUniqueId());
            if (comboWebJson.getData() != null && !CollectionUtils.isEmpty(comboWebJson.getData().getImgList())) {
                updateWebJson(comboImgDetails, comboWebJson);
            }
            comboImg.setWebJson(JSONObject.toJSONString(comboWebJson));
            saveComboFlag = saveOrUpdate(comboImg);
            if (!saveComboFlag) {
                log.error("ComboImgServiceImpl.save update combo webJson fail. param: {}", bo);
                throw new CommonModuleException("添加多图组件失败");
            }
            comboImgDto.setWebJson(JSONObject.toJSONString(comboWebJson));
        }
        return comboImgDto;
    }

    private void updateWebJson(List<ComboImgDetail> comboImgDetails, WebJsonBo comboWebJson) {
        if(CollectionUtils.isEmpty(comboImgDetails)){
            return;
        }
        List<WebJsonBo.WebJsonImgBo> comboDetailWebJsonList = comboWebJson.getData().getImgList();
        for (int i = 0; i < comboDetailWebJsonList.size() && comboDetailWebJsonList.size() == comboImgDetails.size(); i++) {
            // 更新图片的webJson字段中的按钮ID
            WebJsonBo.WebJsonImgBo webJsonComboDetailBo = comboDetailWebJsonList.get(i);
            ComboImgDetail comboImgDetail = comboImgDetails.get(i);
            webJsonComboDetailBo.setId(comboImgDetail.getId());
            webJsonComboDetailBo.setModuleUniqueId(comboImgDetail.getModuleUniqueId());
            // 更新按钮链接ID
            WebJsonBo.WebJsonLinkBo link = webJsonComboDetailBo.getLink();
            if (link != null) {
                link.setId(comboImgDetail.getLinkModuleId());
            }
        }
    }

    public static void main(String[] args) {
        List<Integer> a = IntStream.range(0, 2).mapToObj(idx ->
        {
            return idx;
        }).collect(Collectors.toList());
        System.out.println(a);
    }

    @TransactionalForException
    @Override
    public String update(ComboImgBo bo) {
        Long comboImgId = bo.getId();
        List<ComboImgDetailBo> comboImgDetailBos = bo.getComboImgDetailBos();

        // 更新按钮组
        ComboImg comboImg = getById(comboImgId);
        if (comboImg == null) {
            throw new CommonModuleException("多图组件不存在");
        }
        BaseHelper.copyNotNullProperties(bo, comboImg);
        if (!updateById(comboImg)) {
            log.error("ComboImgServiceImpl.update combo group update fail. param: {}", bo);
            throw new CommonModuleException("更新多图组件失败");
        }

        List<ComboImgDetail> addOrUpdComboDetailList = new ArrayList<>();
        // 更新图片
        if (!CollectionUtils.isEmpty(comboImgDetailBos)) {
            List<Long> delComboDetailList = new ArrayList<>();
            Map<Long, ComboImgDetail> updComboDetailMap = new HashMap<>();
            QueryWrapper<ComboImgDetail> qw = Wrappers.<ComboImgDetail>query()
                    .eq("combo_img_id", comboImgId);
            List<ComboImgDetail> comboImgDetails = comboImgDetailService.baseFindList(qw);

            // 将所有需要更新的按钮放到一个Map中
            for (ComboImgDetailBo comboImgDetailBo : comboImgDetailBos) {
                Long comboImgDetailBoId = comboImgDetailBo.getId();
                ComboImgDetail comboImgDetail = BaseHelper.r2t(comboImgDetailBo, ComboImgDetail.class);
                comboImgDetail.setChannelNo(comboImg.getChannelNo());
                comboImgDetail.setVersion(comboImg.getVersion());
                comboImgDetail.setComboImgId(comboImgId);
                comboImgDetail.setPageId(comboImg.getPageId());
                comboImgDetail.setStatus(comboImg.getStatus());

                LinkDto linkDto = comboImgDetailBo.getLinkDto();
                if (linkDto != null) {
                    // 添加|更新 链接
                    linkDto.setChannelNo(comboImg.getChannelNo());
                    linkDto.setPageId(comboImg.getPageId());
                    linkDto.setVersion(comboImg.getVersion());
                    linkDto.setStatus(comboImg.getStatus());
                    if (linkDto.getId() == null) {
                        LinkDto linkDto1 = linkService.save(linkDto);
                        Long linkId = linkDto1.getId();
                        comboImgDetail.setLinkModuleId(linkId);
                    } else {
                        LinkDto updLink = linkService.update(linkDto);
                        if (updLink == null) {
                            log.error("ComboImgServiceImpl.update combo group update link fail. param: {}", linkDto);
                        }
                    }
                }
                // 新增或修改的图片
                addOrUpdComboDetailList.add(comboImgDetail);
                if (comboImgDetailBoId != null) {
                    // 需要更新的图片
                    updComboDetailMap.put(comboImgDetailBoId, comboImgDetail);
                }
            }
            // 将数据库中存在编辑后不存在的图片删除
            comboImgDetails.forEach(comboImgDetailDb -> {
                ComboImgDetail comboImgDetail = updComboDetailMap.get(comboImgDetailDb.getId());
                if (comboImgDetail == null) {
                    delComboDetailList.add(comboImgDetailDb.getId());
                }
            });
            boolean updComboFlag = true;
            if(!CollectionUtils.isEmpty(addOrUpdComboDetailList)) {
                updComboFlag = comboImgDetailService.saveOrUpdateBatch(addOrUpdComboDetailList);
            }
            // 更新为删除状态
            boolean delComboFlag = true;
            if(!CollectionUtils.isEmpty(delComboDetailList)){
                delComboFlag = comboImgDetailService.update(null,
                        Wrappers.<ComboImgDetail>lambdaUpdate().in(ComboImgDetail::getId, delComboDetailList).
                                set(ComboImgDetail::getUpdatedTime, LocalDateTime.now()).set(ComboImgDetail::getDeletedFlag, 1));
            }

            if (!updComboFlag || !delComboFlag) {
                log.error("ComboImgServiceImpl.update combo update fail. param: {}", bo);
                throw new CommonModuleException("更新多图组件失败");
            }
        }
        // 更新webJson
        WebJsonBo comboWebJson = JSONObject.parseObject(comboImg.getWebJson(), WebJsonBo.class);
        updateWebJson(addOrUpdComboDetailList, comboWebJson);
        comboImg.setWebJson(JSONObject.toJSONString(comboWebJson));
        if (!saveOrUpdate(comboImg)) {
            log.error("ComboImgServiceImpl.save update combo webJson fail. param: {}", bo);
            throw new CommonModuleException("更新多图组件失败");
        }
        return JSONObject.toJSONString(comboWebJson);
    }

    @Override
    public ComboImgDto findById(Long id,Boolean showRecycleBin) {
        ComboImg comboImg = getById(id);
        if (comboImg == null) {
            return null;
        }
        ComboImgDto comboImgDto = BaseHelper.r2t(comboImg, ComboImgDto.class);
        LambdaQueryWrapper<ComboImgDetail> query = Wrappers.<ComboImgDetail>lambdaQuery().eq(ComboImgDetail::getComboImgId, id).eq(ComboImgDetail::getDeletedFlag,0).notIn(ComboImgDetail::getStatus, Collections.singletonList(2)).orderByAsc(ComboImgDetail::getSort);
        if(showRecycleBin){
            query = Wrappers.<ComboImgDetail>lambdaQuery().eq(ComboImgDetail::getComboImgId, id).eq(ComboImgDetail::getDeletedFlag,0).orderByAsc(ComboImgDetail::getSort);
        }
        List<ComboImgDetail> comboImgDetails = comboImgDetailService.getBaseMapper().selectList(query);
        if (CollectionUtils.isEmpty(comboImgDetails)) {
            return comboImgDto;
        }
        List<ComboImgDetailDto> comboImgDetailDtos = comboImgDetails.stream().map(o -> BaseHelper.r2t(o, ComboImgDetailDto.class)).collect(Collectors.toList());
        comboImgDto.setComboImgDetailDtos(comboImgDetailDtos);
        return comboImgDto;
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
    public Boolean delete(Long id) {
        ComboImgDto comboImgDto = findById(id,true);
        if (comboImgDto == null) {
            return false;
        }
        boolean delComboFlag = removeById(id);
        if (!delComboFlag) {
            return false;
        }
        List<ComboImgDetailDto> comboImgDetailDtos = comboImgDto.getComboImgDetailDtos();
        return deleteComboDetailAndLink(comboImgDetailDtos);
    }

    @TransactionalForException
    @Override
    public Boolean delete(List<Long> ids) {
        List<ComboImgDto> comboImgDtos = findByIdList(ids,true);
        if (CollectionUtils.isEmpty(comboImgDtos)) {
            return false;
        }
        boolean delComboFlag = removeByIds(ids);
        if (!delComboFlag) {
            return false;
        }
        List<ComboImgDetailDto> comboImgDetailDtos = comboImgDtos.stream().flatMap(comboImgDto -> comboImgDto.getComboImgDetailDtos().stream()).collect(Collectors.toList());
        return deleteComboDetailAndLink(comboImgDetailDtos);
    }

    private Boolean deleteComboDetailAndLink(List<ComboImgDetailDto> comboImgDetailDtos) {
        if (!CollectionUtils.isEmpty(comboImgDetailDtos)) {
            List<Long>  comboDetailIds = comboImgDetailDtos.stream().map(ComboImgDetailDto::getId).filter(o -> o != null).distinct().collect(Collectors.toList());
            List<Long> linkIds = comboImgDetailDtos.stream().map(ComboImgDetailDto::getLinkModuleId).filter(o -> o != null).distinct().collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(comboDetailIds) && !comboImgDetailService.removeByIds(comboDetailIds)) {
                log.error("ComboImgServiceImpl.deleteModule combo delete fail. param: {}", comboDetailIds);
                throw new CommonModuleException("删除图片失败");
            }
            if (!CollectionUtils.isEmpty(linkIds) && !linkService.removeByIds(linkIds)) {
                log.error("ComboImgServiceImpl.deleteModule combo link delete fail. param: {}", linkIds);
                throw new CommonModuleException("删除坑位链接失败");
            }
        }
        return true;
    }

    @Override
    public List<ComboImgDto> findByIdList(List<Long> ids,Boolean showRecycleBin) {
        List<ComboImgDto> comboDtos = new ArrayList<>();
        List<ComboImg> comboImgs = listByIds(ids);
        if (CollectionUtils.isEmpty(comboImgs)) {
            return comboDtos;
        }

        Map<Long, List<ComboImgDetail>> comboDetailMap = new HashMap<>();
        QueryWrapper<ComboImgDetail> query = Wrappers.<ComboImgDetail>query().in("combo_img_id", ids).notIn("status", Collections.singletonList(2));
        if(showRecycleBin){
            query = Wrappers.<ComboImgDetail>query().in("combo_img_id", ids);
        }
        List<ComboImgDetail> comboImgDetails = comboImgDetailService.baseFindList(query);
        if (!CollectionUtils.isEmpty(comboImgDetails)) {
            comboDetailMap = comboImgDetails.stream().collect(Collectors.toMap(
                    ComboImgDetail::getComboImgId,
                    o -> {
                        List<ComboImgDetail> tmpArr = new ArrayList<>();
                        tmpArr.add(o);
                        return tmpArr;
                    },
                    (ov, nv) -> {
                        ov.addAll(nv);
                        return ov;
                    }));
        }
        for (ComboImg comboImg : comboImgs) {
            ComboImgDto comboImgDto = BaseHelper.r2t(comboImg, ComboImgDto.class);
            Long comboImgId = comboImgDto.getId();
            List<ComboImgDetail> comboDetailById = comboDetailMap.get(comboImgId);
            if (!org.springframework.util.CollectionUtils.isEmpty(comboDetailById)) {
                List<ComboImgDetailDto> comboImgDetailDtos = comboDetailById.stream().map(o -> BaseHelper.r2t(o, ComboImgDetailDto.class)).collect(Collectors.toList());
                comboImgDto.setComboImgDetailDtos(comboImgDetailDtos);
            }
            comboDtos.add(comboImgDto);
        }

        return comboDtos;
    }
}
