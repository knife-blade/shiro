package com.touchealth.platform.processengine.entity.page;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.touchealth.platform.processengine.constant.CommonConstant;
import com.touchealth.platform.processengine.constant.PageCenterConsts;
import com.touchealth.platform.processengine.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author liufengqiang
 * @date 2020-11-18 15:12:50
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("page_manager")
public class PageManager extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 渠道号
     */
    private String channelNo;
    /**
     * 版本id
     */
    private Long versionId;
    /**
     * 页面名称
     */
    private String pageName;
    /**
     * 业务类型
     *
     * @see PageCenterConsts.BusinessType
     */
    private Integer businessType;
    /**
     * 所属文件夹
     */
    private Long folderId;
    /**
     * 是否是文件夹
     */
    private Boolean isFolder;
    /**
     * 发布状态
     *
     * @see CommonConstant.STATUS
     */
    private Integer status;
    /**
     * 页面标识 相同页面不同版本使用同一个值
     */
    private String pageUniqueId;
    /**
     * 排序
     */
    private Long sortNo;
    /**
     * 改动状态 0.新增 1.改动 2.未改动（发布后） 3.删除
     *
     * @see PageCenterConsts.ChangerStatus
     */
    private Integer changeStatus;
    /**
     * 老改动状态 0.新增 1.改动 2.未改动（发布后） 3.删除
     *
     * @see PageCenterConsts.ChangerStatus
     */
    private Integer oldChangeStatus;
    /**
     * 页面标识
     */
    private String pageTags;
    /**
     * 操作人ids
     */
    private String operatorIds;
    /**
     * 路由名称
     */
    private String routerName;
    /**
     * 来源id
     */
    @TableField(exist = false)
    private Long sourceId;
    /**
     * 是否支持模板配置：默认0不支持
     */
    private Boolean isSupportTemplate;
    /**
     * 是否是登录组件；0|否 1|是
     */
    private Boolean isSign;
    /**
     * 页面类型 0.入口页面 1.非入口页面（不展示在页面列表）
     */
    private Integer pageType;
    /**
     * 是否禁止修改页面【前端用】
     */
    private Boolean disableEdit;
    /**
     * 是否隐藏【没勾选的业务需要隐藏】 false.不隐藏 true.隐藏
     */
    private Boolean isHide;
}
