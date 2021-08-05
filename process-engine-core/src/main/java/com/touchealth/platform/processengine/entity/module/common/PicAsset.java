package com.touchealth.platform.processengine.entity.module.common;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.touchealth.platform.processengine.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.touchealth.platform.processengine.constant.*;

/**
 * <p>
 * 组件分类表
 * </p>
 *
 * @author SunYang
 * @since 2020-11-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("module_pic_asset")
public class PicAsset extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 版本间的唯一组件标识。同一个组件，不同版本该值不变。
     */
    @TableField(fill = FieldFill.INSERT)
    private Long moduleUniqueId;

    /**
     * 按钮组件所在的渠道号
     */
    private String channelNo;

    /**
     * 图片或图片文件夹名
     */
    private String name;

    /**
     * 图片或图片文件夹深度
     */
    private Integer depth;

    /**
     * 图片上级图片文件夹ID。根目录改值为-1
     */
    private Long pId;

    /**
     * 图片地址
     */
    private String url;

    /**
     * 资源类型。<br>
     *     <url>
     *         <li>0：图片{@link ModuleConstant#PIC_TYPE}</li>
     *         <li>1：图片文件夹{@link ModuleConstant#PIC_FOLDER_TYPE}</li>
     *         <li>2：只读图片{@link ModuleConstant#PIC_READONLY_TYPE}</li>
     *         <li>3：只读图片文件夹{@link ModuleConstant#PIC_READONLY_FOLDER_TYPE}</li>
     *     </url>
     */
    private Integer type;

    /**
     * 图片OSS路径
     */
    private String ossPath;

    /**
     * 图片OSS名称
     */
    private String ossFilename;


}
