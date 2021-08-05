package com.touchealth.platform.processengine.pojo.dto.page;

import lombok.Data;

import java.util.List;

/**
 * @program: process-engine
 * @description: 页面数据统计展示信息
 * @author: xianghy
 * @create: 2020/12/11
 **/
@Data
public class DataStatisticsDto {

    /**
     * 页面浏览次数
     */
    private String visitCount;

    /**
     * 页面浏览人数
     */
    private String visitPersonCount;

    /**
     * 页面平均停留时长
     */
    private String visitAvgTime;

    /**
     * 组件信息
     */
    private List<DataStatisticsModuleDto> moduleDtoList;

}
