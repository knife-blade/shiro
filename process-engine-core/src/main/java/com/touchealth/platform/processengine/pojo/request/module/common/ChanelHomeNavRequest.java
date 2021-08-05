package com.touchealth.platform.processengine.pojo.request.module.common;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * 查询渠道对应的首页导航组件参数
 */
@Data
public class ChanelHomeNavRequest {
    /**
     * 渠道编码
     */
    @NotBlank(message = "渠道号不能为空")
    String channelNo;

}
