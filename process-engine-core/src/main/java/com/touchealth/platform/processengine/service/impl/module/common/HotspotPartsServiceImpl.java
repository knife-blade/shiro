package com.touchealth.platform.processengine.service.impl.module.common;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.touchealth.platform.processengine.dao.module.common.HotspotDao;
import com.touchealth.platform.processengine.entity.module.common.Hotspot;
import com.touchealth.platform.processengine.entity.module.common.HotspotParts;
import com.touchealth.platform.processengine.dao.module.common.HotspotPartsDao;
import com.touchealth.platform.processengine.pojo.bo.WebJsonBo;
import com.touchealth.platform.processengine.pojo.dto.module.common.HotspotPartsDto;
import com.touchealth.platform.processengine.pojo.request.module.common.HotspotRequest;
import com.touchealth.platform.processengine.service.module.common.HotspotPartsService;
import com.touchealth.platform.processengine.service.impl.BaseServiceImpl;
import com.touchealth.platform.processengine.service.module.common.LinkService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 热区通用组件表之热区部分表 服务实现类
 * </p>
 *
 * @author LJH
 * @since 2020-11-25
 */
@Service
public class HotspotPartsServiceImpl extends BaseServiceImpl<HotspotPartsDao, HotspotParts> implements HotspotPartsService {

    @Resource
    private HotspotPartsDao hotspotPartsDao;

    @Resource
    private HotspotDao hotspotDao;

    @Resource
    private LinkService linkService;


    @Override
    public PageInfo<HotspotPartsDto> getList(HotspotRequest request) {
        PageHelper.startPage(request.getPageNo(),request.getPageSize());
        List<HotspotPartsDto> hotspotDtos = hotspotPartsDao.getList(request);
        if(CollectionUtils.isEmpty(hotspotDtos)){
            return new PageInfo<>(hotspotDtos);
        }
        // 获取webjson
        Hotspot hotspot = hotspotDao.selectById(hotspotDtos.get(0).getHotspotId());
        if(null == hotspot){
            return null;
        }
        WebJsonBo webJsonBo = JSONObject.parseObject(hotspot.getWebJson(), WebJsonBo.class);
        List<WebJsonBo.HotspotPartsBo> hotspotStyles = webJsonBo.getData().getHotspotList();
        Map<Long,WebJsonBo.HotspotPartsBo> hotspotStyleMap = hotspotStyles.stream().collect(Collectors.toMap(WebJsonBo.HotspotPartsBo::getId, style -> style));
        // 获取热区位置和链接
        hotspotDtos.forEach(p -> {
            p.setComponentId(p.getHotspotId());
            WebJsonBo.HotspotPartsBo style = hotspotStyleMap.get(p.getId());
            if(null != style){
                p.setStyle(style);
                WebJsonBo.WebJsonLinkBo link = style.getLink();
                p.setLinkDto(null == link?null: linkService.findById(link.getId()));
            }
        });
        return new PageInfo<>(hotspotDtos);
    }
}
