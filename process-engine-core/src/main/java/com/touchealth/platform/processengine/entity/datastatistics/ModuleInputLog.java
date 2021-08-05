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
@Document(collection = "module_input_log")
public class ModuleInputLog extends BaseLog implements Serializable {

    /**
     * 比率
     * 如：1/5
     */
    private String ratio;

    private Long time;

    public ModuleInputLog() {
    }

    public ModuleInputLog(String ratio, Long time) {
        this.ratio = ratio;
        this.time = time;
    }
}
