package com.touchealth.platform.processengine.pojo.request.datastatistics;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
* @Author: zhangbin
* @Description: 空模块埋点入参
* @DateTime: 2021/7/13 17:27
*/
@Data
public class DataStatisticsForBlankRequest implements Serializable {

    /**
     * ID
     */
    private Long moduleId;

    private List<ChilrenRequest> chilrenlist;

    @Data
    public static class ChilrenRequest {

        /**
         * 元素id
         */
        private String id;

        /**
         * 时间
         */
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
        private List<Date> showTime;

        /**
         * 时间
         */
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
        private List<Date> clickTime;

    }

}
