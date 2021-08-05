package com.touchealth.platform.processengine.pojo.request.page;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author liufengqiang
 * @date 2021-04-07 11:11:31
 */
@Data
public class PlatformChannelConfigRequest {

    /**
     * 渠道配置类型 0.医院数据 1.套餐数据 2.用户互通
     */
    @NotNull(message = "渠道配置类型不能为空")
    private Integer configType;
    /**
     * 医院数据展示规则 0.全部展示 1.黑名单 2.白名单
     */
    private Integer displayRules;
    /**
     * 数据id列表
     */
    private List<Long> dataIds;
}
