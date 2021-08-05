package com.touchealth.platform.processengine.pojo.request.page;

import com.touchealth.platform.processengine.constant.CommonConstant;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author liufengqiang
 * @date 2020-11-17 15:05:49
 */
@Data
@NoArgsConstructor
public class PageModuleRequest {

    /**
     * 组件类型
     *
     * @see CommonConstant.ModuleType
     */
    private Integer moduleType;
    /**
     * 上个组件id
     */
    private Long lastId;
    /**
     * 前端页面配置信息
     */
    @NotBlank(message = "配置信息不能为空")
    private String webJson;

    public PageModuleRequest(Integer moduleType) {
        this.moduleType = moduleType;
    }
}
