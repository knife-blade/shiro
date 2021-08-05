package com.touchealth.platform.processengine.service.module.common;

import com.touchealth.platform.processengine.entity.module.common.BtnGroup;
import com.touchealth.platform.processengine.exception.CommonModuleException;
import com.touchealth.platform.processengine.pojo.bo.module.common.BtnGroupBo;
import com.touchealth.platform.processengine.pojo.dto.module.common.BtnDto;
import com.touchealth.platform.processengine.pojo.dto.module.common.BtnGroupDto;
import com.touchealth.platform.processengine.service.module.BaseModuleService;

import java.util.List;

/**
 * <p>
 * 按钮组通用组件表 服务类
 * </p>
 *
 * @author SunYang
 * @since 2020-11-16
 */
public interface BtnGroupService extends BaseModuleService<BtnGroup> {

    BtnGroupDto save(BtnGroupBo bo);

    /**
     * 编辑组件。<br>
     * 更新时，需指定按钮组的ID({@link BtnGroupDto#getId()})，
     * 若按钮组下的按钮变更需要指定按钮组下的按钮ID({@link BtnDto#getId()})，若没有指定按钮组下的按钮ID，那么会认为是在按钮组下新增了一个按钮。
     * 并且若发现更新时，参数中({@link BtnGroupDto#getButtons()})没有原有的按钮组下的按钮，会认为需要该按钮需要被删除。
     * @param bo
     * @return
     */
    BtnGroupDto update(BtnGroupBo bo) throws CommonModuleException;

    BtnGroupDto findById(Long id);

    List<BtnGroupDto> findByIdList(List<Long> ids);

    Boolean delete(Long id);

    Boolean delete(List<Long> ids);

}
