package com.touchealth.platform.processengine.service.impl.module;

import com.touchealth.platform.processengine.constant.CommonConstant;
import com.touchealth.platform.processengine.entity.module.common.*;
import com.touchealth.platform.processengine.exception.BusinessException;
import com.touchealth.platform.processengine.service.module.BaseModuleService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;

/**
 * @author liufengqiang
 * @date 2020-11-30 14:21:30
 */
@Service
public class ModuleService {

    @Resource
    private BaseModuleService<Banner> bannerService;
    @Resource
    private BaseModuleService<Navigate> navigateService;
    @Resource
    private BaseModuleService<Hotspot> hotspotService;
    @Resource
    private BaseModuleService<ComboImg> comboImgService;
    @Resource
    private BaseModuleService<Delimiter> delimiterService;
    @Resource
    private BaseModuleService<BtnGroup> btnGroupService;

    @Resource
    private BaseModuleService<Login> loginService;

    @Resource
    private BaseModuleService<PersonalInfo> personalInfoService;

    @Resource
    private BaseModuleService<OrderManagement> orderManagementService;

    @Resource
    private BaseModuleService<MyMod> myModService;
    @Resource
    private BaseModuleService<Object> emptyBusinessService;

    public BaseModuleService getInstance(Integer moduleType) {
        CommonConstant.ModuleType componentType = CommonConstant.ModuleType.getByCode(moduleType);
        Assert.notNull(componentType, "组件类型异常");
        switch (componentType) {
            case CAROUSEL:
                return bannerService;
            case NAVIGATION:
                return navigateService;
            case HOTSPOT:
                return hotspotService;
            case LIST_PHOTOS:
                return comboImgService;
            case INTERVAL:
                return delimiterService;
            case BUTTON:
                return btnGroupService;
            case LOGIN:
                return loginService;
            case PERSONAL_INFO:
                return personalInfoService;
            case ORDER_MANAGEMENT:
                return orderManagementService;
            case MY_MOD:
                return myModService;
            case EMPTY_BUSINESS:
                return emptyBusinessService;
            default:
                throw new BusinessException("组件类型异常");
        }
    }
}
