package com.touchealth.platform.processengine.entity.datastatistics;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @program: process-engine
 * @author: xianghy
 * @create: 2020/11/23
 **/
@Data
@Document(collection = "module_board")
public class ModuleBoard extends BaseBoard implements Serializable {

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
     * 模块ID
     */
    private Long moduleId;

    /**
     * 模块uniqueID
     */
    private Long moduleUniqueId;

    /**
     * 模块类型key
     */
    private String moduleTypeKey;

    /**
     * 模块名称
     */
    private String moduleName;

}
