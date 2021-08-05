package com.touchealth.platform.processengine.service.module.common;

import com.touchealth.platform.processengine.entity.module.common.Delimiter;
import com.touchealth.platform.processengine.pojo.dto.module.common.DelimiterDto;
import com.touchealth.platform.processengine.service.module.BaseModuleService;

import java.util.List;

/**
 * <p>
 * 分隔符通用组件表 服务类
 * </p>
 *
 * @author SunYang
 * @since 2020-11-16
 */
public interface DelimiterService extends BaseModuleService<Delimiter> {

    Boolean updateModuleStatus(Long moduleId, Integer status, Long versionId);

    DelimiterDto save(DelimiterDto dto);

    DelimiterDto update(DelimiterDto dto);

    DelimiterDto findById(Long id);

    List<DelimiterDto> findByIdList(List<Long> ids);

    Boolean delete(Long id);

    Boolean delete(List<Long> ids);
}
