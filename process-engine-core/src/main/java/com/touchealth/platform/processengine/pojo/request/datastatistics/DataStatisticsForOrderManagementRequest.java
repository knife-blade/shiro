package com.touchealth.platform.processengine.pojo.request.datastatistics;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @program: 订单管理组件埋点请求参数
 * @author: lvx
 * @create: 2021/01/20
 **/
@Data
public class DataStatisticsForOrderManagementRequest implements Serializable {

    /**
     * ID
     */
    private Long moduleId;

    private List<DataStatisticsForImgRequest> imgList;
}
