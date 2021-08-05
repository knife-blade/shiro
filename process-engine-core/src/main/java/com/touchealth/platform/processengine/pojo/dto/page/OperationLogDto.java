package com.touchealth.platform.processengine.pojo.dto.page;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author liufengqiang
 * @date 2020-11-17 14:16:44
 */
@Data
public class OperationLogDto {

    /** 版本名 */
    private String versionName;
    /** 版本日志 */
    private List<LogDto> logs;

    @Data
    public static class LogDto {

        /** 头像 */
        private String avatar;
        /** 用户名 */
        private String realName;
        /** 更新时间 */
        private LocalDateTime createdTime;
        /** 修改页面 */
        private String updatedPage;
        /** 修改内容 */
        private List<VersionOperationLogDto> updatedContents;
    }
}
