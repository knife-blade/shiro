package com.touchealth.platform.processengine.service.impl.module.common;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.touchealth.platform.processengine.dao.module.common.PageTemplateDao;
import com.touchealth.platform.processengine.entity.module.common.PageTemplate;
import com.touchealth.platform.processengine.service.impl.BaseServiceImpl;
import com.touchealth.platform.processengine.service.module.common.PageTemplateService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 页面配置模板 服务实现类
 * </p>
 *
 * @author liqone
 * @since 2020-12-30
 */
@Service
public class PageTemplateServiceImpl extends BaseServiceImpl<PageTemplateDao, PageTemplate> implements PageTemplateService {

    @Override
    public PageTemplate findByType(String type) {
        QueryWrapper<PageTemplate> queryWrapper = new QueryWrapper<>(new PageTemplate());
        queryWrapper.getEntity().setType(type);
        queryWrapper.orderByAsc("id");

        return this.getOne(queryWrapper, false);
    }

    @Override
    public List<PageTemplate> listByType(String type) {
        QueryWrapper<PageTemplate> queryWrapper = new QueryWrapper<>(new PageTemplate());
        queryWrapper.getEntity().setType(type);
        queryWrapper.orderByAsc("id");

        return this.list(queryWrapper);
    }
}
