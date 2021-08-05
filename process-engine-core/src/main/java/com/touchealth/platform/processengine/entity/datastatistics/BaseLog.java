package com.touchealth.platform.processengine.entity.datastatistics;

import lombok.Data;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * @program: process-engine
 * @author: xianghy
 * @create: 2020/11/23
 **/
@Data
public class BaseLog implements Serializable {

    /**
     * ID
     */
    @Id
    private String id;

    /**
     * 最小元素的id
     */
    private Long elementId;

    /**
     * 组成当前最小元素的uniqueId
     */
    private Long uniqueId;

    /**
     * user_board#uniqueId
     */
    private String userBoardUniqueId;

    /**
     * 渠道ID
     */
    private String channelNo;

    /**
     * 页面ID
     */
    private Long pageId;

    /**
     * 页面唯一ID
     */
    private String pageUniqueId;

    /**
     * 模块ID
     */
    private Long moduleId;

    /**
     * 模块uniqueID
     */
    private Long moduleUniqueId;

    /**
     * 模块类型，如：banner、button
     */
    private String moduleType;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 空模块元素唯一标识
     */
    private String blankUniqueId;
}
