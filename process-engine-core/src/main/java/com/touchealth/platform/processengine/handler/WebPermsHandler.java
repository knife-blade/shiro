package com.touchealth.platform.processengine.handler;

import com.touchealth.platform.processengine.constant.PermsConstant;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户将权限标识符转为前端显示的格式
 */
public class WebPermsHandler {

    private static final Map<String, String> WEB_NAME_PERM_MAP;
    static {
        WEB_NAME_PERM_MAP = new HashMap<>();
        WEB_NAME_PERM_MAP.put(PermsConstant.MENU_SHOW_PAGE_CENTER, "页面中心");
        WEB_NAME_PERM_MAP.put(PermsConstant.MENU_SHOW_CHANNEL_CENTER, "渠道中心");
        WEB_NAME_PERM_MAP.put(PermsConstant.MENU_SHOW_STAFF_CENTER, "员工中心");
        WEB_NAME_PERM_MAP.put(PermsConstant.MENU_SHOW_DEPT_MANAGE, "部门管理");
        WEB_NAME_PERM_MAP.put(PermsConstant.MENU_SHOW_STAFF_MANAGE, "员工管理");
        WEB_NAME_PERM_MAP.put(PermsConstant.MENU_SHOW_OP_CENTER, "运营中心");
        WEB_NAME_PERM_MAP.put(PermsConstant.MENU_SHOW_KEZHU_CENTER, "科助中心");
        WEB_NAME_PERM_MAP.put(PermsConstant.MENU_SHOW_EXPORT_MANAGE, "导出列表");
        WEB_NAME_PERM_MAP.put(PermsConstant.MENU_SHOW_PHYSICAL_CENTER, "体检中心");
        WEB_NAME_PERM_MAP.put(PermsConstant.MENU_SHOW_PHYSICAL_REPORT, "体检报告");
        WEB_NAME_PERM_MAP.put(PermsConstant.MENU_SHOW_PHYSICAL_HEALTH_ARCHIVE, "健康档案");
        WEB_NAME_PERM_MAP.put(PermsConstant.MENU_SHOW_PHYSICAL_PROJECT_LIB_MANAGE, "项目库管理");
        WEB_NAME_PERM_MAP.put(PermsConstant.MENU_SHOW_PHYSICAL_UNIT_LIB_MANAGE, "单位库管理");
        WEB_NAME_PERM_MAP.put(PermsConstant.MENU_SHOW_PHYSICAL_GROUP_ENTERPRISE_MANAGE, "团检企业管理");
        WEB_NAME_PERM_MAP.put(PermsConstant.MENU_SHOW_PHYSICAL_HOSPITAL_MANAGE, "医院管理");
        WEB_NAME_PERM_MAP.put(PermsConstant.MENU_SHOW_PHYSICAL_COMMON_MEAL_MANAGE, "通用套餐管理");
        WEB_NAME_PERM_MAP.put(PermsConstant.MENU_SHOW_PHYSICAL_QUESTION_MANAGE, "问卷管理");
        WEB_NAME_PERM_MAP.put(PermsConstant.MENU_SHOW_PHYSICAL_GLOBAL_DAY, "全局体检日");
        WEB_NAME_PERM_MAP.put(PermsConstant.MENU_SHOW_USER_CENTER, "用户中心");

        WEB_NAME_PERM_MAP.put(PermsConstant.OP_PAGE_COMMON_MODULE, "通用组件");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_PAGE_BUSINESS_MODULE, "业务组件");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_PAGE, "操作页面");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_PAGE_MANAGE, "管理页面");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_PAGE_RELEASE, "发布");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_PAGE_COMMIT_RELEASE, "提交审核");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_CHANNEL_ADD_ROOT, "新增渠道");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_CHANNEL_ADD, "添加子渠道");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_CHANNEL_PUT_ON_OFF, "上/下架");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_CHANNEL_EDIT, "编辑");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_CHANNEL_DEL, "删除");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_CHANNEL_EDIT_ADMIN, "管理员账号");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_CHANNEL_DATA_CONF, "数据配置");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_STAFF_ADD, "新建");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_STAFF_EDIT, "编辑");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_STAFF_DEL, "删除");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_STAFF_AUTH, "权限配置");

        WEB_NAME_PERM_MAP.put(PermsConstant.OP_OPEN_ORDER, "订单管理");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_OPEN_INVOICE, "发票管理");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_OPEN_STOCK, "号源管理");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_OPEN_DATA, "数据看板");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_OPEN_GROUP, "团检管理");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_OPEN_MESSAGE, "公众号消息推送管理");

        WEB_NAME_PERM_MAP.put(PermsConstant.OP_USER_DETAIL, "查看详情");

        WEB_NAME_PERM_MAP.put(PermsConstant.OP_PHYSICAL_REPORT, "体检报告管理");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_PHYSICAL_REPORT_UPLOAD, "体检报告上传");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_PHYSICAL_REPORT_ADD_TASK, "新增任务");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_PHYSICAL_REPORT_UPD_TASK, "修改任务");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_PHYSICAL_REPORT_UPLOAD_TASK, "上传报告");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_PHYSICAL_REPORT_DEL_TASK, "删除");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_PHYSICAL_HEALTH_ARCHIVE, "查看详情");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_PHYSICAL_PROJECT_LIB_ADD_HOSPITAL, "新增医院");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_PHYSICAL_PROJECT_LIB_COMMON_LIB_UPD_HOSPITAL, "标准库医院编辑");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_PHYSICAL_PROJECT_LIB_SPEC_LIB_UPD_HOSPITAL, "个性库医院编辑");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_PHYSICAL_PROJECT_LIB_SPEC_LIB_DEL_HOSPITAL, "个性库医院删除");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_PHYSICAL_GROUP_CO_MANGE, "团检管理");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_PHYSICAL_GROUP_CO_ADD, "新增团检");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_PHYSICAL_GROUP_CO_PERSON_CONF, "体检人配置");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_PHYSICAL_GROUP_CO_UPD, "编辑");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_PHYSICAL_GROUP_CO_RELEASE, "上线");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_PHYSICAL_GROUP_CO_RESERVE, "预约情况");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_PHYSICAL_GROUP_CO_DEL, "删除");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_PHYSICAL_GROUP_CO_CO_MANGE, "企业管理");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_PHYSICAL_GROUP_CO_CO_ADD, "新增企业");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_PHYSICAL_GROUP_CO_CO_UPD, "编辑");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_PHYSICAL_GROUP_CO_CO_DEL, "删除");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_PHYSICAL_HOSPITAL_BASE, "基本信息");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_PHYSICAL_HOSPITAL_SOURCE_NUM, "号源信息");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_PHYSICAL_HOSPITAL_PAY_CONF, "支付配置");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_PHYSICAL_HOSPITAL_WEBSITE_CONF, "网点配置");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_PHYSICAL_HOSPITAL_MEAL, "体检套餐");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_PHYSICAL_HOSPITAL_ITEM_PKG_CONF, "加项包配置");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_PHYSICAL_HOSPITAL_QUESTION_CONF, "问卷管理");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_PHYSICAL_QUESTION_ADD, "新增问卷");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_PHYSICAL_QUESTION_CONTENT, "设置问卷内容");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_PHYSICAL_QUESTION_RULE, "问卷管理-设置评估规则");
        WEB_NAME_PERM_MAP.put(PermsConstant.OP_PHYSICAL_QUESTION_ON_OFF, "问卷管理-启用禁用");

    }

    public static String getPermWebName(String permCode) {
        return WEB_NAME_PERM_MAP.getOrDefault(permCode, "");
    }

}
