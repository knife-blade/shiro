package com.touchealth.platform.processengine.controllermobile;

import com.touchealth.api.core.bo.UserThirdpartyAuthBo;
import com.touchealth.api.core.bo.UserThirdpartyBo;
import com.touchealth.api.core.constant.ThirdpartyConstant;
import com.touchealth.api.core.service.IdGeneratorService;
import com.touchealth.api.core.service.SysWechatAccountService;
import com.touchealth.api.core.service.UserThirdpartyAuthService;
import com.touchealth.api.core.service.UserThirdpartyService;
import com.touchealth.api.file.service.UploadService;
import com.touchealth.api.ops.service.wechatofficialaccounts.OfficialAccountsService;
import com.touchealth.common.bo.core.SysWechatAccountBo;
import com.touchealth.common.bo.core.UserBo;
import com.touchealth.common.constant.Globaldefine;
import com.touchealth.common.utils.JsonUtil;
import com.touchealth.common.utils.WeChatUtils;
import com.touchealth.platform.processengine.annotation.PassToken;
import com.touchealth.platform.processengine.constant.UserConstant;
import com.touchealth.platform.processengine.pojo.dto.Response;
import com.touchealth.platform.processengine.pojo.dto.user.TokenDto;
import com.touchealth.platform.processengine.pojo.request.user.SignUpRequest;
import com.touchealth.platform.processengine.service.user.UserService;
import com.touchealth.platform.processengine.utils.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * @author liufengqiang
 * @date 2021-01-20 14:41:25
 */
@Slf4j
@RestController
@RequestMapping("/mobile/user")
public class MobileUserController {

    @Resource
    private UserService userService;

    @Resource
    private UserThirdpartyService userThirdpartyService;

    @Resource
    private UserThirdpartyAuthService userThirdpartyAuthService;

    @Resource
    private SysWechatAccountService sysWechatAccountService;

    @Resource
    private IdGeneratorService idGeneratorService;

    @Resource
    private UploadService uploadService;

    @Resource
    private OfficialAccountsService officialAccountsService;

    private static final String OPENID = "openid";
    private static final String oPENID = "openId";
    private static final String UNIONID = "unionid";

    /**
     * 流程引擎客户端用户登录
     * @param signUpRequest 登录请求参数
     * @param referer 跳转地址
     * @param httpServletRequest 请求对象
     * @return
     */
    @PassToken
    @PostMapping("/login")
    public TokenDto signUp(@RequestBody SignUpRequest signUpRequest,
                           @RequestHeader(value = "Referer", required = false) String referer,
                           HttpServletRequest httpServletRequest) {
        String ipAddress = IpUtil.getIpAddress(httpServletRequest);
        signUpRequest.setReferer(referer);
        signUpRequest.setIpAddress(ipAddress);
        TokenDto tokenDto = TokenDto.convert(userService.signUp(signUpRequest));
        // 绑定微信信息
        UserBo userBo = bindWeChat(signUpRequest, tokenDto.getUser().getId(), signUpRequest.getMobile());
        if(null != userBo){
            // 将头像昵称返还前端
            tokenDto.getUser().setNickName(userBo.getNickName());
            tokenDto.getUser().setAvatar(userBo.getAvatar());
        }
        return tokenDto;
    }

    private UserBo bindWeChat(SignUpRequest request, Long userId, String mobile) {
        try {
            Map userInfoMap = request.getAuthInfo();
            if (userInfoMap != null && userInfoMap.containsKey(OPENID)) {
                //存储微信授权信息
                String openId = userInfoMap.get(OPENID).toString();
                UserThirdpartyBo userThirdpartyBo = userThirdpartyService.findByWechatAppid(request.getAppId());
                UserThirdpartyAuthBo userThirdpartyAuthBo =
                        userThirdpartyAuthService.findByUserIdAndThirdpartyId(userId, userThirdpartyBo.getId());
                if (userThirdpartyAuthBo == null) {
                    userThirdpartyAuthBo = new UserThirdpartyAuthBo();
                    userThirdpartyAuthBo.setId(idGeneratorService.generateIdByTables(
                            Globaldefine.tableIdGenerator.SYS_USER_THIRDPARTY_AUTH));
                    userThirdpartyAuthBo.setUserId(userId);
                    userThirdpartyAuthBo.setOpenId(openId);
                    if(null != userInfoMap.get(UNIONID)){
                        userThirdpartyAuthBo.setUnionId(userInfoMap.get(UNIONID).toString());
                    }
                    Base64 base64 = new Base64();
                    if (userInfoMap.containsKey("nickname")) {
                        userInfoMap.put("nickname",
                                base64.encodeToString(userInfoMap.get("nickname").toString().getBytes("UTF-8")));
                    }
                    if (userInfoMap.containsKey("nickName")) {
                        userInfoMap.put("nickName",
                                base64.encodeToString(userInfoMap.get("nickName").toString().getBytes("UTF-8")));
                    }
                    userThirdpartyAuthBo.setBaseInfo(JsonUtil.getJsonFromObject(userInfoMap));
                    userThirdpartyAuthBo.setThirdpartyId(userThirdpartyBo.getId());
                    userThirdpartyAuthBo.setType(ThirdpartyConstant.UserThirdpartyType.WECHAT.getType());
                    userThirdpartyAuthBo.setState(ThirdpartyConstant.UserThirdpartyAuthState.NORMAL.getState());
                    userThirdpartyAuthBo.setCreateAt(new Date());
                    userThirdpartyAuthBo.setUpdateAt(new Date());
                    //存储微信授权信息
                    userThirdpartyAuthService.save(userThirdpartyAuthBo);
                    // 设置用户的头像昵称为微信头像昵称
                    String headImgUrl =  userInfoMap.containsKey("headimgurl") ?
                            (String) userInfoMap.get("headimgurl") : (String) userInfoMap.get("avatarUrl");
                    String nickName = userInfoMap.containsKey("nickname") ?
                            (String) userInfoMap.get("nickname") : (String) userInfoMap.get("nickName");
                    UserBo userBo = userService.findByMobileAndType(mobile, UserConstant.USER_TYPE_PROCESS_ENGINE_H5);
                    if(null != userBo){
                        if (org.apache.commons.lang.StringUtils.isNotEmpty(nickName)) {
                            userBo.setNickName(new String(base64.decode(nickName), StandardCharsets.UTF_8));
                        }
                        if (org.apache.commons.lang.StringUtils.isNotEmpty(headImgUrl)) {
                            // 上传至七牛云
                            Long avatar = uploadService.uploadFileByUrl(headImgUrl,nickName + "_avatar.jpg", true);
                            userBo.setAvatar(avatar.toString());
                        }
                    }
                    return userService.updateUser(userBo);
                }
            }
        }catch (Exception e){
            log.error("绑定微信信息失败,e:{}",e);
        }
        return null;
    }

    /**
     * 用户跳转Touchealth系统获取登录code
     * @param userId 用户ID
     * @return 对应的code
     */
    @GetMapping("/signCode")
    public String getUserTicket(@RequestAttribute Long userId) {
        return userService.getUserTicket(userId);
    }

    /**
     * 根据手机号获取验证码
     * @param mobile 手机号
     */
    @PassToken
    @GetMapping("validateCode")
    public Boolean getVerificationCode(String mobile) {
        userService.getVerificationCode(mobile, false);
        return true;
    }

    /**
     * 获取微信openId
     * @param appId 公众号appid
     * @param code 用户微信code
     * @param isComponent 是否为公众号
     * @return
     */
    @PassToken
    @GetMapping("/get-wechat-openId")
    public Response getWechatOpenId(@RequestParam String appId,@RequestParam String code,@RequestParam Boolean isComponent){
        try {
            UserThirdpartyBo userThirdpartyBo = userThirdpartyService.findByWechatAppid(appId);
            Map<String, String> authmap = JsonUtil.getObjectFromJson(userThirdpartyBo.getAuthInfo(), Map.class);
            String tokenInfo;
            if (!isComponent || authmap.get("appsecret") != null) {
                tokenInfo = WeChatUtils.getWebAccessToken(authmap.get("appid"), authmap.get("appsecret"), code);
            } else {
                SysWechatAccountBo accountBo = sysWechatAccountService.findAccount();
                String componentAppId = accountBo.getAppId();
                String appSecret = accountBo.getAppSecret();
                String componentAccessToken = officialAccountsService.findComponentAccessToken(componentAppId, appSecret);
                tokenInfo = WeChatUtils.getComponentWebAccessToken(appId, componentAppId, code, componentAccessToken);
            }
            Map tokenMap = JsonUtil.getObjectFromJson(tokenInfo, Map.class);
            if (!tokenMap.containsKey(OPENID) && !tokenMap.containsKey(oPENID)) {
                log.error("获取微信授权信息失败tokenInfo：" + tokenInfo);
                return Response.error("获取授权信息失败");
            }
            String unionId = null;
            String openId = null;
            Map authMap = JsonUtil.getObjectFromJson(WeChatUtils.getUserInfo((String) tokenMap.get("access_token"),tokenMap.get(OPENID).toString()),Map.class);
            if(authMap.get(UNIONID)==null){
                unionId = authMap.get("unionId").toString();
                authMap.put("unionid",unionId);
                authMap.remove("unionId");
                openId = authMap.get("openId").toString();
                authMap.put("openid",openId);
                authMap.remove("openId");
            }
            return Response.ok(authMap);
        }catch (Exception e){
            log.error("获取微信授权信息失败：" + ExceptionUtils.getFullStackTrace(e));
            return Response.error("系统异常");
        }
    }
}
