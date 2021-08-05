package com.touchealth.platform.processengine.pojo.request.datastatistics;


import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
* @Author: zhangbin
* @Description: 空模块获取埋点数据请求对象
* @DateTime: 2021/7/19 15:13
*/
@Data
public class BlankStatisticsRequest implements Serializable {
    /**
     * 页面ID
     */
    private String pageId;

    /**
     * 页面唯一ID
     */
    private List<String> list;
}
