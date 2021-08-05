package com.touchealth.platform.processengine.service.impl.module.common;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.touchealth.platform.processengine.annotation.TransactionalForException;
import com.touchealth.platform.processengine.constant.CommonConstant;
import com.touchealth.platform.processengine.constant.CommonConstant.ModuleType;
import com.touchealth.platform.processengine.dao.module.common.DelimiterDao;
import com.touchealth.platform.processengine.entity.module.common.Delimiter;
import com.touchealth.platform.processengine.entity.page.PageManager;
import com.touchealth.platform.processengine.exception.CommonModuleException;
import com.touchealth.platform.processengine.exception.ParameterException;
import com.touchealth.platform.processengine.handler.ModuleHandler;
import com.touchealth.platform.processengine.pojo.bo.CompareBo;
import com.touchealth.platform.processengine.pojo.bo.WebJsonBo;
import com.touchealth.platform.processengine.pojo.dto.module.common.DelimiterDto;
import com.touchealth.platform.processengine.service.impl.module.BaseModuleServiceImpl;
import com.touchealth.platform.processengine.service.module.common.DelimiterService;
import com.touchealth.platform.processengine.service.page.PageManagerService;
import com.touchealth.platform.processengine.service.page.PlatformVersionService;
import com.touchealth.platform.processengine.utils.BaseHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.touchealth.platform.processengine.constant.CompareConstant.*;

/**
 * <p>
 * 分隔符通用组件表 服务实现类
 * </p>
 *
 * @author SunYang
 * @since 2020-11-16
 */
@Service
@Slf4j
public class DelimiterServiceImpl extends BaseModuleServiceImpl<DelimiterDao, Delimiter> implements DelimiterService {

    @Autowired
    private PageManagerService pageManagerService;
    @Autowired
    private PlatformVersionService platformVersionService;

    @Override
    @TransactionalForException
    public String savePageModule(String webJson, Long pageId) {
        PageManager page = pageManagerService.getById(pageId);
        Assert.notNull(page, "页面不存在");
        DelimiterDto delimiterDto = ModuleHandler.parseDelimiter(page, webJson);

        DelimiterDto delimiter = save(delimiterDto);

        return delimiter.getWebJson();
    }

    @Override
    @TransactionalForException
    public String clonePageModule(Long moduleId, Long pageId) {
        PageManager page = pageManagerService.getById(pageId);
        Assert.notNull(page, "页面不存在");

        Delimiter delimiter = getById(moduleId);

        // 更新前端数据字符串（webJson）
        String webJson = delimiter.getWebJson();
        if (StringUtils.isEmpty(webJson)) {
            return "";
        }
        return savePageModule(webJson, pageId);
    }

    @Override
    public String updatePageModule(String webJson) {
        DelimiterDto updDelimiter = update(ModuleHandler.parseDelimiter(webJson));
        return updDelimiter == null ? "" : updDelimiter.getWebJson();
    }

    @Override
    public Boolean updateModuleStatus(Long moduleId, Integer status) {
        return updateModuleStatus(moduleId, status, null);
    }

    @Override
    public Boolean updateModuleStatus(Long moduleId, Integer status, Long versionId) {
        DelimiterDto delimiterDto = findById(moduleId);
        if (delimiterDto == null) {
            log.debug("DelimiterServiceImpl.updateModuleStatus {} not exits", moduleId);
            throw new CommonModuleException("分隔符组件不存在");
        }
        LambdaUpdateWrapper<Delimiter> uw = Wrappers.<Delimiter>lambdaUpdate().eq(Delimiter::getId, moduleId).set(Delimiter::getStatus, status);
        if (versionId != null) {
            uw.set(Delimiter::getVersion, versionId);
        }
        return update(new Delimiter(), uw);
    }

    @Override
    public String findPageModuleById(Long id) {
        DelimiterDto delimiterDto = findById(id);
        return delimiterDto == null ? "" : delimiterDto.getWebJson();
    }

    @Override
    public List<String> findPageModuleByIdList(List<Long> ids) {
        List<DelimiterDto> delimiterDtoList = findByIdList(ids);
        return CollectionUtils.isEmpty(delimiterDtoList) ? new ArrayList<>() :
                delimiterDtoList.stream().map(DelimiterDto::getWebJson).collect(Collectors.toList());
    }

    @Override
    public Boolean deletePageModule(Long id) {
        return delete(id);
    }

    @Override
    public Boolean deletePageModule(List<Long> ids) {
        return delete(ids);
    }

    @Override
    @TransactionalForException
    public Boolean restoreModule(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return false;
        }
        // TODO 20210106 优化去掉循环
        for (Long id : ids) {
            Boolean updFlag = updateModuleStatus(id, CommonConstant.STATUS.DRAFT.getCode());
            if (updFlag == null || !updFlag) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Boolean restoreModule(Collection<Long> ids, Long versionId) {
        if (CollectionUtils.isEmpty(ids)) {
            return false;
        }
        // TODO 20210106 优化去掉循环
        for (Long id : ids) {
            Boolean updFlag = updateModuleStatus(id, CommonConstant.STATUS.DRAFT.getCode(), versionId);
            if (updFlag == null || !updFlag) {
                return false;
            }
        }
        return true;
    }

    @Override
    public DelimiterDto save(DelimiterDto dto) {
        Delimiter delimiter = BaseHelper.r2t(dto, Delimiter.class);
        delimiter.setId(null);
        delimiter.setCategoryId(CommonConstant.MODULE_CATEGORY.COMMON.getCode());
        boolean saveFlag = save(delimiter);
        if (!saveFlag) {
            log.error("DelimiterServiceImpl.save delimiter save fail. param: {}", dto);
            throw new CommonModuleException("添加分隔符失败");
        }

        // 更新分隔符webJson中的ID
        String webJson = delimiter.getWebJson();
        WebJsonBo webJsonBo = JSONObject.parseObject(webJson, WebJsonBo.class);
        webJsonBo.setId(delimiter.getId());
        webJsonBo.setModuleUniqueId(delimiter.getModuleUniqueId());
        delimiter.setWebJson(JSONObject.toJSONString(webJsonBo));
        saveOrUpdate(delimiter);

        BaseHelper.copyNotNullProperties(delimiter, dto);
        return dto;
    }

    @Override
    public DelimiterDto update(DelimiterDto dto) {
        Long id = dto.getId();
        Delimiter delimiterDb = getById(id);
        if (delimiterDb == null) {
            throw new CommonModuleException("分隔符不存在");
        }
        BaseHelper.copyNotNullProperties(dto, delimiterDb);
        boolean updFlag = updateById(delimiterDb);
        if (!updFlag) {
            log.error("DelimiterServiceImpl.update delimiter update fail. param: {}", dto);
            throw new CommonModuleException("分隔符更新失败");
        }
        return findById(id);
    }

    @Override
    public DelimiterDto findById(Long id) {
        Delimiter delimiter = getById(id);
        if (delimiter == null) {
            return null;
        }
        return BaseHelper.r2t(delimiter, DelimiterDto.class);
    }

    @Override
    public List<DelimiterDto> findByIdList(List<Long> ids) {
        List<Delimiter> delimiters = listByIds(ids);
        if (CollectionUtils.isEmpty(delimiters)) {
            return new ArrayList<>();
        }
        return delimiters.stream().map(o -> BaseHelper.r2t(o, DelimiterDto.class)).collect(Collectors.toList());
    }

    @Override
    public Boolean delete(Long id) {
        return removeById(id);
    }

    /**
     * 分隔符修改前后比对方法（仅编辑组件时使用）
     * @param webJson 修改后的前端传入的按钮组件，不能为空
     * @param delimiterId    修改的分隔符组件ID，为空表示新增
     * @return
     */
    @Override
    public List<CompareBo> compare(String webJson, Long delimiterId) {
        List<CompareBo> ret = new ArrayList<>();

        if (StringUtils.isEmpty(webJson) && delimiterId == null) {
            log.debug("DelimiterServiceImpl.compare param is null");
            return ret;
        }

        if (StringUtils.isEmpty(webJson)) { // 组件被删除
            DelimiterDto oldDelimiterDto = findById(delimiterId);
            if (oldDelimiterDto == null) {
                log.error("DelimiterServiceImpl.compare delimiter not exits. param: {}", delimiterId);
                throw new CommonModuleException("分隔符不存在");
            }
            WebJsonBo oldWebJsonBo = JSONObject.parseObject(oldDelimiterDto.getWebJson(), WebJsonBo.class);
            Assert.isTrue(!StringUtils.isEmpty(oldWebJsonBo), "组件webJson数据异常");
            if (oldWebJsonBo.getStyle() != null) {
                String oldHeight = String.valueOf(oldWebJsonBo.getStyle().getHeight());
                ret.add(new CompareBo(null, ModuleType.INTERVAL, "", OP_DEL, DELIMITER_HEIGHT, oldHeight, ""));
            }
        } else { // 组件被更新
            Assert.isTrue(!StringUtils.isEmpty(webJson), "参数不能为空");
            WebJsonBo webJsonBo = JSONObject.parseObject(webJson, WebJsonBo.class);
            Assert.notNull(webJsonBo.getStyle(), "参数不能为空");

            WebJsonBo.WebJsonStyleBo style = webJsonBo.getStyle();
            String height = String.valueOf(style.getHeight());

            if (delimiterId == null) { // 新增分隔符组件
                ret.add(new CompareBo(null, ModuleType.INTERVAL, "", OP_ADD, DELIMITER_HEIGHT, "", height));
            } else { // 编辑分隔符组件
                DelimiterDto oldDelimiterDto = findById(delimiterId);
                if (oldDelimiterDto == null) {
                    log.error("DelimiterServiceImpl.compare delimiter not exits. param: {}", delimiterId);
                    throw new CommonModuleException("获取分隔符失败");
                }
                try {
                    WebJsonBo oldWebJsonBo = JSONObject.parseObject(oldDelimiterDto.getWebJson(), WebJsonBo.class);
                    if (oldWebJsonBo.getStyle() != null) {
                        String oldHeight = String.valueOf(oldWebJsonBo.getStyle().getHeight());
                        if (!Optional.ofNullable(height).orElse("").equals(oldHeight)) {
                            ret.add(new CompareBo(delimiterId, ModuleType.INTERVAL, "", OP_UPD, DELIMITER_HEIGHT, oldHeight, height));
                        }
                    }
                } catch (JSONException e) {
                    throw new ParameterException("webJson格式异常");
                } catch (Exception e) {
                    log.error("BtnGroupServiceImpl.compare has exception", e);
                    throw new ParameterException("webJson解析异常");
                }
            }
        }

        return ret;
    }

    @Override
    public Boolean delete(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return false;
        }
        return removeByIds(ids);
    }

}
