package com.touchealth.platform.processengine.service.module.common;

import com.touchealth.platform.processengine.entity.module.common.HomeNav;
import com.touchealth.platform.processengine.entity.page.PlatformVersion;
import com.touchealth.platform.processengine.pojo.bo.WebJsonBo;
import com.touchealth.platform.processengine.pojo.dto.module.common.HomeNavDto;
import com.touchealth.platform.processengine.service.BaseService;

/**
 * <p>
 * 首页导航组件 服务类
 * </p>
 *
 * @author liqone
 * @since 2020-12-30
 */
public interface HomeNavService extends BaseService<HomeNav> {

    /**
     * 查询首页导航组件信息
     * @param homeNav 查询条件
     * @return 首页导航组件信息
     */
    HomeNavDto queryHomeNave(HomeNav homeNav);

    /**
     * 更新首页导航组件的配置信息
     * @param webJsonBo 配置信息
     * @param platformVersion 版本信息
     */
    WebJsonBo updateWebJson(WebJsonBo webJsonBo, PlatformVersion platformVersion);

    /**
     * 复制首页导航信息
     * @param oldVersion 旧的版本信息
     * @param newVersion 新的版本信息
     */
    void copyHomeNav(PlatformVersion oldVersion, PlatformVersion newVersion);
}
