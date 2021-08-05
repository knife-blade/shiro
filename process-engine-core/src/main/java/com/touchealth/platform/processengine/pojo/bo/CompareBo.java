package com.touchealth.platform.processengine.pojo.bo;

import com.touchealth.platform.processengine.constant.CommonConstant;
import com.touchealth.platform.processengine.constant.PageCenterConsts;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 页面或组件比对结果业务对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompareBo {

    /**
     * 页面ID
     */
    private Long pageId;
    /**
     * 页面名
     */
    private String pageName;
    /**
     * 组件ID
     */
    private Long moduleId;
    /**
     * 组件类型
     */
    private CommonConstant.ModuleType moduleType;
    /**
     * 修改对象（修改内容）
     */
    private String modifyName;
    /**
     * 操作。如：新增、删除、修改
     */
    private String modifyAction;
    /**
     * 修改内容。如：按钮样式、图片、链接
     */
    private String modifyActionContent;
    /**
     * 修改前的值
     */
    private String modifyBeforeValue;
    /**
     * 修改后的值
     */
    private String modifyAfterValue;
    /** 组合类型
     * @see PageCenterConsts.CombinationType */
    private Integer combinationType;

    @Deprecated
    public CompareBo(Long moduleId, CommonConstant.ModuleType moduleType, String modifyName, String modifyAction, String modifyBeforeValue, String modifyAfterValue) {
        this.moduleId = moduleId;
        this.moduleType = moduleType;
        this.modifyName = modifyName;
        this.modifyAction = modifyAction;
        this.modifyBeforeValue = modifyBeforeValue;
        this.modifyAfterValue = modifyAfterValue;
    }

    public CompareBo(Long moduleId, CommonConstant.ModuleType moduleType, String modifyName, String modifyAction, String modifyActionContent, String modifyBeforeValue, String modifyAfterValue) {
        this.moduleId = moduleId;
        this.moduleType = moduleType;
        this.modifyName = modifyName;
        this.modifyAction = modifyAction;
        this.modifyActionContent = modifyActionContent;
        this.modifyBeforeValue = modifyBeforeValue;
        this.modifyAfterValue = modifyAfterValue;
    }

}
