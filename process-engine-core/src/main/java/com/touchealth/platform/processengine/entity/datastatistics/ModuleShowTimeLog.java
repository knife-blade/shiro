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
@Document(collection = "module_show_time_log")
public class ModuleShowTimeLog extends BaseLog implements Serializable {

    /**
     * 时间戳
     */
    private Long time;

    public ModuleShowTimeLog() {
    }

    public ModuleShowTimeLog(Long time) {
        this.time = time;
    }
}
