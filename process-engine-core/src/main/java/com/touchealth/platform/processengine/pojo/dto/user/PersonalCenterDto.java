package com.touchealth.platform.processengine.pojo.dto.user;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Description:个人中心展示对象
 *
 * @author lvx
 * @date 2021/1/15
 */
@Data
public class PersonalCenterDto {

    /**
     * 用户名
     */
    private String userName;

    /**
     * 账户余额
     */
    private BigDecimal accountBalance;

    /**
     * 头像链接
     */
    private String headPicUrl;

    /**
     * 手机号
     */
    private String mobileNo;

    /**
     * 收藏商品数量
     */
    private Integer collectionGoodNum;

    /**
     * 收藏医院数量
     */
    private Integer collectionHospitalNum;

    /**
     * 我的问答数量
     */
    private Integer questionAndAnswerNum;

    /**
     * 浏览记录数量
     */
    private Integer browsingHistoryNum;

    /**
     * 卡券数量
     */
    private Integer cardVoucherNum;

    /**
     * 优惠券数量
     */
    private Integer couponsNum;

    /**
     * 我的积分数量
     */
    private Integer integrateNum;

}
