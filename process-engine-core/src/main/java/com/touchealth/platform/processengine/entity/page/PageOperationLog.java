package com.touchealth.platform.processengine.entity.page;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.touchealth.platform.processengine.constant.PageCenterConsts;
import com.touchealth.platform.processengine.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author liufengqiang
 * @date 2020-11-24 15:49:25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("page_operation_log")
public class PageOperationLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 渠道号 */
    private String channelNo;
    /** 版本id */
    private Long versionId;
    /** 版本名 */
    private String versionName;
    /** 页面id */
    private Long pageId;
    /** 组件id */
    private Long moduleId;
    /** 文件夹名称 */
    private String folderName;
    /** 页面名称 */
    private String pageName;
    /** 操作号 同一个操作号认为是一组操作 */
    private String operationNo;

    /** 组合类型
     * @see PageCenterConsts.CombinationType */
    private Integer combinationType;
    /** 组件名称 */
    private String moduleName;
    /** 操作 */
    private String operate;
    /** 修改内容 */
    private String content;
    /** 修改名称 */
    private String name;
    /** 初始值 */
    private String initValue;
    /** 完成值 */
    private String finishValue;

    /** 拼接后的完整日志（不入库） */
    @TableField(exist = false)
    private String operationLog;
}
