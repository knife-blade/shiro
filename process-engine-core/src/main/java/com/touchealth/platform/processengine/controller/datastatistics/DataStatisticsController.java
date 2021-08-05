package com.touchealth.platform.processengine.controller.datastatistics;

import com.touchealth.platform.processengine.exception.ErrorMsgException;
import com.touchealth.platform.processengine.pojo.dto.page.DataStatisticsDto;
import com.touchealth.platform.processengine.pojo.request.datastatistics.BlankStatisticsRequest;
import com.touchealth.platform.processengine.service.datastatistics.DataStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * @program: process-engine
 * @author: xianghy
 * @create: 2020/11/23
 **/
@RestController
@RequestMapping("/data/statistics")
public class DataStatisticsController {

    @Autowired
    private DataStatisticsService dataStatisticsService;

    /**
     * 根据pageId 查询页码埋点相关数据
     * @param pageId 页码ID
     * @param userId 用户ID
     * @return 页码埋点数据
     */
    @GetMapping("{pageId}")
    public DataStatisticsDto getPageDataStatistics(@PathVariable("pageId") Long pageId, @RequestAttribute(name = "userId", required = false) Long userId) {
        return dataStatisticsService.getPageDataStatistics(pageId, userId);
    }

    /**
     * 查询页面空模块埋点相关数据
     * @param request 请求体
     * @return 页面埋点数据
     */
    @PostMapping("/blank")
    public DataStatisticsDto getBlankModuleStatistics(@RequestBody BlankStatisticsRequest request, @RequestHeader String channelNo) {
        if(CollectionUtils.isEmpty(request.getList())){
            throw new ErrorMsgException("入参不能为空");
        }
        ArrayList<String> list = (ArrayList<String>) request.getList().stream().filter(str -> str.indexOf('-') > 0).collect(Collectors.toList());
        return dataStatisticsService.getBlankModuleStatistics(list, request.getPageId(), channelNo);
    }

}
