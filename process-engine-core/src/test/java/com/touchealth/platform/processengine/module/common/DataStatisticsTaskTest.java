package com.touchealth.platform.processengine.module.common;

import com.touchealth.platform.processengine.BaseTest;
import com.touchealth.platform.processengine.task.datastatistics.DataStatisticsTask;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @program: process-engine
 * @author: xianghy
 * @create: 2021/1/6
 **/
public class DataStatisticsTaskTest extends BaseTest {

    @Autowired
    private DataStatisticsTask dataStatisticsTask;

    @Test
    public void statisticsEveryDayData() {
        dataStatisticsTask.statisticsEveryDayData(null);
    }

}
