package com.touchealth.platform.processengine.pojo.request.datastatistics;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @program: process-engine
 * @author: xianghy
 * @create: 2020/11/23
 **/
@Data
public class DataStatisticsForNavigateRequest implements Serializable {

    private Long moduleId;

    private List<DataStatisticsForImgRequest> imgList;

}
