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
public class BaseBoard implements Serializable {

    /**
     * ID
     */
    @Id
    private String id;

    /**
     * 上级渠道id 为0表示为顶级渠道
     */
    private Long parentId;

   /**
     * 父渠道名称
     */
    private String parentChannelName;

    /**
     * 平台code
     */
    private String channelNo;

    /**
     * 平台名称
     */
    private String channelName;

    /**
     * 时间
     */
    private Date date;

    /**
     * 时间格式
     */
    private String dateFormat;

    /**
     * 当日PV
     */
    private Long todayPv;

    /**
     * 当日UV
     */
    private Long todayUv;

    /**
     * 是否删除 true|是 false|否
     */
    private Boolean deleteFlag;

    /**
     * 创建时间
     */
    private Date createTime;
}
