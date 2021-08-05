package com.touchealth.platform.processengine.constant;

import com.touchealth.platform.processengine.entity.user.Perms;
import lombok.Getter;

/**
 * 权限资源标识符常量。<font color=red>这里只存放权限资源，其他常量不要放在这里。</font><br>
 * 菜单类型权限，命名以“MENU_”开头；功能类型权限，命名以“OP_”开头；数据类型权限，命名以“DATA_”开头；字段类型权限，命名以“FIELD_”开头；接口类型权限，命名以“API_”开头；
 * @see Perms#getPermsCode()
 */
public interface PermsConstant {

    /*
     * 菜单类型权限，命名以“MENU_”开头
     */
    /** 页面中心 菜单是否展示权限 */
    String MENU_SHOW_PAGE_CENTER = "0:menu:page-center";
    /** 渠道中心 菜单是否展示权限 */
    String MENU_SHOW_CHANNEL_CENTER = "0:menu:channel-center";
    /** 员工中心 菜单是否展示权限 */
    String MENU_SHOW_STAFF_CENTER = "0:menu:staff-center";
    /** 员工中心-部门管理 菜单是否展示权限 */
    String MENU_SHOW_DEPT_MANAGE = "0:menu:dept-manage";
    /** 员工中心-员工管理 菜单是否展示权限 */
    String MENU_SHOW_STAFF_MANAGE = "0:menu:staff-manage";
    /** 运营中心 菜单是否展示权限 */
    String MENU_SHOW_OP_CENTER = "0:menu:op-center";
    /** 运营中心-科助中心 菜单是否展示权限 */
    String MENU_SHOW_KEZHU_CENTER = "0:menu:kezhu-center";
    /** 运营中心-导出列表 菜单是否展示权限 */
    String MENU_SHOW_EXPORT_MANAGE = "0:menu:export-manage";
    /** 用户中心 菜单是否展示权限 */
    String MENU_SHOW_USER_CENTER = "0:menu:user-center";
    /** 体检中心 菜单是否展示权限 */
    String MENU_SHOW_PHYSICAL_CENTER = "0:menu:physical-center";
    /** 体检中心-体检报告 菜单是否展示权限 */
    String MENU_SHOW_PHYSICAL_REPORT = "0:menu:physical-report";
    /** 体检中心-健康档案 菜单是否展示权限 */
    String MENU_SHOW_PHYSICAL_HEALTH_ARCHIVE = "0:menu:physical-health-archive";
    /** 体检中心-项目库管理 菜单是否展示权限 */
    String MENU_SHOW_PHYSICAL_PROJECT_LIB_MANAGE = "0:menu:physical-project-lib";
    /** 体检中心-单位库管理 菜单是否展示权限 */
    String MENU_SHOW_PHYSICAL_UNIT_LIB_MANAGE = "0:menu:physical-unit-lib";
    /** 体检中心-团检企业管理 菜单是否展示权限 */
    String MENU_SHOW_PHYSICAL_GROUP_ENTERPRISE_MANAGE = "0:menu:physical-group-enterprise";
    /** 体检中心-医院管理 菜单是否展示权限 */
    String MENU_SHOW_PHYSICAL_HOSPITAL_MANAGE = "0:menu:physical-hospital";
    /** 体检中心-通用套餐管理 菜单是否展示权限 */
    String MENU_SHOW_PHYSICAL_COMMON_MEAL_MANAGE = "0:menu:physical-common-meal";
    /** 体检中心-问卷管理 菜单是否展示权限 */
    String MENU_SHOW_PHYSICAL_QUESTION_MANAGE = "0:menu:physical-question";
    /** 体检中心-全局体检日 菜单是否展示权限 */
    String MENU_SHOW_PHYSICAL_GLOBAL_DAY = "0:menu:physical-global-day";

    /*
     * 功能类型权限，命名以“OP_”开头
     */

    /** 绘制页面时选择通用组件权限 */
    String OP_PAGE_COMMON_MODULE = "1:op:page-common-module";
    /** 绘制页面时选择业务组件权限 */
    String OP_PAGE_BUSINESS_MODULE = "1:op:page-business-module";
    /** 修改页面名称、拖拽移动页面位置、删除页面等操作权限 */
    String OP_PAGE = "1:op:page";
    /** 添加页面、添加页面文件夹操作权限 */
    String OP_PAGE_MANAGE = "1:op:page-manage";
    /** 页面发布权限 */
    String OP_PAGE_RELEASE = "1:op:page-release";
    /** 页面提交发布审核权限 */
    String OP_PAGE_COMMIT_RELEASE = "1:op:page-commit-release";

    /** 渠道中心 添加一级渠道权限 */
    String OP_CHANNEL_ADD_ROOT = "1:op:channel-add-root";
    /** 渠道中心 添加子渠道权限 */
    String OP_CHANNEL_ADD = "1:op:channel-add";
    /** 渠道中心 渠道上下架操作权限 */
    String OP_CHANNEL_PUT_ON_OFF = "1:op:channel-put-on-off";
    /** 渠道中心 编辑渠道权限 */
    String OP_CHANNEL_EDIT = "1:op:channel-edit";
    /** 渠道中心 删除渠道权限 */
    String OP_CHANNEL_DEL = "1:op:channel-del";
    /** 渠道中心 编辑渠道管理员权限 */
    String OP_CHANNEL_EDIT_ADMIN = "1:op:channel-edit-admin";
    /** 渠道中心 数据管理 */
    String OP_CHANNEL_DATA_CONF = "1:op:channel-data-config";

    /** 员工中心 添加员工权限 */
    String OP_STAFF_ADD = "1:op:staff-add";
    /** 员工中心 编辑员工权限 */
    String OP_STAFF_EDIT = "1:op:staff-edit";
    /** 员工中心 删除员工权限 */
    String OP_STAFF_DEL = "1:op:staff-del";
    /** 员工中心 员工权限配置操作权限 */
    String OP_STAFF_AUTH = "1:op:staff-auth";

    /** 员工中心 岗位订单权限配置操作权限 */
    String OP_OPEN_ORDER = "1:op:open-order";
    /** 员工中心 岗位发票权限配置操作权限 */
    String OP_OPEN_INVOICE = "1:op:open-invoice";
    /** 员工中心 岗位号源权限配置操作权限 */
    String OP_OPEN_STOCK = "1:op:open-stock";
    /** 员工中心 岗位数据权限配置操作权限 */
    String OP_OPEN_DATA = "1:op:open-data";
    /** 员工中心 岗位团检权限配置操作权限 */
    String OP_OPEN_GROUP = "1:op:open-group";
    /** 员工中心 岗位短信权限配置操作权限 */
    String OP_OPEN_MESSAGE = "1:op:open-message";

    /** 用户中心 查看用户详情 */
    String OP_USER_DETAIL = "1:op:user-detail";

    /** 体检中心-体检报告 体检报告管理 */
    String OP_PHYSICAL_REPORT = "1:op:physical-report";
    /** 体检中心-体检报告 体检报告上传 */
    String OP_PHYSICAL_REPORT_UPLOAD = "1:op:physical-report-upload";
    /** 体检中心-体检报告 体检报告上传 新增任务 */
    String OP_PHYSICAL_REPORT_ADD_TASK = "1:op:physical-report-add-task";
    /** 体检中心-体检报告 体检报告上传 修改任务 */
    String OP_PHYSICAL_REPORT_UPD_TASK = "1:op:physical-report-upd-task";
    /** 体检中心-体检报告 体检报告上传 上传报告 */
    String OP_PHYSICAL_REPORT_UPLOAD_TASK = "1:op:physical-report-upload-task";
    /** 体检中心-体检报告 体检报告上传 删除 */
    String OP_PHYSICAL_REPORT_DEL_TASK = "1:op:physical-report-del-task";
    /** 体检中心-健康档案 查看详情 */
    String OP_PHYSICAL_HEALTH_ARCHIVE = "1:op:physical-health-archive-detail";
    /** 体检中心-项目库管理 新增医院 */
    String OP_PHYSICAL_PROJECT_LIB_ADD_HOSPITAL = "1:op:physical-prolib-add-hosp";
    /** 体检中心-项目库管理 标准库医院编辑 */
    String OP_PHYSICAL_PROJECT_LIB_COMMON_LIB_UPD_HOSPITAL = "1:op:physical-prolib-comlib-upd-hosp";
    /** 体检中心-项目库管理 个性库医院编辑 */
    String OP_PHYSICAL_PROJECT_LIB_SPEC_LIB_UPD_HOSPITAL = "1:op:physical-prolib-speclib-upd-hosp";
    /** 体检中心-项目库管理 个性库医院删除 */
    String OP_PHYSICAL_PROJECT_LIB_SPEC_LIB_DEL_HOSPITAL = "1:op:physical-prolib-speclib-del-hosp";
    /** 体检中心-团检业务管理 团检管理 */
    String OP_PHYSICAL_GROUP_CO_MANGE = "1:op:physical-groupco-mag";
    /** 体检中心-团检业务管理 团检管理 新增团检 */
    String OP_PHYSICAL_GROUP_CO_ADD = "1:op:physical-groupco-add";
    /** 体检中心-团检业务管理 团检管理 体检人配置 */
    String OP_PHYSICAL_GROUP_CO_PERSON_CONF = "1:op:physical-groupco-person-conf";
    /** 体检中心-团检业务管理 团检管理 编辑 */
    String OP_PHYSICAL_GROUP_CO_UPD = "1:op:physical-groupco-upd";
    /** 体检中心-团检业务管理 团检管理 上线 */
    String OP_PHYSICAL_GROUP_CO_RELEASE = "1:op:physical-groupco-release";
    /** 体检中心-团检业务管理 团检管理 预约情况 */
    String OP_PHYSICAL_GROUP_CO_RESERVE = "1:op:physical-groupco-reserve";
    /** 体检中心-团检业务管理 团检管理 删除 */
    String OP_PHYSICAL_GROUP_CO_DEL = "1:op:physical-groupco-del";
    /** 体检中心-团检业务管理 企业管理 */
    String OP_PHYSICAL_GROUP_CO_CO_MANGE = "1:op:physical-groupco-co-mag";
    /** 体检中心-团检业务管理 企业管理 新增企业 */
    String OP_PHYSICAL_GROUP_CO_CO_ADD = "1:op:physical-groupco-co-add";
    /** 体检中心-团检业务管理 企业管理 编辑 */
    String OP_PHYSICAL_GROUP_CO_CO_UPD = "1:op:physical-groupco-co-upd";
    /** 体检中心-团检业务管理 企业管理 删除 */
    String OP_PHYSICAL_GROUP_CO_CO_DEL = "1:op:physical-groupco-co-del";
    /** 体检中心-医院管理 基本信息 */
    String OP_PHYSICAL_HOSPITAL_BASE = "1:op:physical-hosp-base";
    /** 体检中心-医院管理 号源信息 */
    String OP_PHYSICAL_HOSPITAL_SOURCE_NUM = "1:op:physical-hosp-src-num";
    /** 体检中心-医院管理 支付配置 */
    String OP_PHYSICAL_HOSPITAL_PAY_CONF = "1:op:physical-hosp-pay-conf";
    /** 体检中心-医院管理 网点配置 */
    String OP_PHYSICAL_HOSPITAL_WEBSITE_CONF = "1:op:physical-hosp-website-conf";
    /** 体检中心-医院管理 体检套餐 */
    String OP_PHYSICAL_HOSPITAL_MEAL = "1:op:physical-hosp-meal";
    /** 体检中心-医院管理 加项包配置 */
    String OP_PHYSICAL_HOSPITAL_ITEM_PKG_CONF = "1:op:physical-hosp-item-pkg-conf";
    /** 体检中心-医院管理 问卷管理 */
    String OP_PHYSICAL_HOSPITAL_QUESTION_CONF = "1:op:physical-hops-question-conf";
    /**
     * 体检中心-问卷管理 添加问卷
     */
    String OP_PHYSICAL_QUESTION_ADD = "1:op:physical-question-add";
    /**
     * 体检中心-问卷管理 问卷内容
     */
    String OP_PHYSICAL_QUESTION_CONTENT = "1:op:physical-question-content";
    /**
     * 体检中心-问卷管理 问卷规则
     */
    String OP_PHYSICAL_QUESTION_RULE = "1:op:physical-question-rule";
    /**
     * 体检中心-问卷管理 启用禁用
     */
    String OP_PHYSICAL_QUESTION_ON_OFF = "1:op:physical-question-on-off";

    /*
     * 数据类型权限，命名以“DATA_”开头
     */
    // TODO pass

    /*
     * 字段类型权限，命名以“FIELD_”开头
     */
    // TODO pass

    /*
     * 接口类型权限，命名以“API_”开头
     */
    // TODO pass

    @Getter
    enum  KeZhuPermEnum {
        OP_OPEN_ORDER ("OP_OPEN_ORDER","订单管理"),
        OP_OPEN_INVOICE("OP_OPEN_INVOICE","发票管理") ,
        OP_OPEN_STOCK ("OP_OPEN_STOCK","号源管理") ,
        OP_OPEN_DATA ("OP_OPEN_DATA","数据看板") ,
        OP_OPEN_GROUP ("OP_OPEN_GROUP","团检管理") ,
        OP_OPEN_MESSAGE("OP_OPEN_MESSAGE","公众号消息推送管理") ;
        private String code;
        private String desc;
        KeZhuPermEnum(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }

}
