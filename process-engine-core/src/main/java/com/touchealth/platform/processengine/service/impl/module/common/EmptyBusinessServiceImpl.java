package com.touchealth.platform.processengine.service.impl.module.common;

import com.touchealth.platform.processengine.dao.module.common.EmptyBusinessDao;
import com.touchealth.platform.processengine.service.impl.module.BaseModuleServiceImpl;
import com.touchealth.platform.processengine.service.module.common.EmptyBusinessService;
import org.springframework.stereotype.Service;

/**
 * @author liufengqiang
 * @date 2021-07-07 14:35:50
 */
@Service
public class EmptyBusinessServiceImpl extends BaseModuleServiceImpl<EmptyBusinessDao, Object> implements EmptyBusinessService {

    @Override
    public String savePageModule(String webJson, Long pageId) {
        return null;
    }

    @Override
    public String clonePageModule(Long moduleId, Long pageId) {
        return null;
    }

    @Override
    public String updatePageModule(String webJson) {
        return null;
    }
}
