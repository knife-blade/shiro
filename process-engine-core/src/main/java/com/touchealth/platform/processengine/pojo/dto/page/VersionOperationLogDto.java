package com.touchealth.platform.processengine.pojo.dto.page;

import lombok.Data;

import java.util.List;

/**
 * @author liufengqiang
 * @date 2021-01-18 16:54:50
 */
@Data
public class VersionOperationLogDto {

    /** 日志内容 */
    private String content;
    /** 日志参数 */
    private String param1;
    /** 日志参数 */
    private String param2;
    /** 操作人头像 */
    private List<String> avatars;
}
