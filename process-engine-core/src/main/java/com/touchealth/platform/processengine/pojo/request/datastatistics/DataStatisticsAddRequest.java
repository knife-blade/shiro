package com.touchealth.platform.processengine.pojo.request.datastatistics;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @program: process-engine
 * @author: xianghy
 * @create: 2020/11/23
 **/
@Data
public class DataStatisticsAddRequest implements Serializable {

    /**
     * 页面ID
     */
    private Long pageId;

    /**
     * 页面唯一ID
     */
    private String pageUniqueId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户UUID
     */
    private String userMark;

    /**
     * 浏览器信息
     */
    private String userAgent;

    /**
     * 跳转信息
     */
    private String referer;

    /**
     * 省份
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 区
     */
    private String district;

    /**
     * IP
     */
    private String ipAddress;

    private String channelNo;

    /**
     * 页面进入时间戳
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date visitStartTime;

    /**
     * 页面结束时间戳
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date visitEndTime;

    /**
     * 轮播图
     */
    private List<DataStatisticsForBannerRequest> banner;

    /**
     * 坑位导航
     */
    private List<DataStatisticsForNavigateRequest> grid;

    /**
     * 多图
     */
    private List<DataStatisticsForComboImgRequest> chunk;

    /**
     * 热区
     */
    private List<DataStatisticsForHotspotRequest> hotspot;

    /**
     * 按钮
     */
    private List<DataStatisticsForButtonRequest> fixedButton;

    /**
     * 个人信息
     */
    private List<DataStatisticsForPersonalInfoRequest> personal;

    /**
     * 订单管理
     */
    private List<DataStatisticsForOrderManagementRequest> orderManage;

    /**
     * 我的模块
     */
    private List<DataStatisticsForMyModRequest> personalModule;

    /**
     * 首页导航组件
     */
    private List<DataStatisticsForHomeNavRequest> navigation;


    private List<DataStatisticsForInputRequest> input;

    private List<DataStatisticsForSelectRequest> select;

    private List<DataStatisticsForSlideRequest> slide;

    private List<DataStatisticsForLoginRequest> login;

    /**
     * 空模块
     */
    private List<DataStatisticsForBlankRequest> blank;

    /**
     * 
     * 用户名称
     */
    private String userName;

    /**
     * 身份类型
     */
    private Integer identityType;

    /**
     * 身份信息
     */
    private String identityNo;

    /**
     * 出生年月
     */
    private Date birthday;

    /**
     * 性别
     */
    private String sex;

}
