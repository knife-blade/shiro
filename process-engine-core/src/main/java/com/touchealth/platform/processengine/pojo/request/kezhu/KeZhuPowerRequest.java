package com.touchealth.platform.processengine.pojo.request.kezhu;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description : 科助用户网点权限维护
 * @Author : dangshilin
 * @CreateTime : 2021年04月20日 14:06
 */
@Data
public class KeZhuPowerRequest implements Serializable {
    private static final long serialVersionUID = 2507186356218033461L;

    /**
     * 流程引擎的岗位员工的userId
     */
    private Long userId;

    /**
     * 医院ID
     */
    private Long hospitalId;

    /**
     * 网点ID
     */
    private Long branchId;

    /**
     * 岗位ID
     */
    private Long postJobId;

    /**
     * 岗位名称
     */
    private String postJobName;

    /**
     * 渠道编码
     */
    private String channelNo;

    /**
     * 配置权限的网点
     */
    private List<KeZhuBranchRequest> branchList;

    /**
     * 权限值对应的ID
     */
    private List<Long> powerList;

    /**
     * 是否开启订单权限 1-开启 / 0-关闭
     */
    private Boolean openOrder;

    /**
     * 是否开启发票权限 1-开启 / 0-关闭
     */
    private Boolean openInvoice;

    /**
     * 是否开启号源权限 1-开启 / 0-关闭
     */
    private Boolean openStock;

    /**
     * 是否开启数据看板权限 1-开启 / 0-关闭
     */
    private Boolean openData;

    /**
     * 是否开启团检管理权限 1-开启 / 0-关闭
     */
    private Boolean openGroup;

    /**
     * 是否开启公众号消息推送管理权限 1-开启 / 0-关闭
     */
    private Boolean openMessage;
}
