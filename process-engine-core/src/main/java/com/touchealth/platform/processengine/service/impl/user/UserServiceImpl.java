package com.touchealth.platform.processengine.service.impl.user;

import cn.hutool.core.lang.Assert;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.touchealth.api.core.bo.UserRegistrationBo;
import com.touchealth.api.core.service.IdGeneratorService;
import com.touchealth.common.bo.core.UserBo;
import com.touchealth.common.constant.CodePrefixConstant;
import com.touchealth.common.constant.Globaldefine;
import com.touchealth.common.constant.UserRegistrationConstant;
import com.touchealth.common.utils.CodeUtil;
import com.touchealth.common.utils.MD5Util;
import com.touchealth.common.utils.RandomStringUtils;
import com.touchealth.physical.api.request.branch.BranchPowerAddRequest;
import com.touchealth.physical.api.request.branch.UserInfoRequest;
import com.touchealth.physical.api.service.branch.BranchPowerApi;
import com.touchealth.platform.basic.response.PlatFormResponse;
import com.touchealth.platform.message.client.bo.SmsSendBo;
import com.touchealth.platform.message.client.service.SmsSendService;
import com.touchealth.platform.processengine.annotation.TransactionalForException;
import com.touchealth.platform.processengine.constant.GlobalDefine;
import com.touchealth.platform.processengine.constant.PermsConstant;
import com.touchealth.platform.processengine.constant.RedisConstant;
import com.touchealth.platform.processengine.constant.UserConstant;
import com.touchealth.platform.processengine.dao.user.UserDao;
import com.touchealth.platform.processengine.entity.page.PlatformChannel;
import com.touchealth.platform.processengine.entity.user.*;
import com.touchealth.platform.processengine.exception.BusinessException;
import com.touchealth.platform.processengine.handler.UserLoginHistoryHandler;
import com.touchealth.platform.processengine.pojo.bo.user.PostJobUserCountBo;
import com.touchealth.platform.processengine.pojo.bo.user.TokenBo;
import com.touchealth.platform.processengine.pojo.dto.BooleanDto;
import com.touchealth.platform.processengine.pojo.dto.PageData;
import com.touchealth.platform.processengine.pojo.dto.kezhu.BranchPowerDto;
import com.touchealth.platform.processengine.pojo.dto.user.PermDto;
import com.touchealth.platform.processengine.pojo.dto.user.UserDto;
import com.touchealth.platform.processengine.pojo.request.user.LoginRequest;
import com.touchealth.platform.processengine.pojo.request.user.SignUpRequest;
import com.touchealth.platform.processengine.pojo.request.user.UserRequest;
import com.touchealth.platform.processengine.pojo.request.user.UserSearchRequest;
import com.touchealth.platform.processengine.service.impl.BaseServiceImpl;
import com.touchealth.platform.processengine.service.kezhu.PostJobPowerService;
import com.touchealth.platform.processengine.service.page.PlatformChannelService;
import com.touchealth.platform.processengine.service.user.*;
import com.touchealth.platform.processengine.utils.BaseHelper;
import com.touchealth.platform.processengine.utils.CommonUtils;
import com.touchealth.platform.processengine.utils.JsonUtil;
import com.touchealth.platform.processengine.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.touchealth.platform.processengine.constant.CommonConstant.SC_CHANNEL_ID;

@Service("userService")
@Slf4j
public class UserServiceImpl extends BaseServiceImpl<UserDao, User> implements UserService {

    @Resource
    private UserDao userDao;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private PostJobService postJobService;
    @Autowired
    private PermsService permsService;
    @Autowired
    private UserPermsService userPermsService;
    @Resource
    private SmsSendService smsSendService;
    @Resource
    private IdGeneratorService idGeneratorService;
    @Resource
    private com.touchealth.api.core.service.UserService userCoreService;
    @Resource
    private UserLoginHistoryService userLoginHistoryService;
    @Autowired
    private PlatformChannelService platformChannelService;
    @Autowired
    private PostJobPermsService postJobPermsService;

    @Resource
    private BranchPowerApi branchPowerApi;

    @Resource
    private PostJobPowerService postJobPowerService;

    @Override
    public void createUser(User user) {
        if (StringUtils.isBlank(user.getSalt())) {
            user.setSalt(org.apache.commons.lang3.RandomStringUtils.randomAscii(10));
        }
        user.setPassword(DigestUtil.md5Hex(DigestUtil.md5Hex(user.getPassword()) + user.getSalt()));
        user.setCode(CodeUtil.getCode(CodePrefixConstant.CodePrefixName.USER));
        save(user);
    }

    @Override
    public TokenBo login(String email, String inputPassword, Collection<Integer> userTypes) {

        Assert.isTrue(StringUtils.isNotBlank(email) && StringUtils.isNotBlank(inputPassword), "参数不能为空！");

        QueryWrapper<User> query = Wrappers.query();
        query.eq("email", email).in("user_type", userTypes)
                .eq("is_disable", 0);
        User user = this.getOne(query);
        if (null == user) {
            throw new BusinessException("用户不存在或账号被限制");
        }
//        if (CommonConstant.IS_DELETE == Optional.ofNullable(user.getIsDel()).orElse(Long.valueOf(CommonConstant.IS_DELETE)).intValue()) {
//            throw new BusinessException("很抱歉，您的账号已被移除，请联系后台管理员");
//        }
        if (UserConstant.STAFF_STATUS.OUT.getCode().equals(user.getStaffStatus())) {
            throw new BusinessException("很抱歉，该员工已离职");
        }
        if (!validatePassword(user, inputPassword)) {
            throw new BusinessException("账号或密码输入错误，请重试");
        }
        return createTokenResponse(user, CommonUtils.uuid());
    }

    @Override
    public TokenBo signUp(SignUpRequest signUpRequest) {

        // 校验参数
        validateSignUpRequest(signUpRequest);
        String mobile = signUpRequest.getMobile();

        // 校验手机号验证码是否正确
        boolean isValida = validCode(mobile, signUpRequest.getVerificationCode());
        if (!isValida) {
            throw new BusinessException("验证码不正确！");
        }

        // 获取用户
        UserBo userBo = userCoreService.findByMobileAndUserTypeAndIsDel(mobile, UserConstant.USER_TYPE_PROCESS_ENGINE_H5, false);
        if (userBo == null) {
            // 注册
            userBo = registerUser(signUpRequest);
        }

        TokenBo tokenBo = createTokenResponse(BaseHelper.r2t(userBo, User.class), CommonUtils.uuid());

        // 创建用户登录记录
        UserLoginHistory history = UserLoginHistoryHandler.instance(tokenBo, signUpRequest.getReferer(), signUpRequest.getIpAddress(), userBo.getUserType());
        userLoginHistoryService.save(history);
        return tokenBo;
    }

    @Override
    public User findByEmail(String email, int userType) {
        return getOne(Wrappers.<User>query().eq("email", email)
                .eq("user_type", userType)
                .eq("is_disable", 0)
                .eq("is_del", 0));
    }

    @Override
    public User findByEmail(String email, List<Integer> userTypes) {
        return getOne(Wrappers.<User>query().eq("email", email)
                .in("user_type", userTypes)
                .eq("is_disable", 0));
    }

    @Override
    public List<User> listByEmail(List<String> emails, int userType) {
        return list(Wrappers.<User>query().in("email", emails)
                .eq("user_type", userType)
                .eq("is_disable", 0)
                .eq("is_del", 0));
    }

    @Override
    public List<User> listByMobile(List<String> mobileList, List<Integer> userTypes) {
        return list(Wrappers.<User>query().in("mobile_no", mobileList)
                .in("user_type", userTypes)
                .eq("is_disable", 0)
                .eq("is_del", 0));
    }

    @Override
    @Cacheable(value = RedisConstant.REDIS_CACHE_10_MINUTE, key = "\"_\" + #userId")
    public User findById(Long userId) {
        return this.getById(userId);
    }

    @Override
    public UserBo findByMobileAndType(String mobile, int userType) {
        return userCoreService.findByMobileAndUserTypeAndIsDel(mobile,userType , false);
    }

    @Override
    public List<User> findByMobileAndType(String mobile, List<Integer> userTypes) {
        return list(Wrappers.<User>query().in("mobile_no", mobile)
                .in("user_type", userTypes)
                .eq("is_disable", 0)
                .eq("is_del", 0));
    }

    @Override
    public void getVerificationCode(String mobile, Boolean bgUser) {

        // 数据校验
        validateParam(mobile);

        Long msgTemplateId;
        if (bgUser) {
            // 数据引擎管理后台用户
            List<User> userList = findUserByMobileAndUserTypes(mobile, Arrays.asList(UserConstant.USER_TYPE_PROCESS_ENGINE,UserConstant.USER_TYPE_PROCESS_ENGINE_ADMIN));
            if (CollectionUtils.isEmpty(userList)) {
                throw new BusinessException("用户不存在或账号被限制！");
            }
            User user = userList.get(0);
            if (user.getIsDisable() != null && 0 != user.getIsDisable()) {
                throw new BusinessException("当前账号已被禁用，请联系管理员！");
            }
            msgTemplateId = 174L;
        } else {
            msgTemplateId = 173L;
        }
        SmsSendBo smsSendBo = SmsSendBo.buildCode(msgTemplateId, mobile, null);
        smsSendBo.setPlatformName("process-engine");
        PlatFormResponse platFormResponse = smsSendService.send(smsSendBo);
        if (platFormResponse == null || !"00000".equals(platFormResponse.getCode())) {
            throw new BusinessException("短信验证码发送失败,请稍后再试！");
        }
    }

    @Override
    public List<User> findUserByMobileAndUserTypes(String mobile, List<Integer> userTypes) {
        Assert.notBlank(mobile, "参数不能为空！");
        return userDao.selectList(Wrappers.lambdaQuery(User.class)
                .eq(User::getMobileNo, mobile)
                .in(User::getUserType, userTypes));
    }

    @Override
    public TokenBo login(LoginRequest request) {
        if (request == null || request.getLoginType() == null) {
            throw new BusinessException("参数不能为空！");
        }

        // 账号密码登录
        TokenBo tokenBo;
        if (UserConstant.USER_SIGN_TYPE_4_ACCESS == request.getLoginType()) {
            tokenBo = login(request.getEmail(), request.getPassword(),
                    Arrays.asList(UserConstant.USER_TYPE_PROCESS_ENGINE, UserConstant.USER_TYPE_PROCESS_ENGINE_SUPER));
        } else {

            // 手机号验证码登录
            tokenBo = platformUserLoginForCode(request);
        }

        // 创建用户登录记录
        UserLoginHistory history = UserLoginHistoryHandler.instance(tokenBo, request.getReferer(), request.getIpAddress(), tokenBo.getUser().getUserType());
        userLoginHistoryService.save(history);
        return tokenBo;
    }


    @Override
    public String getUserTicket(Long userId) {
        return userCoreService.getUserTicket(userId, UserConstant.USER_TYPE_PROCESS_ENGINE_H5);
    }

    @Override
    public PageData<UserDto> pageList(UserSearchRequest req) {
        String channelNo = req.getChannelNo();
        String search = req.getSearch();
        Long deptId = req.getDeptId();
        Long postJobId = req.getPostJobId();
        Long pageNo = Optional.ofNullable(req.getPageNo()).orElse(1L);
        Long pageSize = Optional.ofNullable(req.getPageSize()).orElse(10L);

        LambdaQueryWrapper<User> qw = Wrappers.<User>lambdaQuery()
                .eq(User::getChannelNo, channelNo);
        if(req.getType() != null && req.getType() == 0){
            //sass员工列表
            qw.in(User::getUserType,Arrays.asList(UserConstant.USER_TYPE_PROCESS_ENGINE,UserConstant.USER_TYPE_PROCESS_ENGINE_ADMIN));
        }
        if(req.getType() != null && req.getType() == 1){
            //科助中心员工
            qw.eq(User::getUserType,UserConstant.USER_TYPE_KE_ZHU);
        }
        if (deptId != null) {
            qw.eq(User::getDeptId, deptId);
        }
        if (postJobId != null) {
            qw.eq(User::getPostJobId, postJobId);
        }
        if (!StringUtils.isEmpty(search)) {
            qw.and(i -> i.like(User::getRealName, search)
                    .or().like(User::getCode, search)
                    .or().like(User::getMobileNo, search)
                    .or().like(User::getEmail, search));
        }

        qw.orderByDesc(User::getCreatedAt);

        Page<User> page = page(new Page<>(pageNo, pageSize), qw);

        List<User> records = page.getRecords();
        if (!CollectionUtils.isEmpty(records)) {
            List<UserDto> userDtos = records.stream()
                    .map(o -> getUserDto(o))
                    .collect(Collectors.toList());
            return new PageData<>(pageNo.intValue(), pageSize.intValue(), (int)page.getTotal(), (int)page.getPages(), userDtos);
        }
        return new PageData<>(pageNo.intValue(), pageSize.intValue(), 0, new ArrayList<>());
    }

    @Override
    public List<PostJobUserCountBo> countByPostJobIds(List<Long> postJobIdList) {
        return userDao.countByPostJobIds(postJobIdList);
    }

    @TransactionalForException
    @Override
    @CacheEvict(value = RedisConstant.REDIS_CACHE_USER_PERMS, key = "\"_\" + #chooseChannelNo + \"_\" + #userId")
    public Boolean updatePerms(String chooseChannelNo, Long userId, List<Long> postJobIds, List<PermDto> permList) {
        // 查询用户
        User user = findById(userId);
        Assert.notNull(user, "用户不存在");

        PlatformChannel mainPlatform = platformChannelService.getById(SC_CHANNEL_ID);
        String mainChannelNo = mainPlatform == null ? "" : mainPlatform.getChannelNo();
        boolean isAdmin = false, isMainChannel = false;
        if (mainChannelNo.equals(user.getChannelNo())) {
            isMainChannel = true;
        }
        if (UserConstant.USER_TYPE_PROCESS_ENGINE_ADMIN.equals(user.getUserType())) {
            isAdmin = true;
        }

        // 删除用户权限
        userPermsService.remove(Wrappers.<UserPerms>lambdaQuery()
                .eq(UserPerms::getChannelNo, chooseChannelNo)
                .eq(UserPerms::getUserId, userId));

        if (!CollectionUtils.isEmpty(permList)) {
            List<Long> permIds = permList.stream().map(PermDto::getId).collect(Collectors.toList());
            // 查询权限
            List<Perms> perms = permsService.listByIds(permIds);
            if (!CollectionUtils.isEmpty(perms)) {
                boolean finalIsMainChannel = isMainChannel, finalIsAdmin = isAdmin;
                List<UserPerms> userPerms = perms.stream()
                        .filter(o -> {
                            // 20200122 需求，仅超级管理员才有添加子渠道权限，仅势成超级管理员才能添加一级渠道
                            if (!finalIsMainChannel && !finalIsAdmin && PermsConstant.OP_CHANNEL_ADD_ROOT.equals(o.getPermsCode())) {
                                return false;
                            }
                            if (!finalIsAdmin && PermsConstant.OP_CHANNEL_ADD.equals(o.getPermsCode())) {
                                return false;
                            }
                            return true;
                        })
                        .map(o -> new UserPerms(chooseChannelNo, userId, o.getId(), o.getType()))
                        .collect(Collectors.toList());
                // 添加用户权限
                userPermsService.saveBatch(userPerms);
            }
        }
        return true;
    }

    @Override
    public UserDto getUserDto(User user) {
        UserDto userDto = BaseHelper.r2t(user, UserDto.class);
        Long userDeptId = userDto.getDeptId();
        if (userDeptId != null) {
            Department dept = departmentService.getCacheById(userDeptId);
            userDto.setDeptName(dept == null ? "" : dept.getName());
        }
        Long userPostId = userDto.getPostJobId();
        if (userPostId != null) {
            PostJob postJob = postJobService.getCacheById(userPostId);
            userDto.setPostJobName(postJob == null ? "" : postJob.getName());
        }
        return userDto;
    }

    @Override
    public UserBo updateUser(UserBo userBo) {
        return userCoreService.updateUser(userBo);
    }

    @Override
    public Map<String, String> validToken(String token) {
        try {
            String userInfo = JwtUtil.parseJWT(token).getSubject();
            /** userInfo包含的信息：{@link UserServiceImpl#createTokenResponse(User, String)} */
            return JsonUtil.getObjectFromJson(userInfo, Map.class);
        } catch (Exception e) {
            log.error("token解析失败", e);
            return null;
        }
    }

    @TransactionalForException
    @Override
    public boolean saveAndAuth(User user) {
        boolean saveFlag = save(user);
        if (saveFlag && user.getPostJobId() != null) {
            List<PostJobPerms> postJobPerms = postJobPermsService.list(Wrappers.<PostJobPerms>lambdaQuery().eq(PostJobPerms::getPostJobId, user.getPostJobId()));
            if (CollectionUtils.isNotEmpty(postJobPerms)) {
                List<UserPerms> userPerms = postJobPerms.stream()
                        .map(o -> new UserPerms(user.getChannelNo(), user.getId(), o.getPermsId(), o.getPermsType()))
                        .collect(Collectors.toList());
                boolean userPermsSaveFlag = userPermsService.saveBatch(userPerms);
                if (!userPermsSaveFlag) {
                    log.warn("UserService.saveAndAuth save user perms fail, 问题不大，可以手动授权 {}", user.getId());
                }
            }
        }
        return saveFlag;
    }

    @TransactionalForException
    @Override
    public boolean saveOrUpdateBatchAndAuth(List<User> saveOrUpdUserList) {
        boolean saveFlag = saveOrUpdateBatch(saveOrUpdUserList);
        if (saveFlag) {
            List<Long> postJobIds = saveOrUpdUserList.stream().map(User::getPostJobId).collect(Collectors.toList());
            List<PostJobPerms> postJobPerms = postJobPermsService.list(Wrappers.<PostJobPerms>lambdaQuery().in(PostJobPerms::getPostJobId, postJobIds));
            if (CollectionUtils.isNotEmpty(postJobPerms)) {
                Map<Long, List<PostJobPerms>> postJobPermsMap = postJobPerms.stream().collect(Collectors.toMap(
                        PostJobPerms::getPostJobId,
                        o -> {
                            List<PostJobPerms> arr = new ArrayList<>();
                            arr.add(o);
                            return arr;
                        },
                        (ov, nv) -> {
                            ov.addAll(nv);
                            return ov;
                        }));

                List<UserPerms> userPermsAll = saveOrUpdUserList.stream()
                        .filter(user -> user.getPostJobId() != null && CollectionUtils.isNotEmpty(postJobPermsMap.get(user.getPostJobId())))
                        .flatMap(user -> {
                            Long postJobId = user.getPostJobId();
                            List<PostJobPerms> postJobPermsList = postJobPermsMap.get(postJobId);
                            return postJobPermsList.stream()
                                    .map(o -> new UserPerms(user.getChannelNo(), user.getId(), o.getPermsId(), o.getPermsType()));
                        })
                        .collect(Collectors.toList());
                boolean userPermsSaveFlag = userPermsService.saveBatch(userPermsAll);
                if (!userPermsSaveFlag) {
                    log.warn("UserService.saveAndAuth save user perms fail, 问题不太大，可以手动授权");
                }
            }
        }
        return saveFlag;
    }

    @CacheEvict(value = RedisConstant.REDIS_CACHE_10_MINUTE, key = "\"_\" + #req.id")
    @TransactionalForException
    @Override
    public BooleanDto updateStaffAndAuth(UserRequest req, Long userId) {
        boolean changePostJob = false;
        Long id = req.getId();
        String realName = req.getRealName();
        String code = req.getCode();
        String mobileNo = req.getMobileNo();
        String email = req.getEmail();
        String password = req.getPassword();
        String password1 = req.getPassword1();
        Long deptId = req.getDeptId();
        Long postJobId = req.getPostJobId();
        Integer staffStatus = req.getStaffStatus();

        if (StringUtils.isNotEmpty(password) && !password.equals(password1)) {
            log.error("StaffController add. inconsistent passwords");
            return new BooleanDto(false, "请校验两次输入的密码是否相同。");
        }

        User user = getById(id);
        if (user == null) {
            log.error("StaffController edit. user not exits");
            return new BooleanDto(false, "很抱歉，未找到该用户。");
        }

        LambdaUpdateWrapper<User> uw = Wrappers.<User>lambdaUpdate()
                .eq(User::getId, id)
                .set(User::getUpdatedBy, userId);

        if (StringUtils.isNotEmpty(realName)) {
            uw.set(User::getRealName, realName);
        }
        if (StringUtils.isNotEmpty(code)) {
            uw.set(User::getCode, code);
        }
        if (StringUtils.isNotEmpty(mobileNo)) {
            uw.set(User::getMobileNo, mobileNo);
        }
        if (StringUtils.isNotEmpty(email)) {
            uw.set(User::getEmail, email);
        }
        if (StringUtils.isNotEmpty(password)) {
            String securityPwd = DigestUtil.md5Hex(DigestUtil.md5Hex(password) + user.getSalt());
            uw.set(User::getPassword, securityPwd);
        }
        if (deptId != null) {
            uw.set(User::getDeptId, deptId);
        }
        if (postJobId != null) {
            uw.set(User::getPostJobId, postJobId);
            changePostJob = !postJobId.equals(user.getPostJobId());
        }
        if (staffStatus != null) {
            uw.set(User::getStaffStatus, staffStatus);
        }

        boolean updateFlag = update(new User(), uw);

        if (updateFlag && changePostJob) {
            if (null != user.getPlatformUserId()){
                PostJob postJob = postJobService.getCacheById(req.getPostJobId());
                if (null == postJob){
                    return new BooleanDto(false, "很抱歉，岗位信息有误!");
                }
                Department department = departmentService.getCacheById(postJob.getDeptId());
                // 删除旧的用户权限关系,添加新的用户权限关系
                List<BranchPowerDto> list = postJobPowerService.findAllPowerByPostJobId(req.getPostJobId());
                if (org.apache.commons.collections.CollectionUtils.isNotEmpty(list)){
                    List<BranchPowerAddRequest> addList = com.touchealth.platform.basic.util.BaseHelper.resourceList2target(list, BranchPowerAddRequest.class);
                    UserInfoRequest userInfo = new UserInfoRequest();
                    BeanUtils.copyProperties(user,userInfo);
                    userInfo.setUserId(user.getPlatformUserId());
                    userInfo.setDeptId(Optional.ofNullable(department).map(e->e.getId()).orElse(null));
                    userInfo.setDeptName(Optional.ofNullable(department).map(e->e.getName()).orElse(null));
                    branchPowerApi.deleteAndSaveUserPower(userInfo,addList);
                }
            }
            // 删除用户当前所有旧岗位权限
            List<PostJobPerms> oldPostJobPerms = postJobPermsService.list(Wrappers.<PostJobPerms>lambdaQuery().eq(PostJobPerms::getPostJobId, user.getPostJobId()));
            if (CollectionUtils.isNotEmpty(oldPostJobPerms)) {
                List<Long> permsIds = oldPostJobPerms.stream().map(PostJobPerms::getPermsId).collect(Collectors.toList());
                userPermsService.remove(Wrappers.<UserPerms>lambdaQuery().eq(UserPerms::getUserId, id).in(UserPerms::getPermsId, permsIds));
            }
            // 重置当前用户新的岗位权限
            List<PostJobPerms> postJobPerms = postJobPermsService.list(Wrappers.<PostJobPerms>lambdaQuery().eq(PostJobPerms::getPostJobId, postJobId));
            if (CollectionUtils.isNotEmpty(postJobPerms)) {
                List<UserPerms> userPerms = postJobPerms.stream()
                        .map(o -> new UserPerms(user.getChannelNo(), user.getId(), o.getPermsId(), o.getPermsType()))
                        .collect(Collectors.toList());
                boolean userPermsSaveFlag = userPermsService.saveBatch(userPerms);
                if (!userPermsSaveFlag) {
                    log.warn("UserService.updateAndAuth update user perms fail, 问题不大，可以手动授权 {}", user.getId());
                }
            }
        }

        return new BooleanDto(updateFlag, "很抱歉，更新员工信息失败，请稍后重试。");
    }

    @CacheEvict(value = RedisConstant.REDIS_CACHE_10_MINUTE, key = "\"_\" + #id")
    @Override
    public boolean deleteAndFlushCache(Long id) {
        return removeById(id);
    }

    private Boolean validatePassword(User user, String inputPassword) {
        String inputPasswordMd5 = DigestUtil.md5Hex(DigestUtil.md5Hex(inputPassword) + user.getSalt());
        return inputPasswordMd5.equals(user.getPassword());
    }

    private TokenBo createTokenResponse(User user, String signCode) {
        TokenBo tokenBo = new TokenBo();
        Map<String, String> map = new HashMap<>(6);
        map.put("userId", String.valueOf(user.getId()));
        map.put("realName", user.getRealName());
        map.put("userType", String.valueOf(user.getUserType()));
        map.put("currentUserUniqueMark", signCode);
        String subject = JsonUtil.getJsonFromObject(map);
        String token = JwtUtil.createJWT(signCode, subject, GlobalDefine.Jwt.JWT_TTL);
        tokenBo.setUser(user);
        tokenBo.setToken(token);
        tokenBo.setCurrentUserUniqueMark(signCode);
        return tokenBo;
    }

    private void validateParam(String mobile) {
        Assert.notBlank(mobile, "手机号不能为空！");
        Assert.isTrue(11 == mobile.length(), "请输入正确的手机号！");

    }

    private void validateSignUpRequest(SignUpRequest signUpRequest) {
        if (null == signUpRequest || StringUtils.isBlank(signUpRequest.getMobile())
                || StringUtils.isBlank(signUpRequest.getVerificationCode())) {
            throw new BusinessException("参数不能为空！");
        }
        Assert.isTrue(11 == signUpRequest.getMobile().length(), "请输入正确的手机号！");

    }

    /**
     * 校验短信验证码是否正确
     *
     * @param mobile           手机号
     * @param verificationCode 验证码
     * @return 正确返回true 错误返回false
     */
    private boolean validCode(String mobile, String verificationCode) {
        Boolean isValida = smsSendService.validCode(mobile, verificationCode).get();
        return isValida != null && isValida;
    }

    private UserBo registerUser(SignUpRequest signUpRequest) {

        UserBo userBo = new UserBo();
        userBo.setId(idGeneratorService.generateIdByTables(Globaldefine.tableIdGenerator.SYS_USER));
        userBo.setActivatedAt(new Date());
        userBo.setIsActivating(true);
        userBo.setMobileNo(signUpRequest.getMobile());
        userBo.setUserType(UserConstant.USER_TYPE_PROCESS_ENGINE_H5);
        userBo.setIsDel(false);
        userBo.setIsDisable(false);
        userBo.setSalt(MD5Util.getSaltString());
        String password = MD5Util.getMD5Format(
                MD5Util.getMD5Format(RandomStringUtils.getRandomString(8)) + userBo.getSalt());
        userBo.setPassword(password);
        userCoreService.createUser(userBo);

        UserRegistrationBo registrationBo = initUserRegistrationBo(signUpRequest.getClientType());
        registrationBo.setUserId(userBo.getId());
        registrationBo.setId(idGeneratorService.generateIdByTables(Globaldefine.tableIdGenerator.SYS_USER_REGISTRATION));
        userCoreService.saveUserRegistration(registrationBo);

        return userBo;
    }

    private UserRegistrationBo initUserRegistrationBo(Integer clientType) {
        UserRegistrationBo bo = new UserRegistrationBo();
        bo.setClientType(clientType == null ? null : clientType + "");
        bo.setType(UserRegistrationConstant.getType(4));
        bo.setCreatedAt(new Date());
        bo.setCode("-");
        return bo;
    }

    private TokenBo platformUserLoginForCode(LoginRequest request) {

        // 验证码登录
        Assert.isTrue(UserConstant.USER_SIGN_TYPE_4_VERIFICATION_CODE == request.getLoginType(), "非法请求！");
        String mobile = request.getMobile();
        Assert.isTrue(StringUtils.isNotBlank(mobile) && StringUtils.isNotBlank(request.getVerificationCode()), "参数不能为空！");
        // 获取用户信息
        List<User> userList = findUserByMobileAndUserTypes(mobile, Arrays.asList(UserConstant.USER_TYPE_PROCESS_ENGINE,UserConstant.USER_TYPE_PROCESS_ENGINE_ADMIN));
        if (CollectionUtils.isEmpty(userList) || 0L != userList.get(0).getIsDisable() ||
                UserConstant.STAFF_STATUS.OUT.getCode().equals(userList.get(0).getStaffStatus())) {
            throw new BusinessException("用户不存在或账号被限制！");
        }

        // 验证短信验证码是否正确
        boolean isValida = validCode(mobile, request.getVerificationCode());
        if (!isValida) {
            throw new BusinessException("验证码不正确！");
        }

        return createTokenResponse(userList.get(0), CommonUtils.uuid());
    }


}
