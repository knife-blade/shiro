package com.touchealth.platform.processengine.pojo.dto.page;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 渠道配置数据
 *
 * @author liufengqiang
 * @date 2021-04-10 10:53:40
 */
@Data
@AllArgsConstructor
public class ChannelConfigDataDto {

    private Long id;
    /**
     * 数据编码
     */
    private String dataNo;
    /**
     * 数据名称
     */
    private String dataName;
    /**
     * 数据来源
     */
    private String dataSource;

    public ChannelConfigDataDto(Long id, String dataNo, String dataName) {
        this.id = id;
        this.dataNo = dataNo;
        this.dataName = dataName;
    }
}
