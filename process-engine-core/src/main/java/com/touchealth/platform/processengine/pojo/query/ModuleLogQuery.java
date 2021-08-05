package com.touchealth.platform.processengine.pojo.query;

import com.touchealth.platform.processengine.constant.PageCenterConsts;
import com.touchealth.platform.processengine.entity.page.PageManager;
import com.touchealth.platform.processengine.entity.page.PageModule;
import com.touchealth.platform.processengine.pojo.bo.CompareBo;
import lombok.Data;

import java.util.List;

/**
 * @author liufengqiang
 * @date 2021-01-04 17:32:49
 */
@Data
public class ModuleLogQuery {

    private PageModule pageModule;
    private PageManager pageManager;
    private String folderName;

    private Long userId;
    private PageCenterConsts.LogOperate operate;
    private List<CompareBo> compareBos;
}
