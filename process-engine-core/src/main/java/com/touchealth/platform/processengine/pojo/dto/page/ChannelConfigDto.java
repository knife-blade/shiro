package com.touchealth.platform.processengine.pojo.dto.page;

import com.touchealth.common.basic.response.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author liufengqiang
 * @date 2021-04-15 14:53:14
 */
@Data
@AllArgsConstructor
public class ChannelConfigDto {

    /**
     * 医院数据展示规则 0.全部展示 1.黑名单 2.白名单
     */
    private Integer displayRules;
    /**
     * 数据分页列表
     */
    private PageInfo<ChannelConfigDataDto> dataPageIno;
}
