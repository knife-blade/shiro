package com.touchealth.platform.processengine.controller.channel;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.touchealth.common.page.Pager;
import com.touchealth.common.utils.ValidateUtil;
import com.touchealth.platform.basic.response.PlatFormResponse;
import com.touchealth.platform.message.client.bo.SmsSendRequest;
import com.touchealth.platform.message.client.constant.SceneEnum;
import com.touchealth.platform.message.client.service.SmsSendService;
import com.touchealth.platform.processengine.annotation.PassToken;
import com.touchealth.platform.processengine.constant.PageCenterConsts;
import com.touchealth.platform.processengine.constant.UserConstant;
import com.touchealth.platform.processengine.entity.page.PlatformChannel;
import com.touchealth.platform.processengine.entity.user.User;
import com.touchealth.platform.processengine.entity.user.UserChannel;
import com.touchealth.platform.processengine.pojo.dto.Response;
import com.touchealth.platform.processengine.pojo.dto.channel.PlatformChannelDto;
import com.touchealth.platform.processengine.pojo.dto.page.BusinessTypeDto;
import com.touchealth.platform.processengine.pojo.dto.platformchannel.RentDto;
import com.touchealth.platform.processengine.pojo.request.page.PlatformChannelRequest;
import com.touchealth.platform.processengine.pojo.request.page.PresetPageRequest;
import com.touchealth.platform.processengine.service.page.PlatformChannelService;
import com.touchealth.platform.processengine.service.user.UserChannelService;
import com.touchealth.platform.processengine.service.user.UserService;
import com.touchealth.platform.processengine.utils.BaseHelper;
import com.touchealth.platform.processengine.utils.FirstCharUtils;
import com.touchealth.platform.user.client.api.RentApi;
import com.touchealth.platform.user.client.dto.bo.FindByParamsBo;
import com.touchealth.platform.user.client.dto.response.RentRes;
import com.touchealth.process.engine.dto.channel.ChannelBaseInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

import static com.touchealth.platform.processengine.constant.CommonConstant.SC_ADMIN_ID;
import static com.touchealth.platform.processengine.constant.CommonConstant.SC_CHANNEL_ID;

/**
 * 平台中心-渠道管理
 *
 * @author liufengqiang
 * @date 2020-11-13 17:04:10
 */
@RestController
@RequestMapping("/platform-channel")
@Slf4j
public class PlatformChannelController {

    @Resource
    private PlatformChannelService platformChannelService;
    @Resource
    private UserService userService;
    @Resource
    private SmsSendService smsSendService;
    @Resource
    private UserChannelService userChannelService;
    @Resource
    private RentApi rentApi;
    private com.touchealth.platform.user.client.constant.CommonConstant.FindParamMatchType IN =
            com.touchealth.platform.user.client.constant.CommonConstant.FindParamMatchType.IN;

    /**
     * 新增渠道
     */
    @PostMapping
    public Response save(@RequestAttribute Long userId,
                         @RequestBody @Valid PlatformChannelRequest request,
                         @RequestHeader String channelNo) {
        // 查询用户
        User user = userService.findById(userId);
        cn.hutool.core.lang.Assert.notNull(user, "用户不存在");

        // 势成超管或当前渠道超管才能添加渠道
        if (!SC_ADMIN_ID.equals(userId)) {
            Assert.isTrue(StringUtils.isNotBlank(channelNo), "缺少渠道参数");
            PlatformChannel platformChannel = platformChannelService.getByChannelNo(channelNo);
            Assert.notNull(platformChannel, "渠道不存在");
            Assert.isTrue(userId.equals(platformChannel.getAdminId()), "非本机构超管不能添加渠道");
        }

        // 势成主平台不能添加二级渠道
        if (request.getParentId() != null) {
            Assert.isTrue(!SC_ADMIN_ID.equals(request.getParentId()), "势成主平台不能添加二级渠道");
        }

        /*
         * 20200122 需求，仅超级管理员才有添加子渠道权限，仅势成超级管理员才能添加一级渠道
         */
        PlatformChannel mainPlatform = platformChannelService.getById(SC_CHANNEL_ID);
        String mainChannelNo = mainPlatform == null ? "" : mainPlatform.getChannelNo();
        boolean isAdmin = false, isMainChannel = false;
        if (mainChannelNo.equals(user.getChannelNo())) {
            isMainChannel = true;
        }
        if (UserConstant.USER_TYPE_PROCESS_ENGINE_ADMIN.equals(user.getUserType())) {
            isAdmin = true;
        }
        // 仅势成超级管理员才能添加一级渠道
        if (null == request.getParentId() && !(isAdmin && isMainChannel)) {
            return Response.error(1, "很抱歉，您不能添加顶级渠道。");
        }
        // 仅超级管理员才有添加子渠道权限
        if (!isAdmin) {
            return Response.error(1, "很抱歉，您不能添加渠道。");
        }

        PlatformChannel parentChannel = null;
        if (request.getParentId() != null) {
            parentChannel = platformChannelService.getById(request.getParentId());
            Assert.notNull(parentChannel, "父渠道不存在");
        }

        Assert.isTrue(StringUtils.isNotBlank(request.getAdminPassword()), "超级管理员密码不能为空");
        request.setChannelNo(createChannelNo(request.getParentId(), parentChannel == null ? null : parentChannel.getChannelNo()));

        PlatformChannel adminAccount = platformChannelService.getByAdminAccount(request.getAdminAccount());
        if (adminAccount != null) {
            return Response.normal(2, null, "管理员账号重复，请重新配置");
        }

        // 一级渠道租户id必传
        if (request.getParentId() == null && request.getRentId() == null) {
            return Response.error("一级渠道需要关联租户");
        }
        log.info("发送短信参数，{}, {}", user.getMobileNo(), request.getVerifyCode());
        PlatFormResponse<Boolean> response = smsSendService.validCode(user.getMobileNo(), request.getVerifyCode());
        log.info("验证码结果，{}", response);
        Assert.isTrue(response.getData(), "验证码错误");

        PlatformChannel platformChannel = platformChannelService.saveChannel(userId, request);
        return Response.ok(BaseHelper.r2t(platformChannel, PlatformChannelDto.class));
    }

    /**
     * 创建渠道号
     *
     * @param parentId        父渠道id
     * @param parentChannelNo 父渠道号
     * @return
     */
    private String createChannelNo(Long parentId, String parentChannelNo) {
        StringBuilder channelNo = new StringBuilder("CH");
        channelNo.append(parentId == null ? "1" : "2");

        if (parentId == null) {
            PlatformChannel lastChannel = platformChannelService.getOne(Wrappers.<PlatformChannel>lambdaQuery()
                    .isNull(PlatformChannel::getParentId).orderByDesc(PlatformChannel::getCreatedTime).last("limit 1"));
            try {
                channelNo.append(String.format("%04d", Integer.parseInt(lastChannel.getChannelNo().substring(3, 7)) + 1));
            } catch (Exception e) {
                log.error("总渠道解析异常", e);
                channelNo.append("0001");
            }
            channelNo.append("000");
        } else {
            try {
                channelNo.append(parentChannelNo, 3, 7);
            } catch (Exception e) {
                log.error("分渠道解析异常", e);
                channelNo.append("0000");
            }

            PlatformChannel lastChannel = platformChannelService.getOne(Wrappers.<PlatformChannel>lambdaQuery()
                    .eq(PlatformChannel::getParentId, parentId).orderByDesc(PlatformChannel::getCreatedTime).last("limit 1"));
            try {
                channelNo.append(String.format("%03d", Integer.parseInt(lastChannel.getChannelNo().substring(7, 10)) + 1));
            } catch (Exception e) {
                log.error("分渠道解析异常", e);
                channelNo.append("001");
            }
        }
        return channelNo.toString();
    }

    /**
     * 编辑渠道
     */
    @PutMapping("/{id}")
    public Response update(@RequestAttribute Long userId,
                           @PathVariable Long id,
                           @RequestBody @Valid PlatformChannelRequest request) {
        PlatformChannel platformChannel = platformChannelService.getById(id);
        Assert.notNull(platformChannel, "渠道不存在");
        Assert.isTrue(userId == 1L || userId.equals(platformChannel.getAdminId()), "非当前渠道管理员不能编辑");

        PlatFormResponse<Boolean> response = smsSendService.validCode(request.getAdminMobileNo(), request.getVerifyCode());
        log.info("验证码结果，{}", response);
        Assert.isTrue(response.getData(), "验证码错误");

        platformChannelService.updateChannel(userId, platformChannel, request);
        return Response.success;
    }

    /**
     * 根据渠道名称获取渠道管理员账号
     */
    @GetMapping("/admin-account")
    public Response adminAccount(@RequestParam String channelName) {
        int count = 0;
        String adminAccount;
        PlatformChannel platformChannel;
        do {
            adminAccount = FirstCharUtils.first(channelName) + (count == 0 ? "" : count) + "@admintouchealth.com";
            platformChannel = platformChannelService.getByAdminAccount(adminAccount);
            count++;
        } while (platformChannel != null);
        return Response.ok(adminAccount);
    }

    /**
     * 渠道列表
     * <p>
     * 管理员和超管的区别是：超管有所有权限，管理员只有基础权限 + 配置权限
     * 势成主平台的管理员或超管能看到所有平台，其他平台的管理员或超管只能看到自己平台
     * 管理员或超管能看到父平台和子平台，但只能切换本平台及子平台，不能切换父平台
     */
    @GetMapping
    public Response list(@RequestHeader String channelNo, @RequestAttribute Long userId, String channelName, Long staffId) {
        userId = Optional.ofNullable(staffId).orElse(userId);

        User user = userService.getById(userId);
        Assert.notNull(user, "用户不存在");
        String selfChannel = user.getChannelNo();

        // 势成主平台管理员可操作所有渠道，id=1 代表势成主平台
        boolean isMainChannelAdmin = false;
        PlatformChannel mainPlatform = platformChannelService.getById(SC_CHANNEL_ID);
        if (mainPlatform != null && user.getChannelNo().equals(mainPlatform.getChannelNo()) &&
                UserConstant.USER_TYPE_PROCESS_ENGINE_ADMIN.equals(user.getUserType())) {
            isMainChannelAdmin = true;
        }

        List<PlatformChannel> platformChannels = platformChannelService.listByChannelName(channelName);
        if (CollectionUtils.isEmpty(platformChannels)) {
            return Response.ok(new ArrayList<>());
        }

        // 用户拥有的渠道权限
        List<String> userHasChannelList = new ArrayList<>();
        userHasChannelList.add(selfChannel);

        if (!isMainChannelAdmin) {
            List<UserChannel> userChannels = userChannelService.list(Wrappers.<UserChannel>lambdaQuery().eq(UserChannel::getUserId, userId));
            if (CollectionUtils.isNotEmpty(userChannels)) {
                UserChannel userChannel = userChannels.get(0);
                String channelNoList = Optional.ofNullable(userChannel.getChannelNoList()).orElse("");
                List<String> userHasChannelNoList = Arrays.asList(channelNoList.split(","));
                userHasChannelList.addAll(userHasChannelNoList);
            }
            platformChannels = platformChannels.stream()
                    .filter(o -> userHasChannelList.contains(o.getChannelNo()) || o.getChannelNo().equals(selfChannel))
                    .collect(Collectors.toList());

            Set<Long> channelIds = new HashSet<>();
            platformChannels.forEach(o -> channelIds.addAll(Arrays.stream(o.getLevelIndex().split("-")).filter(StringUtils::isNotEmpty).map(Long::parseLong).collect(Collectors.toSet())));
            if (CollectionUtils.isNotEmpty(channelIds)) {
                platformChannels = platformChannelService.listByIds(channelIds);
            }
        }

        // 渠道名
        Map<Long, String> channelNameMap = platformChannels.stream().collect(Collectors.toMap(PlatformChannel::getId, PlatformChannel::getChannelName));

        Map<Long, List<PlatformChannelDto>> channelMap = new HashMap<>(16);
        platformChannels.forEach(o -> {
            if (o.getParentId() != null) {
                List<PlatformChannelDto> child = channelMap.get(o.getParentId());
                if (child == null) {
                    child = new ArrayList<>();
                }
                child.add(getPlatformChannelDto(o, channelNameMap));
                channelMap.put(o.getParentId(), child);
            }
        });

        List<PlatformChannelDto> dtos = new ArrayList<>();
        boolean finalIsMainChannelAdmin = isMainChannelAdmin;
        List<String> finalUserHasChannelList = userHasChannelList;
        platformChannels.forEach(o -> {
            if (o.getParentId() == null) {
                dtos.add(getPlatformChannel(channelMap, getPlatformChannelDto(o, channelNameMap), finalUserHasChannelList,
                        finalIsMainChannelAdmin, selfChannel));
            }
        });

        dtos.forEach(o -> o.setChildChannelNum(getChildChannelNum(o)));

        String msg = null;
        if (StringUtils.isNotBlank(channelNo)) {
            PlatformChannel platformChannel = platformChannelService.getByChannelNo(channelNo);
            if (platformChannel == null) {
                msg = "当前渠道已被删除，请重新选择渠道";
            }
        }
        // 给渠道追加租户信息
        decorateRentInfo(dtos);
        return Response.normal(msg == null ? 0 : 1, dtos, msg);
    }

    /**
     * 给渠道追加租户信息
     */
    private void decorateRentInfo(List<PlatformChannelDto> dtos) {
        if (CollectionUtils.isEmpty(dtos)) {
            return;
        }
        List<Long> rentIds = dtos.stream().filter(e -> null != e.getRentId()).map(PlatformChannelDto::getRentId)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(rentIds)) {
            return;
        }
        List<FindByParamsBo> findByParamsBos = new ArrayList<>();
        findByParamsBos.add(new FindByParamsBo("id", Long.class, rentIds, IN));
        findByParamsBos.add(new FindByParamsBo("deletedFlag", Long.class, 0L));
        List<RentRes> rentResList = rentApi.findRent(findByParamsBos,
                new com.touchealth.platform.basic.response.Pager(1, Integer.MAX_VALUE)).get().getResultList();
        Map<Long, RentRes> rentResMap = rentResList.stream().collect(Collectors.toMap(RentRes::getId, e -> e));
        dtos.forEach(dto -> dto.setRentName(rentResMap.getOrDefault(dto.getRentId(), new RentRes()).getRentName()));
    }

    private PlatformChannelDto getPlatformChannelDto(PlatformChannel o, Map<Long, String> channelNameMap) {
        PlatformChannelDto dto = BaseHelper.r2t(o, PlatformChannelDto.class);

        if (StringUtils.isNotBlank(o.getBusinessType())) {
            dto.setBusinessType(Arrays.stream(o.getBusinessType().split(","))
                    .map(x -> PageCenterConsts.BusinessType.getDtoByCode(Integer.parseInt(x))).collect(Collectors.toList()));
        }

        if (o.getParentId() != null) {
            dto.setParentName(channelNameMap.get(o.getParentId()));
        }
        return dto;
    }

    private Integer getChildChannelNum(PlatformChannelDto children) {
        if (CollectionUtils.isEmpty(children.getChildren())) {
            return 0;
        } else {
            int num = children.getChildren().size();
            for (PlatformChannelDto o : children.getChildren()) {
                Integer childChannelNum = getChildChannelNum(o);
                num += childChannelNum;
                o.setChildChannelNum(childChannelNum);
            }
            return num;
        }
    }

    private PlatformChannelDto getPlatformChannel(Map<Long, List<PlatformChannelDto>> salesOrganMap, PlatformChannelDto channelDto,
                                                  List<String> userHasChannelList, Boolean isMainChannelAdmin, String selfChannel) {
        List<PlatformChannelDto> salesOrganDtos = salesOrganMap.get(channelDto.getId());
        if (salesOrganDtos != null) {
            List<PlatformChannelDto> dtos = new ArrayList<>();
            salesOrganDtos.forEach(o -> {
                PlatformChannelDto salesOrgan = getPlatformChannel(salesOrganMap, o, userHasChannelList, isMainChannelAdmin, selfChannel);
                salesOrgan.setHasPerm(isMainChannelAdmin || userHasChannelList.contains(o.getChannelNo()));
                salesOrgan.setOneself(o.getChannelNo().equals(selfChannel));
                dtos.add(salesOrgan);
            });
            channelDto.setChildren(dtos);
        }
        channelDto.setHasPerm(isMainChannelAdmin || userHasChannelList.contains(channelDto.getChannelNo()));
        channelDto.setOneself(channelDto.getChannelNo().equals(selfChannel));
        return channelDto;
    }

    /**
     * 渠道详情
     */
    @GetMapping("/details")
    public Response details(@RequestHeader String channelNo) {
        PlatformChannel platformChannel = platformChannelService.getByChannelNo(channelNo);
        Assert.notNull(platformChannel, "渠道不存在");

        PlatformChannelDto dto = BaseHelper.r2t(platformChannel, PlatformChannelDto.class);
        Set<Integer> businessSet;
        if (StringUtils.isNotBlank(platformChannel.getBusinessType())) {
            businessSet = Arrays.stream(platformChannel.getBusinessType().split(",")).map(Integer::parseInt).collect(Collectors.toSet());
        } else {
            businessSet = new HashSet<>();
        }
        businessSet.add(PageCenterConsts.BusinessType.COMMON.getCode());
        dto.setBusinessType(businessSet.stream().map(PageCenterConsts.BusinessType::getDtoByCode).collect(Collectors.toList()));
        if (null != platformChannel.getRentId()) {
            RentRes rentRes = rentApi.detail(platformChannel.getRentId()).get();
            dto.setRentId(rentRes.getId());
            dto.setRentName(rentRes.getRentName());
        }
        return Response.ok(dto);
    }

    /**
     * 删除渠道
     */
    @DeleteMapping("/{id}")
    public Response delete(@PathVariable Long id) {
        Assert.isTrue(id != 1L, "势成主平台不能删除");
        platformChannelService.deleteChannel(id);
        return Response.success;
    }

    /**
     * 渠道上架/下架
     */
    @PutMapping("/{id}/shelf")
    public Response shelfStatus(@PathVariable Long id, @RequestBody PlatformChannelRequest request) {
        Assert.notNull(request.getShelfStatus(), "上架状态不能为空");
        platformChannelService.update(new PlatformChannel(), Wrappers.<PlatformChannel>lambdaUpdate()
                .set(PlatformChannel::getShelfStatus, request.getShelfStatus())
                .eq(PlatformChannel::getId, id));
        return Response.success;
    }

    /**
     * 业务类型列表
     */
    @GetMapping("/businessType")
    public Response businessType() {
        return Response.ok(Arrays.stream(PageCenterConsts.BusinessType.values()).filter(o -> o.getCode() != 0).map(o ->
                new BusinessTypeDto(o.getCode(), o.getName(), o.getDesc())).collect(Collectors.toList()));
    }

    /**
     * 未关联渠道的租户列表
     */
    @GetMapping("/unused-rent-list")
    public Response<List<RentDto>> rentList() {
        Pager<RentDto> pager = platformChannelService.unusedRentList(new Pager(1, Integer.MAX_VALUE));
        return Response.ok(pager.getResultList());
    }

    /**
     * 根据渠道code获取对应已发布的登录页ID
     *
     * @param channelNo 渠道code
     * @return
     */
    @PassToken
    @GetMapping("/loginPage")
    public String getLoginPageIdByChannelNo(@RequestHeader("channelNo") String channelNo) {
        return platformChannelService.getLoginPageIdByChannelNo(channelNo);
    }

    /**
     * 新增渠道获取验证码
     */
    @GetMapping("/verify-code")
    public Response verifyCode(@RequestParam String mobileNo,@RequestHeader String channelNo) {
        if (!ValidateUtil.isMobileNO(mobileNo)) {
            return Response.error("非法的手机号");
        }
        PlatformChannel platformChannel = platformChannelService.getByChannelNo(channelNo);

        log.info("发送短信参数，{}", mobileNo);
        SmsSendRequest request = SmsSendRequest.instance(SceneEnum.SCENE77.getSceneId(), mobileNo, null, platformChannel.getRentId());
        PlatFormResponse response = smsSendService.sendSms(request);
        log.info("发送短信返回，{}", response);
        if (response.error()) {
            return Response.error(response.getMessage());
        }
        return Response.success;
    }

    /**
     * 势成云新增预置业务页面
     */
    @PostMapping("/preset")
    public void preset(@RequestAttribute Long userId, @RequestBody @Valid List<PresetPageRequest> request) {
        platformChannelService.addPresetPage(userId, request);
    }

    /**
     * 一级渠道基本信息列表
     */
    @GetMapping("/base")
    public List<ChannelBaseInfoDto> baseInfo() {
        return BaseHelper.r2t(platformChannelService.list(Wrappers.<PlatformChannel>lambdaQuery()
                .isNull(PlatformChannel::getParentId).orderByDesc(PlatformChannel::getCreatedTime)), ChannelBaseInfoDto.class);
    }
}
