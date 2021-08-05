package com.touchealth.platform.processengine.pojo.dto.module.common;

import com.touchealth.platform.processengine.pojo.bo.WebJsonBo;
import lombok.Data;

/**
 * <p>
 * 首页导航组件
 * </p>
 *
 * @author liqone
 * @since 2020-12-30
 */
@Data
public class HomeNavDto{
    private Long id;

    /**
     * 版本间的唯一组件标识。同一个组件，不同版本该值不变。
     */
    private Long moduleUniqueId;

    /**
     * 轮播图组件所在的渠道编码
     */
    private String channelNo;

    /**
     * 组件分类ID
     */
    private Long categoryId;

    /**
     * 前端页面配置信息，JSON格式。该值来源于前端，原样返回给前端。
     */
    private WebJsonBo webJson;

    /**
     * 版本号ID
     */
    private Long version;


}
