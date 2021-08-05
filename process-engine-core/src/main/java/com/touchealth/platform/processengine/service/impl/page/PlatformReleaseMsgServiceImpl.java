package com.touchealth.platform.processengine.service.impl.page;

import com.touchealth.platform.processengine.dao.page.PlatformReleaseMsgDao;
import com.touchealth.platform.processengine.entity.page.PlatformReleaseMsg;
import com.touchealth.platform.processengine.service.impl.BaseServiceImpl;
import com.touchealth.platform.processengine.service.page.PlatformReleaseMsgService;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * @author liufengqiang
 * @date 2021-01-25 15:10:22
 */
@Service
public class PlatformReleaseMsgServiceImpl extends BaseServiceImpl<PlatformReleaseMsgDao, PlatformReleaseMsg> implements PlatformReleaseMsgService {

    @Override
    public void saveMsg(Collection<PlatformReleaseMsg> platformReleaseMsgs) {
        saveBatch(platformReleaseMsgs);
    }
}
