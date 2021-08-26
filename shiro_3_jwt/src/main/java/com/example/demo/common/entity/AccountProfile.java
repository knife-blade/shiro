package com.example.demo.common.entity;

import com.example.demo.common.util.auth.ShiroUtil;
import lombok.Data;

/**
 * 存放账户的信息。
 *   在登录后会实例化一个此对象，然后放到subject里边。
 *   获取方法：{@link ShiroUtil#getProfile()}
 */
@Data
public class AccountProfile {
    private String id;
    private String userName;
}
