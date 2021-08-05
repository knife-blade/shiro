package com.touchealth.platform.processengine.service.impl.datastatistics;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.touchealth.platform.processengine.constant.CommonConstant;
import com.touchealth.platform.processengine.constant.PageCenterConsts;
import com.touchealth.platform.processengine.constant.RedisConstant;
import com.touchealth.platform.processengine.constant.RocketMqMsgTagConstant;
import com.touchealth.platform.processengine.constant.datastatistics.DataStatisticsModuleTypeEnum;
import com.touchealth.platform.processengine.entity.datastatistics.ModuleClickTimeLog;
import com.touchealth.platform.processengine.entity.datastatistics.ModuleRatioLog;
import com.touchealth.platform.processengine.entity.datastatistics.ModuleShowTimeLog;
import com.touchealth.platform.processengine.entity.datastatistics.UserBoard;
import com.touchealth.platform.processengine.entity.module.common.*;
import com.touchealth.platform.processengine.entity.page.PageManager;
import com.touchealth.platform.processengine.entity.page.PageModule;
import com.touchealth.platform.processengine.entity.page.PlatformChannel;
import com.touchealth.platform.processengine.entity.page.PlatformVersion;
import com.touchealth.platform.processengine.entity.user.User;
import com.touchealth.platform.processengine.exception.BusinessException;
import com.touchealth.platform.processengine.pojo.bo.WebJsonBo;
import com.touchealth.platform.processengine.pojo.dto.module.common.HomeNavDto;
import com.touchealth.platform.processengine.pojo.dto.page.DataStatisticsDto;
import com.touchealth.platform.processengine.pojo.dto.page.DataStatisticsModuleDto;
import com.touchealth.platform.processengine.pojo.request.datastatistics.*;
import com.touchealth.platform.processengine.service.ProducerService;
import com.touchealth.platform.processengine.service.common.RedisService;
import com.touchealth.platform.processengine.service.datastatistics.*;
import com.touchealth.platform.processengine.service.module.common.*;
import com.touchealth.platform.processengine.service.page.PageManagerService;
import com.touchealth.platform.processengine.service.page.PageModuleService;
import com.touchealth.platform.processengine.service.page.PlatformChannelService;
import com.touchealth.platform.processengine.service.page.PlatformVersionService;
import com.touchealth.platform.processengine.service.user.UserService;
import com.touchealth.platform.processengine.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.touchealth.platform.processengine.constant.CommonConstant.ModuleType.HOME_NAV;
import static com.touchealth.platform.processengine.constant.PageCenterConsts.ROUTER_NAME_HOME;

/**
 * @program: process-engine
 * @author: xianghy
 * @create: 2020/11/23
 **/
@Slf4j
@Service("dataStatisticsService")
public class DataStatisticsServiceImpl implements DataStatisticsService {

    @Autowired
    private UserBoardService userBoardService;

    @Autowired
    private ModuleRatioLogService moduleRatioLogService;

    @Autowired
    private ModuleShowTimeLogService moduleShowTimeLogService;

    @Autowired
    private ModuleClickTimeLogService moduleClickTimeLogService;

    @Autowired
    private PageManagerService pageManagerService;

    @Autowired
    private BannerService bannerService;

    @Autowired
    private BannerImgService bannerImgService;

    @Autowired
    private NavigateService navigateService;

    @Autowired
    private NavigateImgService navigateImgService;

    @Autowired
    private ComboImgService comboImgService;

    @Autowired
    private ComboImgDetailService comboImgDetailService;

    @Autowired
    private HotspotService hotspotService;

    @Autowired
    private HotspotPartsService hotspotPartsService;

    @Autowired
    private BtnGroupService btnGroupService;

    @Autowired
    private BtnService btnService;

    @Autowired
    private ProducerService producerService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private PageModuleService pageModuleService;

    @Resource
    private UserService userService;

    @Resource
    private PlatformChannelService platformChannelService;

    @Resource
    private PersonalInfoService personalInfoService;

    @Resource
    private PersonalInfoImgService personalInfoImgService;

    @Resource
    private OrderManagementService orderManagementService;

    @Resource
    private OrderMgtImgService orderMgtImgService;

    @Resource
    private MyModService myModService;

    @Resource
    private MyModImgService myModImgService;

    @Resource
    private HomeNavService homeNavService;

    @Resource
    private HomeNavImgService homeNavImgService;

    @Resource
    private PlatformVersionService platformVersionService;

    @Override
    public void addDataStatistics(DataStatisticsAddRequest request) {

        // 校验参数
        validateDataStatisticsAddRequest(request);

        Long pageId = request.getPageId();
        PageManager pageManager = pageManagerService.getById(pageId);


        // 添加用户进入页面信息
        UserBoard userBoard = new UserBoard();
        BeanUtils.copyProperties(request, userBoard);
        userBoard.setCreateTime(new Date());
        String userBoardUniqueId = userBoardService.getUniqueId();
        userBoard.setUniqueId(userBoardUniqueId);
        userBoard.setVisitEndTime(request.getVisitEndTime() == null ? 0 : request.getVisitEndTime().getTime());
        userBoard.setVisitStartTime(request.getVisitStartTime() == null ? 0 : request.getVisitStartTime().getTime());
        userBoardService.save(userBoard);

        String userMark = request.getUserMark();
        String channelNo = pageManager.getChannelNo();
        String pageUniqueId = pageManager.getPageUniqueId();


        MqRequest mqRequest = new MqRequest(userMark, channelNo, pageId, pageUniqueId, userBoardUniqueId);
        // 处理banner组件相关数据
        if (CollectionUtils.isNotEmpty(request.getBanner())) {
            mqRequest.setBannerRequestList(request.getBanner());
            producerService.sendMsg(RocketMqMsgTagConstant.TAG_DATA_STATISTICS_BANNER, mqRequest);
        }

        // 处理navigate组件
        if (CollectionUtils.isNotEmpty(request.getGrid())) {
            mqRequest.setNavigateRequestList(request.getGrid());
            producerService.sendMsg(RocketMqMsgTagConstant.TAG_DATA_STATISTICS_NAVIGATE, mqRequest);
        }

        // 处理comboImg
        if (CollectionUtils.isNotEmpty(request.getChunk())) {
            mqRequest.setComboImgRequestList(request.getChunk());
            producerService.sendMsg(RocketMqMsgTagConstant.TAG_DATA_STATISTICS_COMBO_IMG, mqRequest);
        }

        // 处理hotspot
        if (CollectionUtils.isNotEmpty(request.getHotspot())) {
            mqRequest.setHotspotRequestList(request.getHotspot());
            producerService.sendMsg(RocketMqMsgTagConstant.TAG_DATA_STATISTICS_HOTSPOT, mqRequest);
        }

        // button
        if (CollectionUtils.isNotEmpty(request.getFixedButton())) {
            mqRequest.setButtonRequestList(request.getFixedButton());
            producerService.sendMsg(RocketMqMsgTagConstant.TAG_DATA_STATISTICS_BUTTON, mqRequest);
        }

        // login
        if (CollectionUtils.isNotEmpty(request.getLogin())) {
            mqRequest.setLoginRequestList(request.getLogin());
            producerService.sendMsg(RocketMqMsgTagConstant.TAG_DATA_STATISTICS_LOGIN, mqRequest);
        }

        // personalInfo
        if (CollectionUtils.isNotEmpty(request.getPersonal())) {
            mqRequest.setPersonalInfoRequestList(request.getPersonal());
            producerService.sendMsg(RocketMqMsgTagConstant.TAG_DATA_STATISTICS_PERSONAL_INFO, mqRequest);
        }

        // orderManagement
        if (CollectionUtils.isNotEmpty(request.getOrderManage())) {
            mqRequest.setOrderManagementRequestList(request.getOrderManage());
            producerService.sendMsg(RocketMqMsgTagConstant.TAG_DATA_STATISTICS_ORDER_MANAGEMENT, mqRequest);
        }

        // myMod
        if (CollectionUtils.isNotEmpty(request.getPersonalModule())) {
            mqRequest.setMyModRequestList(request.getPersonalModule());
            producerService.sendMsg(RocketMqMsgTagConstant.TAG_DATA_STATISTICS_MY_MOD, mqRequest);
        }

        // homeNav
        if (CollectionUtils.isNotEmpty(request.getNavigation())) {
            mqRequest.setHomeNavRequestList(request.getNavigation());
            producerService.sendMsg(RocketMqMsgTagConstant.TAG_DATA_STATISTICS_HOME_NAV, mqRequest);
        }

        // blank
        if (CollectionUtils.isNotEmpty(request.getBlank())) {
            mqRequest.setBlankRequestList(request.getBlank());
            producerService.sendMsg(RocketMqMsgTagConstant.TAG_DATA_STATISTICS_BLANK, mqRequest);
        }

        // 保存页面相关统计数据到redis
        long visitTime = userBoard.getVisitEndTime() - userBoard.getVisitStartTime();
        processPageVisitTimeForRedis(channelNo, userMark, pageUniqueId, visitTime, request.getVisitStartTime());
    }


    @Override
    public void processDataStatisticsForBannerRequest(MqRequest mqRequest) {
        if (mqRequest == null || StringUtils.isBlank(mqRequest.getUserBoardUniqueId())
                || CollectionUtils.isEmpty(mqRequest.getBannerRequestList())) {
            log.error("数据埋点，banner相关数据处理参数为null");
            return;
        }

        Banner banner;
        Long pageId = mqRequest.getPageId();
        String channelNo = mqRequest.getChannelNo();
        String pageUniqueId = mqRequest.getPageUniqueId();
        String userBoardUniqueId = mqRequest.getUserBoardUniqueId();
        List<DataStatisticsForBannerRequest> bannerRequestList = mqRequest.getBannerRequestList();
        for (DataStatisticsForBannerRequest request : bannerRequestList) {

            banner = bannerService.getById(request.getModuleId());

            // 滑动比例
            if (CollectionUtils.isNotEmpty(request.getRatioList())) {

                for (DataStatisticsForRatioRequest ratio : request.getRatioList()) {

                    ModuleRatioLog ratioLog = initModuleRatioLog(channelNo, pageId, pageUniqueId, userBoardUniqueId,
                            banner.getId(), banner.getModuleUniqueId(), DataStatisticsModuleTypeEnum.BANNER);
                    ratioLog.setRatio(ratio.getRatio());
                    ratioLog.setElementId(banner.getId());
                    ratioLog.setUniqueId(banner.getModuleUniqueId());
                    ratioLog.setTime(ratio.getTime() != null ? ratio.getTime().getTime() : 0L);
                    moduleRatioLogService.save(ratioLog);
                }
            }

            // 曝光时间和点击时间
            if (CollectionUtils.isEmpty(request.getImgList())) {
                continue;
            }

            for (DataStatisticsForImgRequest img : request.getImgList()) {

                BannerImg bannerImg = bannerImgService.getById(img.getId());

                // 曝光时间
                if (CollectionUtils.isNotEmpty(img.getShowTime())) {
                    for (Date date : img.getShowTime()) {
                        // 保存曝光日志
                        saveShowTimeLog(mqRequest, banner.getId(), banner.getModuleUniqueId(), bannerImg.getId(),
                                bannerImg.getModuleUniqueId(), date.getTime(), DataStatisticsModuleTypeEnum.BANNER);
                    }
                }

                // 点击时间
                if (CollectionUtils.isNotEmpty(img.getClickTime())) {
                    for (Date date : img.getClickTime()) {
                        // 保存点击日志
                        ModuleClickTimeLog timeLog = saveCheckTimeLog(mqRequest, banner.getId(), banner.getModuleUniqueId(), bannerImg.getId(),
                                bannerImg.getModuleUniqueId(), date.getTime(), DataStatisticsModuleTypeEnum.BANNER);

                        // 保存点击次数
                        processClickTimeForRedis(DataStatisticsModuleTypeEnum.BANNER, timeLog.getUniqueId(), mqRequest.getUserCode(), date);
                    }
                }
            }

        }

    }

    @Override
    public void processDataStatisticsForNavigateRequest(MqRequest mqRequest) {

        if (mqRequest == null || StringUtils.isBlank(mqRequest.getUserBoardUniqueId())
                || CollectionUtils.isEmpty(mqRequest.getNavigateRequestList())) {
            log.error("数据埋点，navigate相关数据处理参数为null");
            return;
        }

        for (DataStatisticsForNavigateRequest navigateRequest : mqRequest.getNavigateRequestList()) {

            if (CollectionUtils.isEmpty(navigateRequest.getImgList())) {
                continue;
            }

            // 曝光时间和点击时间
            Navigate navigate = navigateService.getById(navigateRequest.getModuleId());
            for (DataStatisticsForImgRequest img : navigateRequest.getImgList()) {

                NavigateImg navigateImg = navigateImgService.getById(img.getId());

                // 曝光时间
                if (CollectionUtils.isNotEmpty(img.getShowTime())) {
                    for (Date date : img.getShowTime()) {
                        // 保存曝光日志
                        saveShowTimeLog(mqRequest, navigate.getId(), navigate.getModuleUniqueId(), navigateImg.getId(),
                                navigateImg.getModuleUniqueId(), date.getTime(), DataStatisticsModuleTypeEnum.NAVIGATE);
                    }
                }

                // 点击时间
                if (CollectionUtils.isNotEmpty(img.getClickTime())) {
                    for (Date date : img.getClickTime()) {
                        // 保存点击日志
                        ModuleClickTimeLog timeLog = saveCheckTimeLog(mqRequest, navigate.getId(), navigate.getModuleUniqueId(), navigateImg.getId(),
                                navigateImg.getModuleUniqueId(), date.getTime(), DataStatisticsModuleTypeEnum.NAVIGATE);

                        // 保存点击次数
                        processClickTimeForRedis(DataStatisticsModuleTypeEnum.NAVIGATE, timeLog.getUniqueId(), mqRequest.getUserCode(), date);
                    }
                }
            }


        }
    }

    @Override
    public void processDataStatisticsForComboImgRequest(MqRequest mqRequest) {
        if (mqRequest == null || StringUtils.isBlank(mqRequest.getUserBoardUniqueId())
                || CollectionUtils.isEmpty(mqRequest.getComboImgRequestList())) {
            log.error("数据埋点，comboImg相关数据处理参数为null");
            return;
        }

        for (DataStatisticsForComboImgRequest comboImgRequest : mqRequest.getComboImgRequestList()) {
            if (CollectionUtils.isEmpty(comboImgRequest.getImgList())) {
                continue;
            }
            ComboImg comboImg = comboImgService.getById(comboImgRequest.getModuleId());

            // 曝光时间和点击时间
            for (DataStatisticsForImgRequest img : comboImgRequest.getImgList()) {

                ComboImgDetail detail = comboImgDetailService.getById(img.getId());
                // 曝光时间
                if (CollectionUtils.isNotEmpty(img.getShowTime())) {
                    for (Date date : img.getShowTime()) {
                        // 保存曝光日志
                        saveShowTimeLog(mqRequest, comboImg.getId(), comboImg.getModuleUniqueId(), detail.getId(),
                                detail.getModuleUniqueId(), date.getTime(), DataStatisticsModuleTypeEnum.COMBO_IMG);
                    }
                }

                // 点击时间
                if (CollectionUtils.isNotEmpty(img.getClickTime())) {
                    for (Date date : img.getClickTime()) {
                        // 保存点击日志
                        ModuleClickTimeLog timeLog = saveCheckTimeLog(mqRequest, comboImg.getId(), comboImg.getModuleUniqueId(), detail.getId(),
                                detail.getModuleUniqueId(), date.getTime(), DataStatisticsModuleTypeEnum.COMBO_IMG);

                        // 保存点击次数
                        processClickTimeForRedis(DataStatisticsModuleTypeEnum.COMBO_IMG, timeLog.getUniqueId(), mqRequest.getUserCode(), date);
                    }
                }
            }
        }
    }

    @Override
    public void processDataStatisticsForHotspotRequest(MqRequest mqRequest) {
        if (mqRequest == null || StringUtils.isBlank(mqRequest.getUserBoardUniqueId())
                || CollectionUtils.isEmpty(mqRequest.getHotspotRequestList())) {
            log.error("数据埋点，hotspot相关数据处理参数为null");
            return;
        }

        for (DataStatisticsForHotspotRequest hotspotRequest : mqRequest.getHotspotRequestList()) {
            if (CollectionUtils.isEmpty(hotspotRequest.getHotspotList())) {
                continue;
            }
            Hotspot hotspot = hotspotService.getById(hotspotRequest.getModuleId());

            // 曝光时间和点击时间
            for (DataStatisticsForHotspotRequest.PartRequest partRequest : hotspotRequest.getHotspotList()) {

                HotspotParts hotspotParts = hotspotPartsService.getById(partRequest.getId());
                // 曝光时间
                if (CollectionUtils.isNotEmpty(partRequest.getShowTime())) {
                    for (Date date : partRequest.getShowTime()) {
                        // 保存曝光日志
                        saveShowTimeLog(mqRequest, hotspot.getId(), hotspot.getModuleUniqueId(), hotspotParts.getId(),
                                hotspotParts.getModuleUniqueId(), date.getTime(), DataStatisticsModuleTypeEnum.HOTSPOT);
                    }
                }

                // 点击时间
                if (CollectionUtils.isNotEmpty(partRequest.getClickTime())) {
                    for (Date date : partRequest.getClickTime()) {
                        // 保存点击日志
                        ModuleClickTimeLog timeLog = saveCheckTimeLog(mqRequest, hotspot.getId(), hotspot.getModuleUniqueId(), hotspotParts.getId(),
                                hotspotParts.getModuleUniqueId(), date.getTime(), DataStatisticsModuleTypeEnum.HOTSPOT);

                        // 保存点击次数
                        processClickTimeForRedis(DataStatisticsModuleTypeEnum.HOTSPOT, timeLog.getUniqueId(), mqRequest.getUserCode(), date);
                    }
                }
            }

        }
    }


    @Override
    public void processDataStatisticsForButtonRequest(MqRequest mqRequest) {
        if (mqRequest == null || StringUtils.isBlank(mqRequest.getUserBoardUniqueId())
                || CollectionUtils.isEmpty(mqRequest.getButtonRequestList())) {
            log.error("数据埋点，button相关数据处理参数为null");
            return;
        }

        for (DataStatisticsForButtonRequest buttonRequest : mqRequest.getButtonRequestList()) {
            if (CollectionUtils.isEmpty(buttonRequest.getButtonList())) {
                continue;
            }
            BtnGroup btnGroup = btnGroupService.getById(buttonRequest.getModuleId());

            // 曝光时间和点击时间
            for (DataStatisticsForButtonRequest.ButtonRequest btnRequest : buttonRequest.getButtonList()) {

                Btn btn = btnService.getById(btnRequest.getId());
                // 曝光时间
                if (CollectionUtils.isNotEmpty(btnRequest.getShowTime())) {
                    for (Date date : btnRequest.getShowTime()) {
                        // 保存曝光日志
                        saveShowTimeLog(mqRequest, btnGroup.getId(), btnGroup.getModuleUniqueId(), btn.getId(),
                                btn.getModuleUniqueId(), date.getTime(), DataStatisticsModuleTypeEnum.BUTTON);
                    }
                }

                // 点击时间
                if (CollectionUtils.isNotEmpty(btnRequest.getClickTime())) {
                    for (Date date : btnRequest.getClickTime()) {
                        // 保存点击日志
                        ModuleClickTimeLog timeLog = saveCheckTimeLog(mqRequest, btnGroup.getId(), btnGroup.getModuleUniqueId(), btn.getId(),
                                btn.getModuleUniqueId(), date.getTime(), DataStatisticsModuleTypeEnum.BUTTON);

                        // 保存点击次数
                        processClickTimeForRedis(DataStatisticsModuleTypeEnum.BUTTON, timeLog.getUniqueId(), mqRequest.getUserCode(), date);
                    }
                }
            }

        }
    }

    @Override
    public void processDataStatisticsForLoginRequest(MqRequest mqRequest) {
        if (mqRequest == null || StringUtils.isBlank(mqRequest.getUserBoardUniqueId())
                || CollectionUtils.isEmpty(mqRequest.getLoginRequestList())) {
            log.error("数据埋点，登录相关数据处理参数为null");
            return;
        }

        for (DataStatisticsForLoginRequest loginRequest : mqRequest.getLoginRequestList()) {

            if (CollectionUtils.isEmpty(loginRequest.getClickTime())) {
                continue;
            }
            PlatformChannel platformChannel = platformChannelService.getByChannelNo(mqRequest.getChannelNo());
            PageModule pageModule = pageModuleService.getByModuleIdAndVersion(loginRequest.getModuleId(),platformChannel.getReleaseVersion());
            if (pageModule == null) {
                log.error("登录组件数据埋点错误，未查询到对应的模块信息。pageModuleId={}", loginRequest.getModuleId());
                continue;
            }

            for (Date date : loginRequest.getClickTime()) {
                // 保存点击日志
                ModuleClickTimeLog timeLog = saveCheckTimeLog(mqRequest, pageModule.getId(), pageModule.getModuleUniqueId(), pageModule.getId(),
                        pageModule.getModuleUniqueId(), date.getTime(), DataStatisticsModuleTypeEnum.LOGIN);

                // 保存点击次数
                processClickTimeForRedis(DataStatisticsModuleTypeEnum.LOGIN, pageModule.getModuleUniqueId(), mqRequest.getUserCode(), date);
            }
        }
    }

    @Override
    public void processDataStatisticsForPersonalInfoRequest(MqRequest mqRequest) {
        if (mqRequest == null || StringUtils.isBlank(mqRequest.getUserBoardUniqueId())
                || CollectionUtils.isEmpty(mqRequest.getPersonalInfoRequestList())) {
            log.error("数据埋点，personalInfo相关数据处理参数为null");
            return;
        }

        List<DataStatisticsForPersonalInfoRequest> personalInfoRequestList = mqRequest.getPersonalInfoRequestList();
        for (DataStatisticsForPersonalInfoRequest request : personalInfoRequestList) {

            PersonalInfo personalInfo = personalInfoService.getById(request.getModuleId());
            if (personalInfo == null) {
                log.error("个人信息数据埋点错误，未查询到对应的模块信息。moduleId={}", request.getModuleId());
                continue;
            }

            // 曝光时间和点击时间
            if (!CollectionUtils.isEmpty(request.getVtButtonList())) {
                for (DataStatisticsForPersonalInfoRequest.VtButtonRequest vtButton : request.getVtButtonList()) {

                    // 曝光时间
                    if (CollectionUtils.isNotEmpty(vtButton.getShowTime())) {
                        for (Date date : vtButton.getShowTime()) {
                            // 保存曝光日志
                            saveShowTimeLog(mqRequest, personalInfo.getId(), personalInfo.getModuleUniqueId(), vtButton.getId(),
                                    vtButton.getModuleUniqueId(), date.getTime(), DataStatisticsModuleTypeEnum.PERSONAL_INFO);
                        }
                    }

                    // 点击时间
                    if (CollectionUtils.isNotEmpty(vtButton.getClickTime())) {
                        for (Date date : vtButton.getClickTime()) {
                            // 保存点击日志
                            ModuleClickTimeLog timeLog = saveCheckTimeLog(mqRequest, personalInfo.getId(), personalInfo.getModuleUniqueId(), vtButton.getId(),
                                    vtButton.getModuleUniqueId(), date.getTime(), DataStatisticsModuleTypeEnum.PERSONAL_INFO);

                            // 保存点击次数
                            processClickTimeForRedis(DataStatisticsModuleTypeEnum.PERSONAL_INFO, timeLog.getUniqueId(), mqRequest.getUserCode(), date);
                        }
                    }
                }
            }

            // 曝光时间和点击时间
            if (!CollectionUtils.isEmpty(request.getImgList())) {
                for (DataStatisticsForImgRequest img : request.getImgList()) {

                    PersonalInfoImg personalInfoImg = personalInfoImgService.getById(img.getId());
                    if (personalInfoImg == null) {
                        log.error("个人信息数据埋点错误，未查询到对应的模块图片信息。imgId={}", img.getId());
                        continue;
                    }

                    // 曝光时间
                    if (CollectionUtils.isNotEmpty(img.getShowTime())) {
                        for (Date date : img.getShowTime()) {
                            // 保存曝光日志
                            saveShowTimeLog(mqRequest, personalInfo.getId(), personalInfo.getModuleUniqueId(), personalInfoImg.getId(), personalInfoImg.getModuleUniqueId(), date.getTime(), DataStatisticsModuleTypeEnum.PERSONAL_INFO);
                        }
                    }

                    // 点击时间
                    if (CollectionUtils.isNotEmpty(img.getClickTime())) {
                        for (Date date : img.getClickTime()) {
                            // 保存点击日志
                            ModuleClickTimeLog timeLog = saveCheckTimeLog(mqRequest, personalInfo.getId(), personalInfo.getModuleUniqueId(), personalInfoImg.getId(),
                                    personalInfoImg.getModuleUniqueId(), date.getTime(), DataStatisticsModuleTypeEnum.PERSONAL_INFO);

                            // 保存点击次数
                            processClickTimeForRedis(DataStatisticsModuleTypeEnum.PERSONAL_INFO, timeLog.getUniqueId(), mqRequest.getUserCode(), date);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void processDataStatisticsForOrderManagementRequest(MqRequest mqRequest) {
        if (mqRequest == null || StringUtils.isBlank(mqRequest.getUserBoardUniqueId())
                || CollectionUtils.isEmpty(mqRequest.getOrderManagementRequestList())) {
            log.error("数据埋点，orderManagement相关数据处理参数为null");
            return;
        }

        List<DataStatisticsForOrderManagementRequest> orderManagementRequestList = mqRequest.getOrderManagementRequestList();
        for (DataStatisticsForOrderManagementRequest request : orderManagementRequestList) {

            OrderManagement orderManagement = orderManagementService.getById(request.getModuleId());
            if (orderManagement == null) {
                log.error("订单管理数据埋点错误，未查询到对应的模块信息。moduleId={}", request.getModuleId());
                continue;
            }

            // 曝光时间和点击时间
            if (!CollectionUtils.isEmpty(request.getImgList())) {
                for (DataStatisticsForImgRequest img : request.getImgList()) {

                    OrderMgtImg orderMgtImg = orderMgtImgService.getById(img.getId());
                    if (orderMgtImg == null) {
                        log.error("订单管理数据埋点错误，未查询到对应的模块图片信息。imgId={}", img.getId());
                        continue;
                    }

                    // 曝光时间
                    if (CollectionUtils.isNotEmpty(img.getShowTime())) {
                        for (Date date : img.getShowTime()) {
                            // 保存曝光日志
                            saveShowTimeLog(mqRequest, orderManagement.getId(), orderManagement.getModuleUniqueId(), orderMgtImg.getId(), orderMgtImg.getModuleUniqueId(), date.getTime(), DataStatisticsModuleTypeEnum.ORDER_MANAGEMENT);
                        }
                    }

                    // 点击时间
                    if (CollectionUtils.isNotEmpty(img.getClickTime())) {
                        for (Date date : img.getClickTime()) {
                            // 保存点击日志
                            ModuleClickTimeLog timeLog = saveCheckTimeLog(mqRequest, orderManagement.getId(), orderManagement.getModuleUniqueId(), orderMgtImg.getId(),
                                    orderMgtImg.getModuleUniqueId(), date.getTime(), DataStatisticsModuleTypeEnum.ORDER_MANAGEMENT);

                            // 保存点击次数
                            processClickTimeForRedis(DataStatisticsModuleTypeEnum.ORDER_MANAGEMENT, timeLog.getUniqueId(), mqRequest.getUserCode(), date);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void processDataStatisticsForMyModRequest(MqRequest mqRequest) {
        if (mqRequest == null || StringUtils.isBlank(mqRequest.getUserBoardUniqueId())
                || CollectionUtils.isEmpty(mqRequest.getMyModRequestList())) {
            log.error("数据埋点，myMod相关数据处理参数为null");
            return;
        }

        List<DataStatisticsForMyModRequest> myModRequestList = mqRequest.getMyModRequestList();
        for (DataStatisticsForMyModRequest request : myModRequestList) {

            MyMod myMod = myModService.getById(request.getModuleId());
            if (myMod == null) {
                log.error("我的模块数据埋点错误，未查询到对应的模块信息。moduleId={}", request.getModuleId());
                continue;
            }

            // 曝光时间和点击时间
            if (!CollectionUtils.isEmpty(request.getImgList())) {
                for (DataStatisticsForImgRequest img : request.getImgList()) {

                    MyModImg myModImg = myModImgService.getById(img.getId());
                    if (myModImg == null) {
                        log.error("我的模块数据埋点错误，未查询到对应的模块图片信息。imgId={}", img.getId());
                        continue;
                    }

                    // 曝光时间
                    if (CollectionUtils.isNotEmpty(img.getShowTime())) {
                        for (Date date : img.getShowTime()) {
                            // 保存曝光日志
                            saveShowTimeLog(mqRequest, myMod.getId(), myMod.getModuleUniqueId(), myModImg.getId(), myModImg.getModuleUniqueId(), date.getTime(), DataStatisticsModuleTypeEnum.MY_MOD);
                        }
                    }

                    // 点击时间
                    if (CollectionUtils.isNotEmpty(img.getClickTime())) {
                        for (Date date : img.getClickTime()) {
                            // 保存点击日志
                            ModuleClickTimeLog timeLog = saveCheckTimeLog(mqRequest, myMod.getId(), myMod.getModuleUniqueId(), myModImg.getId(),
                                    myModImg.getModuleUniqueId(), date.getTime(), DataStatisticsModuleTypeEnum.MY_MOD);

                            // 保存点击次数
                            processClickTimeForRedis(DataStatisticsModuleTypeEnum.MY_MOD, timeLog.getUniqueId(), mqRequest.getUserCode(), date);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void processDataStatisticsForHomeNavRequest(MqRequest mqRequest) {
        if (mqRequest == null || StringUtils.isBlank(mqRequest.getUserBoardUniqueId())
                || CollectionUtils.isEmpty(mqRequest.getHomeNavRequestList())) {
            log.error("数据埋点，homeNav相关数据处理参数为null");
            return;
        }
        //所有的埋点数据都关联至首页
        PlatformChannel platformChannel = platformChannelService.getByChannelNo(mqRequest.getChannelNo());
        Assert.notNull(platformChannel, "平台不存在");
        QueryWrapper<PageManager> wrapper = new QueryWrapper<PageManager>(new PageManager());
        wrapper.getEntity().setVersionId(platformChannel.getReleaseVersion());
        wrapper.getEntity().setChannelNo(mqRequest.getChannelNo());
        wrapper.getEntity().setRouterName(ROUTER_NAME_HOME);
        PageManager homePage = pageManagerService.getOne(wrapper);
        Assert.notNull(homePage,"首页信息不存在");
        mqRequest.setPageId(homePage.getId());
        mqRequest.setPageUniqueId(homePage.getPageUniqueId());
        List<DataStatisticsForHomeNavRequest> homeNavRequestList = mqRequest.getHomeNavRequestList();
        for (DataStatisticsForHomeNavRequest request : homeNavRequestList) {
            HomeNav homeNav = homeNavService.getById(request.getModuleId());
            if (homeNav == null) {
                log.error("首页导航数据埋点错误，未查询到对应的模块信息。moduleId={}", request.getModuleId());
                continue;
            }

            // 曝光时间和点击时间
            if (!CollectionUtils.isEmpty(request.getImgList())) {
                //首页导航默认所有的埋点信息都关联到首页页面

                for (DataStatisticsForImgRequest img : request.getImgList()) {

                    HomeNavImg homeNavImg = homeNavImgService.getById(img.getId());
                    if (homeNavImg == null) {
                        log.error("首页导航数据埋点错误，未查询到对应的模块图片信息。imgId={}", img.getId());
                        continue;
                    }

                    // 曝光时间
                    if (CollectionUtils.isNotEmpty(img.getShowTime())) {
                        for (Date date : img.getShowTime()) {
                            // 保存曝光日志
                            saveShowTimeLog(mqRequest, homeNav.getId(), homeNav.getModuleUniqueId(), homeNavImg.getId(), homeNavImg.getModuleUniqueId(), date.getTime(), DataStatisticsModuleTypeEnum.HOME_NAV);
                        }
                    }

                    // 点击时间
                    if (CollectionUtils.isNotEmpty(img.getClickTime())) {
                        for (Date date : img.getClickTime()) {
                            // 保存点击日志
                            ModuleClickTimeLog timeLog = saveCheckTimeLog(mqRequest, homeNav.getId(), homeNav.getModuleUniqueId(), homeNavImg.getId(),
                                    homeNavImg.getModuleUniqueId(), date.getTime(), DataStatisticsModuleTypeEnum.HOME_NAV);

                            // 保存点击次数
                            processClickTimeForRedis(DataStatisticsModuleTypeEnum.HOME_NAV, timeLog.getUniqueId(), mqRequest.getUserCode(), date);
                        }
                    }
                }
            }
        }
    }

    //保存空模块埋点数据
    @Override
    public void processDataStatisticsForBlankRequest(MqRequest mqRequest){
        if (mqRequest == null || StringUtils.isBlank(mqRequest.getUserBoardUniqueId())
                || CollectionUtils.isEmpty(mqRequest.getBlankRequestList())) {
            log.error("数据埋点，blank相关数据处理参数为null");
            return;
        }
        List<DataStatisticsForBlankRequest> blankRequestList = mqRequest.getBlankRequestList();
        for (DataStatisticsForBlankRequest blankrequest : blankRequestList) {
            // 曝光时间和点击时间
            if (CollectionUtils.isEmpty(blankrequest.getChilrenlist())) {
                continue;
            }
            for (DataStatisticsForBlankRequest.ChilrenRequest chilren : blankrequest.getChilrenlist()) {
                String blankUniqueId = String.format(RedisConstant.BLANK_UNIQUEID, blankrequest.getModuleId(), chilren.getId(), mqRequest.getChannelNo());
                // 曝光时间
                if (CollectionUtils.isNotEmpty(chilren.getShowTime())) {
                    for (Date date : chilren.getShowTime()) {
                        // 保存曝光日志
                        ModuleShowTimeLog showTimeLog = initModuleShowTimeLog(mqRequest.getChannelNo(), mqRequest.getPageId(), mqRequest.getPageUniqueId(), mqRequest.getUserBoardUniqueId(), blankrequest.getModuleId(),
                                null, DataStatisticsModuleTypeEnum.BLANK);
                        showTimeLog.setTime(date.getTime());
                        showTimeLog.setBlankUniqueId(blankUniqueId);
                        moduleShowTimeLogService.save(showTimeLog);
                    }
                }

                // 点击时间
                if (CollectionUtils.isNotEmpty(chilren.getClickTime())) {
                    for (Date date : chilren.getClickTime()) {
                        // 保存点击日志
                        ModuleClickTimeLog clickTimeLog = initModuleClickTimeLog(mqRequest.getChannelNo(), mqRequest.getPageId(), mqRequest.getPageUniqueId(), mqRequest.getUserBoardUniqueId(), blankrequest.getModuleId(),
                                null, DataStatisticsModuleTypeEnum.BLANK);
                        clickTimeLog.setClickTime(date.getTime());
                        clickTimeLog.setBlankUniqueId(blankUniqueId);
                        moduleClickTimeLogService.save(clickTimeLog);

                        // 保存点击次数
                        saveClickTimeForRedis(DataStatisticsModuleTypeEnum.BLANK, blankUniqueId, mqRequest.getUserCode(), date);
                    }
                }
            }
        }
    }

    @Override
    public DataStatisticsDto getPageDataStatistics(Long pageId, Long userId) {
        if (null == pageId) {
            throw new BusinessException("参数不能为空！");
        }

        // 查询页面信息
        PageManager pageManager = pageManagerService.getById(pageId);
        if (null == pageManager) {
            throw new BusinessException("页面信息不存在！");
        }

        // 返回的实体
        DataStatisticsDto statisticsDto = new DataStatisticsDto();

        // 页面访问数据
        Boolean dataFlag = redisService.getValue(RedisConstant.DATA_STATISTICS_FIND_DATA_TYPE);
        dataFlag = dataFlag == null ? false : dataFlag;
        String pageUniqueIdStr = String.valueOf(pageManager.getPageUniqueId());
        String yesterdayStr = DateUtil.dateTimeToStr(DateUtil.add(new Date(), -1), DateUtil.DATE_DAY);

        // 页面浏览次数
        String key = dataFlag ? RedisConstant.DATA_STATISTICS_PAGE_CLICK_TIMES
                : String.format(RedisConstant.DATA_STATISTICS_PAGE_EVERY_DAY_CLICK_TIMES, yesterdayStr);
        Integer count = redisService.hget(key, pageUniqueIdStr);
        statisticsDto.setVisitCount(converterPageShowTime(count == null ? 0 : Long.valueOf(count)));

        // 浏览人数
        key = dataFlag ? String.format(RedisConstant.DATA_STATISTICS_PAGE_PERSON_CLICK_TIMES, pageUniqueIdStr)
                : String.format(RedisConstant.DATA_STATISTICS_PAGE_PERSON_EVERY_DAY_CLICK_TIMES, yesterdayStr, pageUniqueIdStr);
        long visitPersonCount = redisService.pfcount(key);
        statisticsDto.setVisitPersonCount(String.valueOf(visitPersonCount));

        key = dataFlag ? RedisConstant.DATA_STATISTICS_PAGE_VISIT_TIMES
                : String.format(RedisConstant.DATA_STATISTICS_PAGE_EVERY_DAY_VISIT_TIMES, yesterdayStr);
        //根据数据大小可为Integer或Long
        Object object = redisService.hget(key, pageUniqueIdStr);
        Long visitTime = null;
        if (object instanceof Integer) {
             visitTime = Long.valueOf((Integer) object);
        } else if (object instanceof Long) {
             visitTime = (Long) object;
        }
        statisticsDto.setVisitAvgTime("0");
        if (visitTime != null && visitPersonCount > 0L) {
            statisticsDto.setVisitAvgTime(DateUtil.formatLongToTimeStr(visitTime / visitPersonCount));
        }
        // 获取对应的模块信息
        List<PageModule> pageModuleList = pageModuleService.listByPageId(pageId);
        //如果是首页，获取对应的首页导航信息
        if(ROUTER_NAME_HOME.equals(pageManager.getRouterName())){
            HomeNav navQuery =  new HomeNav();
            navQuery.setVersion(pageManager.getVersionId());
            navQuery.setChannelNo(pageManager.getChannelNo());
            HomeNavDto homeNavDto = homeNavService.queryHomeNave(navQuery);
            Optional.ofNullable(homeNavDto).ifPresent(homeNav ->{
                PageModule pageModule = new PageModule();
                pageModule.setPageId(pageManager.getId());
                pageModule.setChannelNo(pageManager.getChannelNo());
                pageModule.setVersionId(pageManager.getVersionId());
                pageModule.setModuleId(homeNav.getId());
                pageModule.setModuleType(HOME_NAV.getCode());
                pageModule.setModuleUniqueId(homeNav.getModuleUniqueId());
                pageModuleList.add(pageModule);
            } );
        }
        if (CollectionUtils.isEmpty(pageModuleList)) {
            return statisticsDto;
        }

        // 组装对应的模块信息
        processDataStatisticsForModule(statisticsDto, pageModuleList, dataFlag);
        return statisticsDto;
    }

    @Override
    public DataStatisticsDto getBlankModuleStatistics(List<String> list, String pageId, String channelNo){
        if (CollectionUtils.isEmpty(list)) {
            throw new BusinessException("参数异常！");
        }
        //当前版本不是发布态则用最新发布态moduleId
        List<String> moduleIds = getModuleIds(pageId, channelNo);
        // 页面访问数据
        Boolean flag = redisService.getValue(RedisConstant.DATA_STATISTICS_FIND_DATA_TYPE);
        flag = flag == null ? false : flag;
        String yesterdayStr = DateUtil.dateTimeToStr(DateUtil.add(new Date(), -1), DateUtil.DATE_DAY);
        // 返回的实体
        DataStatisticsDto statisticsDto = new DataStatisticsDto();
        List<DataStatisticsModuleDto> moduleDtoList = new ArrayList<DataStatisticsModuleDto>();
        int num = -1;
        for(String item : list){
            Long moduleIdCurrent = Long.valueOf(item.substring(0 , item.indexOf('-')));
            String chilrenId = item.substring(item.indexOf('-') + 1);
            Boolean have = false;
            for(DataStatisticsModuleDto moduleDto : moduleDtoList){
                if(moduleIdCurrent.equals(moduleDto.getModuleUniqueId())){
                    have = true;
                }
            }
            if(!have){
                num++;//当有新的moduleId出现时, 也要用对应的新的发布态moduleId
            }
            //当前为草稿版本则用最新发布版本的moduleId
            Long moduleId = CollectionUtils.isEmpty(moduleIds) ? moduleIdCurrent : Long.valueOf(moduleIds.get(num));
            if (moduleId == null || StringUtils.isBlank(chilrenId)) {
                throw new BusinessException("参数异常！");
            }
            DataStatisticsModuleDto.ElementDto elementDto = new DataStatisticsModuleDto.ElementDto(chilrenId);
            String blankUniqueId = String.format(RedisConstant.BLANK_UNIQUEID, moduleId.toString(), chilrenId, channelNo);
            setElementDto(DataStatisticsModuleTypeEnum.BLANK, yesterdayStr, blankUniqueId, flag, elementDto);

           if(have){
               for(DataStatisticsModuleDto moduleDto : moduleDtoList){
                   if(moduleIdCurrent.equals(moduleDto.getModuleUniqueId())){
                       moduleDto.getElementDtoList().add(elementDto);
                   }
               }
           }else{
               DataStatisticsModuleDto moduleDto = new DataStatisticsModuleDto();
               moduleDto.setModuleUniqueId(moduleIdCurrent);
               moduleDto.setElementDtoList(new ArrayList<DataStatisticsModuleDto.ElementDto>());
               moduleDto.getElementDtoList().add(elementDto);
               moduleDtoList.add(moduleDto);
           }
        };
        statisticsDto.setModuleDtoList(moduleDtoList);
        return statisticsDto;
    }

    private void processDataStatisticsForModule(DataStatisticsDto statisticsDto, List<PageModule> pageModuleList, boolean dataFlag) {
        DataStatisticsModuleDto moduleDto;
        List<DataStatisticsModuleDto> moduleDtoList = new ArrayList<>(pageModuleList.size());
        String yesterdayStr = DateUtil.dateTimeToStr(DateUtil.add(new Date(), -1), DateUtil.DATE_DAY);
        for (PageModule pageModule : pageModuleList) {
            moduleDto = new DataStatisticsModuleDto(pageModule.getModuleId(), null);
            moduleDtoList.add(moduleDto);
            if (CommonConstant.ModuleType.CAROUSEL.getCode().equals(pageModule.getModuleType())) {
                // banner
                processElementDtoForBanner(pageModule, moduleDto, yesterdayStr, dataFlag);

            } else if (CommonConstant.ModuleType.NAVIGATION.getCode().equals(pageModule.getModuleType())) {
                // 导航
                processElementDtoForNavigate(pageModule, moduleDto, yesterdayStr, dataFlag);

            } else if (CommonConstant.ModuleType.HOTSPOT.getCode().equals(pageModule.getModuleType())) {
                // 热区
                processElementDtoForHotspot(pageModule, moduleDto, yesterdayStr, dataFlag);

            } else if (CommonConstant.ModuleType.LIST_PHOTOS.getCode().equals(pageModule.getModuleType())) {
                // 列表多图
                processElementDtoForComboImg(pageModule, moduleDto, yesterdayStr, dataFlag);

            } else if (CommonConstant.ModuleType.BUTTON.getCode().equals(pageModule.getModuleType())) {
                // 按钮
                processElementDtoForButton(pageModule, moduleDto, yesterdayStr, dataFlag);

            } else if (CommonConstant.ModuleType.LOGIN.getCode().equals(pageModule.getModuleType())) {

                List<DataStatisticsModuleDto.ElementDto> elementDtoList = new ArrayList<>();
                String webJson = pageModule.getWebJson();
                Long moduleUniqueId = 0L;
                if(StringUtils.isNotEmpty(webJson)) {
                    WebJsonBo webJsonBo = JSONObject.parseObject(webJson, WebJsonBo.class);
                    moduleUniqueId = webJsonBo.getModuleUniqueId();
                }
                DataStatisticsModuleDto.ElementDto elementDto = converterMap2ShowCounter(DataStatisticsModuleTypeEnum.LOGIN, yesterdayStr, pageModule.getModuleUniqueId(), dataFlag);
                elementDto.setUniqueId(moduleUniqueId);
                elementDtoList.add(elementDto);
                moduleDto.setElementDtoList(elementDtoList);
                moduleDto.setModuleUniqueId(moduleUniqueId);
            } else if (CommonConstant.ModuleType.PERSONAL_INFO.getCode().equals(pageModule.getModuleType())) {
                // 个人信息
                processElementDtoForPersonalInfo(pageModule, moduleDto, yesterdayStr, dataFlag);

            } else if (CommonConstant.ModuleType.ORDER_MANAGEMENT.getCode().equals(pageModule.getModuleType())) {
                // 订单管理
                processElementDtoForOrderManagement(pageModule, moduleDto, yesterdayStr, dataFlag);

            } else if (CommonConstant.ModuleType.MY_MOD.getCode().equals(pageModule.getModuleType())) {
                // 我的模块
                processElementDtoForMyMod(pageModule, moduleDto, yesterdayStr, dataFlag);

            } else if (CommonConstant.ModuleType.HOME_NAV.getCode().equals(pageModule.getModuleType())) {
                // 首页导航
                processElementDtoForHomeNav(pageModule, moduleDto, yesterdayStr, dataFlag);

            }
        }
        statisticsDto.setModuleDtoList(moduleDtoList);
    }

    /**
     * 获取用户浏览次数和人数
     *
     * @param moduleTypeEnum 模块类型
     * @param day            获取的时间
     * @param uniqueId       元素ID
     * @return
     */
    private DataStatisticsModuleDto.ElementDto converterMap2ShowCounter(DataStatisticsModuleTypeEnum moduleTypeEnum, String day, Long uniqueId, boolean dataFlag) {
        DataStatisticsModuleDto.ElementDto elementDto = new DataStatisticsModuleDto.ElementDto(uniqueId);
        // 每日点击次数
        String uniqueIdStr = String.valueOf(uniqueId);
        setElementDto(moduleTypeEnum, day, uniqueIdStr, dataFlag, elementDto);
        return elementDto;
    }

    /**
     * 设置埋点数据
     */
    private DataStatisticsModuleDto.ElementDto setElementDto(DataStatisticsModuleTypeEnum moduleTypeEnum, String day, String uniqueIdStr, boolean dataFlag, DataStatisticsModuleDto.ElementDto elementDto){
        String key = dataFlag ? String.format(RedisConstant.DATA_STATISTICS_CLICK_TIMES, moduleTypeEnum.type())
                : String.format(RedisConstant.DATA_STATISTICS_EVERY_DAY_CLICK_TIMES, moduleTypeEnum.type(), day);
        Integer count = redisService.hget(key, uniqueIdStr);
        elementDto.setCount(converterPageShowTime(count == null ? 0 : Long.valueOf(count)));

        // 用户数量
        key = dataFlag ? String.format(RedisConstant.DATA_STATISTICS_PERSON_CLICK_TIMES, moduleTypeEnum.type(), uniqueIdStr)
                : String.format(RedisConstant.DATA_STATISTICS_EVERY_DAY_PERSON_CLICK_TIMES, moduleTypeEnum.type(), day, uniqueIdStr);
        long personCount = redisService.pfcount(key);
        elementDto.setPersonCount(converterPageShowTime(personCount));
        return elementDto;
    }

    private ModuleShowTimeLog initModuleShowTimeLog(String channelNo, Long pageId, String pageUniqueId, String userBoardUniqueId,
                                                    Long moduleId, Long moduleUniqueId, DataStatisticsModuleTypeEnum moduleTypeEnum) {

        ModuleShowTimeLog showTimeLog = new ModuleShowTimeLog();
        showTimeLog.setChannelNo(channelNo);
        showTimeLog.setPageUniqueId(pageUniqueId);

        showTimeLog.setPageId(pageId);
        showTimeLog.setModuleId(moduleId);
        showTimeLog.setModuleUniqueId(moduleUniqueId);
        showTimeLog.setUserBoardUniqueId(userBoardUniqueId);

        showTimeLog.setModuleType(moduleTypeEnum == null ? null : moduleTypeEnum.type());
        return showTimeLog;
    }

    private ModuleClickTimeLog initModuleClickTimeLog(String channelNo, Long pageId, String pageUniqueId, String userBoardUniqueId,
                                                      Long moduleId, Long moduleUniqueId, DataStatisticsModuleTypeEnum moduleTypeEnum) {
        ModuleClickTimeLog clickTimeLog = new ModuleClickTimeLog();
        clickTimeLog.setPageId(pageId);
        clickTimeLog.setModuleId(moduleId);
        clickTimeLog.setChannelNo(channelNo);
        clickTimeLog.setPageUniqueId(pageUniqueId);
        clickTimeLog.setModuleUniqueId(moduleUniqueId);
        clickTimeLog.setUserBoardUniqueId(userBoardUniqueId);

        clickTimeLog.setCreateTime(new Date());
        clickTimeLog.setModuleType(moduleTypeEnum == null ? null : moduleTypeEnum.type());
        return clickTimeLog;
    }


    private ModuleRatioLog initModuleRatioLog(String channelNo, Long pageId, String pageUniqueId, String userBoardUniqueId,
                                              Long moduleId, Long moduleUniqueId, DataStatisticsModuleTypeEnum moduleTypeEnum) {
        ModuleRatioLog ratioLog = new ModuleRatioLog();
        ratioLog.setPageId(pageId);
        ratioLog.setChannelNo(channelNo);
        ratioLog.setPageUniqueId(pageUniqueId);

        ratioLog.setModuleId(moduleId);
        ratioLog.setModuleUniqueId(moduleUniqueId);
        ratioLog.setUserBoardUniqueId(userBoardUniqueId);

        ratioLog.setCreateTime(new Date());
        ratioLog.setModuleType(moduleTypeEnum == null ? null : moduleTypeEnum.type());
        return ratioLog;
    }

    private void processPageVisitTimeForRedis(String channelCode, String userCode, String pageUniqueId, long visitTime, Date clickTime) {
        // 保存页面点击次数到redis
        String pageUniqueIdStr = String.valueOf(pageUniqueId);
        redisService.hsetIncr(RedisConstant.DATA_STATISTICS_PAGE_CLICK_TIMES, pageUniqueIdStr);

        // 每日点击次数
        String today = DateUtil.dateTimeToStr(clickTime, DateUtil.DATE_DAY);
        redisService.hsetIncr(String.format(RedisConstant.DATA_STATISTICS_PAGE_EVERY_DAY_CLICK_TIMES, today), pageUniqueIdStr);

        // 浏览时长
        if (visitTime > 0) {
            // 页面浏览时长
            redisService.hsetIncr(RedisConstant.DATA_STATISTICS_PAGE_VISIT_TIMES, pageUniqueIdStr, visitTime);
            redisService.hsetIncr(String.format(RedisConstant.DATA_STATISTICS_PAGE_EVERY_DAY_VISIT_TIMES, today), pageUniqueIdStr, visitTime);

            // 渠道停留时长
            redisService.hsetIncr(RedisConstant.DATA_STATISTICS_PLATFORM_CHANNEL_VISIT_TIMES, channelCode, visitTime);
            redisService.hsetIncr(String.format(RedisConstant.DATA_STATISTICS_PLATFORM_CHANNEL_EVERY_DAY_VISIT_TIMES, today), channelCode, visitTime);
        }

        // 页面用户浏览数
        redisService.pfadd(String.format(RedisConstant.DATA_STATISTICS_PAGE_PERSON_CLICK_TIMES, pageUniqueIdStr), userCode);
        redisService.pfadd(String.format(RedisConstant.DATA_STATISTICS_PAGE_PERSON_EVERY_DAY_CLICK_TIMES, today, pageUniqueIdStr), userCode);

        // 渠道每日访问人数和总访问人数
        redisService.pfadd(String.format(RedisConstant.DATA_STATISTICS_PLATFORM_CHANNEL_PERSON_TIMES, channelCode), userCode);
        redisService.pfadd(String.format(RedisConstant.DATA_STATISTICS_PLATFORM_CHANNEL_EVERY_DAY_PERSON_TIMES, channelCode, today), userCode);

        // 页面新增访问数量
        String setKey = String.format(RedisConstant.DATA_STATISTICS_PAGE_VISIT_PERSON, pageUniqueIdStr);
        boolean isMember = redisService.isMember(setKey, userCode);
        if (!isMember) {
            // 未访问过该页面
            redisService.sadd(setKey, userCode);

            // 当日页面新增访问人数自增
            redisService.hsetIncr(String.format(RedisConstant.DATA_STATISTICS_PAGE_NEW_VISIT_PERSON, today), pageUniqueIdStr);
        }

        // 渠道每日新增用户数
        setKey = String.format(RedisConstant.DATA_STATISTICS_PLATFORM_CHANNEL_VISIT_PERSON, channelCode);
        isMember = redisService.isMember(setKey, userCode);
        if (!isMember) {
            // 未访问过该页面
            redisService.sadd(setKey, userCode);

            // 渠道新增用户数自增
            redisService.hsetIncr(String.format(RedisConstant.DATA_STATISTICS_PLATFORM_CHANNEL_NEW_VISIT_PERSON, today), channelCode);
        }
    }

    private void processClickTimeForRedis(DataStatisticsModuleTypeEnum moduleTypeEnum, Long uniqueId, String userCode, Date clickTime) {
        String uniqueIdStr = String.valueOf(uniqueId);
        saveClickTimeForRedis( moduleTypeEnum, uniqueIdStr, userCode, clickTime);
    }

    private void saveClickTimeForRedis(DataStatisticsModuleTypeEnum moduleTypeEnum, String uniqueIdStr, String userCode, Date clickTime) {
        // 总点击数
        redisService.hsetIncr(String.format(RedisConstant.DATA_STATISTICS_CLICK_TIMES, moduleTypeEnum.type()), uniqueIdStr);

        // 每日点击次数
        String today = DateUtil.dateTimeToStr(clickTime, DateUtil.DATE_DAY);
        redisService.hsetIncr(String.format(RedisConstant.DATA_STATISTICS_EVERY_DAY_CLICK_TIMES, moduleTypeEnum.type(), today), uniqueIdStr);

        // 用户数量
        redisService.pfadd(String.format(RedisConstant.DATA_STATISTICS_PERSON_CLICK_TIMES, moduleTypeEnum.type(), uniqueIdStr), userCode);

        // 用户数量-每日
        redisService.pfadd(String.format(RedisConstant.DATA_STATISTICS_EVERY_DAY_PERSON_CLICK_TIMES, moduleTypeEnum.type(), today, uniqueIdStr), userCode);
    }

    private void validateDataStatisticsAddRequest(DataStatisticsAddRequest request) {
        if (null == request || StringUtils.isBlank(request.getPageUniqueId())
                || StringUtils.isBlank(request.getChannelNo()) || StringUtils.isBlank(request.getUserMark())) {
            throw new BusinessException("参数不能为空！");
        }
        Assert.notNull(request.getVisitStartTime(), "进入不能为空！");

        // 页面信息
        Long releaseVersion = platformChannelService.getReleaseVersionIdByChannelNo(request.getChannelNo());
        if (null == releaseVersion) {
            throw new BusinessException("非法请求！");
        }

        PageManager pageManager = pageManagerService.getByPageUniqueId(releaseVersion, request.getPageUniqueId());
        if (null == pageManager) {
            throw new BusinessException("页面信息不存在！");
        }
        request.setPageId(pageManager.getId());

        Long userId = request.getUserId();
        if (null == userId) {
            // 用户未登陆
            if (StringUtils.isBlank(request.getUserMark())) {
                throw new BusinessException("用户code不能为空！");
            }
        } else {

            User user = userService.getById(userId);
            request.setUserMark(user.getCode());

            request.setSex(user.getSex());
            request.setUserName(user.getRealName());
            request.setBirthday(user.getBirthday());
            request.setIdentityNo(user.getIdentityNo());
            request.setIdentityType(user.getIdentityType());
        }

    }

    private String converterPageShowTime(Long num) {
        if (num == null) {
            num = 0L;
        }

        if (10000L > num) {
            return String.valueOf(num);
        }

        if (1000000L <= num) {
            return "100w+";
        }
        return (num / 10000L) + "w+";
    }

    private void processElementDtoForButton(PageModule pageModule, DataStatisticsModuleDto moduleDto, String yesterdayStr, boolean dataFlag) {
        // 按钮
        BtnGroup btnGroup = btnGroupService.getById(pageModule.getModuleId());
        if (btnGroup == null) {
            return;
        }
        Btn btn = new Btn();
        btn.setBtnGroupId(btnGroup.getId());
        btn.setDeletedFlag(CommonConstant.IS_NOT_DELETE);
        List<Btn> btnList = btnService.baseFindList(btn);
        if (CollectionUtils.isEmpty(btnList)) {
            return;
        }
        DataStatisticsModuleDto.ElementDto elementDto;
        List<DataStatisticsModuleDto.ElementDto> elementDtoList = new ArrayList<>(btnList.size());
        for (Btn button : btnList) {
            elementDto = converterMap2ShowCounter(DataStatisticsModuleTypeEnum.BUTTON, yesterdayStr, button.getModuleUniqueId(), dataFlag);
            elementDtoList.add(elementDto);
        }
        moduleDto.setElementDtoList(elementDtoList);
    }

    private void processElementDtoForBanner(PageModule pageModule, DataStatisticsModuleDto moduleDto, String yesterdayStr, boolean dataFlag) {
        // banner
        DataStatisticsModuleDto.ElementDto elementDto;
        Banner banner = bannerService.getById(pageModule.getModuleId());
        if (banner == null) {
            return;
        }
        List<BannerImg> bannerImgList = bannerImgService.findByBannerId(banner.getId());
        if (CollectionUtils.isEmpty(bannerImgList)) {
            return;
        }
        List<DataStatisticsModuleDto.ElementDto> elementDtoList = new ArrayList<>(bannerImgList.size());
        for (BannerImg bannerImg : bannerImgList) {
            elementDto = converterMap2ShowCounter(DataStatisticsModuleTypeEnum.BANNER, yesterdayStr, bannerImg.getModuleUniqueId(), dataFlag);
            elementDtoList.add(elementDto);
        }
        moduleDto.setElementDtoList(elementDtoList);
    }

    private void processElementDtoForNavigate(PageModule pageModule, DataStatisticsModuleDto moduleDto, String yesterdayStr, boolean dataFlag) {
        // 导航
        DataStatisticsModuleDto.ElementDto elementDto;
        Navigate navigate = navigateService.getById(pageModule.getModuleId());
        if (navigate == null) {
            return;
        }
        List<NavigateImg> navigateImgList = navigateImgService.findByNavigateId(navigate.getId());
        if (CollectionUtils.isEmpty(navigateImgList)) {
            return;
        }

        List<DataStatisticsModuleDto.ElementDto> elementDtoList = new ArrayList<>(navigateImgList.size());
        for (NavigateImg img : navigateImgList) {
            elementDto = converterMap2ShowCounter(DataStatisticsModuleTypeEnum.NAVIGATE, yesterdayStr, img.getModuleUniqueId(), dataFlag);
            elementDtoList.add(elementDto);
        }
        moduleDto.setElementDtoList(elementDtoList);
    }

    private void processElementDtoForHotspot(PageModule pageModule, DataStatisticsModuleDto moduleDto, String yesterdayStr, boolean dataFlag) {
        // 热区
        DataStatisticsModuleDto.ElementDto elementDto;
        Hotspot hotspot = hotspotService.getById(pageModule.getModuleId());
        if (hotspot == null) {
            return;
        }
        HotspotParts hotspotParts = new HotspotParts();
        hotspotParts.setHotspotId(hotspot.getId());
        hotspotParts.setDeletedFlag(CommonConstant.IS_NOT_DELETE);
        List<HotspotParts> partsList = hotspotPartsService.baseFindList(hotspotParts);
        if (CollectionUtils.isEmpty(partsList)) {
            return;
        }

        List<DataStatisticsModuleDto.ElementDto> elementDtoList = new ArrayList<>(partsList.size());
        for (HotspotParts parts : partsList) {
            elementDto = converterMap2ShowCounter(DataStatisticsModuleTypeEnum.HOTSPOT, yesterdayStr, parts.getModuleUniqueId(), dataFlag);
            elementDtoList.add(elementDto);
        }
        moduleDto.setElementDtoList(elementDtoList);
    }

    private void processElementDtoForComboImg(PageModule pageModule, DataStatisticsModuleDto moduleDto, String yesterdayStr, boolean dataFlag) {
        // 列表多图
        ComboImg comboImg = comboImgService.getById(pageModule.getModuleId());
        if (comboImg == null) {
            return;
        }
        ComboImgDetail comboImgDetail = new ComboImgDetail();
        comboImgDetail.setComboImgId(comboImg.getId());
        comboImgDetail.setDeletedFlag(CommonConstant.IS_NOT_DELETE);
        List<ComboImgDetail> detailList = comboImgDetailService.baseFindList(comboImgDetail);
        if (CollectionUtils.isEmpty(detailList)) {
            return;
        }

        DataStatisticsModuleDto.ElementDto elementDto;
        List<DataStatisticsModuleDto.ElementDto> elementDtoList = new ArrayList<>(detailList.size());
        for (ComboImgDetail detail : detailList) {
            elementDto = converterMap2ShowCounter(DataStatisticsModuleTypeEnum.COMBO_IMG, yesterdayStr, detail.getModuleUniqueId(), dataFlag);
            elementDtoList.add(elementDto);
        }
        moduleDto.setElementDtoList(elementDtoList);
    }

    private void processElementDtoForPersonalInfo(PageModule pageModule, DataStatisticsModuleDto moduleDto, String yesterdayStr, boolean dataFlag) {
        // personalInfo
        PersonalInfo personalInfo = personalInfoService.getById(pageModule.getModuleId());
        if (personalInfo == null) {
            return;
        }
        WebJsonBo webJsonBo = JSONObject.parseObject(personalInfo.getWebJson(), WebJsonBo.class);
        if (webJsonBo.getData() != null) {
            List<DataStatisticsModuleDto.ElementDto> elementDtoList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(webJsonBo.getData().getVtButtonList())) {
                for (WebJsonBo.WebJsonVtButtonBo webJsonVtButtonBo : webJsonBo.getData().getVtButtonList()) {
                    DataStatisticsModuleDto.ElementDto elementDto = converterMap2ShowCounter(DataStatisticsModuleTypeEnum.PERSONAL_INFO, yesterdayStr, webJsonVtButtonBo.getModuleUniqueId(), dataFlag);
                    elementDtoList.add(elementDto);
                }
            }

            if (CollectionUtils.isNotEmpty(webJsonBo.getData().getImgList())) {
                for (WebJsonBo.WebJsonImgBo webJsonImgBo : webJsonBo.getData().getImgList()) {
                    DataStatisticsModuleDto.ElementDto elementDto = converterMap2ShowCounter(DataStatisticsModuleTypeEnum.PERSONAL_INFO, yesterdayStr, webJsonImgBo.getModuleUniqueId(), dataFlag);
                    elementDtoList.add(elementDto);
                }
            }

            moduleDto.setElementDtoList(elementDtoList);
        }
    }

    private void processElementDtoForOrderManagement(PageModule pageModule, DataStatisticsModuleDto moduleDto, String yesterdayStr, boolean dataFlag) {
        // orderManagement
        OrderManagement orderManagement = orderManagementService.getById(pageModule.getModuleId());
        if (orderManagement == null) {
            return;
        }
        WebJsonBo webJsonBo = JSONObject.parseObject(orderManagement.getWebJson(), WebJsonBo.class);
        if (webJsonBo.getData() != null) {
            List<DataStatisticsModuleDto.ElementDto> elementDtoList = new ArrayList<>();

            if (CollectionUtils.isNotEmpty(webJsonBo.getData().getImgList())) {
                for (WebJsonBo.WebJsonImgBo webJsonImgBo : webJsonBo.getData().getImgList()) {
                    DataStatisticsModuleDto.ElementDto elementDto = converterMap2ShowCounter(DataStatisticsModuleTypeEnum.ORDER_MANAGEMENT, yesterdayStr, webJsonImgBo.getModuleUniqueId(), dataFlag);
                    elementDtoList.add(elementDto);
                }
            }

            moduleDto.setElementDtoList(elementDtoList);
        }
    }

    private void processElementDtoForMyMod(PageModule pageModule, DataStatisticsModuleDto moduleDto, String yesterdayStr, boolean dataFlag) {
        // myMod
        MyMod myMod = myModService.getById(pageModule.getModuleId());
        if (myMod == null) {
            return;
        }
        WebJsonBo webJsonBo = JSONObject.parseObject(myMod.getWebJson(), WebJsonBo.class);
        if (webJsonBo.getData() != null) {
            List<DataStatisticsModuleDto.ElementDto> elementDtoList = new ArrayList<>();

            if (CollectionUtils.isNotEmpty(webJsonBo.getData().getImgList())) {
                for (WebJsonBo.WebJsonImgBo webJsonImgBo : webJsonBo.getData().getImgList()) {
                    DataStatisticsModuleDto.ElementDto elementDto = converterMap2ShowCounter(DataStatisticsModuleTypeEnum.MY_MOD, yesterdayStr, webJsonImgBo.getModuleUniqueId(), dataFlag);
                    elementDtoList.add(elementDto);
                }
            }

            moduleDto.setElementDtoList(elementDtoList);
        }
    }

    private void processElementDtoForHomeNav(PageModule pageModule, DataStatisticsModuleDto moduleDto, String yesterdayStr, boolean dataFlag) {
        // HomaNav
        HomeNav homeNav = homeNavService.getById(pageModule.getModuleId());
        if (homeNav == null) {
            return;
        }
        WebJsonBo webJsonBo = JSONObject.parseObject(homeNav.getWebJson(), WebJsonBo.class);
        if (webJsonBo.getData() != null) {
            List<DataStatisticsModuleDto.ElementDto> elementDtoList = new ArrayList<>();

            if (CollectionUtils.isNotEmpty(webJsonBo.getData().getImgList())) {
                for (WebJsonBo.WebJsonImgBo webJsonImgBo : webJsonBo.getData().getImgList()) {
                    DataStatisticsModuleDto.ElementDto elementDto = converterMap2ShowCounter(DataStatisticsModuleTypeEnum.HOME_NAV, yesterdayStr, webJsonImgBo.getModuleUniqueId(), dataFlag);
                    elementDtoList.add(elementDto);
                }
            }

            moduleDto.setElementDtoList(elementDtoList);
        }
    }

    private void saveShowTimeLog(MqRequest request, Long moduleId, Long moduleUniqueId, Long childModuleId, Long childModuleUniqueId, Long dateTime, DataStatisticsModuleTypeEnum moduleTypeEnum) {
        Long pageId = request.getPageId();
        String channelNo = request.getChannelNo();
        String pageUniqueId = request.getPageUniqueId();
        String userBoardUniqueId = request.getUserBoardUniqueId();

        ModuleShowTimeLog timeLog = initModuleShowTimeLog(channelNo, pageId, pageUniqueId, userBoardUniqueId, moduleId,
                moduleUniqueId, moduleTypeEnum);
        timeLog.setTime(dateTime);
        timeLog.setElementId(childModuleId);
        timeLog.setUniqueId(childModuleUniqueId);
        moduleShowTimeLogService.save(timeLog);
    }

    private ModuleClickTimeLog saveCheckTimeLog(MqRequest request, Long moduleId, Long moduleUniqueId, Long childModuleId, Long childModuleUniqueId, Long dateTime, DataStatisticsModuleTypeEnum moduleTypeEnum) {
        Long pageId = request.getPageId();
        String channelNo = request.getChannelNo();
        String pageUniqueId = request.getPageUniqueId();
        String userBoardUniqueId = request.getUserBoardUniqueId();

        ModuleClickTimeLog timeLog = initModuleClickTimeLog(channelNo, pageId, pageUniqueId, userBoardUniqueId, moduleId,
                moduleUniqueId, moduleTypeEnum);
        timeLog.setElementId(childModuleId);
        timeLog.setClickTime(dateTime);
        timeLog.setUniqueId(childModuleUniqueId);
        moduleClickTimeLogService.save(timeLog);

        return timeLog;
    }

    private List<String> getModuleIds(String pageId, String channelNo){
        ArrayList<String> strings = new ArrayList<>();
        try {
            PageManager manager = pageManagerService.getById(pageId);
            PlatformVersion platformVersion = platformVersionService.getById(manager.getVersionId());
            if(PageCenterConsts.VersionStatus.RELEASE.getCode().equals(platformVersion.getStatus())){
                return strings;
            }else {
                List<PlatformVersion> versionList = platformVersionService.listByChannelNo(channelNo);
                Long version = (versionList.stream().filter(v->PageCenterConsts.VersionStatus.RELEASE.getCode().equals(v.getStatus())).sorted(Comparator.comparing(PlatformVersion::getCreatedTime).reversed()).limit(1).map(item -> item.getId()).collect(Collectors.toList())).get(0);
                QueryWrapper<PageManager> wrapper = new QueryWrapper<PageManager>(new PageManager());
                wrapper.getEntity().setVersionId(version);
                wrapper.getEntity().setChannelNo(channelNo);
                wrapper.getEntity().setPageUniqueId(manager.getPageUniqueId());
                PageManager page = pageManagerService.getOne(wrapper);
                List<PageModule> pageModules = pageModuleService.listByPageId(page.getId());
                strings = (ArrayList<String>)pageModules.stream().map(item -> {
                    return item.getModuleId().toString();
                }).collect(Collectors.toList());
            }
        }catch (Exception e){
            log.error(String.format("com.touchealth.platform.processengine.service.impl.datastatistics.DataStatisticsServiceImpl.getModuleIds 查询数据异常pageId=%s , channelNo= %s", pageId,channelNo));
            throw new BusinessException(String.format("com.touchealth.platform.processengine.service.impl.datastatistics.DataStatisticsServiceImpl.getModuleIds 查询数据异常pageId=%s , channelNo= %s", pageId,channelNo));
        }
        return strings;
    }

}
