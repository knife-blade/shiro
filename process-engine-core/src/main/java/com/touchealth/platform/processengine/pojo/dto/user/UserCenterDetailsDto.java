package com.touchealth.platform.processengine.pojo.dto.user;

import lombok.Data;

/**
 * @author liufengqiang
 * @date 2021-04-10 11:30:23
 */
@Data
public class UserCenterDetailsDto {

    private Long id;
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 手机号
     */
    private String mobileNo;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 地区
     */
    private String distinct;
}
