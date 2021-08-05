package com.touchealth.platform.processengine.service.datastatistics;

import com.touchealth.platform.processengine.pojo.dto.page.DataStatisticsDto;
import com.touchealth.platform.processengine.pojo.request.datastatistics.MqRequest;
import com.touchealth.platform.processengine.pojo.request.datastatistics.DataStatisticsAddRequest;

import java.util.List;

/**
 * @program: process-engine
 * @author: xianghy
 * @create: 2020/11/23
 **/
public interface DataStatisticsService {

    /**
     * 新增埋点数据
     * @param request 请求参数
     */
    void addDataStatistics(DataStatisticsAddRequest request);

    /**
     * 处理banner埋点数据
     * @param mqRequest 请求参数
     */
    void processDataStatisticsForBannerRequest(MqRequest mqRequest);

    /**
     * navigate数据处理
     * @param mqRequest 请求参数
     */
    void processDataStatisticsForNavigateRequest(MqRequest mqRequest);

    /**
     * comboImg数据处理
     * @param mqRequest 请求参数
     */
    void processDataStatisticsForComboImgRequest(MqRequest mqRequest);

    /**
     * hotspot数据处理
     * @param mqRequest 请求参数
     */
    void processDataStatisticsForHotspotRequest(MqRequest mqRequest);

    /**
     * button数据处理
     * @param mqRequest 请求参数
     */
    void processDataStatisticsForButtonRequest(MqRequest mqRequest);

    /**
     * 登录组件
     * @param mqRequest 请求参数
     */
    void processDataStatisticsForLoginRequest(MqRequest mqRequest);

    /**
     * 个人信息组件
     * @param mqRequest 请求参数
     */
    void processDataStatisticsForPersonalInfoRequest(MqRequest mqRequest);

    /**
     * 订单管理组件
     * @param mqRequest 请求参数
     */
    void processDataStatisticsForOrderManagementRequest(MqRequest mqRequest);

    /**
     * 我的模块组件
     * @param mqRequest 请求参数
     */
    void processDataStatisticsForMyModRequest(MqRequest mqRequest);

    /**
     * 首页导航模块组件
     * @param mqRequest 请求参数
     */
    void processDataStatisticsForHomeNavRequest(MqRequest mqRequest);

    /**
     * 保存空模块埋点数据
     * @param mqRequest 请求参数
     */
    void processDataStatisticsForBlankRequest(MqRequest mqRequest);

    /**
     * 获取页码相关埋点数据
     * @param pageId
     * @param userId
     * @return
     */
    DataStatisticsDto getPageDataStatistics(Long pageId, Long userId);

    /**
     根据pageId 查询页面指定空模块埋点相关数据
     * @param list 空模块id与列表元素id
     * @param pageId 页面id
     * @param channelNo 渠道id
     * @return 页面埋点数据
     */
    DataStatisticsDto getBlankModuleStatistics(List<String> list , String pageId, String channelNo);
}
