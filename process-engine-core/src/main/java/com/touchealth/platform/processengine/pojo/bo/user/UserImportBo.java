package com.touchealth.platform.processengine.pojo.bo.user;

import lombok.Data;

@Data
public class UserImportBo {

    private String realName;
    private String code;
    private String mobileNo;
    private String email;
    private String password;
    private Long deptId;
    private String deptName;
    private Long postJobId;
    private String postJobName;
    private Integer staffStatus;
    private String staffStatusStr;

}
