package com.touchealth.platform.processengine.pojo.dto.page;

import lombok.Data;

import java.util.List;

/**
 * @author liufengqiang
 * @date 2020-12-14 14:18:23
 */
@Data
public class RecycleBinDto {

    /** 版本id */
    private Long versionId;
    /** 版本名 */
    private String versionName;
    /** 页面列表 */
    private List<PageManagerDto> pageList;
}
