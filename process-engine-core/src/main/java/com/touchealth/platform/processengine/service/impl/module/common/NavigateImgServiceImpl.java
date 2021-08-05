package com.touchealth.platform.processengine.service.impl.module.common;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.touchealth.platform.processengine.constant.CommonConstant;
import com.touchealth.platform.processengine.entity.module.common.NavigateImg;
import com.touchealth.platform.processengine.dao.module.common.NavigateImgDao;
import com.touchealth.platform.processengine.exception.BusinessException;
import com.touchealth.platform.processengine.pojo.dto.module.common.NavigateImgDto;
import com.touchealth.platform.processengine.pojo.request.module.common.NavigateRequest;
import com.touchealth.platform.processengine.service.module.common.LinkService;
import com.touchealth.platform.processengine.service.module.common.NavigateImgService;
import com.touchealth.platform.processengine.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 坑位导航通用组件表之图片内容表 服务实现类
 * </p>
 *
 * @author LJH
 * @since 2020-11-26
 */
@Service
public class NavigateImgServiceImpl extends BaseServiceImpl<NavigateImgDao, NavigateImg> implements NavigateImgService {

    @Resource
    private NavigateImgDao navigateImgDao;

    @Resource
    private LinkService linkService;

    @Override
    public PageInfo<NavigateImgDto> queryList(NavigateRequest request) {
        PageHelper.startPage(request.getPageNo(),request.getPageSize());
        List<NavigateImgDto> navigateImgDtos = navigateImgDao.queryList(request);
        if(CollectionUtils.isEmpty(navigateImgDtos)){
            return new PageInfo<>();
        }
        // 获取链接
        navigateImgDtos.forEach(b->{
            b.setComponentId(b.getNavigateId());
            if(null != b.getLinkModuleId()){
                b.setLinkDto(linkService.findById(b.getLinkModuleId()));
            }
        });
        return new PageInfo<>(navigateImgDtos);
    }

    @Override
    public List<NavigateImg> findByNavigateId(Long navigateId) {
        if (null == navigateId) {
            throw new BusinessException("参数不能为空！");
        }
        NavigateImg navigateImg = new NavigateImg();
        navigateImg.setDeletedFlag(CommonConstant.IS_NOT_DELETE);
        navigateImg.setNavigateId(navigateId);
        return baseFindList(navigateImg);
    }
}
