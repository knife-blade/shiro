package com.touchealth.platform.processengine.dao.page;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.touchealth.platform.processengine.entity.page.PageManager;
import com.touchealth.platform.processengine.pojo.query.PageManagerQuery;

import java.util.List;

/**
 * @author liufengqiang
 * @date 2020-11-18 15:27:38
 */
public interface PageManagerDao extends BaseMapper<PageManager> {

    /**
     * 连表查询页面
     * @param query
     * @return
     */
    List<PageManager> listByModuleType(PageManagerQuery query);
}
