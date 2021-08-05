package com.touchealth.platform.processengine.pojo.dto;

import lombok.Data;

/**
 * Description:用户登录信息
 *
 * @author gavin
 * @date 2020/10/28
 */
@Data
public class UserLoginInfoDto {

    private Long userId;

    private String token;
}
