package com.touchealth.platform.processengine.entity.page;

import com.baomidou.mybatisplus.annotation.TableName;
import com.touchealth.platform.processengine.constant.CommonConstant;
import com.touchealth.platform.processengine.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 页面-组件 关联关系
 *
 * @author liufengqiang
 * @date 2020-11-20 11:28:57
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("page_module")
public class PageModule extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 渠道号 */
    private String channelNo;
    /** 版本id */
    private Long versionId;
    /** 页面id */
    private Long pageId;
    /** 组件唯一id */
    private Long moduleUniqueId;
    /** 组件类型
     * @see CommonConstant.ModuleType */
    private Integer moduleType;
    /** 组件id */
    private Long moduleId;
    /** 排序 */
    private Long sortNo;
    /** 前端页面配置信息 */
    private String webJson;
    /** 操作人ids */
    private String operatorIds;
}
