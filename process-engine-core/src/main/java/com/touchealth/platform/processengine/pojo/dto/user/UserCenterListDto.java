package com.touchealth.platform.processengine.pojo.dto.user;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author liufengqiang
 * @date 2021-04-10 11:30:23
 */
@Data
public class UserCenterListDto {

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
     * 注册时间
     */
    private LocalDateTime createdAt;
}
