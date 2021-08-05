package com.touchealth.platform.processengine.pojo.request.datastatistics;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @program: 我的模块组件埋点请求参数
 * @author: lvx
 * @create: 2021/01/20
 **/
@Data
public class DataStatisticsForMyModRequest implements Serializable {

    /**
     * ID
     */
    private Long moduleId;

    private List<DataStatisticsForImgRequest> imgList;
}
