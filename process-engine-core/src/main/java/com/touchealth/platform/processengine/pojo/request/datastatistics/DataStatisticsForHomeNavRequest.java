package com.touchealth.platform.processengine.pojo.request.datastatistics;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @program: 首页导航组件埋点请求参数
 * @author: liqone
 * @create: 2021/01/20
 **/
@Data
public class DataStatisticsForHomeNavRequest implements Serializable {

    /**
     * ID
     */
    private Long moduleId;

    private List<DataStatisticsForImgRequest> imgList;
}
