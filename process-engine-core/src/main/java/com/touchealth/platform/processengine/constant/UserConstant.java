package com.touchealth.platform.processengine.constant;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public interface UserConstant {

    /**
     * 用户类型-流程引擎后台普通员工
     */
    Integer USER_TYPE_PROCESS_ENGINE = 10;
    /**
     * 用户类型-流程引擎后台超级管理员
     */
    Integer USER_TYPE_PROCESS_ENGINE_ADMIN = 11;

    /**
     * 用户类型-运营中心-科助中心员工
     */
    Integer USER_TYPE_KE_ZHU = 12;

    /**
     * {@link UserConstant#USER_TYPE_PROCESS_ENGINE_ADMIN}
     */
    Integer USER_TYPE_PROCESS_ENGINE_SUPER = USER_TYPE_PROCESS_ENGINE_ADMIN;
    /**
     * 前端用户
     */
    Integer USER_TYPE_PROCESS_ENGINE_H5 = 1;

    /**
     * 员工状态-在职
     */
    Integer STAFF_STATUS_IN = 0;
    /**
     * 员工状态-离职
     */
    Integer STAFF_STATUS_OUT = 1;

    /**
     * 用户状态-启用
     */
    Integer USE_ENABLE = 0;
    /**
     * 用户状态-禁用
     */
    Integer USE_DISABLE = 1;

    /**
     * 权限类型-菜单
     */
    Integer PERM_TYPE_MENU = 0;
    /**
     * 权限类型-功能
     */
    Integer PERM_TYPE_OP = 1;
    /**
     * 权限类型-数据
     */
    Integer PERM_TYPE_DATA = 2;
    /**
     * 权限类型-字段
     */
    Integer PERM_TYPE_FIELD = 3;
    /**
     * 权限类型-接口
     */
    Integer PERM_TYPE_API = 4;

    /**
     * 默认用户头像
     */
    String DEFAULT_AVATAR = "http://sckj-ygys.oss-cn-hangzhou.aliyuncs.com/pic/assets/default_avatar.png";

    /**
     * 员工状态。
     */
    @Getter
    enum STAFF_STATUS {
        IN(STAFF_STATUS_IN, "在职"),
        OUT(STAFF_STATUS_OUT, "离职"),
        ;
        private Integer code;
        private String name;

        STAFF_STATUS(Integer code, String name) {
            this.code = code;
            this.name = name;
        }

        public static Map<Integer, STAFF_STATUS> CODE_MAP;
        public static Map<String, STAFF_STATUS> NAME_MAP;
        static{
            if (CODE_MAP == null) {
                CODE_MAP = Arrays.stream(STAFF_STATUS.values()).collect(Collectors.toMap(STAFF_STATUS::getCode, o -> o));
            }
            if (NAME_MAP == null) {
                NAME_MAP = Arrays.stream(STAFF_STATUS.values()).collect(Collectors.toMap(STAFF_STATUS::getName, o -> o));
            }
        }
    }

    /**
     * 账号密码登录
     */
    int USER_SIGN_TYPE_4_ACCESS = 0;

    /**
     * 验证码登录
     */
    int USER_SIGN_TYPE_4_VERIFICATION_CODE = 1;

}
