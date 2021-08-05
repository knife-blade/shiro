package com.touchealth.platform.processengine.controller.user;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.touchealth.platform.processengine.annotation.PassToken;
import com.touchealth.platform.processengine.entity.user.User;
import com.touchealth.platform.processengine.pojo.bo.user.TokenBo;
import com.touchealth.platform.processengine.pojo.dto.Response;
import com.touchealth.platform.processengine.pojo.dto.user.TokenDto;
import com.touchealth.platform.processengine.pojo.dto.user.UserNameDto;
import com.touchealth.platform.processengine.pojo.request.user.LoginRequest;
import com.touchealth.platform.processengine.service.user.UserService;
import com.touchealth.platform.processengine.utils.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.stream.Collectors;

import static com.touchealth.platform.processengine.constant.UserConstant.USER_TYPE_PROCESS_ENGINE_SUPER;

@Slf4j
@RequestMapping("/user")
@RestController
public class LoginController {

    @Resource
    private UserService userService;

    /**
     * 流程引擎管理平台登录
     */
    @PassToken
    @PostMapping("/login")
    public Response login(@RequestBody @Valid LoginRequest request,
                          @RequestHeader(value = "Referer", required = false) String referer,
                          HttpServletRequest httpServletRequest) {
        String ipAddress = IpUtil.getIpAddress(httpServletRequest);
        request.setIpAddress(ipAddress);
        request.setReferer(referer);
        TokenBo tokenBo = userService.login(request);
        return Response.ok(TokenDto.convert(tokenBo));
    }

    /**
     * 根据手机号获取验证码
     * @param mobile 手机号
     */
    @PassToken
    @GetMapping("/validateCode")
    public Boolean getVerificationCode(String mobile) {
        userService.getVerificationCode(mobile, true);
        return true;
    }

    /**
     * 用户名列表
     */
    @GetMapping("/name")
    public Response userName() {
        return Response.ok(userService.list(Wrappers.<User>lambdaQuery().ne(User::getUserType, USER_TYPE_PROCESS_ENGINE_SUPER))
                .stream().map(o -> new UserNameDto(o.getId(), o.getRealName())).collect(Collectors.toList()));
    }
}



















