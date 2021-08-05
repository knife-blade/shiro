package com.touchealth.platform.processengine.service.page;

import com.touchealth.platform.processengine.entity.page.PlatformVersion;
import com.touchealth.platform.processengine.pojo.dto.page.OperationLogDto;
import com.touchealth.platform.processengine.service.BaseService;

import java.util.List;

/**
 * @author liufengqiang
 * @date 2020-11-13 14:40:34
 */
public interface PlatformVersionService extends BaseService<PlatformVersion> {

    /**
     * 创建新版本
     * @param channelNo
     * @param lastVersionName
     * @return
     */
    PlatformVersion createNewVersion(String channelNo, String lastVersionName);

    /**
     * 版本发布
     *
     * @param userId
     * @param platformVersion
     * @return
     */
    PlatformVersion release(Long userId, PlatformVersion platformVersion);

    /**
     * 根据渠道号获取版本列表
     *
     * @param channelNo
     * @return
     */
    List<PlatformVersion> listByChannelNo(String channelNo);

    /**
     * 获取默认版本，当版本号为空时返回草稿版本
     * @param channelNo
     * @param versionId
     * @return
     */
    PlatformVersion getDefaultVersion(String channelNo, Long versionId);

    /**
     * 获取默认版本号，当版本号为空时返回草稿版本
     * @param channelNo
     * @param versionId
     * @return
     */
    Long getDefaultVersionId(String channelNo, Long versionId);

    /**
     * 根据版本名查询版本
     * @param channelNo
     * @param versionName
     * @return
     */
    PlatformVersion getByVersionName(String channelNo, String versionName);

    /**
     * 更新版本更新时间
     * @param versionId
     */
    void updateVersionTime(Long versionId);

    /**
     * 版本操作日志
     * @param channelNo
     * @param versionId
     * @return
     */
    OperationLogDto operationLog(String channelNo, Long versionId);
}
