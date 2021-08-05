package com.touchealth.platform.processengine.entity.datastatistics;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * @program: process-engine
 * @author: xianghy
 * @create: 2020/11/23
 **/
@Data
@Document(collection = "user_board")
public class UserBoard implements Serializable {

    /**
     * ID
     */
    @Id
    private String id;

    /**
     * 标识ID
     */
    @Indexed
    private String uniqueId;

    /**
     * 上级渠道id 为0表示为顶级渠道
     */
    private Long parentId;

    /**
     * 平台code
     */
    private String channelNo;

    /**
     * 平台名称
     */
    private String channelName;

    /**
     * 页面ID
     */
    private Long pageId;

    /**
     * 页面唯一ID
     */
    private String pageUniqueId;

    /**
     * 页面名称
     */
    private String pageName;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户token
     */
    private String userMark;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 身份类型
     */
    private Integer identityType;

    /**
     * 身份信息
     */
    private String identityNo;

    /**
     * 出生年月
     */
    private Date birthday;

    /**
     * 性别
     */
    private String sex;

    /**
     * IP
     */
    private String ipAddress;

    /**
     * 访问时间，时间戳
     */
    private Long visitStartTime;

    /**
     * 访问结束时间，毫秒值
     */
    private Long visitEndTime;

    /**
     * 访问时长 visitTime = visitEndTime - visitStartTime
     */
    private Long visitTime;

    /**
     * 浏览器信息
     */
    private String userAgent;

    /**
     * 跳转信息
     */
    private String referer;

    /**
     * 身份
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 区
     */
    private String district;

    /**
     * 是否删除 true|是 false|否
     */
    private Boolean deleteFlag;

    /**
     * 创建时间
     */
    private Date createTime;

    public UserBoard() {
    }
}
