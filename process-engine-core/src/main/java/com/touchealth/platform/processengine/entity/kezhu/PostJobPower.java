package com.touchealth.platform.processengine.entity.kezhu;

import com.baomidou.mybatisplus.annotation.TableName;
import com.touchealth.platform.processengine.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description : 岗位权限关系
 * @Author : dangshilin
 * @CreateTime : 2021年04月20日 14:26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("post_job_power")
public class PostJobPower extends BaseEntity {
    private static final long serialVersionUID = 7061175032292491544L;

    /**
     * 渠道编码
     */
    private String channelNo;

    /**
     * 岗位ID
     */
    private Long postJobId;

    /**
     * 岗位名称
     */
    private String postJobName;

    /**
     * 医院ID
     */
    private Long hospitalId;

    /**
     * 医院名称
     */
    private String hospitalName;

    /**
     * 网点ID
     */
    private Long branchId;

    /**
     * 网点名称
     */
    private String branchName;

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
