package com.touchealth.platform.processengine.service.module.common;

import com.touchealth.platform.processengine.entity.module.common.PageTemplate;
import com.touchealth.platform.processengine.service.BaseService;

import java.util.List;

/**
 * <p>
 * 页面配置模板 服务类
 * </p>
 *
 * @author liqone
 * @since 2020-12-30
 */
public interface PageTemplateService extends BaseService<PageTemplate> {

    /**
     * 根据类型查询模版
     * @param type 类型
     * @return 模版配置数据
     */
    PageTemplate findByType(String type);

    /**
     * 根据类型获取模版列表
     * @param type
     * @return
     */
    List<PageTemplate> listByType(String type);
}
