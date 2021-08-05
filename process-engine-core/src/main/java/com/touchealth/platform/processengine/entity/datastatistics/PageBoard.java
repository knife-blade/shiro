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
@Document(collection = "page_board")
public class PageBoard extends BaseBoard implements Serializable {

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
     * 访问次数
     */
    private Long visitCounter;

    /**
     * 浏览人数
     */
    private Long visitUserCounter;

    /**
     * 浏览时长，时间戳
     */
    private Long visitTime;


    public PageBoard() {
    }
}
