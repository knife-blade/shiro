package com.touchealth.platform.processengine.pojo.request.kezhu;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Description : 更新用户网点权限请求
 * @Author : dangshilin
 * @CreateTime : 2021年04月21日 10:48
 */
@Data
public class UpdateUserPowerRequest implements Serializable {
    private static final long serialVersionUID = -8033269632020361375L;

    /**
     * 流程引擎的岗位员工的userId
     */
    @NotNull(message = "员工信息不能为空!")
    private Long userId;

    @NotNull(message = "医院信息不能为空!")
    private Long hospitalId;

    @NotNull(message = "网点信息不能为空!")
    private Long branchId;

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
