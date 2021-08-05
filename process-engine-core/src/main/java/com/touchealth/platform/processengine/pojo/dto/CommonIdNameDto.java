package com.touchealth.platform.processengine.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 通用id、name返回实体
 *
 * @author liufengqiang
 * @date 2020-12-01 15:41:38
 */
@Data
@AllArgsConstructor
public class CommonIdNameDto {

    private Long id;
    private String name;
}
