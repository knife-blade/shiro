package com.touchealth.platform.processengine.task.datastatistics;

import com.touchealth.platform.processengine.constant.RedisConstant;
import com.touchealth.platform.processengine.entity.datastatistics.PlatformChannelBoard;
import com.touchealth.platform.processengine.entity.page.PlatformChannel;
import com.touchealth.platform.processengine.service.common.RedisService;
import com.touchealth.platform.processengine.service.datastatistics.ModuleClickTimeLogService;
import com.touchealth.platform.processengine.service.datastatistics.PlatformChannelBoardService;
import com.touchealth.platform.processengine.service.page.PlatformChannelService;
import com.touchealth.platform.processengine.utils.DateUtil;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import io.micrometer.core.instrument.binder.BaseUnits;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @program: process-engine
 * @author: xianghy
 * @create: 2020/12/23
 **/
@Slf4j
@Component
public class DataStatisticsTask {

    @Autowired
    private PlatformChannelService platformChannelService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private PlatformChannelBoardService platformChannelBoardService;

    @Autowired
    private ModuleClickTimeLogService moduleClickTimeLogService;

    /**
     * 统计前一日的相关数据
     * @param param
     * @return
     */
    @XxlJob("statisticsEveryDayData")
    public ReturnT<String> statisticsEveryDayData(String param) {

        // 获取渠道信息
        List<PlatformChannel> platformChannelList = platformChannelService.listByChannelName(null);
        if (CollectionUtils.isEmpty(platformChannelList)) {
            log.info("statisticsEveryDayData platformChannelList is empty");
            return ReturnT.SUCCESS;
        }
        Map<Long, String> nameMap = platformChannelList.stream().collect(Collectors.toMap(PlatformChannel::getId, PlatformChannel::getChannelName));

        // 获取对应平台下的页面信息
        String channelCode;
        PlatformChannelBoard channelBoard;
        Date today = new Date();
        Date yesterday = DateUtil.add(new Date(), -1);
        String dateStr = DateUtil.dateTimeToStr(today, DateUtil.DATE_DAY);
        List<PlatformChannelBoard> channelBoardList = new ArrayList<>(platformChannelList.size());
        for (PlatformChannel platformChannel : platformChannelList) {
            channelBoard = new PlatformChannelBoard();

            channelCode = platformChannel.getChannelNo();
            BeanUtils.copyProperties(platformChannel, channelBoard);

            channelBoard.setDate(yesterday);
            channelBoard.setCreateTime(today);
            channelBoard.setDeleteFlag(false);
            channelBoard.setDateFormat(DateUtil.SQL_DATE);
            channelBoard.setParentId(platformChannel.getParentId() == null ? 0L : platformChannel.getParentId());

            if (null != platformChannel.getParentId()) {
                channelBoard.setParentChannelName(nameMap.get(platformChannel.getParentId()));
            }

            // 当日UV(当日访问人数)
            long count = redisService.pfcount(String.format(RedisConstant.DATA_STATISTICS_PLATFORM_CHANNEL_EVERY_DAY_PERSON_TIMES, channelCode, dateStr));
            channelBoard.setTodayUv(count);

            // 昨日新增人数
            Integer personCount = redisService.hget(String.format(RedisConstant.DATA_STATISTICS_PLATFORM_CHANNEL_NEW_VISIT_PERSON, dateStr), channelCode);
            channelBoard.setTodayAddUserNumber(personCount == null ? 0L : personCount);

            // 当日PV
            count = moduleClickTimeLogService.countByChannelNo(channelCode);
            channelBoard.setTodayPv(count);
            channelBoardList.add(channelBoard);
        }
        platformChannelBoardService.save(channelBoardList);

        XxlJobLogger.log("DataStatisticsTask.statisticsEveryDayData run success");
        return ReturnT.SUCCESS;
    }

}
