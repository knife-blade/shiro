package com.touchealth.platform.processengine.pojo.bo.module.common;

import com.baomidou.mybatisplus.annotation.TableField;
import com.touchealth.platform.processengine.constant.CommonConstant;
import com.touchealth.platform.processengine.pojo.dto.module.BaseDto;
import lombok.Data;

@Data
public class LoginBo extends BaseDto{
    /**
     * 渠道ID
     */
    private String channelNo;
    /**
     * 页面ID
     */
    private Long pageId;

    /**
     * 组件分类ID
     */
    private Long categoryId;

    /**
     * 状态。0：草稿；1：保存（下架）；2：垃圾；3：已发布（上架）；4：当前发布
     * @see CommonConstant.STATUS
     */
    private Integer status;
    /**
     * 前端页面配置信息，JSON格式。该值来源于前端，原样返回给前端。每次更新时的对比记录操作日志也使用该字段。
     */
    private String webJson;
    /**
     * 版本号ID
     */
    private Long version;
    /**
     * 版本号
     */
    private String versionName;

    /**
     * 中文主标题
     */
    private String titleChinese;

    /**
     * 中文副标题
     */
    private String subtitleChinese;

    /**
     * 中文用户协议
     */
    private String agreementUrlChinese;

    /**
     * 中文隐私协议
     */
    private String privacyAgreementUrlChinese;

    /**
     * 英文主标题
     */
    private String titleEng;

    /**
     * 英文副标题
     */
    private String subtitleEng;

    /**
     * 英文用户协议
     */
    private String agreementUrlEng;

    /**
     * 英文文隐私协议
     */
    private String privacyAgreementUrlEng;
}
