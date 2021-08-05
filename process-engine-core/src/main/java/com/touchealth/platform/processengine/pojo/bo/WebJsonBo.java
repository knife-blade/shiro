package com.touchealth.platform.processengine.pojo.bo;

import com.touchealth.platform.processengine.constant.PageCenterConsts;
import com.touchealth.platform.processengine.constant.WebJsonConstant;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 前端传入的json对应的传输对象
 * @see WebJsonConstant
 */
@Data
public class WebJsonBo {

    /**
     * 后端唯一标识
     */
    private Long id;
    /**
     * 版本间的唯一组件标识。同一个组件，不同版本该值不变。
     */
    private Long moduleUniqueId;

    /**
     * 组件所属分类。0：通用组件；1：业务组件；
     */
    private Integer belongType;
    private String belongTypeDesc;
    /**
     * 组件类别。0：页面组件；1：营销组件；2：其他组件；3：体检组件；4：商城组件；5：健管组件；
     */
    private Integer category;
    private String categoryDesc;
    /**
     * 组件类型。0：轮播图；1：坑位导航；2：热区；3：列表多图；4：间隔；5：固定按钮；
     */
    private Integer moduleType;
    private String moduleTypeDesc;
    /**
     * 组件名称。0：banner；1：grid；2：hotspot；3：chunk；4：divider；5：fixed-button；
     */
    private String name;
    /**
     * 组件前端ID
     */
    private String blockId;
    /**
     * 组件布局类型，根据组件各自定义
     */
    private Integer layoutType;
    /**
     * 组件渲染状态，用于判断渲染组件占位图还是真实数据。'INIT'：初始化；'PREVIEW'：预览；
     */
    private String status;
    /**
     * 组件样式，根据组件各自定义，后续可以扩展组件边距、圆角等
     */
    private WebJsonStyleBo style;
    /**
     * 是否内置组件。默认：true；
     */
    private Boolean isInitModule;
    /**
     * 内置组件是否隐藏 （只在isInitModule 为ture中使用，默认为false）
     */
    private Boolean isHidden;
    /**
     * 组件数据，根据组件各自定义
     */
    private WebJsonDataBo data;

    /**
     * 链接组件数据对象
     */
    @Data
    public static class WebJsonLinkBo {

        /**
         * 后端唯一标识
         */
        private Long id;
        /**
         * 版本间的唯一组件标识。同一个组件，不同版本该值不变。
         */
        private Long moduleUniqueId;
        /**
         * 链接类型。0：站内链接；1：站外链接；
         */
        private Integer linkType;
        /**
         * 页面类型
         * @see PageCenterConsts.BusinessType
         */
        private Integer businessType;
        /**
         * 页面类型字符串
         * @see PageCenterConsts.BusinessType
         */
        private String businessTypeName;
        /**
         * 页面版本唯一ID（62进制）
         * 实际上存储的是pageUniqueId
         */
        private String pageId;
        /**
         * 页面路由，仅当 linkType 为 0 时可用，通用页面统一为 entry，业务页面各自定义
         */
        private String routerName;
        /**
         * 页面名称，仅当 linkType 为 0 时可用
         */
        private String pageName;
        /**
         * 站外链接地址，仅当 linkType 为 1 时可用
         */
        private String pageUrl;
        /**
         * 跳转页面参数，仅当 linkType 为 0 时可用
         */
        private Map<String, Object> params;

        public void setBusinessTypeName(String businessTypeName) {
            if (businessType == null) {
                this.businessTypeName = "";
            } else {
                this.businessTypeName = PageCenterConsts.BusinessType.getNameByCode(businessType);
            }
        }

        public String getBusinessTypeName() {
            if (businessType == null) {
                return "";
            }
            return PageCenterConsts.BusinessType.getNameByCode(businessType);
        }
    }

    /**
     * 前端组件数据对象
     */
    @Data
    public static class WebJsonDataBo {
        /**
         * 图片列表
         */
        private List<WebJsonImgBo> imgList;
        /**
         * 按钮列表
         */
        private List<WebJsonButtonBo> buttonList;
        /**
         * 虚拟按钮列表
         */
        private List<WebJsonVtButtonBo> vtButtonList;

        /**
         * 标题
         */
        private String title;

        /**
         * 图片
         */
        private String imgUrl;

        /**
         * 图片地址ID供前端使用
         */
        private String imgId;

        /**
         * 背景图片
         */
        private String bgImg;

        /**
         * 模块列表 如1：体检订单 2：商城订单 3：服务订单 4：卡券
         */
        private List<Integer> module;

        /**
         * 数据统计数显示。 如1：收藏商品 2：收藏医院 3：我的问答 4：浏览记录 5：账户余额 6：卡券 7：优惠券 8：我的积分
         */
        private List<Integer> dataStatistics;

        /**
         * 热区详情样式列表
         */
        private List<HotspotPartsBo> hotspotList;

        /******************************** 登录组件相关 开始 *********************************/
        /**
         * 中文主标题
         */
        private String titleChinese;

        /**
         * 中文副标题
         */
        private String subtitleChinese;

        /**
         * 中文用户协议
         */
        private String agreementUrlChinese;

        /**
         * 中文隐私协议
         */
        private String privacyAgreementUrlChinese;

        /**
         * 英文主标题
         */
        private String titleEng;

        /**
         * 英文副标题
         */
        private String subtitleEng;

        /**
         * 英文用户协议
         */
        private String agreementUrlEng;

        /**
         * 英文隐私协议
         */
        private String privacyAgreementUrlEng;
        /******************************** 登录组件相关 结束  *********************************/

    }

    /**
     * 热区样式
     */
    @Data
    public static class HotspotPartsBo{
        /**
         * 后端唯一标识
         */
        private Long id;

        /**
         * 版本间的唯一组件标识。同一个组件，不同版本该值不变。
         */
        private Long moduleUniqueId;

        private String uuid;

        /**
         * 标题
         */
        private String title;

        /**
         * 热区宽度
         */
        private Integer width;

        /**
         * 热区高度度
         */
        private Integer height;

        /**
         * 以绘制面板左上角为原点，热区 X 轴偏移
         */
        private Integer offsetX;
        /**
         * 以绘制面板左上角为原点，热区 Y 轴偏移
         */
        private Integer offsetY;
        /**
         * 以背景图左上角为原点，热区 X 轴偏移
         */
        private Integer realOffsetX;
        /**
         * 以背景图左上角为原点，热区 Y 轴偏移
         */
        private Integer realOffsetY;
        /**
         * 链接信息
         */
        private WebJsonLinkBo link;
    }

    /**
     * 图片列表数据对象
     */
    @Data
    public static class WebJsonImgBo {
        /**
         * 后端唯一标识
         */
        private Long id;
        /**
         * 版本间的唯一组件标识。同一个组件，不同版本该值不变。
         */
        private Long moduleUniqueId;
        /**
         * 图片名
         */
        private String title;
        /**
         * 图片地址
         */
        private String imgUrl;

        /**
         * 图片地址ID供前端使用
         */
        private String imgId;
        /**
         * 链接信息
         */
        private WebJsonLinkBo link;
        /**
         * 投放时间。[开始时间,截止时间]
         */
        private List<Date> period;
    }



    /**
     * 按钮列表数据对象
     */
    @Data
    public static class WebJsonButtonBo {
        /**
         * 后端唯一标识
         */
        private Long id;
        /**
         * 版本间的唯一组件标识。同一个组件，不同版本该值不变。
         */
        private Long moduleUniqueId;
        /**
         * 按钮标题
         */
        private String title;
        /**
         * 文字颜色，仅当 layoutType 为 1 或 2 时可用
         */
        private String color;
        /**
         * 背景颜色，仅当 layoutType 为 1 或 2 时可用
         */
        private String bgColor;
        /**
         * 图片链接，仅当 layoutType 为 3 时可用
         */
        private String imgUrl;
        /**
         * 跳转链接，结构同链接组件
         */
        private WebJsonLinkBo link;
    }

    /**
     * 虚拟按钮列表数据对象(如收藏商品、我的问答等统计数据)
     */
    @Data
    public static class WebJsonVtButtonBo {
        /**
         * 后端唯一标识
         */
        private Long id;
        /**
         * 版本间的唯一组件标识。同一个组件，不同版本该值不变。
         */
        private Long moduleUniqueId;
        /**
         * 按钮标题
         */
        private String title;
        /**
         * 虚拟按钮类型 1:数据统计数显示 2:默认按钮
         */
        private Integer type;
        /**
         * 跳转链接，结构同链接组件
         */
        private WebJsonLinkBo link;
    }

    @Data
    public static class WebJsonStyleBo {
        /**
         * 宽度
         */
        Integer width;
        /**
         * 高度
         */
        Integer height;

        public String styleStr(){
            return this.width+"*"+this.height+"像素";
        }
    }
}
