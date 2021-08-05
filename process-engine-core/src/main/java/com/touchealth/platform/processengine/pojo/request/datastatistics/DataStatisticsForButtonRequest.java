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
public class DataStatisticsForButtonRequest implements Serializable {

    /**
     * ID
     */
    private Long moduleId;

    public List<ButtonRequest> buttonList;
    
    @Data
    public static class ButtonRequest {

        /**
         * id
         */
        private Long id;

        /**
         * 曝光时间
         */
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
        private List<Date> showTime;

        /**
         * 点击时间
         */
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
        private List<Date> clickTime;
    }

}
