package com.touchealth.platform.processengine.service.impl.module.common;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.touchealth.platform.processengine.constant.CommonConstant;
import com.touchealth.platform.processengine.dao.module.common.LinkDao;
import com.touchealth.platform.processengine.entity.module.common.Link;
import com.touchealth.platform.processengine.exception.CommonModuleException;
import com.touchealth.platform.processengine.pojo.dto.module.common.LinkDto;
import com.touchealth.platform.processengine.service.impl.BaseServiceImpl;
import com.touchealth.platform.processengine.service.module.common.LinkService;
import com.touchealth.platform.processengine.utils.BaseHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 链接通用组件表 服务实现类
 * </p>
 *
 * @author SunYang
 * @since 2020-11-16
 */
@Service
@Slf4j
public class LinkServiceImpl extends BaseServiceImpl<LinkDao, Link> implements LinkService {

    @Override
    public LinkDto save(LinkDto dto) {
        Link link = BaseHelper.r2t(dto, Link.class);
        link.setId(null);
        link.setCategoryId(CommonConstant.MODULE_CATEGORY.COMMON.getCode());
        boolean saveFlag = save(link);
        if (!saveFlag) {
            log.error("LinkServiceImpl.save link save fail. param: {}", dto);
            throw new CommonModuleException("添加链接失败");
        }
        dto.setId(link.getId());
        dto.setModuleUniqueId(link.getModuleUniqueId());
        return dto;
    }

    @Override
    public LinkDto update(LinkDto dto) {
        Long id = dto.getId();
        Link linkDb = getById(id);
        if (linkDb == null) {
            throw new CommonModuleException("链接不存在");
        }
        BaseHelper.copyNotNullProperties(dto, linkDb);
        boolean updFlag = updateById(linkDb);
        if (!updFlag) {
            log.error("LinkServiceImpl.update link update fail. param: {}", dto);
            throw new CommonModuleException("更新链接失败");
        }
        return findById(id);
    }

    @Override
    public LinkDto findById(Long id) {
        Link link = getById(id);
        if (link == null) {
            return null;
        }
        return BaseHelper.r2t(link, LinkDto.class);
    }

    @Override
    public List<LinkDto> findByIdList(List<Long> ids) {
        List<Link> links = listByIds(ids);
        if (CollectionUtils.isEmpty(links)) {
            return new ArrayList<>();
        }
        return links.stream().map(o -> BaseHelper.r2t(o, LinkDto.class)).collect(Collectors.toList());
    }

    @Override
    public Boolean delete(Long id) {
        return removeById(id);
    }

    @Override
    public Boolean delete(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return false;
        }
        return removeByIds(ids);
    }

    @Override
    public Integer countByVersionsAndToId(List<Long> versions, String targetShortId) {
        return new LambdaQueryChainWrapper<>(getBaseMapper())
                .in(Link::getVersion, versions)
                .eq(Link::getLinkToId, targetShortId)
                .count();
    }

}
