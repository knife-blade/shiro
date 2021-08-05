package com.touchealth.platform.processengine.service.impl.module.common;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.touchealth.platform.processengine.entity.module.common.ComboImgDetail;
import com.touchealth.platform.processengine.dao.module.common.ComboImgDetailDao;
import com.touchealth.platform.processengine.pojo.dto.module.common.ComboImgDetailDto;
import com.touchealth.platform.processengine.pojo.dto.module.common.NavigateImgDto;
import com.touchealth.platform.processengine.pojo.request.module.common.ComboImgDetailRequest;
import com.touchealth.platform.processengine.pojo.request.module.common.NavigateRequest;
import com.touchealth.platform.processengine.service.module.common.ComboImgDetailService;
import com.touchealth.platform.processengine.service.impl.BaseServiceImpl;
import com.touchealth.platform.processengine.service.module.common.LinkService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 组合图通用组件表之图片内容表 服务实现类
 * </p>
 *
 * @author LJH
 * @since 2020-11-30
 */
@Service
public class ComboImgDetailServiceImpl extends BaseServiceImpl<ComboImgDetailDao, ComboImgDetail> implements ComboImgDetailService {

    @Resource
    private ComboImgDetailDao comboImgDetailDao;

    @Resource
    private LinkService linkService;

    @Override
    public PageInfo<ComboImgDetailDto> queryList(ComboImgDetailRequest request) {
        PageHelper.startPage(request.getPageNo(),request.getPageSize());
        List<ComboImgDetailDto> comboImgDetailDtos = comboImgDetailDao.queryList(request);
        if(CollectionUtils.isEmpty(comboImgDetailDtos)){
            return new PageInfo<>();
        }
        // 获取链接
        comboImgDetailDtos.forEach(b->{
            b.setComponentId(b.getComboImgId());
            if(null != b.getLinkModuleId()){
                b.setLinkDto(linkService.findById(b.getLinkModuleId()));
            }
        });
        return new PageInfo<>(comboImgDetailDtos);
    }
}
