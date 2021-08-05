package com.touchealth.platform.processengine.pojo.query;

import com.touchealth.platform.processengine.constant.CommonConstant;
import lombok.Data;

/**
 * @author liufengqiang
 * @date 2020-11-23 10:52:23
 */
@Data
public class PageManagerQuery {

    /**
     * 渠道号
     */
    private String channelNo;
    /**
     * 版本id
     */
    private Long versionId;
    /**
     * 页面类型
     */
    private Integer businessType;
    /**
     * 查询类型 0/空.默认 1.仅显示改动项
     */
    private Integer queryType;
    /**
     * 是否是文件夹
     */
    private Boolean isFolder;
    /**
     * 排序 0.sortNo 1.updatedTime
     */
    private Integer orderBy;
    /**
     * 是否删除（回收站） true 查询删除数据 false 查询未删除数据 null 查询所有数据
     */
    private Boolean isDeleted = false;
    /**
     * 组件类型
     *
     * @see CommonConstant.ModuleType
     */
    private Integer moduleType;
    /**
     * 页面类型 0.默认 1.跳链（不展示在页面列表）
     */
    private Integer pageType;
    /**
     * 是否隐藏【没勾选的业务需要隐藏】 0.不隐藏 1.隐藏
     */
    private Integer isHide;
}
