package com.touchealth.platform.processengine.service.impl.module;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.touchealth.platform.processengine.pojo.bo.CompareBo;
import com.touchealth.platform.processengine.service.impl.BaseServiceImpl;
import com.touchealth.platform.processengine.service.module.BaseModuleService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 组件默认实现类
 * 所有组件必须继承本类，重写本类所有方法或者实现BaseModuleService接口
 * 继承本类好处是组件接口改了不会报错，项目能跑起来
 *
 * @author liufengqiang
 * @date 2020-12-02 11:08:48
 */
public abstract class BaseModuleServiceImpl<M extends BaseMapper<T>, T> extends BaseServiceImpl<M, T> implements BaseModuleService<T> {

    @Override
    public Boolean updateModuleStatus(Long moduleId, Integer status) {
        return null;
    }

    @Override
    public String findPageModuleById(Long id) {
        return null;
    }

    @Override
    public String getModuleById(Long id, String... param) {
        return null;
    }

    @Override
    public List<String> findPageModuleByIdList(List<Long> ids) {
        return null;
    }

    @Override
    public Boolean deletePageModule(Long id) {
        return false;
    }

    @Override
    public Boolean restoreModule(Collection<Long> ids, Long versionId) {
        return null;
    }

    @Override
    public Boolean deletePageModule(List<Long> ids) {
        return null;
    }

    @Override
    public Boolean restoreModule(List<Long> ids) {
        return null;
    }

    @Override
    public List<CompareBo> compare(String webJson, Long moduleId) {
        return new ArrayList<>();
    }
}
