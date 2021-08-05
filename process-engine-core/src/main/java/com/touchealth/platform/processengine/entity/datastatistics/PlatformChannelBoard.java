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
@Document(collection = "platform_channel_board")
public class PlatformChannelBoard extends BaseBoard implements Serializable {

    /**
     * 今日新增用户
     */
    private Long todayAddUserNumber;

}
