package com.touchealth.platform.processengine.service.module.common;

import com.touchealth.platform.processengine.entity.module.common.Link;
import com.touchealth.platform.processengine.pojo.dto.module.common.LinkDto;
import com.touchealth.platform.processengine.service.BaseService;

import java.util.List;

/**
 * <p>
 * 链接通用组件表 服务类
 * </p>
 *
 * @author SunYang
 * @since 2020-11-16
 */
public interface LinkService extends BaseService<Link> {

    LinkDto save(LinkDto dto);

    LinkDto update(LinkDto dto);

    LinkDto findById(Long id);

    List<LinkDto> findByIdList(List<Long> ids);

    Boolean delete(Long id);

    Boolean delete(List<Long> ids);

    /**
     * 根据版本号列表和链接目标ID（短的）查询记录数量
     * @param versions      版本集合
     * @param targetShortId 短的跳转目标ID
     * @return
     */
    Integer countByVersionsAndToId(List<Long> versions, String targetShortId);
}
