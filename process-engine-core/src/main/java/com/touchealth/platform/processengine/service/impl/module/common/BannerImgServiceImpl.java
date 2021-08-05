package com.touchealth.platform.processengine.service.impl.module.common;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.touchealth.platform.processengine.dao.module.common.BannerImgDao;
import com.touchealth.platform.processengine.entity.module.common.BannerImg;
import com.touchealth.platform.processengine.exception.BusinessException;
import com.touchealth.platform.processengine.pojo.dto.module.common.BannerImgDto;
import com.touchealth.platform.processengine.pojo.request.module.common.BannerImgRequest;
import com.touchealth.platform.processengine.service.impl.BaseServiceImpl;
import com.touchealth.platform.processengine.service.module.common.BannerImgService;
import com.touchealth.platform.processengine.service.module.common.LinkService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 轮播图通用组件表之图片内容表 服务实现类
 * </p>
 *
 * @author Xxx
 * @since 2020-11-23
 */
@Service
public class BannerImgServiceImpl extends BaseServiceImpl<BannerImgDao, BannerImg> implements BannerImgService {

    @Resource
    private BannerImgDao bannerImgDao;

    @Resource
    private LinkService linkService;

    @Override
    public PageInfo<BannerImgDto> queryBannerImgList(BannerImgRequest request) {
        PageHelper.startPage(request.getPageNo(),request.getPageSize());
        List<BannerImgDto> bannerImgDtos =  bannerImgDao.queryBannerImgList(request);
        if(CollectionUtils.isEmpty(bannerImgDtos)) {
            return new PageInfo<>();
        }
        bannerImgDtos.forEach(b->{
            b.setComponentId(b.getBannerId());
            if(null != b.getLinkModuleId()){
                b.setLinkDto(linkService.findById(b.getLinkModuleId()));
            }
        });
        return new PageInfo<>(bannerImgDtos);
    }

    @Override
    public List<BannerImgDto> queryBannerImgByBannerId(Long bannerId, Integer showUnStart) {
        return bannerImgDao.queryBannerImgByBannerId(bannerId,showUnStart);
    }

    @Override
    public List<BannerImg> findByBannerId(Long bannerId) {
        if (null == bannerId) {
            throw new BusinessException("参数不能为空！");
        }
        return bannerImgDao.selectList(new QueryWrapper<BannerImg>().lambda().eq(BannerImg::getBannerId, bannerId));
    }
}
