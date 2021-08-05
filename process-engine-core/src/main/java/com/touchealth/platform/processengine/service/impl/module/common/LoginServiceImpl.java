package com.touchealth.platform.processengine.service.impl.module.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.touchealth.platform.processengine.annotation.TransactionalForException;
import com.touchealth.platform.processengine.constant.CommonConstant;
import com.touchealth.platform.processengine.constant.RedisConstant;
import com.touchealth.platform.processengine.dao.module.common.LoginDao;
import com.touchealth.platform.processengine.entity.module.common.Banner;
import com.touchealth.platform.processengine.entity.module.common.Login;
import com.touchealth.platform.processengine.entity.page.PageManager;
import com.touchealth.platform.processengine.exception.BusinessException;
import com.touchealth.platform.processengine.exception.CommonModuleException;
import com.touchealth.platform.processengine.handler.ModuleHandler;
import com.touchealth.platform.processengine.pojo.bo.CompareBo;
import com.touchealth.platform.processengine.pojo.bo.WebJsonBo;
import com.touchealth.platform.processengine.pojo.bo.module.common.LoginBo;
import com.touchealth.platform.processengine.pojo.dto.module.common.BannerDto;
import com.touchealth.platform.processengine.service.common.RedisService;
import com.touchealth.platform.processengine.service.impl.BaseServiceImpl;
import com.touchealth.platform.processengine.service.module.common.LoginService;
import com.touchealth.platform.processengine.service.page.PageManagerService;
import com.touchealth.platform.processengine.service.page.PlatformVersionService;
import com.touchealth.platform.processengine.utils.BaseHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

import static com.touchealth.platform.processengine.constant.CompareConstant.*;

/**
 * @program: process-engine
 * @author: xianghy
 * @create: 2021/1/5
 **/
@Slf4j
@Service("loginService")
public class LoginServiceImpl extends BaseServiceImpl<LoginDao, Login> implements LoginService {

    @Autowired
    private RedisService redisService;

    @Resource
    private PageManagerService pageManagerService;

    @Resource
    private PlatformVersionService platformVersionService;

    private final static String LOGIN_WEB_JSON_TEMPLATE = "{\"belongType\":0,\"category\":0,\"moduleType\":7,\"isInitModule\":true,\"name\":\"login\",\"blockId\":\"e0c73ed5-65b9-4bb5-9ea2-9fa9f5e016d1\",\"layoutType\":1,\"status\":\"PREVIEW\",\"data\":{\"agreementUrlChinese\":\"\",\"agreementUrlEng\":\"\",\"privacyAgreementUrlChinese\":\"\",\"privacyAgreementUrlEng\":\"\",\"subtitleChinese\":\"一站式健康管理平台\",\"subtitleEng\":\"Health Platform \",\"titleChinese\":\"势成一健康\",\"titleEng\":\"Touchealth\"}}";

    @TransactionalForException
    @Override
    public String savePageModule(String webJson, Long pageId) {
        PageManager page = pageManagerService.getById(pageId);
        Assert.notNull(page, "页面不存在");
        String initJson = StringUtils.isNotEmpty(webJson)?webJson : redisService.getValue(RedisConstant.LOGIN_WEB_JSON_TEMPLATE);
        if (StringUtils.isEmpty(initJson)) {
            initJson = LOGIN_WEB_JSON_TEMPLATE;
        }
        WebJsonBo webJsonBo = JSONObject.parseObject(initJson, WebJsonBo.class);
        LoginBo loginBo = ModuleHandler.parseLogin(page, initJson);
        loginBo.setId(null);
        Login login = BaseHelper.r2t(loginBo,Login.class);
        login.setCategoryId(CommonConstant.MODULE_CATEGORY.COMMON.getCode());
        boolean saveLoginFlag = save(login);
        if (!saveLoginFlag) {
            log.error("LoginServiceImpl.save login save fail. param: {}",loginBo);
            throw new CommonModuleException("添加login组件失败");
        }
        webJsonBo.setId(login.getId());
        webJsonBo.setModuleUniqueId(login.getModuleUniqueId());
        login.setWebJson(JSONObject.toJSONString(webJsonBo));
        // 更新webJSON
        saveOrUpdate(login);
        return login.getWebJson();
    }

    @Override
    public String clonePageModule(Long moduleId, Long pageId) {
        PageManager page = pageManagerService.getById(pageId);
        Assert.notNull(page, "页面不存在");
        Long versionId = page.getVersionId();
        Assert.notNull(platformVersionService.getById(versionId), "无效的版本号");

        Login login = getById(moduleId);
        Assert.notNull(login, "登录组件不存在");

        // 更新前端数据字符串（webJson）
        String webJson = login.getWebJson();
        if (org.springframework.util.StringUtils.isEmpty(webJson)) {
            return "";
        }
        return savePageModule(webJson, pageId);
    }

    @Override
    public String updatePageModule(String webJson) {
        LoginBo loginBo = ModuleHandler.parseLogin(null, webJson);
        Login login = getById(loginBo.getId());
        login.setWebJson(webJson);
        BaseHelper.copyNotNullProperties(loginBo, login);
        updateById(login);
        return webJson;
    }

    @Override
    public Boolean updateModuleStatus(Long moduleId, Integer status) {
        Login login = getById(moduleId);
        Assert.notNull(login, "登录组件不存在");
        login.setStatus(status);
        if(!updateById(login)){
            throw new CommonModuleException("更新login组件状态失败");
        }
        return true;
    }

    @Override
    public String findPageModuleById(Long id) {
        return null;
    }

    @Override
    public String getModuleById(Long id, String... param) {
        Login login = getById(id);
        Assert.notNull(login, "登录组件不存在");
        return login.getWebJson();
    }

    @Override
    public List<String> findPageModuleByIdList(List<Long> ids) {
        return null;
    }

    @Override
    public Boolean deletePageModule(Long id) {
        return removeById(id);
    }

    @Override
    public Boolean deletePageModule(List<Long> ids) {
        return removeByIds(ids);
    }

    @Override
    public Boolean restoreModule(List<Long> ids) {
        return batchUpdateModuleStatus(new ArrayList<>(ids), CommonConstant.STATUS.DRAFT.getCode());
    }

    public Boolean batchUpdateModuleStatus(List<Long> moduleIds, Integer status){
        return batchUpdateModuleStatusAndVersion(moduleIds,status,null);
    }

    public Boolean batchUpdateModuleStatusAndVersion(List<Long> moduleIds, Integer status,Long versionId){
        List<Login> logins = listByIds(moduleIds);
        if (CollectionUtils.isEmpty(logins)) {
            log.debug("LoginServiceImpl.batchUpdateModuleStatus {} not exits", moduleIds);
            throw new CommonModuleException("组件不存在");
        }
        // 更新login页状态
        LambdaUpdateWrapper<Login> loginUpdateWrapper = Wrappers.<Login>lambdaUpdate().in(Login::getId, moduleIds).set(Login::getStatus, status);
        if(null != versionId){
            loginUpdateWrapper.set(Login::getVersion,versionId);
        }
        return update(new Login(),loginUpdateWrapper);
    }

    @Override
    public Boolean restoreModule(Collection<Long> ids, Long versionId) {
        return batchUpdateModuleStatusAndVersion(new ArrayList<>(ids), CommonConstant.STATUS.DRAFT.getCode(),versionId);
    }

    @Override
    public List<CompareBo> compare(String webJson, Long moduleId) {
        List<CompareBo> ret = new ArrayList<>();
        if (org.springframework.util.StringUtils.isEmpty(webJson) && moduleId == null) {
            log.debug("LoginServiceImpl.compare param is null");
            return ret;
        }

        if (org.springframework.util.StringUtils.isEmpty(webJson)) {
            // 组件被删除
            Login login = getById(moduleId);
            String loginWebJson = login.getWebJson();
            Assert.isTrue(!org.springframework.util.StringUtils.isEmpty(loginWebJson), "组件webJson数据异常");
            WebJsonBo webJsonBo = JSONObject.parseObject(loginWebJson, WebJsonBo.class);
            WebJsonBo.WebJsonDataBo data = webJsonBo.getData();
            recordNavigateOp(ret, data.getTitleChinese(), "", data.getTitleChinese(), OP_UPD, TITLE_CHINESE, moduleId,false);
            recordNavigateOp(ret, data.getTitleEng(), "", data.getTitleChinese(), OP_UPD, TITLE_ENG, moduleId,false);
            recordNavigateOp(ret, data.getSubtitleChinese(), "", data.getTitleChinese(), OP_UPD, SUBTITLE_CHINESE, moduleId,false);
            recordNavigateOp(ret, data.getSubtitleEng(), "", data.getTitleChinese(), OP_UPD, SUBTITLE_ENG, moduleId,false);
            recordNavigateOp(ret, data.getAgreementUrlChinese(), "", data.getTitleChinese(), OP_UPD, AGREEMENT_URL_CHINESE, moduleId,false);
            recordNavigateOp(ret, data.getAgreementUrlEng(), "", data.getTitleChinese(), OP_UPD, AGREEMENT_URL_ENG, moduleId,false);
            recordNavigateOp(ret, data.getPrivacyAgreementUrlChinese(), "", data.getTitleChinese(), OP_UPD, PRIVACY_AGREEMENT_URL_CHINESE, moduleId,false);
            recordNavigateOp(ret, data.getPrivacyAgreementUrlEng(), "", data.getTitleChinese(), OP_UPD, PRIVACY_AGREEMENT_URL_ENG, moduleId,false);
        }else { // 组件被更新
            WebJsonBo webJsonBo = JSON.parseObject(webJson, WebJsonBo.class);
            WebJsonBo.WebJsonDataBo data = webJsonBo.getData();
            if (moduleId == null) {
                recordNavigateOp(ret, "", data.getTitleChinese(), data.getTitleChinese(), OP_ADD, TITLE_CHINESE, moduleId,false);
                recordNavigateOp(ret, "", data.getTitleEng(), data.getTitleChinese(), OP_ADD, TITLE_ENG, moduleId,false);
                recordNavigateOp(ret, "", data.getSubtitleChinese(), data.getTitleChinese(), OP_ADD, SUBTITLE_CHINESE, moduleId,false);
                recordNavigateOp(ret, "", data.getSubtitleEng(), data.getTitleChinese(), OP_ADD, SUBTITLE_ENG, moduleId,false);
                recordNavigateOp(ret, "", data.getAgreementUrlChinese(), data.getTitleChinese(), OP_ADD, AGREEMENT_URL_CHINESE, moduleId,false);
                recordNavigateOp(ret, "", data.getAgreementUrlEng(), data.getTitleChinese(), OP_ADD, AGREEMENT_URL_ENG, moduleId,false);
                recordNavigateOp(ret, "", data.getPrivacyAgreementUrlChinese(), data.getTitleChinese(), OP_ADD, PRIVACY_AGREEMENT_URL_CHINESE, moduleId,false);
                recordNavigateOp(ret, "", data.getPrivacyAgreementUrlEng(), data.getTitleChinese(), OP_ADD, PRIVACY_AGREEMENT_URL_ENG, moduleId,false);
            }else{
                Login login = getById(moduleId);
                WebJsonBo oldWebJson = JSONObject.parseObject(login.getWebJson(), WebJsonBo.class);
                WebJsonBo.WebJsonDataBo oldData = oldWebJson.getData();
                if (oldData != null){
                    recordNavigateOp(ret, oldData.getTitleChinese(), data.getTitleChinese(), oldData.getTitleChinese(), OP_UPD, TITLE_CHINESE, moduleId,true);
                    recordNavigateOp(ret, oldData.getTitleEng(), data.getTitleEng(), oldData.getTitleChinese(), OP_UPD, TITLE_ENG, moduleId,true);
                    recordNavigateOp(ret, oldData.getSubtitleChinese(), data.getSubtitleChinese(), oldData.getTitleChinese(), OP_UPD, SUBTITLE_CHINESE, moduleId,true);
                    recordNavigateOp(ret, oldData.getSubtitleEng(), data.getSubtitleEng(), oldData.getTitleChinese(), OP_UPD, SUBTITLE_ENG, moduleId,true);
                    recordNavigateOp(ret, oldData.getAgreementUrlChinese(), data.getAgreementUrlChinese(), oldData.getTitleChinese(), OP_UPD, AGREEMENT_URL_CHINESE, moduleId,true);
                    recordNavigateOp(ret, oldData.getAgreementUrlEng(), data.getAgreementUrlEng(), oldData.getTitleChinese(), OP_UPD, AGREEMENT_URL_ENG, moduleId,true);
                    recordNavigateOp(ret, oldData.getPrivacyAgreementUrlChinese(), data.getPrivacyAgreementUrlChinese(), oldData.getTitleChinese(), OP_UPD, PRIVACY_AGREEMENT_URL_CHINESE, moduleId,true);
                    recordNavigateOp(ret, oldData.getPrivacyAgreementUrlEng(), data.getPrivacyAgreementUrlEng(), oldData.getTitleChinese(), OP_UPD, PRIVACY_AGREEMENT_URL_ENG, moduleId,true);
                }
            }
        }
        return ret;
    }

    private void recordNavigateOp(List<CompareBo> compareList, String oldValue, String newValue, String moduleName,
                                  String opName, String opContent, Long moduleId,Boolean update) {
        if (update && Optional.ofNullable(newValue).orElse("").equals(oldValue)) {
                return;
        }
        if (!org.springframework.util.StringUtils.isEmpty(oldValue) || !org.springframework.util.StringUtils.isEmpty(newValue)) {
            // 新增链接参数操作记录
            compareList.add(new CompareBo(null, CommonConstant.ModuleType.NAVIGATION, moduleName, opName,opContent, oldValue, newValue));
        }
    }
}
