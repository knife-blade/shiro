package com.touchealth.platform.processengine.pojo.dto.channel;

import com.touchealth.platform.processengine.pojo.dto.page.VersionOperationLogDto;
import lombok.Data;

import java.util.List;

/**
 * @author liufengqiang
 * @date 2020-11-17 14:03:52
 */
@Data
public class PlatformVersionDetailsDto {

    /** 改动数量 */
    private Integer updateNum;
    /** 改动涉及人员 */
    private List<String> realNames;

    /** 新增页面 */
    private List<VersionDto> addPages;
    /** 改动页面 */
    private List<VersionDto> updatePages;
    /** 删除页面 */
    private List<VersionDto> deletePages;

    @Data
    public static class VersionDto {

        /** 页面名称 */
        private String pageName;
        /** 操作人头像 */
        private List<String> avatars;
        /** 操作记录 */
        private List<VersionOperationLogDto> operationLogs;
    }
}
