package com.touchealth.platform.processengine.service.impl.page;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchealth.common.basic.response.PageInfo;
import com.touchealth.common.page.Pager;
import com.touchealth.common.utils.DesensitizationUtils;
import com.touchealth.physical.api.dto.hsp.HospitalDto;
import com.touchealth.physical.api.dto.hsp.SetMealDto;
import com.touchealth.physical.api.query.PageCommonQuery;
import com.touchealth.physical.api.service.hsp.HospitalSetMealApi;
import com.touchealth.platform.basic.response.PlatFormResponse;
import com.touchealth.platform.processengine.annotation.TransactionalForException;
import com.touchealth.platform.processengine.constant.CommonConstant;
import com.touchealth.platform.processengine.constant.PageCenterConsts;
import com.touchealth.platform.processengine.dao.page.PlatformChannelDao;
import com.touchealth.platform.processengine.entity.page.PageManager;
import com.touchealth.platform.processengine.entity.page.PageModule;
import com.touchealth.platform.processengine.entity.page.PlatformChannel;
import com.touchealth.platform.processengine.entity.page.PlatformVersion;
import com.touchealth.platform.processengine.entity.user.User;
import com.touchealth.platform.processengine.entity.user.UserChannel;
import com.touchealth.platform.processengine.exception.BusinessException;
import com.touchealth.platform.processengine.pojo.dto.page.ChannelConfigDataDto;
import com.touchealth.platform.processengine.pojo.dto.page.ChannelConfigDto;
import com.touchealth.platform.processengine.pojo.dto.platformchannel.RentDto;
import com.touchealth.platform.processengine.pojo.dto.user.UserCenterListDto;
import com.touchealth.platform.processengine.pojo.request.page.PageModuleRequest;
import com.touchealth.platform.processengine.pojo.request.page.PlatformChannelConfigRequest;
import com.touchealth.platform.processengine.pojo.request.page.PlatformChannelRequest;
import com.touchealth.platform.processengine.pojo.request.page.PresetPageRequest;
import com.touchealth.platform.processengine.service.impl.BaseServiceImpl;
import com.touchealth.platform.processengine.service.impl.module.ModuleService;
import com.touchealth.platform.processengine.service.page.PageManagerService;
import com.touchealth.platform.processengine.service.page.PageModuleService;
import com.touchealth.platform.processengine.service.page.PlatformChannelService;
import com.touchealth.platform.processengine.service.page.PlatformVersionService;
import com.touchealth.platform.processengine.service.user.UserChannelService;
import com.touchealth.platform.processengine.service.user.UserService;
import com.touchealth.platform.processengine.utils.BaseHelper;
import com.touchealth.platform.processengine.utils.ConvertUtils;
import com.touchealth.platform.processengine.utils.PageUtils;
import com.touchealth.platform.user.client.api.RentApi;
import com.touchealth.platform.user.client.api.UserApi;
import com.touchealth.platform.user.client.dto.bo.FindByParamsBo;
import com.touchealth.platform.user.client.dto.request.UserReq;
import com.touchealth.platform.user.client.dto.response.RentRes;
import com.touchealth.platform.user.client.dto.response.UserRes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.touchealth.platform.processengine.constant.CommonConstant.SC_CHANNEL_ID;
import static com.touchealth.platform.processengine.constant.UserConstant.USER_TYPE_PROCESS_ENGINE_ADMIN;

/**
 * @author liufengqiang
 * @date 2020-11-24 15:48:24
 */
@Service
@Slf4j
public class PlatformChannelServiceImpl extends BaseServiceImpl<PlatformChannelDao, PlatformChannel> implements PlatformChannelService {

    @Resource
    private PlatformVersionService platformVersionService;
    @Resource
    private PageManagerService pageManagerService;
    @Resource
    private PageModuleService pageModuleService;
    @Resource
    private ModuleService moduleService;
    @Resource
    private UserService userService;
    @Resource
    private UserChannelService userChannelService;
    @Resource
    private UserApi userApi;
    @Resource
    private HospitalSetMealApi hospitalSetMealApi;
    @Resource
    private RentApi rentApi;
    private com.touchealth.platform.user.client.constant.CommonConstant.FindParamMatchType NOT_IN =
            com.touchealth.platform.user.client.constant.CommonConstant.FindParamMatchType.NOT_IN;

    @Override
    @TransactionalForException
    public PlatformChannel saveChannel(Long userId, PlatformChannelRequest request) {
        Assert.notNull(request, "参数不能为空");
        PlatformChannel platformChannel = BaseHelper.r2t(request, PlatformChannel.class);
        platformChannel.setId(IdWorker.getId(platformChannel));

        // 新增渠道
        String levelIndex;
        if (platformChannel.getParentId() != null) {
            PlatformChannel parentChannel = getById(platformChannel.getParentId());
            levelIndex = platformChannel.getId() + "-" + parentChannel.getLevelIndex();
        } else {
            Long platformChannelId = platformChannel.getId();
            levelIndex = platformChannelId == null ? "" : String.valueOf(platformChannelId);
        }
        platformChannel.setLevelIndex(levelIndex);
        platformChannel.setShelfStatus(0);

        // 创建超级管理员
        User user = new User();
        user.setMobileNo(request.getAdminMobileNo());
        user.setEmail(request.getAdminAccount());
        user.setRealName(platformChannel.getChannelName() + "超级管理员");
        user.setPassword(request.getAdminPassword());
        user.setUserType(USER_TYPE_PROCESS_ENGINE_ADMIN);
        user.setChannelNo(request.getChannelNo());
        userService.createUser(user);
        platformChannel.setAdminId(user.getId());
        save(platformChannel);

        // 添加超管对应渠道权限
        UserChannel userChannel = new UserChannel();
        userChannel.setUserId(user.getId());
        userChannel.setChannelNoList(platformChannel.getChannelNo());
        userChannelService.save(userChannel);

        // 更新所有父渠道权限
        if (platformChannel.getParentId() != null && StringUtils.isNotBlank(platformChannel.getLevelIndex())) {
            Set<Long> channelIds = Arrays.stream(platformChannel.getLevelIndex().split("-")).map(Long::parseLong).collect(Collectors.toSet());
            List<PlatformChannel> platformChannels = list(Wrappers.<PlatformChannel>lambdaQuery().in(PlatformChannel::getId, channelIds));
            if (CollectionUtils.isNotEmpty(platformChannels)) {
                Set<Long> userIds = platformChannels.stream().map(PlatformChannel::getAdminId).collect(Collectors.toSet());
                List<UserChannel> userChannels = userChannelService.list(Wrappers.<UserChannel>lambdaQuery().in(UserChannel::getUserId, userIds));

                userChannels.forEach(o -> {
                    Set<String> channelSet = Arrays.stream(o.getChannelNoList().split(",")).collect(Collectors.toSet());
                    channelSet.add(platformChannel.getChannelNo());
                    o.setChannelNoList(String.join(",", channelSet));
                });
                userChannelService.updateBatchById(userChannels);
            }
        }

        // 新增第一个版本
        PlatformVersion newVersion = platformVersionService.createNewVersion(platformChannel.getChannelNo(), null);

        // 复制势成云所有的预置页面和手动添加的页面，以及勾选了业务对应的所有页面
        PlatformChannel mainChannel = getById(1L);
        Set<Integer> businessType = new HashSet<>();
        businessType.add(PageCenterConsts.BusinessType.COMMON.getCode());
        if (StringUtils.isNotBlank(platformChannel.getBusinessType())) {
            businessType.addAll(Arrays.stream(platformChannel.getBusinessType().split(",")).map(Integer::parseInt).collect(Collectors.toSet()));
        }

        List<PageManager> oldPageManagers = pageManagerService.listByVersionId(platformVersionService.getDefaultVersionId(mainChannel.getChannelNo(), null), false, null);
        oldPageManagers.forEach(o -> o.setIsHide(!businessType.contains(o.getBusinessType())));

        pageManagerService.copyPageToChannel(oldPageManagers, newVersion, platformChannel.getChannelNo());
        return platformChannel;
    }

    @Override
    @TransactionalForException
    public void deleteChannel(Long id) {
        PlatformChannel platformChannel = getById(id);
        Assert.notNull(platformChannel, "渠道不存在");
        if (platformChannel.getParentId() == null) {
            // 删除组件
            List<PageModule> pageModules = pageModuleService.listByChannelNo(platformChannel.getChannelNo());
            pageModules.forEach(o -> moduleService.getInstance(o.getModuleType()).deletePageModule(o.getModuleId()));

            // 删除组件关联
            pageModuleService.remove(Wrappers.<PageModule>lambdaQuery().eq(PageModule::getChannelNo, platformChannel.getChannelNo()));

            // 删除页面
            pageManagerService.remove(Wrappers.<PageManager>lambdaQuery().eq(PageManager::getChannelNo, platformChannel.getChannelNo()));
        }

        // 删除机构及子机构管理员
        LambdaQueryWrapper<PlatformChannel> queryWrapper = Wrappers.<PlatformChannel>lambdaQuery()
                .eq(PlatformChannel::getId, id)
                .or().likeLeft(PlatformChannel::getLevelIndex, "-" + id);
        List<PlatformChannel> platformChannels = list(queryWrapper);
        userService.removeByIds(platformChannels.stream().map(PlatformChannel::getAdminId).collect(Collectors.toSet()));

        // 删除机构以及子机构
        remove(queryWrapper);
    }

    @Override
    public void updateChannel(Long userId, PlatformChannel platformChannel, PlatformChannelRequest request) {
        // 更新业务
        if (!request.getBusinessType().equals(platformChannel.getBusinessType())) {
            PlatformVersion draftVersion = platformVersionService.getDefaultVersion(platformChannel.getChannelNo(), null);

            Set<Integer> businessType = new HashSet<>();
            businessType.add(PageCenterConsts.BusinessType.COMMON.getCode());
            if (StringUtils.isNotBlank(request.getBusinessType())) {
                businessType.addAll(Arrays.stream(request.getBusinessType().split(",")).map(Integer::parseInt).collect(Collectors.toSet()));
            }

            List<PageManager> oldPageManagers = pageManagerService.listByVersionId(draftVersion.getId(), null, null);
            oldPageManagers.forEach(o -> o.setIsHide(!businessType.contains(o.getBusinessType())));
            pageManagerService.updateBatchById(oldPageManagers);
        }

        // 更新管理员手机号密码
        if (platformChannel.getAdminId() != null) {
            User user = userService.getById(platformChannel.getAdminId());
            if (!user.getMobileNo().equals(request.getAdminMobileNo()) || StringUtils.isNotBlank(request.getAdminPassword())) {
                userService.update(new User(), Wrappers.<User>lambdaUpdate()
                        .set(!user.getMobileNo().equals(request.getAdminMobileNo()), User::getMobileNo, request.getAdminMobileNo())
                        .set(StringUtils.isNotBlank(request.getAdminPassword()), User::getPassword,
                                DigestUtil.md5Hex(DigestUtil.md5Hex(request.getAdminPassword()) + user.getSalt()))
                        .eq(User::getId, platformChannel.getAdminId()));
            }
        }

        BeanUtils.copyProperties(request, platformChannel);
        updateById(platformChannel);
    }


    @Override
    public PlatformChannel getByChannelNo(String channelNo) {
        return getOne(new QueryWrapper<PlatformChannel>().lambda().eq(PlatformChannel::getChannelNo, channelNo));
    }

    @Override
    public Long getReleaseVersionIdByChannelNo(String channelNo) {
        PlatformChannel platformChannel = getByChannelNo(channelNo);
        Assert.notNull(platformChannel, "渠道异常");
        return platformChannel.getReleaseVersion();
    }

    @Override
    public List<PlatformChannel> listByChannelName(String channelName) {
        QueryWrapper<PlatformChannel> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(channelName)) {
            queryWrapper.lambda().like(PlatformChannel::getChannelName, channelName);
        }
        return list(queryWrapper.lambda().orderByDesc(PlatformChannel::getCreatedTime));
    }

    @Override
    public PlatformChannel getByAdminAccount(String adminAccount) {
        return getOne(Wrappers.<PlatformChannel>lambdaQuery().eq(PlatformChannel::getAdminAccount, adminAccount));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Pager<RentDto> unusedRentList(Pager pager) {
        List<FindByParamsBo> finds = new ArrayList<>();
        List<PlatformChannel> channelExistRent = findChannelExistRent();
        if (CollectionUtils.isNotEmpty(channelExistRent)) {
            Set<Long> rentIds = channelExistRent.stream().map(PlatformChannel::getRentId).collect(Collectors.toSet());
            finds.add(new FindByParamsBo("id", Long.class, rentIds, NOT_IN));
        }
        finds.add(new FindByParamsBo("disable", Integer.class, 0));
        finds.add(new FindByParamsBo("deletedFlag", Long.class, 0L));
        com.touchealth.platform.basic.response.Pager<RentRes> rentResPager =
                rentApi.findRent(finds, BaseHelper.r2t(pager, com.touchealth.platform.basic.response.Pager.class)).get();
        pager = BaseHelper.r2t(rentResPager, Pager.class);
        pager.setResultList(RentDto.toRentDto(rentResPager.getResultList()));
        return pager;
    }

    @Override
    public void saveConfig(Long id, String channelNo, PlatformChannelConfigRequest request) {
        PlatformChannel platformChannel = getById(id);
        Assert.notNull(platformChannel, "渠道不存在");
        PlatformChannel currentChannel = getByChannelNo(channelNo);
        Assert.notNull(currentChannel, "当前渠道不存在");
        Assert.isTrue(platformChannel.getParentId() == null && platformChannel.getId() != 1L, "势成云和二级渠道不能配置数据");

        // 医院渠道不能配置医院和套餐数据
        if (platformChannel.getHospitalId() != null) {
        }

        // 要保存的数据
        List<String> dataIds = request.getDataIds().stream().map(String::valueOf).collect(Collectors.toList());
        Integer oldDisplayRules = null;

        switch (request.getConfigType()) {
            case 0:
                Assert.notNull(request.getDisplayRules(), "配置数据展示规则不能为空");
                if (request.getDisplayRules() != 0) {
                    Assert.isTrue(CollectionUtils.isNotEmpty(request.getDataIds()), "您未配置医院数据，请配置后保存");
                }

                oldDisplayRules = platformChannel.getHospitalDisplayRules();

                if (currentChannel.getId() == 1L) {
                    if (platformChannel.getHospitalPermitRules().equals(request.getDisplayRules())) {
                        if (StringUtils.isNotBlank(platformChannel.getHospitalPermitNos())) {
                            dataIds.addAll(Arrays.asList(platformChannel.getHospitalPermitNos().split(",")));
                        }
                    }
                    platformChannel.setHospitalPermitNos(String.join(",", dataIds));

                    platformChannel.setHospitalPermitRules(request.getDisplayRules());
                } else {
                    platformChannel.setHospitalDisplayRules(request.getDisplayRules());
                    if (oldDisplayRules.equals(request.getDisplayRules()) && StringUtils.isNotBlank(platformChannel.getHospitalNos())) {
                        dataIds.addAll(Arrays.asList(platformChannel.getHospitalNos().split(",")));
                    }
                    platformChannel.setHospitalNos(String.join(",", dataIds));
                }
                break;
            case 1:
                if (request.getDisplayRules() != 0) {
                    Assert.isTrue(CollectionUtils.isNotEmpty(request.getDataIds()), "您未配置套餐数据，请配置后保存");
                }

                oldDisplayRules = platformChannel.getSetMealDisplayRules();

                if (currentChannel.getId() == 1L) {
                    if (platformChannel.getHospitalPermitRules().equals(request.getDisplayRules())) {
                        if (StringUtils.isNotBlank(platformChannel.getSetMealPermitNos())) {
                            dataIds.addAll(Arrays.asList(platformChannel.getSetMealPermitNos().split(",")));
                        }
                    }
                    platformChannel.setSetMealPermitNos(String.join(",", dataIds));

                    platformChannel.setSetMealPermitRules(request.getDisplayRules());
                } else {
                    platformChannel.setSetMealDisplayRules(request.getDisplayRules());
                    if (oldDisplayRules.equals(request.getDisplayRules()) && StringUtils.isNotBlank(platformChannel.getSetMealNos())) {
                        dataIds.addAll(Arrays.asList(platformChannel.getSetMealNos().split(",")));
                    }
                    platformChannel.setSetMealNos(String.join(",", dataIds));
                }
                break;
            case 2:
                Assert.isTrue(CollectionUtils.isNotEmpty(request.getDataIds()), "您未配置渠道数据，请配置后保存");
                platformChannel.setBindChannelNos(String.join(",", dataIds));
                break;
            default:
                throw new BusinessException("渠道配置类型异常");
        }
        updateById(platformChannel);
    }

    @Override
    public void deleteConfig(Long id, String channelNo, Integer configType, Long dataId) {
        PlatformChannel platformChannel = getById(id);
        Assert.notNull(platformChannel, "渠道不存在");
        PlatformChannel currentChannel = getByChannelNo(channelNo);
        Assert.notNull(currentChannel, "当前渠道不存在");

        switch (configType) {
            case 0:
                String dataIds;
                if (currentChannel.getId() == 1L) {
                    dataIds = platformChannel.getHospitalPermitNos();
                } else {
                    dataIds = platformChannel.getHospitalNos();
                }

                Assert.isTrue(StringUtils.isNotBlank(dataIds), "没有对应配置，删除失败");
                List<String> hospitalIds = new ArrayList<>(Arrays.asList(dataIds.split(",")));
                Assert.isTrue(hospitalIds.remove(String.valueOf(dataId)), "没有对应配置，删除失败");

                if (currentChannel.getId() == 1L) {
                    platformChannel.setHospitalPermitNos(String.join(",", hospitalIds));
                } else {
                    platformChannel.setHospitalNos(String.join(",", hospitalIds));
                }
                break;
            case 1:
                if (currentChannel.getId() == 1L) {
                    dataIds = platformChannel.getSetMealPermitNos();
                } else {
                    dataIds = platformChannel.getSetMealNos();
                }

                Assert.isTrue(StringUtils.isNotBlank(dataIds), "没有对应配置，删除失败");
                List<String> setMealIds = new ArrayList<>(Arrays.asList(dataIds.split(",")));
                Assert.isTrue(setMealIds.remove(String.valueOf(dataId)), "没有对应配置，删除失败");

                if (currentChannel.getId() == 1L) {
                    platformChannel.setSetMealPermitNos(String.join(",", setMealIds));
                } else {
                    platformChannel.setSetMealNos(String.join(",", setMealIds));
                }
                break;
            case 2:
                Assert.isTrue(StringUtils.isNotBlank(platformChannel.getBindChannelNos()), "没有对应配置，删除失败");
                List<String> bindChannelIds = new ArrayList<>(Arrays.asList(platformChannel.getBindChannelNos().split(",")));
                Assert.isTrue(bindChannelIds.remove(String.valueOf(dataId)), "没有对应配置，删除失败");
                platformChannel.setHospitalNos(String.join(",", bindChannelIds));
                break;
            default:
                throw new BusinessException("渠道配置类型异常");
        }

        updateById(platformChannel);
    }

    private List<PlatformChannel> findChannelExistRent() {
        QueryWrapper<PlatformChannel> query = Wrappers.<PlatformChannel>query()
                .ge("rent_id", 0L)
                .eq("deleted_flag", 0);
        return baseFindList(query);
    }

    @Override
    public String getLoginPageIdByChannelNo(String channelNo) {
        Assert.notNull(channelNo, "渠道异常");

        // 获取渠道信息
        Long versionId = getReleaseVersionIdByChannelNo(channelNo);
        if (versionId == null) {
            throw new BusinessException("当前渠道未上线！");
        }
        PageModule pageModule = pageModuleService.findByChannelNoAndVersionIdAndModuleType(channelNo, versionId, CommonConstant.ModuleType.LOGIN.getCode());
        return pageModule == null ? null : ConvertUtils.encode62(pageModule.getPageId());
    }

    @Override
    public List<PlatformChannel> listByChannelNos(List<String> channelNos) {
        return list(Wrappers.<PlatformChannel>lambdaQuery().in(PlatformChannel::getChannelNo, channelNos));
    }

    @Override
    public PlatformChannel getByRentId(Long rentId) {
        return getOne(Wrappers.<PlatformChannel>lambdaQuery().eq(PlatformChannel::getRentId, rentId).isNull(PlatformChannel::getParentId));
    }

    @Override
    public PageInfo<UserCenterListDto> pageUserByRentId(Long rentId, String search, Integer pageNo, Integer pageSize) {
        UserReq userReq = new UserReq();
        userReq.setRentId(rentId);
        userReq.setSearchKey(search);
        if (pageNo != null) {
            userReq.setPageNo(pageNo);
        }
        if (pageSize != null) {
            userReq.setPageSize(pageSize);
        }
        PlatFormResponse<com.touchealth.platform.basic.response.Pager<UserRes>> response = userApi.pageList(userReq);

        com.touchealth.platform.basic.response.Pager<UserRes> pager = response.getDataOrThrowError();
        List<UserCenterListDto> dtos = pager.getResultList().stream().map(o -> {
            UserCenterListDto dto = BaseHelper.r2t(o, UserCenterListDto.class);
            dto.setMobileNo(DesensitizationUtils.desensitization(o.getMobile(), 3, 3, "***"));
            dto.setCreatedAt(LocalDateTime.parse(DateUtil.formatDateTime(o.getCreatedTime()), DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
            return dto;
        }).collect(Collectors.toList());
        return new PageInfo<>(pager.getPageNo(), pager.getPageSize(), pager.getRecords(), dtos);
    }

    /**
     * 添加预置页面：加到所有一级渠道草稿版本中
     */
    @Override
    @TransactionalForException
    public void addPresetPage(Long userId, List<PresetPageRequest> request) {
        if (CollectionUtils.isEmpty(request)) {
            return;
        }

        List<PlatformChannel> platformChannels = list(Wrappers.<PlatformChannel>lambdaQuery().isNull(PlatformChannel::getParentId));

        request.forEach(o -> {
            JSONObject jsonObject = JSON.parseObject(o.getWebJson());
            Integer businessType = PageCenterConsts.BusinessType.getCodeByName(jsonObject.getString("businessType"));

            platformChannels.forEach(x -> {
                Long versionId = platformVersionService.getDefaultVersionId(x.getChannelNo(), null);
                List<String> businessTypeList = Arrays.asList(x.getBusinessType().split(","));

                // 新增文件夹
                PageManager folder = pageManagerService.getOne(Wrappers.<PageManager>lambdaQuery().eq(PageManager::getIsFolder, true)
                        .eq(PageManager::getVersionId, versionId)
                        .eq(PageManager::getBusinessType, businessType));
                if (folder == null) {
                    folder = new PageManager();
                    folder.setIsFolder(true);
                    folder.setBusinessType(businessType);
                    folder.setRouterName(jsonObject.getString("routerName"));
                    folder.setPageName(PageCenterConsts.BusinessType.getDescByName(jsonObject.getString("businessType")));
                    folder.setChannelNo(x.getChannelNo());
                    folder.setVersionId(versionId);
                    folder.setOperatorIds(String.valueOf(userId));
                    folder.setIsHide(!businessTypeList.contains(String.valueOf(businessType)));
                    pageManagerService.savePage(userId, folder);
                }

                // 新增页面
                List<PageManager> pageManagers = pageManagerService.list(Wrappers.<PageManager>lambdaQuery().eq(PageManager::getIsFolder, false)
                        .eq(PageManager::getVersionId, versionId)
                        .eq(PageManager::getRouterName, jsonObject.getString("routerName")));
                PageManager pageManager = null;
                if (CollectionUtils.isNotEmpty(pageManagers)) {
                    pageManager = pageManagers.get(0);
                    if (pageManagers.size() > 1) {
                        pageManagers.remove(0);
                        pageManagerService.removeByIds(pageManagers.stream().map(PageManager::getId).collect(Collectors.toList()));
                    }
                }

                if (pageManager == null) {
                    pageManager = new PageManager();
                    pageManager.setFolderId(folder.getId());
                    pageManager.setChannelNo(x.getChannelNo());
                    pageManager.setVersionId(versionId);
                    pageManager.setBusinessType(businessType);
                    pageManager.setRouterName(jsonObject.getString("routerName"));
                    pageManager.setOperatorIds(String.valueOf(userId));
                    pageManager.setPageName(jsonObject.getString("pageName"));
                    pageManager.setPageType(o.getPageType());
                    pageManager.setDisableEdit(jsonObject.getBoolean("disableEdit"));
                    if (pageManager.getDisableEdit() == null) {
                        pageManager.setDisableEdit(true);
                    }
                    pageManager.setIsFolder(false);
                    pageManager.setChangeStatus(0);
                    pageManager.setIsHide(!businessTypeList.contains(String.valueOf(businessType)));
                    pageManagerService.savePage(userId, pageManager);
                } else {
                    pageManager.setBusinessType(businessType);
                    pageManager.setPageName(jsonObject.getString("pageName"));
                    pageManager.setPageType(o.getPageType());
                    pageManager.setDisableEdit(jsonObject.getBoolean("disableEdit"));
                    if (pageManager.getDisableEdit() == null) {
                        pageManager.setDisableEdit(true);
                    }
                    pageManagerService.updateById(pageManager);
                }

                // 新增组件
                pageModuleService.remove(Wrappers.<PageModule>lambdaQuery().eq(PageModule::getPageId, pageManager.getId()));
                JSONArray modules = jsonObject.getJSONArray("modules");
                for (int i = 0; i < modules.size(); i++) {
                    JSONObject item = modules.getJSONObject(i);
                    PageModuleRequest pageModuleRequest = new PageModuleRequest();
                    pageModuleRequest.setModuleType(item.getInteger("moduleType"));
                    pageModuleRequest.setWebJson(item.getJSONObject("webJson").toJSONString());
                    pageModuleService.saveModule(pageManager.getUpdatedBy(), pageManager, pageModuleRequest);
                }
            });
        });
    }

    @Override
    public ChannelConfigDto pageDataConfig(Integer pageNo, Integer pageSize, Long id, Integer dataType, Integer configType, Integer displayRules, String search, Boolean isMain) {
        PlatformChannel platformChannel = getById(id);
        Assert.notNull(platformChannel, "渠道不存在");
        PageInfo<ChannelConfigDataDto> pageInfo = null;
        Integer oldDisplayRules = null;

        if (dataType == 0) {
            switch (configType) {
                case 0:
                    String hospitalNos = isMain ? platformChannel.getHospitalPermitNos() : platformChannel.getHospitalNos();
                    if (StringUtils.isNotBlank(hospitalNos)) {
                        List<Long> hospitalIds = Arrays.stream(hospitalNos.split(",")).map(Long::parseLong).collect(Collectors.toList());
                        List<HospitalDto> hospitalDtos = hospitalSetMealApi.listHospitalByIds(hospitalIds.subList(pageSize * (pageNo - 1), Math.min(hospitalIds.size(), pageSize * pageNo)));
                        List<ChannelConfigDataDto> dtos = hospitalDtos.stream().map(o -> new ChannelConfigDataDto(o.getId(), o.getCode(), o.getName())).collect(Collectors.toList());
                        pageInfo = new PageInfo<>(pageNo, pageSize, hospitalIds.size(), dtos);
                    }
                    oldDisplayRules = isMain ? platformChannel.getHospitalPermitRules() : platformChannel.getHospitalDisplayRules();
                    break;
                case 1:
                    String setMealNos = isMain ? platformChannel.getSetMealPermitNos() : platformChannel.getSetMealNos();
                    if (StringUtils.isNotBlank(setMealNos)) {
                        List<Long> setMealIds = Arrays.stream(setMealNos.split(",")).map(Long::parseLong).collect(Collectors.toList());
                        List<SetMealDto> setMealDtos = hospitalSetMealApi.listSetMealByIds(setMealIds.subList(pageSize * (pageNo - 1), Math.min(setMealIds.size(), pageSize * pageNo)));
                        List<ChannelConfigDataDto> dtos = setMealDtos.stream().map(o -> new ChannelConfigDataDto(
                                o.getId(), o.getCode(), o.getName(), o.getHospitalName())).collect(Collectors.toList());
                        pageInfo = new PageInfo<>(pageNo, pageSize, setMealIds.size(), dtos);
                    }
                    oldDisplayRules = isMain ? platformChannel.getSetMealPermitRules() : platformChannel.getSetMealDisplayRules();
                    break;
                case 2:
                    String channelNos = platformChannel.getBindChannelNos();
                    if (StringUtils.isNotBlank(channelNos)) {
                        List<Long> channelIds = Arrays.stream(channelNos.split(",")).map(Long::parseLong).collect(Collectors.toList());
                        List<PlatformChannel> platformChannels = listByIds(channelIds.subList(pageSize * (pageNo - 1), Math.min(channelIds.size(), pageSize * pageNo)));
                        List<ChannelConfigDataDto> dtos = platformChannels.stream().map(o -> new ChannelConfigDataDto(
                                o.getId(), o.getChannelNo(), o.getChannelName())).collect(Collectors.toList());
                        pageInfo = new PageInfo<>(pageNo, pageSize, channelIds.size(), dtos);
                    }
                    break;
                default:
                    throw new BusinessException("渠道配置类型错误");
            }
        } else {
            Assert.notNull(displayRules, "医院数据展示规则不能为空");

            switch (configType) {
                case 0:
                    oldDisplayRules = isMain ? platformChannel.getHospitalPermitRules() : platformChannel.getHospitalDisplayRules();

                    PageCommonQuery query = new PageCommonQuery(pageNo, pageSize);
                    query.setSearch(search);
                    List<Long> hospitalIdsNotIn = new ArrayList<>();

                    if (isMain) {
                        if (oldDisplayRules.equals(displayRules) && StringUtils.isNotBlank(platformChannel.getHospitalPermitNos())) {
                            hospitalIdsNotIn.addAll(Arrays.stream(platformChannel.getHospitalPermitNos().split(",")).map(Long::parseLong).collect(Collectors.toList()));
                        }
                    } else {
                        if (oldDisplayRules.equals(displayRules) && StringUtils.isNotBlank(platformChannel.getHospitalNos())) {
                            hospitalIdsNotIn.addAll(Arrays.stream(platformChannel.getHospitalNos().split(",")).map(Long::parseLong).collect(Collectors.toList()));
                        }

                        if (StringUtils.isNotBlank(platformChannel.getHospitalPermitNos())) {
                            List<Long> strings = Arrays.stream(platformChannel.getHospitalPermitNos().split(",")).map(Long::parseLong).collect(Collectors.toList());
                            if (platformChannel.getHospitalPermitRules() == 1) {
                                hospitalIdsNotIn.addAll(strings);
                            } else if (platformChannel.getHospitalPermitRules() == 2) {
                                query.setIdsIn(strings);
                            }
                        }
                    }

                    query.setIdsNotIn(hospitalIdsNotIn);
                    PageInfo<HospitalDto> hospitalPageInfo = hospitalSetMealApi.pageHospitalByQuery(query);
                    pageInfo = PageUtils.changePage(hospitalPageInfo, hospitalPageInfo.getData().stream().map(
                            o -> new ChannelConfigDataDto(o.getId(), o.getCode(), o.getName())).collect(Collectors.toList()));
                    break;
                case 1:
                    oldDisplayRules = isMain ? platformChannel.getSetMealPermitRules() : platformChannel.getSetMealDisplayRules();

                    PageCommonQuery setMealQuery = new PageCommonQuery(pageNo, pageSize);
                    setMealQuery.setSearch(search);
                    List<Long> setMealIdsNotIn = new ArrayList<>();

                    if (isMain) {
                        if (oldDisplayRules.equals(displayRules) && StringUtils.isNotBlank(platformChannel.getSetMealPermitNos())) {
                            setMealIdsNotIn.addAll(Arrays.stream(platformChannel.getSetMealPermitNos().split(",")).map(Long::parseLong).collect(Collectors.toList()));
                        }
                    } else {
                        if (oldDisplayRules.equals(displayRules) && StringUtils.isNotBlank(platformChannel.getSetMealNos())) {
                            setMealIdsNotIn.addAll(Arrays.stream(platformChannel.getSetMealNos().split(",")).map(Long::parseLong).collect(Collectors.toList()));
                        }

                        if (StringUtils.isNotBlank(platformChannel.getSetMealPermitNos())) {
                            List<Long> strings = Arrays.stream(platformChannel.getSetMealPermitNos().split(",")).map(Long::parseLong).collect(Collectors.toList());
                            if (platformChannel.getSetMealPermitRules() == 1) {
                                setMealIdsNotIn.addAll(strings);
                            } else if (platformChannel.getSetMealPermitRules() == 2) {
                                setMealQuery.setIdsIn(strings);
                            }
                        }
                    }

                    setMealQuery.setIdsNotIn(setMealIdsNotIn);
                    PageInfo<SetMealDto> setMealPage = hospitalSetMealApi.pageSetMealByQuery(setMealQuery);
                    pageInfo = PageUtils.changePage(setMealPage, setMealPage.getData().stream().map(o ->
                            new ChannelConfigDataDto(o.getId(), o.getCode(), o.getName(), o.getHospitalName())).collect(Collectors.toList()));
                    break;
                case 2:
                    List<String> channelNoList = new ArrayList<>();
                    PlatformChannel mainChannel = getById(SC_CHANNEL_ID);
                    channelNoList.add(mainChannel.getChannelNo());
                    channelNoList.add(platformChannel.getChannelNo());

                    String channelNos = platformChannel.getBindChannelNos();
                    if (StringUtils.isNotBlank(channelNos)) {
                        channelNoList = Arrays.asList(channelNos.split(","));
                    }
                    Page<PlatformChannel> page = page(new Page<>(pageNo, pageSize), Wrappers.<PlatformChannel>lambdaQuery()
                            .isNull(PlatformChannel::getParentId)
                            .like(StringUtils.isNotBlank(search), PlatformChannel::getChannelName, search)
                            .notIn(PlatformChannel::getChannelNo, channelNoList));
                    List<ChannelConfigDataDto> dataDtos = page.getRecords().stream().map(o ->
                            new ChannelConfigDataDto(o.getId(), o.getChannelNo(), o.getChannelName())).collect(Collectors.toList());
                    pageInfo = PageUtils.changePage(page, dataDtos);
                    break;
                default:
                    throw new BusinessException("渠道配置类型错误");
            }
        }

        if (pageInfo == null) {
            pageInfo = new PageInfo<>();
        }
        return new ChannelConfigDto(oldDisplayRules, pageInfo);
    }
}
