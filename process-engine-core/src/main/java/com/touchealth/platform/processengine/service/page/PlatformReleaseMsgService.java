package com.touchealth.platform.processengine.service.page;

import com.touchealth.platform.processengine.entity.page.PlatformReleaseMsg;
import com.touchealth.platform.processengine.service.BaseService;

import java.util.Collection;

/**
 * 发布审批消息
 * @author liufengqiang
 * @date 2021-01-25 14:42:37
 */
public interface PlatformReleaseMsgService extends BaseService<PlatformReleaseMsg> {

    /**
     * 新增消息
     * @param platformReleaseMsgs
     */
    void saveMsg(Collection<PlatformReleaseMsg> platformReleaseMsgs);
}
