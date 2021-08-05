package com.touchealth.platform.processengine.service.impl.module.common;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.touchealth.platform.processengine.annotation.TransactionalForException;
import com.touchealth.platform.processengine.constant.CommonConstant;
import com.touchealth.platform.processengine.dao.module.common.OrderManagementDao;
import com.touchealth.platform.processengine.entity.module.common.OrderManagement;
import com.touchealth.platform.processengine.entity.module.common.OrderMgtImg;
import com.touchealth.platform.processengine.entity.page.PageManager;
import com.touchealth.platform.processengine.exception.CommonModuleException;
import com.touchealth.platform.processengine.handler.ModuleHandler;
import com.touchealth.platform.processengine.pojo.bo.WebJsonBo;
import com.touchealth.platform.processengine.pojo.bo.module.common.OrderManagementBo;
import com.touchealth.platform.processengine.pojo.bo.module.common.OrderMgtImgBo;
import com.touchealth.platform.processengine.pojo.dto.module.common.LinkDto;
import com.touchealth.platform.processengine.service.impl.module.BaseModuleServiceImpl;
import com.touchealth.platform.processengine.service.module.common.LinkService;
import com.touchealth.platform.processengine.service.module.common.OrderManagementService;
import com.touchealth.platform.processengine.service.module.common.OrderMgtImgService;
import com.touchealth.platform.processengine.service.page.PageManagerService;
import com.touchealth.platform.processengine.service.page.PlatformVersionService;
import com.touchealth.platform.processengine.utils.BaseHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单管理组件表 服务实现类
 * </p>
 *
 * @author lvx
 * @since 2021-01-14
 */
@Service
@Slf4j
public class OrderManagementServiceImpl extends BaseModuleServiceImpl<OrderManagementDao, OrderManagement> implements OrderManagementService {

    @Resource
    private PageManagerService pageManagerService;

    @Resource
    private PlatformVersionService platformVersionService;

    @Resource
    private LinkService linkService;

    @Resource
    private OrderMgtImgService orderMgtImgService;

    @Override
    @TransactionalForException
    public String savePageModule(String webJson, Long pageId) {
        PageManager page = pageManagerService.getById(pageId);
        Assert.notNull(page, "页面不存在");
        OrderManagementBo orderManagementBo = ModuleHandler.parseOrderManagement(page, webJson);

        return save(orderManagementBo);
    }

    public String save(OrderManagementBo bo) {
        // 添加订单管理组件
        bo.setId(null);
        OrderManagement orderManagement = BaseHelper.r2t(bo, OrderManagement.class);
        orderManagement.setCategoryId(CommonConstant.MODULE_CATEGORY.COMMON.getCode());
        boolean saveFlag = save(orderManagement);
        if (!saveFlag) {
            log.error("OrderManagementServiceImpl.save order management fail. param: {}", bo);
            throw new CommonModuleException("添加订单管理组件失败");
        }

        List<OrderMgtImgBo> orderMgtImgBos = bo.getOrderMgtImgs();
        // 添加图片
        List<OrderMgtImg> orderMgtImgs = new ArrayList<>();
        if (!CollectionUtils.isEmpty(orderMgtImgBos)) {
            orderMgtImgBos.forEach(img -> {
                img.setId(null);
                OrderMgtImg orderMgtImg = BaseHelper.r2t(img, OrderMgtImg.class);
                orderMgtImg.setChannelNo(orderManagement.getChannelNo());
                orderMgtImg.setOrderMgtId(orderManagement.getId());
                orderMgtImg.setPageId(orderManagement.getPageId());
                orderMgtImg.setVersion(orderManagement.getVersion());
                orderMgtImg.setStatus(orderManagement.getStatus());
                orderMgtImg.setUrl(img.getUrl());
                LinkDto linkDto = img.getLinkDto();
                if (linkDto != null) {
                    // 添加链接
                    linkDto.setChannelNo(orderManagement.getChannelNo());
                    linkDto.setVersion(orderManagement.getVersion());
                    linkDto.setPageId(orderManagement.getPageId());
                    linkDto.setStatus(orderManagement.getStatus());
                    LinkDto linkDto1 = linkService.save(linkDto);
                    Long linkId = linkDto1.getId();
                    orderMgtImg.setLinkModuleId(linkId);
                }
                orderMgtImgs.add(orderMgtImg);
            });
            boolean saveImgFlag = orderMgtImgService.saveBatch(orderMgtImgs);
            if (!saveImgFlag) {
                log.error("OrderManagementServiceImpl save images fail. param: {}", bo);
                throw new CommonModuleException("保存图片失败");
            }
        }

        // 更新订单管理组件中的webJson里的图片ID
        WebJsonBo webJson = JSONObject.parseObject(orderManagement.getWebJson(), WebJsonBo.class);
        if (webJson != null) {
            webJson.setId(orderManagement.getId());
            webJson.setModuleUniqueId(orderManagement.getModuleUniqueId());
            updateWebJson(orderMgtImgs, webJson);
            orderManagement.setWebJson(JSONObject.toJSONString(webJson));
            saveFlag = saveOrUpdate(orderManagement);
            if (!saveFlag) {
                log.error("OrderManagementServiceImpl.save update order management webJson fail. param: {}", bo);
                throw new CommonModuleException("添加订单管理组件失败");
            }

            return JSONObject.toJSONString(webJson);
        }

        return null;
    }

    @Override
    @TransactionalForException
    public String clonePageModule(Long moduleId, Long pageId) {
        PageManager page = pageManagerService.getById(pageId);

        Assert.notNull(page, "页面不存在");
        Long versionId = page.getVersionId();

        Assert.notNull(platformVersionService.getById(versionId), "无效的版本号");

        OrderManagement orderManagement = getById(moduleId);
        Assert.notNull(orderManagement, "订单管理组件不存在");

        // 更新前端数据字符串（webJson）
        String webJson = orderManagement.getWebJson();
        if (StringUtils.isEmpty(webJson)) {
            return "";
        }

        return savePageModule(webJson, pageId);
    }

    @Override
    @TransactionalForException
    public String updatePageModule(String webJson) {
        OrderManagementBo bo = ModuleHandler.parseOrderManagement(null, webJson);
        Long orderManagementId = bo.getId();
        List<OrderMgtImgBo> imgBos = bo.getOrderMgtImgs();

        // 更新订单管理组件
        OrderManagement orderManagement = getById(orderManagementId);
        if (orderManagement == null) {
            throw new CommonModuleException("订单管理组件不存在");
        }
        BaseHelper.copyNotNullProperties(bo, orderManagement);
        if (!updateById(orderManagement)) {
            log.error("OrderManagementServiceImpl.updatePageModule update order management fail. param: {}", bo);
            throw new CommonModuleException("更新订单管理组件失败");
        }

        List<OrderMgtImg> addOrUpdImgList = new ArrayList<>();
        // 更新图片
        if (!CollectionUtils.isEmpty(imgBos)) {
            List<Long> delImgList = new ArrayList<>();
            Map<Long, OrderMgtImg> updOrderMgtImgMap = new HashMap<>();
            QueryWrapper<OrderMgtImg> qw = Wrappers.<OrderMgtImg>query()
                    .eq("order_mgt_id", orderManagementId);
            List<OrderMgtImg> orderMgtImgs = orderMgtImgService.baseFindList(qw);

            // 将所有需要更新的图片放到一个Map中
            for (OrderMgtImgBo orderMgtImgBo : imgBos) {
                Long imgId = orderMgtImgBo.getId();
                OrderMgtImg orderMgtImg = BaseHelper.r2t(orderMgtImgBo, OrderMgtImg.class);
                orderMgtImg.setChannelNo(orderManagement.getChannelNo());
                orderMgtImg.setVersion(orderManagement.getVersion());
                orderMgtImg.setOrderMgtId(orderManagementId);
                orderMgtImg.setPageId(orderManagement.getPageId());
                orderMgtImg.setStatus(orderManagement.getStatus());
                LinkDto linkDto = orderMgtImgBo.getLinkDto();
                if (linkDto != null) {
                    // 添加|更新 链接
                    linkDto.setChannelNo(orderManagement.getChannelNo());
                    linkDto.setPageId(orderManagement.getPageId());
                    linkDto.setStatus(orderManagement.getStatus());
                    linkDto.setVersion(orderManagement.getVersion());
                    if (linkDto.getId() == null) {
                        LinkDto linkDto1 = linkService.save(linkDto);
                        Long linkId = linkDto1.getId();
                        orderMgtImg.setLinkModuleId(linkId);
                    } else {
                        LinkDto updLink = linkService.update(linkDto);
                        if (updLink == null) {
                            log.error("OrderManagementServiceImpl.updatePageModule update link fail. param: {}", linkDto);
                        }
                    }
                }
                // 新增或修改的图片
                addOrUpdImgList.add(orderMgtImg);
                if (imgId != null) {
                    // 需要更新的图片
                    updOrderMgtImgMap.put(imgId, orderMgtImg);
                }
            }
            // 将数据库中存在编辑后不存在的图片删除
            orderMgtImgs.forEach(e -> {
                Long id = e.getId();
                OrderMgtImg mgtImg = updOrderMgtImgMap.get(id);
                if (mgtImg == null) {
                    delImgList.add(e.getId());
                }
            });
            boolean updImgFlag = orderMgtImgService.saveOrUpdateBatch(addOrUpdImgList);
            // 修改到回收站状态
            boolean delImgFlag = true;
            if (!CollectionUtils.isEmpty(delImgList)) {
                delImgFlag = orderMgtImgService.update(null,
                        Wrappers.<OrderMgtImg>lambdaUpdate().in(OrderMgtImg::getId, delImgList).set(OrderMgtImg::getUpdatedTime, LocalDateTime.now()).set(OrderMgtImg::getDeletedFlag, 1));
            }
            if (!updImgFlag || !delImgFlag) {
                log.error("OrderManagementServiceImpl.updatePageModule fail. param: {}", bo);
                throw new CommonModuleException("更新订单管理组件失败");
            }
        }
        // 更新webJson
        WebJsonBo updateWebJson = JSONObject.parseObject(orderManagement.getWebJson(), WebJsonBo.class);
        updateWebJson(addOrUpdImgList, updateWebJson);
        orderManagement.setWebJson(JSONObject.toJSONString(updateWebJson));
        if (!saveOrUpdate(orderManagement)) {
            log.error("OrderManagementServiceImpl.updatePageModule update webJson fail. param: {}", bo);
            throw new CommonModuleException("修改订单管理组件失败");
        }

        return JSONObject.toJSONString(updateWebJson);
    }

    private void updateWebJson(List<OrderMgtImg> orderMgtImgs, WebJsonBo webJson) {
        if (!CollectionUtils.isEmpty(orderMgtImgs)) {
            List<WebJsonBo.WebJsonImgBo> imgWebJsonList = webJson.getData().getImgList();
            for (int i = 0; i < imgWebJsonList.size() && imgWebJsonList.size() == orderMgtImgs.size(); i++) {
                // 更新图片的webJson字段中的按钮ID
                WebJsonBo.WebJsonImgBo webJsonImgBo = imgWebJsonList.get(i);
                OrderMgtImg orderMgtImg = orderMgtImgs.get(i);
                webJsonImgBo.setId(orderMgtImg.getId());
                webJsonImgBo.setModuleUniqueId(orderMgtImg.getModuleUniqueId());
                // 更新按钮链接ID
                WebJsonBo.WebJsonLinkBo link = webJsonImgBo.getLink();
                if (link != null) {
                    link.setId(orderMgtImg.getLinkModuleId());
                }
            }
        }
    }
}
