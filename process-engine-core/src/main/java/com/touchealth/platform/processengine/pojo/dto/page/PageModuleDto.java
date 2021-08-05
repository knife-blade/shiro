package com.touchealth.platform.processengine.pojo.dto.page;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * @author liufengqiang
 * @date 2020-11-17 15:45:58
 */
@Data
public class PageModuleDto {

    private Long id;
    /** 组件类型 0.按钮 1.间隔 */
    private Integer moduleType;
    /** 前端页面配置信息 */
    private JSONObject webJson;
}
