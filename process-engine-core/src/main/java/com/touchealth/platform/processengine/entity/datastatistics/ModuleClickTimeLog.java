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
@Document(collection = "module_click_time_log")
public class ModuleClickTimeLog extends BaseLog implements Serializable {

    /**
     * 点击时间，时间戳
     */
    private Long clickTime;

    public ModuleClickTimeLog() {
    }

    public ModuleClickTimeLog(Long clickTime) {
        this.clickTime = clickTime;
    }
}
