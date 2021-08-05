package com.touchealth.platform.processengine.controller.user;


import com.touchealth.platform.processengine.constant.CommonConstant;
import com.touchealth.platform.processengine.constant.UserConstant;
import com.touchealth.platform.processengine.controller.BaseController;
import com.touchealth.platform.processengine.entity.user.PostJob;
import com.touchealth.platform.processengine.entity.user.User;
import com.touchealth.platform.processengine.exception.BusinessException;
import com.touchealth.platform.processengine.pojo.dto.BooleanDto;
import com.touchealth.platform.processengine.pojo.dto.user.PermDetailDto;
import com.touchealth.platform.processengine.pojo.dto.user.PermDto;
import com.touchealth.platform.processengine.pojo.dto.user.PermTemplateDepartmentDto;
import com.touchealth.platform.processengine.pojo.request.user.PermsMenuRequest;
import com.touchealth.platform.processengine.pojo.request.user.PermsStaffRequest;
import com.touchealth.platform.processengine.pojo.request.user.PermsTemplateRequest;
import com.touchealth.platform.processengine.service.user.PermsService;
import com.touchealth.platform.processengine.service.user.PostJobPermsService;
import com.touchealth.platform.processengine.service.user.PostJobService;
import com.touchealth.platform.processengine.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static com.touchealth.platform.processengine.constant.PermsConstant.OP_CHANNEL_ADD_ROOT;

/**
 * <p>
 * 权限资源表 前端控制器
 * </p>
 *
 * @author SunYang
 * @since 2020-12-30
 */
@RestController
@RequestMapping("/perms")
@Slf4j
public class PermsController extends BaseController {

    @Autowired
    private PostJobPermsService postJobPermsService;
    @Autowired
    private PostJobService postJobService;
    @Autowired
    private UserService userService;
    @Autowired
    private PermsService permsService;

    /**
     * 查询权限菜单及其所有功能数据等权限。<br>
     * 当req.channelNo为空时，默认使用当前所在渠道。<br>
     * 当req.staffId为空时，默认使用当前登录用户ID。
     * @param req
     * @param userId
     * @param channelNo
     * @return
     */
    @GetMapping("/all")
    public Map<String, PermDetailDto> all(PermsMenuRequest req,
                                                @RequestAttribute Long userId,
                                                @RequestHeader String channelNo) {
        List<PermDetailDto> allPerms;

        String chooseChannelNo = req.getChannelNo();
        Long staffId = Optional.ofNullable(req.getStaffId()).orElse(userId);
        Long postJobId = req.getPostJobId();

        chooseChannelNo = StringUtils.isEmpty(chooseChannelNo) ? channelNo : chooseChannelNo;

        User currUser = userService.findById(userId);
        if (currUser == null) {
            log.error("PermsController.all user not exits. {}", userId);
            return new HashMap<>();
        }

        // 查询模板权限菜单
        if (postJobId != null) {
            allPerms = permsService.flatListPostJobTempMenu(chooseChannelNo, postJobId);
        }
        // 查询员工权限菜单
        else {
            allPerms = permsService.flatListUserMenu(chooseChannelNo, staffId);
        }

        Map<String, PermDetailDto> ret = new HashMap<>(allPerms.size());

        // 查询菜单下的权限明细
        if (CollectionUtils.isNotEmpty(allPerms)) {
            List<PermDetailDto> childMenuPermList = new ArrayList<>();

            for (PermDetailDto menu : allPerms) {
                childMenuPermList.addAll(menu.getChilds());

                List<PermDetailDto> permList = new ArrayList<>();
                Long menuId = menu.getId();

                String permCodeOp = "OP", permCodeData = "DATA", permCodeField = "FIELD";

                PermDetailDto opPerms = new PermDetailDto(permCodeOp, "功能权限");
                PermDetailDto dataPerms = new PermDetailDto(permCodeData, "数据查看权限");
                PermDetailDto fieldPerms = new PermDetailDto(permCodeField, "字段查看权限");
                getMenuPermDetail(chooseChannelNo, staffId, postJobId, permList, menuId, opPerms, dataPerms, fieldPerms, false, null);

                menu.setPermList(permList);
            }

            ret = allPerms.stream().collect(Collectors.toMap(PermDetailDto::getCode, o -> o));
        }

        return ret;
    }

    /**
     * 查询权限菜单。<br>
     * 当req.channelNo为空时，默认使用当前所在渠道。<br>
     * 当req.staffId为空时，默认使用当前登录用户ID。
     * @param req
     * @param userId
     * @param channelNo
     * @return
     */
    @GetMapping("/menu")
    public List<PermDetailDto> menu(PermsMenuRequest req,
                                    @RequestAttribute Long userId,
                                    @RequestHeader String channelNo) {
        List<PermDetailDto> ret = new ArrayList<>();

        String chooseChannelNo = req.getChannelNo();
        Long staffId = Optional.ofNullable(req.getStaffId()).orElse(userId);

        chooseChannelNo = StringUtils.isEmpty(chooseChannelNo) ? channelNo : chooseChannelNo;

        User currUser = userService.findById(userId);
        if (currUser == null) {
            log.error("PermsController.all user not exits. {}", userId);
            return new ArrayList<>();
        }
        boolean isAdmin = UserConstant.USER_TYPE_PROCESS_ENGINE_ADMIN.equals(currUser.getUserType());

        // 查询员工权限菜单
        if (staffId != null) {
            ret = permsService.listUserMenu(chooseChannelNo, staffId, isAdmin);
        }
        return ret;
    }

    /**
     * 查询权限菜单明细
     * @param menuId
     * @param req
     * @return
     */
    @GetMapping("/{menuId}/detail")
    public List<PermDetailDto> menuDetail(@PathVariable("menuId") Long menuId,
                                    @RequestAttribute Long userId,
                                    PermsMenuRequest req) {
        String permCodeOp = "OP", permCodeData = "DATA", permCodeField = "FIELD";
        List<PermDetailDto> ret = new ArrayList<>();
        PermDetailDto opPerms = new PermDetailDto(permCodeOp, "功能权限");
        PermDetailDto dataPerms = new PermDetailDto(permCodeData, "数据查看权限");
        PermDetailDto fieldPerms = new PermDetailDto(permCodeField, "字段查看权限");

        String chooseChannelNo = req.getChannelNo();
        Long staffId = Optional.ofNullable(req.getStaffId()).orElse(userId);
        Long postJobId = req.getPostJobId();

        User currUser = userService.findById(userId);
        if (currUser == null) {
            log.error("PermsController.all user not exits. {}", userId);
            return new ArrayList<>();
        }
        boolean isAdmin = UserConstant.USER_TYPE_PROCESS_ENGINE_ADMIN.equals(currUser.getUserType());
        boolean isScAdmin = CommonConstant.SC_ADMIN_ID.equals(userId);

        // 查询模板权限明细
        getMenuPermDetail(chooseChannelNo, staffId, postJobId, ret, menuId, opPerms, dataPerms, fieldPerms, isAdmin, isScAdmin);
        return ret;
    }

    /**
     * 查询所有部门岗位权限模板
     * @param search
     * @param chooseChannelNo
     * @param channelNo
     * @return
     */
    @GetMapping("/template/all")
    public List<PermTemplateDepartmentDto> templateAll(String search,
                                                       @RequestParam("channelNo") String chooseChannelNo,
                                                       @RequestHeader String channelNo) {
        channelNo = Optional.ofNullable(chooseChannelNo).orElse(channelNo);
        return postJobPermsService.getDepartmentPermTreeBySearch(channelNo, search);
    }

    /**
     * 添加|修改部门岗位模板权限
     * @param req
     * @return
     */
    @PostMapping("/template/auth")
    public BooleanDto templateAuth(@RequestBody PermsTemplateRequest req,
                                @RequestHeader String channelNo) {
        Long postJobId = req.getPostJobId();
        List<PermDto> permList = req.getPermList();
        List<Long> permIds = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(permList)) {
            permIds = permList.stream().map(PermDto::getId).distinct().collect(Collectors.toList());
        }

        PostJob postJob = postJobService.getById(postJobId);
        Assert.notNull(postJob, "无效的岗位");
        if (!channelNo.equals(postJob.getChannelNo())) {
            throw new BusinessException("该渠道下不能添加权限");
        }

        return new BooleanDto(postJobPermsService.saveOrUpdateByPostJobId(postJob, permIds), "很抱歉，添加岗位模板失败，请稍后重试。");
    }

    /**
     * 添加|修改员工权限
     * @param req
     * @return
     */
    @PostMapping("/staff/auth")
    public BooleanDto staffAuth(@RequestBody PermsStaffRequest req,
                                @RequestAttribute Long userId,
                                @RequestHeader String channelNo) {
        Long staffId = Optional.ofNullable(req.getStaffId()).orElse(userId);
        String chooseChannelNo = Optional.ofNullable(req.getChannelNo()).orElse(channelNo);
        List<Long> postJobIds = req.getPostJobIds();
        List<PermDto> permList = req.getPermList();

        return new BooleanDto(userService.updatePerms(chooseChannelNo, staffId, postJobIds, permList), "很抱歉，添加权限失败，请稍后重试。");
    }

    /**
     * 查询菜单下的权限
     * @param chooseChannelNo
     * @param staffId
     * @param postJobId
     * @param permList
     * @param menuId
     * @param opPerms
     * @param dataPerms
     * @param fieldPerms
     * @param isAdmin
     */
    private void getMenuPermDetail(String chooseChannelNo, Long staffId, Long postJobId, List<PermDetailDto> permList,
                                   Long menuId, PermDetailDto opPerms, PermDetailDto dataPerms, PermDetailDto fieldPerms, boolean isAdmin, Boolean isScAdmin) {
        List<PermDto> permDtos;
        // 查询模板权限明细
        if (postJobId != null) {
            permDtos = permsService.listPostJobPermDetailByMenu(chooseChannelNo, menuId, postJobId);
        }
        // 查询员工权限明细
        else {
            permDtos = permsService.listUserPermDetailByMenu(chooseChannelNo, menuId, staffId);
        }

        // 添加前端展示的权限名
        if (CollectionUtils.isNotEmpty(permDtos)) {
            for (PermDto perm : permDtos) {
                Integer type = perm.getType();

                boolean have;
                if (isScAdmin != null && isScAdmin) {
                    have = true;
                } else {
                    if (OP_CHANNEL_ADD_ROOT.equals(perm.getPermsCode())) {
                        have = perm.getHave();
                    } else {
                        have = isAdmin || perm.getHave();
                    }
                }

                PermDetailDto permDetailDto = new PermDetailDto(perm.getId(), perm.getCode(), perm.getName(), perm.getType(),
                        have, perm.getGroup(), perm.getPermsCode());
                if (UserConstant.PERM_TYPE_OP.equals(type)) {
                    opPerms.getChilds().add(permDetailDto);
                }
                if (UserConstant.PERM_TYPE_DATA.equals(type)) {
                    dataPerms.getChilds().add(permDetailDto);
                }
                if (UserConstant.PERM_TYPE_FIELD.equals(type)) {
                    fieldPerms.getChilds().add(permDetailDto);
                }
                if (UserConstant.PERM_TYPE_MENU.equals(type)) {
                    // pass
                }
                if (UserConstant.PERM_TYPE_API.equals(type)) {
                    // pass
                }
            }
        }

        permList.add(opPerms);
        permList.add(dataPerms);
        permList.add(fieldPerms);
    }

}
