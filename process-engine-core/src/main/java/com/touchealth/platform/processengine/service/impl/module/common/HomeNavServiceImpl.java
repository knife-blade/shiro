package com.touchealth.platform.processengine.service.impl.module.common;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.touchealth.platform.processengine.annotation.TransactionalForException;
import com.touchealth.platform.processengine.constant.CommonConstant;
import com.touchealth.platform.processengine.dao.module.common.HomeNavDao;
import com.touchealth.platform.processengine.entity.module.common.HomeNav;
import com.touchealth.platform.processengine.entity.module.common.HomeNavImg;
import com.touchealth.platform.processengine.entity.page.PlatformVersion;
import com.touchealth.platform.processengine.handler.ModuleHandler;
import com.touchealth.platform.processengine.pojo.bo.WebJsonBo;
import com.touchealth.platform.processengine.pojo.dto.module.common.HomeNavDto;
import com.touchealth.platform.processengine.pojo.dto.module.common.LinkDto;
import com.touchealth.platform.processengine.service.impl.BaseServiceImpl;
import com.touchealth.platform.processengine.service.module.common.HomeNavImgService;
import com.touchealth.platform.processengine.service.module.common.HomeNavService;
import com.touchealth.platform.processengine.service.module.common.LinkService;
import com.touchealth.platform.processengine.service.page.PageManagerService;
import com.touchealth.platform.processengine.utils.BaseHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 首页导航组件 服务实现类
 * </p>
 *
 * @author liqone
 * @since 2020-12-30
 */
@Service
public class HomeNavServiceImpl extends BaseServiceImpl<HomeNavDao, HomeNav> implements HomeNavService {

    @Resource
    private LinkService linkService;

    @Resource
    private HomeNavImgService homeNavImgService;

    @Resource
    private PageManagerService pageManagerService;

    @Override
    public HomeNavDto queryHomeNave(HomeNav navQuery) {
        Assert.notNull(navQuery,"非法的查询参数");
        QueryWrapper<HomeNav> queryWrapper = new QueryWrapper(navQuery);
        HomeNav homeNav = super.getOne(queryWrapper);
        HomeNavDto homeNavDto = BaseHelper.r2t(homeNav, HomeNavDto.class);
        homeNavDto.setWebJson(JSONObject.parseObject(homeNav.getWebJson(),WebJsonBo.class));
        return homeNavDto;
    }

    @Override
    @TransactionalForException
    public WebJsonBo updateWebJson(WebJsonBo webJsonBo,PlatformVersion platformVersion) {
        Assert.notNull(webJsonBo,"配置信息不能为空");
        HomeNav homeNav = null;
        Long id = webJsonBo.getId();
        if(id!=null){
            //更新webjson及组件配置
            homeNav = super.getById(id);
            Assert.notNull(homeNav,"导航组件信息不存在");
        }else{
            Assert.notNull(platformVersion,"版本信息不存在");
            //新建homenav组件并且初始化配置
            homeNav = new HomeNav();
            id = IdWorker.getId(homeNav);
            homeNav.setId(id);
            if(webJsonBo.getModuleUniqueId()!=null){
                homeNav.setModuleUniqueId(webJsonBo.getModuleUniqueId());
            }else{
                homeNav.setModuleUniqueId(id);
            }
            homeNav.setChannelNo(platformVersion.getChannelNo());
            homeNav.setCategoryId(CommonConstant.MODULE_CATEGORY.COMMON.getCode());
            homeNav.setVersion(platformVersion.getId());
            webJsonBo.setId(id);
            webJsonBo.setModuleUniqueId(homeNav.getModuleUniqueId());
        }
        //解析对应的图片组件信息
        WebJsonBo.WebJsonDataBo data = webJsonBo.getData();
        if(data!=null){
            List<WebJsonBo.WebJsonImgBo> imgList = data.getImgList();
            if(CollectionUtils.isNotEmpty(imgList)){
                for (WebJsonBo.WebJsonImgBo webJsonImgBo : imgList) {
                    HomeNavImg homeNavImg = parseWebJsonImg(webJsonImgBo);
                    WebJsonBo.WebJsonLinkBo link = webJsonImgBo.getLink();
                    if(link!=null){
                        LinkDto linkDto = new LinkDto();
                        ModuleHandler.wrapLinkDto(link,linkDto);
                        //如果没有id，新增id
                        if(linkDto.getId()==null){
                            linkDto.setChannelNo(homeNav.getChannelNo());
                            //TODO 这里需要考虑是否需要首页导航根据页面进行状态的变更
                            linkDto.setStatus(CommonConstant.STATUS.PUBLISHED.getCode());
                            linkDto.setVersion(homeNav.getVersion());
                            //链接归属于首页导航，并不属于页面
                            linkDto.setPageId(-1L);
                            linkDto = linkService.save(linkDto);
                            link.setId(linkDto.getId());
                            homeNavImg.setLinkModuleId(linkDto.getId());
                        }else{
                            linkService.update(linkDto);
                        }
                    }
                    if(homeNavImg.getId()==null){
                        homeNavImg.setChannelNo(homeNav.getChannelNo());
                        homeNavImg.setVersion(homeNav.getVersion());
                        homeNavImg.setHomeNavId(id);
                        homeNavImgService.save(homeNavImg);
                        webJsonImgBo.setId(homeNavImg.getId());
                        webJsonImgBo.setModuleUniqueId(homeNavImg.getModuleUniqueId());
                    }
                }
            }
        }
        homeNav.setWebJson(JSONObject.toJSONString(webJsonBo));
        boolean flg = super.saveOrUpdate(homeNav);
        Assert.isTrue(flg,"更新失败");
        return webJsonBo;
    }

    /**
     * WebJsonBo.WebJsonImgBo 转为 HomeNavImg对象
     * @param webJsonImgBo img
     * @return
     */
    private HomeNavImg parseWebJsonImg(WebJsonBo.WebJsonImgBo webJsonImgBo){
        HomeNavImg homeNavImg = new HomeNavImg();
        homeNavImg.setId(webJsonImgBo.getId());
        homeNavImg.setModuleUniqueId(webJsonImgBo.getModuleUniqueId());
        homeNavImg.setName(webJsonImgBo.getTitle());
        homeNavImg.setUrl(webJsonImgBo.getImgUrl());
        return homeNavImg;
    }

    @Override
    public void copyHomeNav(PlatformVersion oldVersion, PlatformVersion newVersion) {
        Assert.notNull(oldVersion,"旧版本信息不能为空");
        Assert.notNull(newVersion,"新版本信息不能为空");
        QueryWrapper<HomeNav> queryWrapper = new QueryWrapper(new HomeNav());
        queryWrapper.getEntity().setChannelNo(oldVersion.getChannelNo());
        queryWrapper.getEntity().setVersion(oldVersion.getId());
        HomeNav homeNav = super.getOne(queryWrapper);
        Optional.ofNullable(homeNav).ifPresent(oldHomeNav->{
//            HomeNav newHomeNav = new HomeNav();
//            newHomeNav.setModuleUniqueId(oldHomeNav.getModuleUniqueId());
//            newHomeNav.setCategoryId(oldHomeNav.getCategoryId());
//            newHomeNav.setWebJson(oldHomeNav.getWebJson());
//            newHomeNav.setChannelNo(newVersion.getChannelNo());
//            newHomeNav.setVersion(newVersion.getId());
//            super.save(newHomeNav);
            WebJsonBo webJsonBo = JSONObject.parseObject(oldHomeNav.getWebJson(),WebJsonBo.class);
            webJsonBo.setId(null);
            webJsonBo.setModuleUniqueId(oldHomeNav.getModuleUniqueId());
            List<WebJsonBo.WebJsonImgBo> imgList = webJsonBo.getData().getImgList();
            if(CollectionUtils.isNotEmpty(imgList)){
                for (WebJsonBo.WebJsonImgBo webJsonImgBo : imgList) {
                    webJsonImgBo.setId(null);
                    WebJsonBo.WebJsonLinkBo link = webJsonImgBo.getLink();
                    if(link!=null){
                        link.setId(null);
                        //如果是内链，更新页面id
//                        if(link.getLinkType()==0){
//                            QueryWrapper<PageManager> wrapper = new QueryWrapper<PageManager>(new PageManager());
//                            PageManager oldPage = pageManagerService.getById(Long.valueOf(link.getPageId()));
//                            Assert.notNull(oldPage,"原始页面信息不存在");
//                            wrapper.getEntity().setPageUniqueId(oldPage.getPageUniqueId());
//                            wrapper.getEntity().setVersionId(newVersion.getId());
//                            wrapper.getEntity().setChannelNo(newVersion.getChannelNo());
//                            PageManager newPage = pageManagerService.getOne(wrapper);
//                            Assert.notNull(newPage,"复制后的页面信息不存在");
//                            link.setPageName(newPage.getPageName());
//                        }
                    }
                }
            }
            updateWebJson(webJsonBo,newVersion);
        });
    }
}
