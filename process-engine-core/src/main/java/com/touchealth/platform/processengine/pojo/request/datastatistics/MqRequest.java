package com.touchealth.platform.processengine.pojo.request.datastatistics;

import lombok.Data;

import java.util.List;

/**
 * @program: process-engine
 * @author: xianghy
 * @create: 2020/12/9
 **/
@Data
public class MqRequest {

    /**
     * 用户唯一标识
     */
    private String userCode;

    /**
     * 渠道ID
     */
    private String channelNo;

    /**
     * 页面ID
     */
    private Long pageId;

    private String pageUniqueId;

    private String userBoardUniqueId;

    private List<DataStatisticsForBannerRequest> bannerRequestList;

    private List<DataStatisticsForNavigateRequest> navigateRequestList;

    private List<DataStatisticsForComboImgRequest> comboImgRequestList;

    private List<DataStatisticsForHotspotRequest> hotspotRequestList;

    private List<DataStatisticsForButtonRequest> buttonRequestList;

    private List<DataStatisticsForInputRequest> inputRequestList;

    private List<DataStatisticsForSelectRequest> selectRequestList;

    private List<DataStatisticsForSlideRequest> slideRequestList;

    private List<DataStatisticsForLoginRequest> loginRequestList;

    private List<DataStatisticsForBlankRequest> blankRequestList;
    /**
     * 个人信息
     */
    private List<DataStatisticsForPersonalInfoRequest> personalInfoRequestList;

    /**
     * 订单管理
     */
    private List<DataStatisticsForOrderManagementRequest> orderManagementRequestList;

    /**
     * 我的模块
     */
    private List<DataStatisticsForMyModRequest> myModRequestList;

    /**
     * 首页导航组件
     */
    private List<DataStatisticsForHomeNavRequest> homeNavRequestList;

    public MqRequest() {
    }

    public MqRequest(String userCode, String channelNo, Long pageId, String pageUniqueId, String userBoardUniqueId) {
        this.userCode = userCode;
        this.channelNo = channelNo;
        this.pageId = pageId;
        this.pageUniqueId = pageUniqueId;
        this.userBoardUniqueId = userBoardUniqueId;
    }
}
