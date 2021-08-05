package com.touchealth.platform.processengine.pojo.dto.module;

import lombok.Data;

import java.io.Serializable;

/**
 * 基础DTO
 */
@Data
public class BaseDto  {

    /**
     * 唯一标识
     */
    private Long id;
    /**
     * 版本间的唯一组件标识。同一个组件，不同版本该值不变。
     */
    private Long moduleUniqueId;

}
