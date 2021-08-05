package com.touchealth.platform.processengine.service.impl.module.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.touchealth.platform.processengine.constant.CommonConstant;
import com.touchealth.platform.processengine.constant.CommonConstant.ModuleType;
import com.touchealth.platform.processengine.utils.BaseHelper;
import com.touchealth.platform.processengine.annotation.TransactionalForException;
import com.touchealth.platform.processengine.dao.module.common.BtnGroupDao;
import com.touchealth.platform.processengine.entity.module.common.Btn;
import com.touchealth.platform.processengine.entity.module.common.BtnGroup;
import com.touchealth.platform.processengine.entity.module.common.Link;
import com.touchealth.platform.processengine.entity.page.PageManager;
import com.touchealth.platform.processengine.entity.page.PlatformVersion;
import com.touchealth.platform.processengine.exception.CommonModuleException;
import com.touchealth.platform.processengine.exception.ParameterException;
import com.touchealth.platform.processengine.handler.ModuleHandler;
import com.touchealth.platform.processengine.pojo.bo.CompareBo;
import com.touchealth.platform.processengine.pojo.bo.WebJsonBo;
import com.touchealth.platform.processengine.pojo.bo.module.common.BtnBo;
import com.touchealth.platform.processengine.pojo.bo.module.common.BtnGroupBo;
import com.touchealth.platform.processengine.pojo.dto.module.common.BtnDto;
import com.touchealth.platform.processengine.pojo.dto.module.common.BtnGroupDto;
import com.touchealth.platform.processengine.pojo.dto.module.common.LinkDto;
import com.touchealth.platform.processengine.service.impl.module.BaseModuleServiceImpl;
import com.touchealth.platform.processengine.service.module.common.BtnGroupService;
import com.touchealth.platform.processengine.service.module.common.BtnService;
import com.touchealth.platform.processengine.service.module.common.LinkService;
import com.touchealth.platform.processengine.service.page.PageManagerService;
import com.touchealth.platform.processengine.service.page.PlatformVersionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.touchealth.platform.processengine.constant.CompareConstant.*;
import static com.touchealth.platform.processengine.constant.WebJsonConstant.WEB_LINK_TYPE_OUTSIDE;

/**
 * <p>
 * 按钮组通用组件表 服务实现类
 * </p>
 *
 * @author SunYang
 * @since 2020-11-16
 */
@Service
@Slf4j
public class BtnGroupServiceImpl extends BaseModuleServiceImpl<BtnGroupDao, BtnGroup> implements BtnGroupService {

    @Autowired
    private BtnService btnService;
    @Autowired
    private LinkService linkService;
    @Autowired
    private PageManagerService pageManagerService;
    @Autowired
    private PlatformVersionService platformVersionService;

    @Override
    @TransactionalForException
    public String savePageModule(String webJsonBo, Long pageId) {
        PageManager page = pageManagerService.getById(pageId);
        Assert.notNull(page, "页面不存在");
        BtnGroupBo btnGroupBo = ModuleHandler.parseBtn(page, webJsonBo);

        BtnGroupDto btnGroup = save(btnGroupBo);

        return btnGroup.getWebJson();
    }

    @Override
    @TransactionalForException
    public String clonePageModule(Long moduleId, Long pageId) {
        PageManager page = pageManagerService.getById(pageId);

        Assert.notNull(page, "页面不存在");
        Long versionId = page.getVersionId();
        String channelNo = page.getChannelNo();

        PlatformVersion version = platformVersionService.getById(versionId);
        Assert.notNull(version, "无效的版本号");

        BtnGroup btnGroup = getById(moduleId);
        Assert.notNull(btnGroup, "按钮组件不存在");

        // 更新前端数据字符串（webJson）
        String webJson = btnGroup.getWebJson();
        if (StringUtils.isEmpty(webJson)) {
            return "";
        }

        return savePageModule(webJson, pageId);
    }

    @Override
    public String updatePageModule(String webJsonBo) {
        BtnGroupDto updBtnGroup = update(ModuleHandler.parseBtn(webJsonBo));
        return updBtnGroup == null ? "" : updBtnGroup.getWebJson();
    }

    @Override
    @TransactionalForException
    public Boolean updateModuleStatus(Long moduleId, Integer status) {
        BtnGroupDto btnGroupDto = findById(moduleId);
        if (btnGroupDto == null) {
            log.debug("BtnGroupServiceImpl.updateModuleStatus {} not exits", moduleId);
            throw new CommonModuleException("组件不存在");
        }
        // 更新按钮组状态
        BtnGroup updBtnGroup = new BtnGroup();
        updBtnGroup.setId(btnGroupDto.getId());
        updBtnGroup.setStatus(status);
        boolean updFlag = updateById(updBtnGroup);
        if (!updFlag) {
            log.error("BtnGroupServiceImpl.updateModuleStatus update btn group status fail. {}", moduleId);
            throw new CommonModuleException("按钮组状态更新失败");
        }

        List<BtnDto> buttons = btnGroupDto.getButtons();
        if (!CollectionUtils.isEmpty(buttons)) {
            List<Long> btnIds = buttons.stream().map(BtnDto::getId).filter(Objects::nonNull).collect(Collectors.toList());
            List<Long> linkIds = buttons.stream().map(BtnDto::getLinkModuleId).filter(Objects::nonNull).collect(Collectors.toList());
            // 更新按钮状态
            if (!CollectionUtils.isEmpty(btnIds)) {
                updFlag = btnService.update(new Btn(),
                        Wrappers.<Btn>lambdaUpdate().in(Btn::getId, btnIds).set(Btn::getStatus, status));
                if (!updFlag) {
                    log.error("BtnGroupServiceImpl.updateModuleStatus update btn status fail. {}", moduleId);
                    throw new CommonModuleException("按钮状态更新失败");
                }
            }
            // 更新按钮链接状态
            if (!CollectionUtils.isEmpty(linkIds)) {
                updFlag = linkService.update(new Link(),
                        Wrappers.<Link>lambdaUpdate().in(Link::getId, linkIds).set(Link::getStatus, status));
                if (!updFlag) {
                    log.error("BtnGroupServiceImpl.updateModuleStatus update btn link status fail. {}", moduleId);
                    throw new CommonModuleException("按钮链接状态更新失败");
                }
            }
        }

        return true;
    }

    /**
     * 批量更新组件状态
     *
     * @param moduleIds
     * @param status
     * @return
     */
    @TransactionalForException
    public Boolean batchUpdateModuleStatus(List<Long> moduleIds, Integer status) {
        return batchUpdateModuleStatus(moduleIds, status, null);
    }

    /**
     * 批量更新组件状态
     *
     * @param moduleIds
     * @param status
     * @param versionId
     * @return
     */
    @TransactionalForException
    public Boolean batchUpdateModuleStatus(List<Long> moduleIds, Integer status, Long versionId) {
        List<BtnGroupDto> btnGroupDtoList = findByIdList(moduleIds);
        if (CollectionUtils.isEmpty(btnGroupDtoList)) {
            log.debug("BtnGroupServiceImpl.batchUpdateModuleStatus {} not exits", moduleIds);
            throw new CommonModuleException("组件不存在");
        }
        // 更新按钮组状态
        boolean updFlag = update(new BtnGroup(), Wrappers.<BtnGroup>lambdaUpdate().in(BtnGroup::getId, moduleIds).set(BtnGroup::getStatus, status));
        if (!updFlag) {
            log.error("BtnGroupServiceImpl.batchUpdateModuleStatus update btn group status fail. {}", moduleIds);
            throw new CommonModuleException("按钮组状态更新失败");
        }

        List<BtnDto> buttons = btnGroupDtoList.stream().flatMap(btnGroups -> btnGroups.getButtons().stream()).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(buttons)) {
            List<Long> btnIds = buttons.stream().map(BtnDto::getId).filter(Objects::nonNull).collect(Collectors.toList());
            List<Long> linkIds = buttons.stream().map(BtnDto::getLinkModuleId).filter(Objects::nonNull).collect(Collectors.toList());
            // 更新按钮状态
            if (!CollectionUtils.isEmpty(btnIds)) {
                LambdaUpdateWrapper<Btn> uw = Wrappers.<Btn>lambdaUpdate().in(Btn::getId, btnIds)
                        .set(Btn::getStatus, status);
                if (versionId != null) {
                    uw.set(Btn::getVersion, versionId);
                }
                updFlag = btnService.update(new Btn(), uw);
                if (!updFlag) {
                    log.error("BtnGroupServiceImpl.batchUpdateModuleStatus update btn status fail. {}", btnIds);
                    throw new CommonModuleException("按钮状态更新失败");
                }
            }
            // 更新按钮链接状态
            if (!CollectionUtils.isEmpty(linkIds)) {
                LambdaUpdateWrapper<Link> uw = Wrappers.<Link>lambdaUpdate().in(Link::getId, linkIds).set(Link::getStatus, status);
                if (versionId != null) {
                    uw.set(Link::getVersion, versionId);
                }
                updFlag = linkService.update(new Link(), uw);
                if (!updFlag) {
                    log.error("BtnGroupServiceImpl.batchUpdateModuleStatus update btn link status fail. {}", linkIds);
                    throw new CommonModuleException("按钮链接状态更新失败");
                }
            }
        }

        return true;
    }

    @Override
    public String findPageModuleById(Long id) {
        BtnGroupDto btnGroupDto = findById(id);
        return btnGroupDto == null ? "" : btnGroupDto.getWebJson();
    }

    @Override
    public List<String> findPageModuleByIdList(List<Long> ids) {
        List<BtnGroupDto> btnGroupDtoList = findByIdList(ids);
        return CollectionUtils.isEmpty(btnGroupDtoList) ? new ArrayList<>() :
                btnGroupDtoList.stream().map(BtnGroupDto::getWebJson).collect(Collectors.toList());
    }

    @Override
    public Boolean deletePageModule(List<Long> ids) {
        return null;
    }

    @Override
    @TransactionalForException
    public Boolean restoreModule(List<Long> ids) {
        return batchUpdateModuleStatus(new ArrayList<>(ids), CommonConstant.STATUS.DRAFT.getCode());
    }

    @Override
    @TransactionalForException
    public Boolean restoreModule(Collection<Long> ids, Long versionId) {
        return batchUpdateModuleStatus(new ArrayList<>(ids), CommonConstant.STATUS.DRAFT.getCode(), versionId);
    }

    /**
     * 按钮修改前后比对方法（仅新增和编辑组件时使用）
     *
     * @param webJson    修改后的前端传入的按钮组件，为空表示被删除
     * @param btnGroupId 修改的按钮组件ID，为空表示新增
     * @return
     */
    @Override
    public List<CompareBo> compare(String webJson, Long btnGroupId) {
        List<CompareBo> ret = new ArrayList<>();
        if (StringUtils.isEmpty(webJson) && btnGroupId == null) {
            log.debug("BtnGroupServiceImpl.compare param is null");
            return ret;
        }

        if (StringUtils.isEmpty(webJson)) { // 组件被删除
            BtnGroupDto oldBtnGroupDto = findById(btnGroupId);
            if (oldBtnGroupDto == null) {
                log.debug("BtnGroupServiceImpl.compare {} not exits", btnGroupId);
                throw new CommonModuleException("按钮组件不存在");
            }
            String btnGroupWebJson = oldBtnGroupDto.getWebJson();
            Assert.isTrue(!StringUtils.isEmpty(btnGroupWebJson), "组件webJson数据异常");
            WebJsonBo webJsonBo = JSONObject.parseObject(btnGroupWebJson, WebJsonBo.class);

            ret.add(new CompareBo(null, ModuleType.BUTTON, "", OP_DEL, BUTTON_STYLE, "",
                    LAYOUT_TYPE_MAP.getOrDefault(webJsonBo.getLayoutType(), "")));

            for (WebJsonBo.WebJsonButtonBo button : webJsonBo.getData().getButtonList()) {
                buttonOpRecord(ret, OP_DEL, null, button, null);
            }
        } else { // 组件被更新
            Assert.isTrue(!StringUtils.isEmpty(webJson), "参数不能为空");
            WebJsonBo webJsonBo = JSON.parseObject(webJson, WebJsonBo.class);
            Assert.notNull(webJsonBo.getData(), "参数不能为空");
            Assert.notEmpty(webJsonBo.getData().getButtonList(), "按钮列表不能为空");

            Integer layoutType = webJsonBo.getLayoutType();
            List<Long> updBtnIds = new ArrayList<>(); // 存放本次更新的按钮ID集合

            if (btnGroupId == null) { // 新增按钮组件

                ret.add(new CompareBo(null, ModuleType.BUTTON, "", OP_ADD, BUTTON_STYLE, "",
                        LAYOUT_TYPE_MAP.getOrDefault(layoutType, "")));

                for (WebJsonBo.WebJsonButtonBo button : webJsonBo.getData().getButtonList()) {
                    buttonOpRecord(ret, OP_ADD, null, null, button);
                }

            } else { // 编辑按钮组件
                BtnGroupDto oldBtnGroupDto = findById(btnGroupId);
                if (oldBtnGroupDto == null) {
                    log.error("BtnGroupServiceImpl.compare btn group not exits. param: {}", btnGroupId);
                    throw new CommonModuleException("获取按钮失败");
                }
                try {
                    WebJsonBo oldWebJson = JSONObject.parseObject(oldBtnGroupDto.getWebJson(), WebJsonBo.class);

                    if (oldWebJson.getData() != null && !CollectionUtils.isEmpty(oldWebJson.getData().getButtonList())) {

                        // 比对按钮组布局
                        Integer oldLayoutType = oldWebJson.getLayoutType();
                        if (!layoutType.equals(oldLayoutType)) {
                            ret.add(new CompareBo(btnGroupId, ModuleType.BUTTON, "", OP_UPD, BUTTON_STYLE,
                                    LAYOUT_TYPE_MAP.getOrDefault(oldLayoutType, ""),
                                    LAYOUT_TYPE_MAP.getOrDefault(layoutType, "")));
                        }

                        List<WebJsonBo.WebJsonButtonBo> oldBtnListWebJson = oldWebJson.getData().getButtonList();
                        Map<Long, WebJsonBo.WebJsonButtonBo> oldBtnMap = oldBtnListWebJson.stream()
                                .collect(Collectors.toMap(WebJsonBo.WebJsonButtonBo::getModuleUniqueId, o -> o));

                        // 比对每个按钮
                        for (WebJsonBo.WebJsonButtonBo btnWebJson : webJsonBo.getData().getButtonList()) {
                            Long btnId = btnWebJson.getId();
                            Long moduleUniqueId = btnWebJson.getModuleUniqueId();
                            WebJsonBo.WebJsonButtonBo oldBtnWebJson = oldBtnMap.get(moduleUniqueId);
                            // 若前端传入的webJson中的按钮没有ID，那么新增按钮
                            if (btnId == null || moduleUniqueId == null || oldBtnWebJson == null) {
                                buttonOpRecord(ret, OP_ADD, null, null, btnWebJson);
                                continue;
                            }
                            updBtnIds.add(moduleUniqueId);

                            // 更新的按钮操作记录
                            buttonOpRecord(ret, OP_UPD, btnId, oldBtnWebJson, btnWebJson);
                        }

                        // 删除的按钮操作记录
                        List<BtnDto> delBtnList = oldBtnGroupDto.getButtons().stream().filter(o -> !updBtnIds.contains(o.getModuleUniqueId())).collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(delBtnList)) {
                            for (BtnDto delBtn : delBtnList) {
                                WebJsonBo.WebJsonButtonBo delBtnWebJson = oldBtnMap.get(delBtn.getId());
                                buttonOpRecord(ret, OP_DEL, null, delBtnWebJson, null);
                            }
                        }
                    }
                } catch (JSONException e) {
                    throw new ParameterException("webJson格式异常");
                } catch (Exception e) {
                    log.error("BtnGroupServiceImpl.compare has exception", e);
                    throw new ParameterException("webJson解析异常");
                }
            }
        }

        return ret;
    }

    /**
     * 添加按钮操作记录
     *
     * @param ret             操作记录集合
     * @param opName          操作类型。如：{@linkplain com.touchealth.platform.processengine.constant.CompareConstant#OP_UPD OP_UPD}、
     *                                      {@link com.touchealth.platform.processengine.constant.CompareConstant#OP_ADD OP_ADD}、
     *                                      {@link com.touchealth.platform.processengine.constant.CompareConstant#OP_DEL OP_DEL}
     * @param oldBtnWebJsonBo 操作前的组件webJson
     * @parma btnId             当更新操作时传入。
     * @parma newBtnWebJsonBo   操作后的组件webJson
     */
    private void buttonOpRecord(List<CompareBo> ret, String opName, Long btnId,
                                WebJsonBo.WebJsonButtonBo oldBtnWebJsonBo, WebJsonBo.WebJsonButtonBo newBtnWebJsonBo) {
        String oldTitle = "", title = "";
        String oldColor = "", color = "";
        String oldBgColor = "", bgColor = "";
        String oldLinkName = "", linkName = "";
        String oldLinkParam = "", linkParam = "";
        String oldBgImg = "", bgImg = "";

        if (oldBtnWebJsonBo != null) {
            oldTitle = oldBtnWebJsonBo.getTitle();
            oldColor = oldBtnWebJsonBo.getColor();
            oldBgColor = oldBtnWebJsonBo.getBgColor();
            oldBgImg = oldBtnWebJsonBo.getImgUrl();
            // 老的链接属性
            WebJsonBo.WebJsonLinkBo oldLink = oldBtnWebJsonBo.getLink();
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

        if (newBtnWebJsonBo != null) {
            title = newBtnWebJsonBo.getTitle();
            color = newBtnWebJsonBo.getColor();
            bgColor = newBtnWebJsonBo.getBgColor();
            bgImg = newBtnWebJsonBo.getImgUrl();
            // 新的链接属性
            WebJsonBo.WebJsonLinkBo link = newBtnWebJsonBo.getLink();
            if (link != null) {
                Integer linkType = link.getLinkType();
                if (WEB_LINK_TYPE_OUTSIDE.equals(linkType)) { // 站外链接
                    linkName = link.getPageUrl();
                } else { // 站内链接
                    linkName = link.getPageName();
                    linkParam = Optional.ofNullable(link.getParams()).orElse(new HashMap<>()).toString()
                            .replaceAll("\\{", "").replaceAll("}", "");
                }
            }
        }

        // 删除组件时，组件名称取旧的标题，更新和添加时取新的标题
        String moduleName = OP_DEL.equals(opName) ? oldTitle : title;

        /* 更新操作需要比对，新增和删除不需要 */
        if (OP_UPD.equals(opName)) {
            // 比对按钮名
            if (!Optional.ofNullable(title).orElse("").equals(oldTitle)) {
                recordButtonOp(ret, oldTitle, title, moduleName, OP_UPD, BUTTON_NAME, btnId);
            }
            // 比对按钮颜色
            if (!Optional.ofNullable(color).orElse("").equals(oldColor)) {
                recordButtonOp(ret, oldColor, color, moduleName, OP_UPD, BUTTON_COLOR, btnId);
            }
            // 比对按钮背景颜色
            if (!Optional.ofNullable(bgColor).orElse("").equals(oldBgColor)) {
                recordButtonOp(ret, oldBgColor, bgColor, moduleName, OP_UPD, BUTTON_BG_COLOR, btnId);
            }
            // 比对按钮背景图片
            if (!Optional.ofNullable(bgImg).orElse("").equals(oldBgImg)) {
                recordButtonOp(ret, oldBgImg, bgImg, moduleName, OP_UPD, BUTTON_BG_IMG, btnId);
            }
            // 比对链接
            if (!Optional.ofNullable(linkName).orElse("").equals(oldLinkName)) {
                recordButtonOp(ret, oldLinkName, linkName, moduleName, OP_UPD, LINK_PATH, btnId);
            }
            // 比对链接参数
            if (!Optional.ofNullable(linkParam).orElse("").equals(oldLinkParam)) {
                recordButtonOp(ret, oldLinkParam, linkParam, moduleName, OP_UPD, LINK_PARAM, btnId);
            }
        } else {
            // 按钮名操作记录
            recordButtonOp(ret, oldTitle, title, moduleName, opName, BUTTON_NAME, null);
            // 按钮颜色操作记录
            recordButtonOp(ret, oldColor, color, moduleName, opName, BUTTON_COLOR, null);
            // 按钮背景颜色操作记录
            recordButtonOp(ret, oldBgColor, bgColor, moduleName, opName, BUTTON_BG_COLOR, null);
            // 按钮背景图片操作记录
            recordButtonOp(ret, oldBgImg, bgImg, moduleName, opName, BUTTON_BG_IMG, null);
            // 链接操作记录
            recordButtonOp(ret, oldLinkName, linkName, moduleName, opName, LINK_PATH, null);
            // 链接参数操作记录
            recordButtonOp(ret, oldLinkParam, linkParam, moduleName, opName, LINK_PARAM, null);
        }
    }

    /**
     * 记录按钮操作记录
     *
     * @param compareList 比对结果集合
     * @param oldValue    改前的值
     * @param newValue    改后的值
     * @param opName      操作
     * @param opContent   操作内容
     * @param moduleId    组件ID
     * @return
     */
    private void recordButtonOp(List<CompareBo> compareList, String oldValue, String newValue, String moduleName,
                                String opName, String opContent, Long moduleId) {
        if (!StringUtils.isEmpty(oldValue) || !StringUtils.isEmpty(newValue)) {
            // 新增链接参数操作记录
            compareList.add(new CompareBo(null, ModuleType.BUTTON, moduleName, opName, opContent, oldValue, newValue));
        }
    }

    @Override
    @TransactionalForException
    public BtnGroupDto save(BtnGroupBo bo) {
        Assert.notNull(bo, "添加按钮参数异常");
        BtnGroupDto ret;
        String btnGroupWebJson = bo.getWebJson();
        Map<Long, Long> linkIdUniqueMap = new HashMap<>();

        List<BtnBo> btnBoList = bo.getButtons();
        if (CollectionUtils.isEmpty(btnBoList)) {
            return null;
        }

        // 添加按钮组
        bo.setId(null);
        BtnGroup btnGroup = BaseHelper.r2t(bo, BtnGroup.class);
        btnGroup.setCategoryId(CommonConstant.MODULE_CATEGORY.COMMON.getCode());
        boolean saveBtnGroupFlag = save(btnGroup);
        if (!saveBtnGroupFlag) {
            log.error("BtnGroupServiceImpl.save save btn group fail. param: {}", bo);
            throw new CommonModuleException("添加按钮组失败");
        }

        ret = BaseHelper.r2t(btnGroup, BtnGroupDto.class);
        ret.setButtons(new ArrayList<>());

        // 添加按钮
        Long id = btnGroup.getId();
        bo.setId(id);
        List<Btn> btns = new ArrayList<>();
        for (BtnBo btnBo : btnBoList) {
            btnBo.setId(null);
            Btn btn = BaseHelper.r2t(btnBo, Btn.class);
            btn.setChannelNo(btnGroup.getChannelNo());
            btn.setBtnGroupId(btnGroup.getId());
            btn.setVersion(btnGroup.getVersion());
            btn.setStatus(btnGroup.getStatus());

            LinkDto linkDto = btnBo.getLinkDto();
            if (linkDto != null) {
                // 添加链接
                linkDto.setId(null);
                linkDto.setChannelNo(btnGroup.getChannelNo());
                linkDto.setPageId(btnGroup.getPageId());
                linkDto.setVersion(btnGroup.getVersion());
                linkDto.setStatus(btnGroup.getStatus());

                LinkDto linkDtoDb = linkService.save(linkDto);
                Long linkId = linkDtoDb.getId();
                btn.setLinkModuleId(linkId);
                linkIdUniqueMap.putIfAbsent(linkId, linkDtoDb.getModuleUniqueId());
            }

            btns.add(btn);

            ret.getButtons().add(BaseHelper.r2t(btn, BtnDto.class));
        }
        boolean saveBtnFlag = btnService.saveBatch(btns);
        if (!saveBtnFlag) {
            log.error("BtnGroupServiceImpl.save save btn fail. param: {}", bo);
            throw new CommonModuleException("保存按钮失败");
        }

        // 再次保存按钮组，为了更新按钮组中的webJson里的按钮组ID和按钮ID
        updateWebJson(btnGroupWebJson, linkIdUniqueMap, btnGroup, btns);

        saveBtnGroupFlag = saveOrUpdate(btnGroup);
        if (!saveBtnGroupFlag) {
            log.error("BtnGroupServiceImpl.save update btn group webJson fail. param: {}", bo);
            throw new CommonModuleException("添加按钮组失败");
        }

        BaseHelper.copyNotNullProperties(btnGroup, ret);
        return ret;
    }

    @Override
    @TransactionalForException
    public BtnGroupDto update(BtnGroupBo bo) throws CommonModuleException {
        Long btnGroupId = bo.getId();

        BtnGroup btnGroupDb = getById(btnGroupId);
        if (btnGroupDb == null) {
            throw new CommonModuleException("按钮组不存在");
        }
        BaseHelper.copyNotNullProperties(bo, btnGroupDb);

        List<BtnBo> btnBoList = bo.getButtons();
        String channelNo = btnGroupDb.getChannelNo();
        Long version = btnGroupDb.getVersion();
        Integer status = btnGroupDb.getStatus();
        Long pageId = btnGroupDb.getPageId();
        String btnGroupWebJson = btnGroupDb.getWebJson();
        Map<Long, Long> linkIdUniqueMap = new HashMap<>();

        // 更新按钮
        if (!CollectionUtils.isEmpty(btnBoList)) {
            List<Btn> addOrUpdBtnList = new ArrayList<>();
            Map<Long, Btn> updBtnMap = new HashMap<>();
            QueryWrapper<Btn> qw = Wrappers.<Btn>query().eq("btn_group_id", btnGroupId);
            List<Btn> btnsDb = btnService.baseFindList(qw);

            // 将所有需要新增和更新的按钮放到一个Map中
            for (BtnBo btnBo : btnBoList) {
                Long btnId = btnBo.getId();
                Btn btn = BaseHelper.r2t(btnBo, Btn.class);
                btn.setChannelNo(channelNo);
                btn.setVersion(version);
                btn.setBtnGroupId(btnGroupId);
                btn.setStatus(status);

                LinkDto linkDto = btnBo.getLinkDto();
                if (linkDto != null) {
                    // 添加|更新 链接
                    linkDto.setChannelNo(channelNo);
                    linkDto.setPageId(pageId);
                    linkDto.setVersion(version);
                    linkDto.setStatus(status);

                    if (linkDto.getId() == null) {
                        LinkDto linkDtoDb = linkService.save(linkDto);
                        Long linkId = linkDtoDb.getId();
                        btn.setLinkModuleId(linkId);
                        linkIdUniqueMap.putIfAbsent(linkId, linkDtoDb.getModuleUniqueId());
                    } else {
                        LinkDto updLink = linkService.update(linkDto);
                        if (updLink == null) {
                            log.error("BtnGroupServiceImpl.update btn group update link fail. param: {}", linkDto);
                        }
                    }
                }

                addOrUpdBtnList.add(btn);
                if (btnId == null) { // 需要新增的按钮
                    // pass
                } else { // 需要更新的按钮
                    updBtnMap.put(btnId, btn);
                }
            }
            // 将现有按钮组下的无用按钮置为删除状态
            List<Long> delBtnIdList = btnsDb.stream().map(Btn::getId)
                    .filter(id -> updBtnMap.get(id) == null).collect(Collectors.toList());

            boolean updBtnFlag = btnService.saveOrUpdateBatch(addOrUpdBtnList);
            boolean delBtnFlag = btnService.removeByIds(delBtnIdList);
            if (!updBtnFlag && !delBtnFlag) {
                log.error("BtnGroupServiceImpl.update btn update fail. param: {}", bo);
                throw new CommonModuleException("更新按钮失败");
            }

            // 更新按钮组webJson
            updateWebJson(btnGroupWebJson, linkIdUniqueMap, btnGroupDb, addOrUpdBtnList);
        }

        // 更新按钮组
        boolean updBtnGroupFlag = updateById(btnGroupDb);
        if (!updBtnGroupFlag) {
            log.error("BtnGroupServiceImpl.update btn group update fail. param: {}", bo);
            throw new CommonModuleException("更新按钮组失败");
        }

        return findById(btnGroupId);
    }

    /**
     * 更新按钮组的webJson字段
     *
     * @param btnGroupWebJson
     * @param linkIdUniqueMap
     * @param btnGroup
     * @param addOrUpdBtnList
     */
    private void updateWebJson(String btnGroupWebJson, Map<Long, Long> linkIdUniqueMap, BtnGroup btnGroup, List<Btn> addOrUpdBtnList) {
        WebJsonBo btnGroupWebJsonBo = JSONObject.parseObject(btnGroupWebJson, WebJsonBo.class);
        if (btnGroupWebJsonBo != null) {
            // 更新按钮组的webJson字段中的按钮组ID
            btnGroupWebJsonBo.setId(btnGroup.getId());
            btnGroupWebJsonBo.setModuleUniqueId(btnGroup.getModuleUniqueId());
            if (btnGroupWebJsonBo.getData() != null && !CollectionUtils.isEmpty(btnGroupWebJsonBo.getData().getButtonList())) {
                List<WebJsonBo.WebJsonButtonBo> btnWebJsonList = btnGroupWebJsonBo.getData().getButtonList();
                for (int i = 0; i < btnWebJsonList.size() && btnWebJsonList.size() == addOrUpdBtnList.size(); i++) {
                    // 更新按钮的webJson字段中的按钮ID
                    WebJsonBo.WebJsonButtonBo webJsonButtonBo = btnWebJsonList.get(i);
                    Btn btn = addOrUpdBtnList.get(i);
                    webJsonButtonBo.setId(btn.getId());
                    webJsonButtonBo.setModuleUniqueId(btn.getModuleUniqueId());
                    // 更新按钮链接ID
                    WebJsonBo.WebJsonLinkBo link = webJsonButtonBo.getLink();
                    if (link != null) {
                        Long linkId = btn.getLinkModuleId();
                        link.setId(linkId);
                        link.setModuleUniqueId(linkIdUniqueMap.get(linkId));
                    }
                }
            }
            btnGroup.setWebJson(JSONObject.toJSONString(btnGroupWebJsonBo));
        }
    }

    @Override
    public BtnGroupDto findById(Long id) {
        BtnGroup btnGroup = getById(id);
        if (btnGroup == null) {
            return null;
        }
        BtnGroupDto btnGroupDto = BaseHelper.r2t(btnGroup, BtnGroupDto.class);
        LambdaQueryWrapper<Btn> query = Wrappers.<Btn>lambdaQuery().eq(Btn::getBtnGroupId, id).orderByAsc(Btn::getSort);
        List<Btn> btnList = btnService.getBaseMapper().selectList(query);
        if (!CollectionUtils.isEmpty(btnList)) {
            List<BtnDto> btnDtoList = btnList.stream().map(o -> BaseHelper.r2t(o, BtnDto.class)).collect(Collectors.toList());
            btnGroupDto.setButtons(btnDtoList);
        }
        return btnGroupDto;
    }

    @Override
    public List<BtnGroupDto> findByIdList(List<Long> ids) {
        List<BtnGroupDto> btnGroupDtos = new ArrayList<>();
        List<BtnGroup> btnGroups = listByIds(ids);
        if (CollectionUtils.isEmpty(btnGroups)) {
            return btnGroupDtos;
        }

        Map<Long, List<Btn>> btnMap = new HashMap<>();
        QueryWrapper<Btn> query = Wrappers.<Btn>query().in("btn_group_id", ids);
        List<Btn> btns = btnService.baseFindList(query);
        if (!CollectionUtils.isEmpty(btns)) {
            btnMap = btns.stream().collect(Collectors.toMap(
                    Btn::getBtnGroupId,
                    o -> {
                        List<Btn> tmpArr = new ArrayList<>();
                        tmpArr.add(o);
                        return tmpArr;
                    },
                    (ov, nv) -> {
                        ov.addAll(nv);
                        return ov;
                    }));
        }

        for (BtnGroup btnGroup : btnGroups) {
            BtnGroupDto btnGroupDto = BaseHelper.r2t(btnGroup, BtnGroupDto.class);
            Long btnGroupId = btnGroupDto.getId();
            List<Btn> btnsByGroup = btnMap.get(btnGroupId);
            if (!CollectionUtils.isEmpty(btnsByGroup)) {
                List<BtnDto> btnDtos = btnsByGroup.stream().map(o -> BaseHelper.r2t(o, BtnDto.class)).collect(Collectors.toList());
                btnGroupDto.setButtons(btnDtos);
            }
            btnGroupDtos.add(btnGroupDto);
        }

        return btnGroupDtos;
    }

    @Override
    @TransactionalForException
    public Boolean delete(Long id) {
        BtnGroupDto btnGroupDto = findById(id);
        if (btnGroupDto == null) {
            return false;
        }
        boolean delBtnGroupFlag = removeById(id);
        if (!delBtnGroupFlag) {
            return false;
        }
        List<BtnDto> buttons = btnGroupDto.getButtons();
        return deleteButtonsAndLinks(buttons);
    }

    @Override
    @TransactionalForException
    public Boolean delete(List<Long> ids) {
        List<BtnGroupDto> btnGroupDtos = findByIdList(ids);
        if (CollectionUtils.isEmpty(btnGroupDtos)) {
            return false;
        }
        boolean delBtnGroupFlag = removeByIds(ids);
        if (!delBtnGroupFlag) {
            return false;
        }

        List<BtnDto> buttons = btnGroupDtos.stream().flatMap(btnGroups -> btnGroups.getButtons().stream()).collect(Collectors.toList());
        return deleteButtonsAndLinks(buttons);
    }

    /**
     * 批量删除按钮和其链接
     *
     * @param buttons
     * @return
     */
    private Boolean deleteButtonsAndLinks(List<BtnDto> buttons) {
        if (!CollectionUtils.isEmpty(buttons)) {
            List<Long> btnIds = buttons.stream().map(BtnDto::getId).filter(o -> o != null).distinct().collect(Collectors.toList());
            List<Long> linkIds = buttons.stream().map(BtnDto::getLinkModuleId).filter(o -> o != null).distinct().collect(Collectors.toList());
            boolean delBtnFlag = btnService.removeByIds(btnIds);
            if (!delBtnFlag) {
                log.error("BtnGroupServiceImpl.deleteModule btn delete fail. param: {}", btnIds);
                throw new CommonModuleException("删除按钮失败");
            }
            boolean delLinkFlag = linkService.delete(linkIds);
            if (!delLinkFlag) {
                log.error("BtnGroupServiceImpl.deleteModule btn link delete fail. param: {}", linkIds);
                throw new CommonModuleException("删除按钮链接失败");
            }
        }
        return null;
    }

}
