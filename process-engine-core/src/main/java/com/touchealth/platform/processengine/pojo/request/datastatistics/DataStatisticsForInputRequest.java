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
public class DataStatisticsForInputRequest implements Serializable {

    /**
     * ID
     */
    private Long moduleId;

    private List<DetailRequest> inputList;

    @Data
    public static class DetailRequest {

        /**
         * 输入文本内容
         */
        private String text;

        /**
         * 时间
         */
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
        private Date time;

    }

}
