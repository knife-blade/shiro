package com.touchealth.platform.processengine.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.touchealth.platform.processengine.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author admin
 * @since 2020-10-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_example")
public class Example extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Integer type;

    private String content;


}
