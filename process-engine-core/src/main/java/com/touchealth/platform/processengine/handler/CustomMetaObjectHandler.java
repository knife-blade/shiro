package com.touchealth.platform.processengine.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.touchealth.platform.processengine.utils.UserUtil;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Description:数据库自定义填充处理类
 *
 * @author admin
 * @date 2020/10/28
 */
@Component
public class CustomMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createdTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updatedTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "createdBy", Long.class, UserUtil.getUserId() == null ? 0L : UserUtil.getUserId());
        this.strictInsertFill(metaObject, "updatedBy", Long.class, UserUtil.getUserId() == null ? 0L : UserUtil.getUserId());

        if (metaObject.hasSetter("moduleUniqueId") && getFieldValByName("moduleUniqueId", metaObject) == null) {
            strictInsertFill(metaObject, "moduleUniqueId", Long.class, (Long) getFieldValByName("id", metaObject));
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 手动将updatedTime和updatedBy置为空，此处设置后会覆盖代码中的赋值，也就是无法通过代码手动更新了
        metaObject.setValue("updatedTime", null);
        metaObject.setValue("updatedBy", null);

        this.strictUpdateFill(metaObject, "updatedTime", LocalDateTime.class, LocalDateTime.now());
        this.strictUpdateFill(metaObject, "updatedBy", Long.class, UserUtil.getUserId() == null ? 0L : UserUtil.getUserId());
    }
}
