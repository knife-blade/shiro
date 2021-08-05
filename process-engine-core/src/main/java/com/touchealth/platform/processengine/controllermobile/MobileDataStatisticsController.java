package com.touchealth.platform.processengine.controllermobile;

import com.touchealth.platform.processengine.annotation.PassToken;
import com.touchealth.platform.processengine.pojo.request.datastatistics.DataStatisticsAddRequest;
import com.touchealth.platform.processengine.service.datastatistics.DataStatisticsService;
import com.touchealth.platform.processengine.utils.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/mobile/data/statistics")
public class MobileDataStatisticsController {

    @Autowired
    private DataStatisticsService dataStatisticsService;

    @PassToken
    @PutMapping("/add")
    public Boolean addDataStatistics(@RequestBody DataStatisticsAddRequest request,
                                     @RequestAttribute(name = "userId", required = false) Long userId,
                                     @RequestHeader String channelNo,
                                     HttpServletRequest servletRequest) {
        request.setUserId(userId);
        String ipAddress = IpUtil.getIpAddress(servletRequest);
        request.setIpAddress(ipAddress);
        request.setChannelNo(channelNo);
        dataStatisticsService.addDataStatistics(request);
        return true;
    }
}
