package com.touchealth.platform.processengine.entity.module.common;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableName;
import com.touchealth.platform.processengine.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 登录组件表
 * </p>
 *
 * @author ljh
 * @since 2021-01-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("module_login")
public class Login extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 版本间的唯一组件标识。同一个组件，不同版本该值不变。
     */
    @TableField(fill = FieldFill.INSERT)
    private Long moduleUniqueId;

    /**
     * 轮播图组件所在的渠道编码
     */
    private String channelNo;

    /**
     * 中文主标题
     */
    @TableField("title_chinese")
    private String titleChinese;

    /**
     * 中文副标题
     */
    @TableField("subtitle_chinese")
    private String subtitleChinese;

    /**
     * 中文用户协议
     */
    @TableField("agreement_url_chinese")
    private String agreementUrlChinese;

    /**
     * 中文隐私协议
     */
    @TableField("privacy_agreement_url_chinese")
    private String privacyAgreementUrlChinese;

    /**
     * 英文主标题
     */
    @TableField("title_eng")
    private String titleEng;

    /**
     * 英文副标题
     */
    @TableField("subtitle_eng")
    private String subtitleEng;

    /**
     * 英文用户协议
     */
    @TableField("agreement_url_eng")
    private String agreementUrlEng;

    /**
     * 英文文隐私协议
     */
    @TableField("privacy_agreement_url_eng")
    private String privacyAgreementUrlEng;

    /**
     * 组件分类ID
     */
    private Long categoryId;

    /**
     * 轮播图组件所在的页面ID
     */
    private Long pageId;

    /**
     * 状态。0：草稿；1：保存（下架）；2：垃圾；3：已发布（上架）；4：当前发布
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


}
