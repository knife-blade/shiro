package com.touchealth.platform.processengine.controller.user;


import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.touchealth.physical.api.dto.branch.ApiBranchDto;
import com.touchealth.physical.api.dto.branch.ApiBranchPowerDto;
import com.touchealth.physical.api.request.branch.BranchPowerAddRequest;
import com.touchealth.physical.api.request.branch.BranchPowerUpdateRequest;
import com.touchealth.physical.api.request.branch.UserInfoRequest;
import com.touchealth.physical.api.service.branch.BranchApi;
import com.touchealth.physical.api.service.branch.BranchPowerApi;
import com.touchealth.platform.basic.response.PlatFormResponse;
import com.touchealth.platform.basic.response.ResponseCode;
import com.touchealth.platform.basic.util.BaseHelper;
import com.touchealth.platform.basic.util.ValidateUtil;
import com.touchealth.platform.message.client.bo.SmsSendBo;
import com.touchealth.platform.message.client.service.SmsSendService;
import com.touchealth.platform.processengine.constant.CommonConstant;
import com.touchealth.platform.processengine.constant.PermsConstant;
import com.touchealth.platform.processengine.constant.UserConstant;
import com.touchealth.platform.processengine.constant.ValidGroup;
import com.touchealth.platform.processengine.controller.BaseController;
import com.touchealth.platform.processengine.entity.page.PlatformChannel;
import com.touchealth.platform.processengine.entity.user.*;
import com.touchealth.platform.processengine.pojo.bo.user.UserImportBo;
import com.touchealth.platform.processengine.pojo.dto.BooleanDto;
import com.touchealth.platform.processengine.pojo.dto.PageData;
import com.touchealth.platform.processengine.pojo.dto.Response;
import com.touchealth.platform.processengine.pojo.dto.kezhu.BranchPowerDto;
import com.touchealth.platform.processengine.pojo.dto.kezhu.KeZhuBranchDto;
import com.touchealth.platform.processengine.pojo.dto.user.UserDto;
import com.touchealth.platform.processengine.pojo.request.kezhu.KeZhuPowerRequest;
import com.touchealth.platform.processengine.pojo.request.user.ChannelBindRequest;
import com.touchealth.platform.processengine.pojo.request.user.UserRequest;
import com.touchealth.platform.processengine.pojo.request.user.UserSearchRequest;
import com.touchealth.platform.processengine.service.kezhu.PostJobPowerService;
import com.touchealth.platform.processengine.service.page.PlatformChannelService;
import com.touchealth.platform.processengine.service.user.*;
import com.touchealth.platform.user.client.api.UserApi;
import com.touchealth.platform.user.client.dto.request.UserReq;
import com.touchealth.platform.user.client.dto.response.UserRes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.touchealth.platform.processengine.constant.CommonConstant.KEY_SPLIT;
import static com.touchealth.platform.processengine.constant.CommonConstant.SC_CHANNEL_ID;

/**
 * <p>
 * 员工管理 前端控制器
 * </p>
 *
 * @author SunYang
 * @since 2020-12-30
 */
@RestController
@RequestMapping("/staff")
@Slf4j
public class StaffController extends BaseController {

    @Autowired
    private UserService userService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private PostJobService postJobService;
    @Autowired
    private UserChannelService userChannelService;
    @Autowired
    private PlatformChannelService platformChannelService;
    @Autowired
    private SmsSendService smsSendService;

    @Resource
    private UserApi userApi;

    @Resource
    private BranchApi branchApi;

    @Resource
    private BranchPowerApi branchPowerApi;

    @Resource
    private PostJobPowerService postJobPowerService;

    @Resource
    private PermsService permsService;

  /**
     * 科助租户ID
     */
    @Value("${kezhu.rent}")
    private String rentId;

    /**
     * 分页查询员工列表
     * @param req
     * @param channelNo
     * @return
     */
    @GetMapping("/page")
    public PageData<UserDto> page(UserSearchRequest req,
                                  @RequestHeader String channelNo) {
        req.setChannelNo(channelNo);
        return userService.pageList(req);
    }

    /**
     * 查询员工详情
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public UserDto getStaff(@PathVariable("id") Long id) {
        User user = userService.findById(id);
        Assert.notNull(user, "用户不存在");
        UserDto userDto = userService.getUserDto(user);
        return userDto;
    }

    /**
     * 导入员工。<br>
     * 员工邮箱唯一，若邮箱已存在，这么会更新。<br>
     * <font color=red>该方法不支持大量数据的导入。</font>
     * @param file
     * @param userId
     * @param channelNo
     * @return
     */
    @RequestMapping("/import")
    public BooleanDto importStaff(@RequestParam("file") MultipartFile file,
                                  @RequestAttribute Long userId,
                                  @RequestHeader String channelNo,
                                  @RequestParam String verifyCode) {
        try {
            BooleanDto x = verifySmsCode(userId, verifyCode);
            if (x != null) {
                return x;
            }

            ExcelReader reader = ExcelUtil.getReader(file.getInputStream());
            reader.setHeaderAlias(new HashMap<String, String>(8) {{
                put("员工姓名", "realName");
                put("员工工号", "code");
                put("手机号", "mobileNo");
                put("企业邮箱", "email");
                put("密码", "password");
                put("部门", "deptName");
                put("岗位", "postJobName");
                put("员工状态", "staffStatusStr");
            }});
            List<UserImportBo> userImportBos = reader.readAll(UserImportBo.class);
            if (CollectionUtils.isEmpty(userImportBos)) {
                log.warn("StaffController.importStaff data is empty");
                return new BooleanDto(false, "没有检测到数据。");
            }

            List<String> emailList = userImportBos.stream().map(UserImportBo::getEmail).distinct().collect(Collectors.toList());
            List<String> mobileList = userImportBos.stream().map(UserImportBo::getMobileNo).distinct().collect(Collectors.toList());
            List<String> deptNameList = userImportBos.stream().map(UserImportBo::getDeptName).distinct().collect(Collectors.toList());
            List<String> postJobNameList = userImportBos.stream().map(UserImportBo::getPostJobName).distinct().collect(Collectors.toList());
            List<String> deptPJComboNameList = userImportBos.stream().map(o -> o.getDeptName() + " , " + o.getPostJobName()).distinct().collect(Collectors.toList());
            Map<String, User> emailUserMap = new HashMap<>(), mobileUserMap = new HashMap<>();
            Map<String, Long> deptIdMap = new HashMap<>(), postJobIdMap = new HashMap<>();
            List<User> saveOrUpdUserList = new ArrayList<>();

            // 查询已存在的邮箱用户
            List<User> emailUsers = userService.listByEmail(emailList, UserConstant.USER_TYPE_PROCESS_ENGINE);
            if (CollectionUtils.isNotEmpty(emailUsers)) {
                emailUserMap = emailUsers.stream().collect(Collectors.toMap(User::getEmail, o -> o));
            }
            // 查询已存在的手机号用户
            List<User> mobileUsers = userService.listByMobile(mobileList, Arrays.asList(UserConstant.USER_TYPE_PROCESS_ENGINE));
            if (CollectionUtils.isNotEmpty(mobileUsers)) {
                mobileUserMap = mobileUsers.stream().collect(Collectors.toMap(User::getEmail, o -> o));
            }

            // 查询部门
            List<Department> deptList = departmentService.list(Wrappers.<Department>lambdaQuery().in(Department::getName, deptNameList));
            if (CollectionUtils.isNotEmpty(deptList)) {
                deptIdMap = deptList.stream().collect(Collectors.toMap(Department::getName, Department::getId, (ov, nv) -> nv));
            }

            List<String> lineNum = new ArrayList<>();

            for (int i = 0; i < userImportBos.size(); i++) {
                UserImportBo userImportBo = userImportBos.get(i);
                String realName = userImportBo.getRealName();
                String code = userImportBo.getCode();
                String mobileNo = userImportBo.getMobileNo();
                String email = userImportBo.getEmail();
                String password = userImportBo.getPassword();
                String deptName = userImportBo.getDeptName();
                String postJobName = userImportBo.getPostJobName();
                Integer staffStatus = UserConstant.STAFF_STATUS.NAME_MAP.getOrDefault(userImportBo.getStaffStatusStr(), UserConstant.STAFF_STATUS.IN).getCode();

                Long deptId = deptIdMap.get(deptName);
                Long postJobId = postJobIdMap.get(deptName + KEY_SPLIT + postJobName);

                if (postJobId == null) {
                    // 查询岗位
                    List<PostJob> postJobList = postJobService.list(Wrappers.<PostJob>lambdaQuery()
                            .eq(PostJob::getDeptId, deptId).in(PostJob::getName, postJobNameList));
                    if (CollectionUtils.isNotEmpty(postJobList)) {
                        for (PostJob postJob : postJobList) {
                            postJobIdMap.putIfAbsent(deptName + KEY_SPLIT + postJob.getName(), postJob.getId());
                        }
                    }
                    postJobId = postJobIdMap.get(deptName + KEY_SPLIT + postJobName);
                }

                log.info("realName={}, code={}, email={}, password={}, deptId={}, postJobId={}, staffStatus={}",
                        realName, code, email, password, deptId, postJobId, staffStatus);
                boolean verify = StringUtils.isBlank(realName) || realName.length() > 10
                        || (code != null && code.length() > 10)
                        || StringUtils.isBlank(mobileNo) || mobileNo.length() != 11
                        || StringUtils.isBlank(email) || email.length() > 50
                        || StringUtils.isBlank(password) || password.length() < 6 || password.length() > 16
                        || deptId == null || postJobId == null
                        || (!"在职".equals(userImportBo.getStaffStatusStr()) && !"离职".equals(userImportBo.getStaffStatusStr()));
                if (verify) {
                    lineNum.add(String.valueOf(i + 2));
                    continue;
                }

                // 创建用户
                User user = buildUser(Optional.ofNullable(emailUserMap.get(email)).orElse(mobileUserMap.get(mobileNo)),
                        userId, channelNo, realName, code, mobileNo, email, password, staffStatus, deptId, postJobId,
                        UserConstant.USER_TYPE_PROCESS_ENGINE);

                saveOrUpdUserList.add(user);
            }

            log.info("员工导入数据，{}", saveOrUpdUserList);
            if (CollectionUtils.isNotEmpty(saveOrUpdUserList)) {
                return new BooleanDto(userService.saveOrUpdateBatchAndAuth(saveOrUpdUserList), "很抱歉，批量导入员工失败，请稍后重试。",
                        CollectionUtils.isNotEmpty(lineNum) ? "您的Excel中，第" + String.join("、", lineNum)
                                + "行存在格式或字段错误，请检查后重新上传，其他无问题数据已导入成功" : "所有员工数据已导入成功");
            } else {
                return new BooleanDto(true, "", "您的Excel中，第" + String.join("、", lineNum) + "行存在格式或字段错误，请检查后重新上传，其他无问题数据已导入成功");
            }
        } catch (IOException e) {
            log.error("StaffController.importStaff has error", e);
            e.printStackTrace();
        }
        return new BooleanDto(false, "很抱歉，导入员工失败。");
    }

    private User buildUser(User user, Long createUserId, String channelNo, String realName, String code, String mobileNo, String email,
                           String password, Integer staffStatus, Long deptId, Long postJobId,Integer userType) {
        String salt = RandomStringUtils.randomAscii(10);
        String securityPwd = DigestUtil.md5Hex(DigestUtil.md5Hex(password) + salt);

        if (user == null) {
            user = new User(code, realName, email, mobileNo, securityPwd, salt, deptId, postJobId, staffStatus, channelNo,
                    userType, UserConstant.USE_ENABLE, Long.valueOf(CommonConstant.IS_NOT_DELETE), 0);
        } else {
            user.setChannelNo(channelNo);
            user.setRealName(realName);
            user.setCode(code);
            user.setMobileNo(mobileNo);
            user.setEmail(email);
            user.setSalt(salt);
            user.setPassword(securityPwd);
            user.setStaffStatus(staffStatus);
            user.setDeptId(deptId);
            user.setPostJobId(postJobId);
        }
        user.setCreatedBy(createUserId);
        return user;
    }

    /**
     * 添加员工
     * @param req
     * @param channelNo
     * @return
     */
    @PostMapping("/add")
    public BooleanDto add(@RequestBody @Valid UserRequest req,
                          @RequestAttribute Long userId,
                          @RequestHeader String channelNo) {
        Long platformUserId = null;
        Integer userType = UserConstant.USER_TYPE_PROCESS_ENGINE;
        try{
            if (null != req.getIsKeZhu() && req.getIsKeZhu()){
                userType = UserConstant.USER_TYPE_KE_ZHU;
                PlatFormResponse<UserRes> response = userApi.save(this.toHandleSaveReq(req));
                if(!ResponseCode.Success.getCode().equals(response.getCode())){
                    return new BooleanDto(false, "很抱歉，用户中台添加员工失败!");
                }
                UserRes userRes = response.getDataOrThrowError();
                platformUserId = userRes.getId();
                if (null != req.getPostJobId()){
                    PostJob postJob = postJobService.getCacheById(req.getPostJobId());
                    if (null == postJob){
                        userApi.remove(platformUserId);
                        return new BooleanDto(false, "很抱歉，岗位信息有误!");
                    }
                    // 关联岗位权限到用户
                    List<BranchPowerDto> list = postJobPowerService.findAllPowerByPostJobId(req.getPostJobId());
                    Department department = departmentService.getCacheById(postJob.getDeptId());
                    if (CollectionUtils.isNotEmpty(list)){
                        List<BranchPowerAddRequest> addList = BaseHelper.resourceList2target(list, BranchPowerAddRequest.class);
                        UserInfoRequest userInfo = new UserInfoRequest();
                        BeanUtils.copyProperties(userRes,userInfo);
                        userInfo.setUserId(platformUserId);
                        userInfo.setDeptId(Optional.ofNullable(department).map(e->e.getId()).orElse(null));
                        userInfo.setDeptName(Optional.ofNullable(department).map(e->e.getName()).orElse(null));
                        branchPowerApi.saveUserPower(userInfo,addList);
                    }
                }
            }
            String realName = req.getRealName();
            String code = req.getCode();
            String mobileNo = req.getMobileNo();
            String email = req.getEmail();
            String password = req.getPassword();
            String password1 = req.getPassword1();
            Long deptId = req.getDeptId();
            Long postJobId = req.getPostJobId();
            Integer staffStatus = req.getStaffStatus();
            String verifyCode = req.getVerifyCode();
            if (StringUtils.isEmpty(verifyCode)) {
                if(platformUserId != null){
                    userApi.remove(platformUserId);
                }
                return new BooleanDto(false, "很抱歉，请输入验证码");
            }

            BooleanDto x = verifySmsCode(userId, verifyCode);
            if (x != null) {
                if(platformUserId != null){
                    userApi.remove(platformUserId);
                }
                return x;
            }

            if (!password.equals(password1)) {
                log.error("StaffController add. inconsistent passwords");
                if(platformUserId != null){
                    userApi.remove(platformUserId);
                }
                return new BooleanDto(false, "请校验两次输入的密码是否相同。");
            }

        x = verifyUserExits(mobileNo, email,req.getIsKeZhu() == null?false:req.getIsKeZhu());
        if (x != null) {
            if(platformUserId != null){
                userApi.remove(platformUserId);
            }
            return x;
        }
        User user = buildUser(null, userId, channelNo, realName, code, mobileNo, email, password, staffStatus, deptId, postJobId,userType);
            user.setPlatformUserId(platformUserId);
            boolean successFlag = userService.saveAndAuth(user);
            if(!successFlag && platformUserId != null){
                userApi.remove(platformUserId);
            }
            return new BooleanDto(successFlag, "很抱歉，添加员工失败，请稍后重试。");
        }catch (Exception e){
            log.error(e.getMessage());
            if(platformUserId != null){
                userApi.remove(platformUserId);
            }
            return new BooleanDto(false,"很抱歉，添加员工失败，请稍后重试。");
        }
    }


    private UserReq toHandleSaveReq(UserRequest request) {
        UserReq req = new UserReq();
        req.setRentId(Long.valueOf(rentId));
        req.setNickName(request.getNickName());
        req.setRealName(request.getRealName());
        req.setMobile(request.getMobileNo());
        req.setEmail(request.getEmail());
        req.setAuthType("email");
        req.setLoginAccount(request.getEmail());
        req.setPassword(request.getPassword());
        req.setStatus(0);
        req.setDisable(0);
        req.setDeletedFlag(0L);
        return req;
    }

    /**
     * 验证用户手机号或邮箱是否已存在
     * @param mobileNo
     * @param email
     * @return
     */
    private BooleanDto verifyUserExits(String mobileNo, String email,Boolean isKeZhu) {
        List<Integer> userTypes = isKeZhu?Arrays.asList(UserConstant.USER_TYPE_KE_ZHU):Arrays.asList(UserConstant.USER_TYPE_PROCESS_ENGINE, UserConstant.USER_TYPE_PROCESS_ENGINE_ADMIN);
        User user = userService.findByEmail(email,userTypes);
        if (user != null) {
            return new BooleanDto(false, "此邮箱已存在，请更换邮箱");
        }
        List<User> userList = userService.findByMobileAndType(mobileNo, userTypes);
        if (CollectionUtils.isNotEmpty(userList)) {
            return new BooleanDto(false, "此手机号已存在，请更换手机号");
        }
        return null;
    }

    private BooleanDto verifySmsCode(@RequestAttribute Long userId, String verifyCode) {
        User user = userService.findById(userId);
        if (user == null || StringUtils.isEmpty(user.getMobileNo())) {
            return new BooleanDto(false, "很抱歉，当前用户无效");
        }
        PlatFormResponse<Boolean> validRes = smsSendService.validCode(user.getMobileNo(), verifyCode);
        if (validRes == null || validRes.getData() == null || !validRes.getData()) {
            return new BooleanDto(false, "很抱歉，验证码无效");
        }
        return null;
    }

    /**
     * 编辑员工
     * @param req
     * @return
     */
    @PutMapping("/edit")
    public BooleanDto edit(@RequestBody @Validated(ValidGroup.Edit.class) UserRequest req,
                        @RequestAttribute Long userId) {
        return userService.updateStaffAndAuth(req, userId);
    }

    /**
     * 删除员工
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public BooleanDto delete(@PathVariable("id") Long id) {
        boolean delFlag = userService.deleteAndFlushCache(id);
        return new BooleanDto(delFlag, "很抱歉，删除员工失败，请稍后重试。");
    }

    /**
     * 为用户绑定渠道
     * @param req
     * @param userId
     * @param channelNo
     * @return
     */
    @PostMapping("/bindchannel")
    public BooleanDto bindChannel(@RequestBody ChannelBindRequest req,
                                  @RequestAttribute("userId") Long userId,
                                  @RequestHeader String channelNo) {
        Long staffId = Optional.ofNullable(req.getStaffId()).orElse(userId);
        List<String> channelNos = Optional.ofNullable(req.getChannelNos()).orElse(new ArrayList<>());
        String userChannelNos = channelNos.stream().collect(Collectors.joining(","));
        List<String> hasChannelNos = new ArrayList<>();

        User user = userService.findById(userId);
        if (user == null || StringUtils.isEmpty(user.getMobileNo())) {
            return new BooleanDto(false, "很抱歉，当前用户无效");
        }
        if (StringUtils.isNotEmpty(user.getChannelNo())) {
            hasChannelNos.add(user.getChannelNo());
        }

        boolean isMainChannelAdmin = false;
        PlatformChannel mainPlatform = platformChannelService.getById(SC_CHANNEL_ID);
        // 势成超管可查询所有渠道
        if (mainPlatform != null &&
                mainPlatform.getChannelNo().equals(user.getChannelNo()) &&
                UserConstant.USER_TYPE_PROCESS_ENGINE_ADMIN.equals(user.getUserType())) {
            isMainChannelAdmin = true;
        }
        // 只有势成超管允许绑定其他渠道，否则只能绑定当前用户拥有的渠道
        if (!isMainChannelAdmin) {
            // 查询当前用户所拥有的权限
            List<UserChannel> currUserChannelNos = userChannelService.list(Wrappers.<UserChannel>lambdaQuery().eq(UserChannel::getUserId, userId));
            if (CollectionUtils.isNotEmpty(currUserChannelNos) && currUserChannelNos.get(0) != null) {
                String[] currUserChannelNoList = currUserChannelNos.get(0).getChannelNoList().split(",");
                if (currUserChannelNoList != null && currUserChannelNoList.length > 0) {
                    hasChannelNos.addAll(Arrays.asList(currUserChannelNoList));
                }
            }

            boolean pass = checkUserBindChannel(hasChannelNos, channelNos);
            if (!pass) {
                return new BooleanDto(false, "很抱歉，当前用户无绑定其他渠道权限。");
            }
        }

        List<UserChannel> userChannels = userChannelService.list(Wrappers.<UserChannel>lambdaQuery().eq(UserChannel::getUserId, staffId));
        if (CollectionUtils.isNotEmpty(userChannels) && userChannels.get(0) != null) {
            UserChannel userChannel = userChannels.get(0);
            userChannel.setChannelNoList(userChannelNos);
            return new BooleanDto(userChannelService.updateById(userChannel), "很抱歉，为员工绑定渠道失败");
        } else {
            UserChannel userChannel = new UserChannel();
            userChannel.setUserId(staffId);
            userChannel.setChannelNoList(userChannelNos);
            return new BooleanDto(userChannelService.save(userChannel), "很抱歉，为员工绑定渠道失败");
        }
    }

    /**
     * 获取短信验证码
     * @param mobileNo
     * @param userId
     * @return
     */
    @GetMapping("/requestVerifyCode")
    public BooleanDto requestVerifyCode(@RequestParam(required = false) String mobileNo,
                                        @RequestAttribute("userId") Long userId) {
        User user = userService.findById(userId);
        if (user == null) {
            return new BooleanDto(false, "很抱歉，当前用户无效");
        }
        mobileNo = Optional.ofNullable(mobileNo).orElse(user.getMobileNo());
        if (StringUtils.isEmpty(mobileNo)) {
            return new BooleanDto(false, "很抱歉，发送验证码失败");
        }
        if (!ValidateUtil.isMobileNO(mobileNo)) {
            return new BooleanDto(false, "很抱歉，手机号无效");
        }
        PlatFormResponse sendRes = smsSendService.send(SmsSendBo.buildCode(CommonConstant.VERIFY_CODE_SMS_MODULE_ID, mobileNo));
        if (sendRes == null || sendRes.error()) {
            return new BooleanDto(false, "很抱歉，" + sendRes.getMessage());
        }
        return new BooleanDto();
    }

    /**
     * 检查用户渠道是否可以绑定。<br>
     * 只能绑定当前渠道和其子渠道
     * @param currChannelNo 当前渠道编码
     * @param channelNos    需要绑定的渠道编码列表
     * @return
     */
    private boolean checkUserChannel(String currChannelNo, List<String> channelNos) {
        List<String> channelPassList = new ArrayList<>();
        for (String channelNo : channelNos) {
            if (currChannelNo.equals(channelNo)) {
                channelPassList.add(channelNo);
                continue;
            }
            PlatformChannel channel = platformChannelService.getByChannelNo(channelNo);
            if (channel == null) {
                log.error("StaffController.checkUserChannel channel is not exits. {}", channelNo);
                return false;
            }
            PlatformChannel pChannel = channel;
            while (pChannel != null) {
                Long parentId = pChannel.getParentId();
                if (parentId == null) {
                    break;
                }
                pChannel = platformChannelService.getById(parentId);
                if (pChannel != null && currChannelNo.equals(pChannel.getChannelNo())) {
                    channelPassList.add(channelNo);
                    break;
                }
            }
        }
        return channelPassList.size() == channelNos.size();
    }

    /**
     * 验证渠道
     * @param hasChannelNos 拥有渠道列表
     * @param channelNos    需要验证的渠道列表
     * @return
     */
    private boolean checkUserBindChannel(List<String> hasChannelNos, List<String> channelNos) {
        return CollectionUtils.isEmpty(
                channelNos.stream().filter(o ->
                        hasChannelNos.stream().allMatch(o1 -> !Optional.ofNullable(o1).orElse("").equals(o))
                ).collect(Collectors.toList())
        );
    }

    /**
     * 搜索医院网点
     * @return
     */
    @GetMapping("/branch")
    public Response getBranchList(){
        List<ApiBranchDto> branchList = branchApi.findBranchList();
        return Response.ok(branchList);
    }

    /**
     * 添加权限
     * @return
     */
    @PostMapping("/power")
    public Response addPower(@RequestBody KeZhuPowerRequest request){
        Assert.notNull(request.getPostJobId(),"岗位信息不能为空!");
        Assert.hasLength(request.getPostJobName(),"岗位名称不能为空!");
        Assert.hasLength(request.getChannelNo(),"渠道编号不能为空!");
        Assert.notEmpty(request.getBranchList(),"网点信息不能为空!");
        this.setPowerValue(request);
        postJobPowerService.savePower(request);
        return Response.ok(null);
    }

    /**
     * 根据岗位ID查询网点列表
     * @param postJobId
     * @return
     */
    @GetMapping("/{id}/branch")
    public Response getPostJobBranch(@PathVariable("id") Long postJobId){
        List<KeZhuBranchDto> list = postJobPowerService.findAllByPostJobId(postJobId);
        return Response.ok(list);
    }

    /**
     * 查询指定岗位指定网点的权限
     * @param postJobId
     * @param hospitalId
     * @param branchId
     * @return
     */
    @GetMapping("/{id}/power")
    public Response getPostJobPowerByJobId(@PathVariable("id") Long postJobId,
                                                 @RequestParam Long hospitalId,
                                                 @RequestParam Long branchId){
        BranchPowerDto dto = postJobPowerService.findByPostJobIdAndHospitalIdAndBranchId(postJobId, hospitalId, branchId);
        ApiBranchPowerDto res = BaseHelper.r2t(dto, ApiBranchPowerDto.class);
        this.setPowerId(res);
        return Response.ok(res);
    }

    /**
     * 编辑岗位网点权限
     * @return
     */
    @PutMapping("/power")
    public Response updatePostJobPower(@RequestBody KeZhuPowerRequest request){
        Assert.notNull(request.getPostJobId(),"岗位信息不能为空!");
        Assert.notNull(request.getHospitalId(),"医院信息不能为空!");
        Assert.notNull(request.getBranchId(),"网点信息不能为空!");
        this.setPowerValue(request);
        postJobPowerService.updateByJob(request);
        return Response.ok(null);
    }

    /**
     * 查看关联用户的网点权限
     * @return
     */
    @GetMapping("/user/power")
    public Response getUserPower(@RequestParam Long userId,
                                    @RequestParam Long hospitalId,
                                    @RequestParam Long branchId){
        User user = userService.findById(userId);
        if (null == user){
            return Response.error("员工信息有误!");
        }
        if (null == user.getPlatformUserId()){
            return Response.error("未查询到此用户!");
        }
        ApiBranchPowerDto dto = branchPowerApi.findByUserIdAndHospitalIdAndbranchId(user.getPlatformUserId(), hospitalId, branchId);
        this.setPowerId(dto);
        return Response.ok(dto);
    }

    /**
     * 修改关联用户的网点权限
     * @return
     */
    @PutMapping("/user/power")
    public Response updateUserPower(@RequestBody KeZhuPowerRequest request){
        Assert.notNull(request.getUserId(),"员工信息不能为空!");
        Assert.notNull(request.getHospitalId(),"医院信息不能为空!");
        Assert.notNull(request.getBranchId(),"网点信息不能为空!");
        User user = userService.findById(request.getUserId());
        if (null == user){
            return Response.error("员工信息有误!");
        }
        if (null == user.getPlatformUserId()){
            return Response.error("未查询到此用户!");
        }
        this.setPowerValue(request);
        BranchPowerUpdateRequest updateRequest = new BranchPowerUpdateRequest();
        BeanUtils.copyProperties(request,updateRequest);
        updateRequest.setIds(Arrays.asList(user.getPlatformUserId()));
        branchPowerApi.updateByUserIds(updateRequest);
        return Response.ok(null);
    }

    private void setPowerValue(KeZhuPowerRequest query) {
        List<Long> permIds = query.getPowerList();
        query.setOpenOrder(false);query.setOpenStock(false);query.setOpenInvoice(false);
        query.setOpenData(false);query.setOpenGroup(false);query.setOpenMessage(false);
        if (CollectionUtils.isNotEmpty(permIds)) {
            QueryWrapper<Perms> queryWrapper = new QueryWrapper<>(new Perms());
            queryWrapper.in("id",permIds);
            List<Perms> permsList = permsService.list(queryWrapper);
            Map<Long, String> longStringMap = permsList.stream().collect(Collectors.toMap(e -> e.getId(), e -> e.getCode()));
            for (Long permId : permIds) {
                String code = longStringMap.get(permId);
                if (PermsConstant.KeZhuPermEnum.OP_OPEN_ORDER.getCode().equals(code)){
                    query.setOpenOrder(true);
                }else if (PermsConstant.KeZhuPermEnum.OP_OPEN_STOCK.getCode().equals(code)){
                    query.setOpenStock(true);
                }else if (PermsConstant.KeZhuPermEnum.OP_OPEN_INVOICE.getCode().equals(code)){
                    query.setOpenInvoice(true);
                }else if (PermsConstant.KeZhuPermEnum.OP_OPEN_DATA.getCode().equals(code)){
                    query.setOpenData(true);
                }else if (PermsConstant.KeZhuPermEnum.OP_OPEN_GROUP.getCode().equals(code)){
                    query.setOpenGroup(true);
                }else if (PermsConstant.KeZhuPermEnum.OP_OPEN_MESSAGE.getCode().equals(code)){
                    query.setOpenMessage(true);
                }
            }
        }
    }


    private void setPowerId(ApiBranchPowerDto dto) {
      if (null != dto){
          List<Long> result = new ArrayList<>();
          QueryWrapper<Perms> queryWrapper = new QueryWrapper<>(new Perms());
          List<String> codeList = Arrays.asList(
                  PermsConstant.KeZhuPermEnum.OP_OPEN_ORDER.getCode(),
                  PermsConstant.KeZhuPermEnum.OP_OPEN_INVOICE.getCode(),
                  PermsConstant.KeZhuPermEnum.OP_OPEN_GROUP.getCode(),
                  PermsConstant.KeZhuPermEnum.OP_OPEN_DATA.getCode(),
                  PermsConstant.KeZhuPermEnum.OP_OPEN_STOCK.getCode(),
                  PermsConstant.KeZhuPermEnum.OP_OPEN_MESSAGE.getCode());
          queryWrapper.in("code",codeList);
          List<Perms> permsList = permsService.list(queryWrapper);
          Map<String, Long> collect = permsList.stream().collect(Collectors.toMap(e -> e.getCode(), e -> e.getId()));
          if (null != dto.getOpenOrder() && dto.getOpenOrder()){
              result.add(collect.get(PermsConstant.KeZhuPermEnum.OP_OPEN_ORDER.getCode()));
          }
          if (null != dto.getOpenStock() && dto.getOpenStock()){
              result.add(collect.get(PermsConstant.KeZhuPermEnum.OP_OPEN_STOCK.getCode()));
          }
          if (null != dto.getOpenInvoice() && dto.getOpenInvoice()){
              result.add(collect.get(PermsConstant.KeZhuPermEnum.OP_OPEN_INVOICE.getCode()));
          }
          if (null != dto.getOpenData() && dto.getOpenData()){
              result.add(collect.get(PermsConstant.KeZhuPermEnum.OP_OPEN_DATA.getCode()));
          }
          if (null != dto.getOpenGroup() && dto.getOpenGroup()){
              result.add(collect.get(PermsConstant.KeZhuPermEnum.OP_OPEN_GROUP.getCode()));
          }
          if (null != dto.getOpenMessage() && dto.getOpenMessage()){
              result.add(collect.get(PermsConstant.KeZhuPermEnum.OP_OPEN_MESSAGE.getCode()));
          }
          dto.setPowerList(result);
      }
    }
}
