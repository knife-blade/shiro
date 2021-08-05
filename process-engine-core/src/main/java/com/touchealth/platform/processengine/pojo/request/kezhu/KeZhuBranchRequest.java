package com.touchealth.platform.processengine.pojo.request.kezhu;

import com.touchealth.platform.processengine.common.constant.CommonObject;
import lombok.Data;

import java.util.List;

/**
 * @Description : 医院,网点层级关系
 * @Author : dangshilin
 * @CreateTime : 2021年04月20日 14:14
 */
@Data
public class KeZhuBranchRequest {

    /**
     * 医院ID
     */
    private Long id;

    /**
     * 医院名称
     */
    private String name;

    /**
     * 网点列表
     */
    private List<CommonObject> branchList;
}
