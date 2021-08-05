package com.touchealth.platform.processengine.handler;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.touchealth.platform.processengine.constant.ModuleConstant;
import com.touchealth.platform.processengine.constant.PageCenterConsts;
import com.touchealth.platform.processengine.entity.page.PageManager;
import com.touchealth.platform.processengine.exception.ParameterException;
import com.touchealth.platform.processengine.pojo.bo.WebJsonBo;
import com.touchealth.platform.processengine.pojo.bo.module.common.*;
import com.touchealth.platform.processengine.pojo.dto.module.common.DelimiterDto;
import com.touchealth.platform.processengine.pojo.dto.module.common.LinkDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.touchealth.platform.processengine.constant.WebJsonConstant.WEB_INSIDE_LINK_TYPE_ARTICLE;
import static com.touchealth.platform.processengine.constant.WebJsonConstant.WEB_INSIDE_LINK_TYPE_GOODS;
import static com.touchealth.platform.processengine.constant.WebJsonConstant.WEB_INSIDE_LINK_TYPE_HEALTH_HOSPITAL;
import static com.touchealth.platform.processengine.constant.WebJsonConstant.WEB_INSIDE_LINK_TYPE_HEALTH_SET_MEAL;
import static com.touchealth.platform.processengine.constant.WebJsonConstant.WEB_INSIDE_LINK_TYPE_PAGE;
import static com.touchealth.platform.processengine.constant.WebJsonConstant.WEB_INSIDE_LINK_TYPE_VIDEO;
import static com.touchealth.platform.processengine.constant.WebJsonConstant.WEB_LINK_TYPE_OUTSIDE;

/**
 * @author liufengqiang
 * @date 2020-11-21 11:46:04
 */
@Slf4j
public class ModuleHandler {

    /**
     * 按钮解析
     * @param webJson
     * @return
     */
    public static BtnGroupBo parseBtn(String webJson) {
        return parseBtn(null, webJson);
    }

    /**
     * 按钮解析
     *
     * @param webJson
     */
    public static BtnGroupBo parseBtn(PageManager pageManager, String webJson) {
        Assert.isTrue(StringUtils.isNotBlank(webJson), "配置信息不能为空");
        BtnGroupBo btnGroupBo = new BtnGroupBo();
        btnGroupBo.setWebJson(webJson);
        if (pageManager != null) {
            btnGroupBo.setChannelNo(pageManager.getChannelNo());
            btnGroupBo.setPageId(pageManager.getId());
            btnGroupBo.setPageName(pageManager.getPageName());
            btnGroupBo.setStatus(pageManager.getStatus());
            btnGroupBo.setVersion(pageManager.getVersionId());
        }
        try {
            WebJsonBo webJsonBo = JSONObject.parseObject(webJson, WebJsonBo.class);
            if (webJsonBo.getData() == null || CollectionUtils.isEmpty(webJsonBo.getData().getButtonList())) {
                return null;
            }
            btnGroupBo.setId(webJsonBo.getId());
            btnGroupBo.setModuleUniqueId(webJsonBo.getModuleUniqueId());
            List<WebJsonBo.WebJsonButtonBo> btnWebJsonList = webJsonBo.getData().getButtonList();
            List<BtnBo> btnBos = IntStream.range(0, btnWebJsonList.size()).mapToObj(idx -> {
                BtnBo btnBo = new BtnBo();
                WebJsonBo.WebJsonButtonBo btnWebJson = btnWebJsonList.get(idx);
                Long btnId = btnWebJson.getId();
                String btnName = btnWebJson.getTitle();
                String btnBgUrl = btnWebJson.getImgUrl();
                WebJsonBo.WebJsonLinkBo linkWebJson = btnWebJson.getLink();
                Long btnModuleUniqueId = btnWebJson.getModuleUniqueId();
                LinkDto linkDto = null;
                if(null != linkWebJson){
                    linkDto = new LinkDto();
                    wrapLinkDto(linkWebJson, linkDto);
                }
                btnBo.setId(btnId);
                btnBo.setModuleUniqueId(btnModuleUniqueId);
                btnBo.setName(btnName);
                btnBo.setBgUrl(btnBgUrl);
                btnBo.setSort(idx);
                btnBo.setLinkDto(linkDto);
//                btnBo.setWebJson(JSONObject.toJSONString(btnWebJson));
                return btnBo;
            }).collect(Collectors.toList());

            btnGroupBo.setButtons(btnBos);

        } catch (JSONException e) {
            throw new ParameterException("webJson格式异常");
        } catch (Exception e) {
            log.error("ModuleService.parseBtn has exception", e);
            throw new ParameterException("webJson解析异常");
        }
        return btnGroupBo;
    }

    /**
     * 链接组件解析
     *
     * @param webJson
     */
    public static LinkDto parseLink(String webJson) {
        return parseLink(null, webJson);
    }

    /**
     * 链接组件解析
     *
     * @param webJson
     */
    public static LinkDto parseLink(PageManager pageManager, String webJson) {
        Assert.isTrue(StringUtils.isNotBlank(webJson), "配置信息不能为空");
        LinkDto linkDto = new LinkDto();
        linkDto.setWebJson(webJson);
        try {
            WebJsonBo.WebJsonLinkBo linkWebJson = JSONObject.parseObject(webJson, WebJsonBo.WebJsonLinkBo.class);
            if (pageManager != null) {
                linkDto.setChannelNo(pageManager.getChannelNo());
                linkDto.setPageId(pageManager.getId());
                linkDto.setStatus(pageManager.getStatus());
                linkDto.setVersion(pageManager.getVersionId());
            }
            wrapLinkDto(linkWebJson, linkDto);
        } catch (JSONException e) {
            throw new ParameterException("webJson格式异常");
        } catch (Exception e) {
            log.error("ModuleService.parseBtn has exception", e);
            throw new ParameterException("webJson解析异常");
        }
        return linkDto;
    }

    /**
     * 链接组件解析
     *
     * @param webJson
     */
    public static DelimiterDto parseDelimiter(String webJson) {
        return parseDelimiter(null, webJson);
    }

    /**
     * 链接组件解析
     *
     * @param webJson
     */
    public static DelimiterDto parseDelimiter(PageManager pageManager, String webJson) {
        Assert.isTrue(StringUtils.isNotBlank(webJson), "配置信息不能为空");
        DelimiterDto delimiterDto = new DelimiterDto();
        try {
            WebJsonBo delimiterWebJson = JSONObject.parseObject(webJson, WebJsonBo.class);
            delimiterDto.setWebJson(webJson);
            if (pageManager != null) {
                delimiterDto.setChannelNo(pageManager.getChannelNo());
                delimiterDto.setPageId(pageManager.getId());
                delimiterDto.setStatus(pageManager.getStatus());
                delimiterDto.setVersion(pageManager.getVersionId());
            }

            delimiterDto.setId(delimiterWebJson.getId());
            delimiterDto.setModuleUniqueId(delimiterWebJson.getModuleUniqueId());
            delimiterDto.setName("");
        } catch (JSONException e) {
            throw new ParameterException("webJson格式异常");
        } catch (Exception e) {
            log.error("ModuleService.parseBtn has exception", e);
            throw new ParameterException("webJson解析异常");
        }
        return delimiterDto;
    }

    public static LoginBo parseLogin(PageManager pageManager, String webJson){
        Assert.isTrue(StringUtils.isNotBlank(webJson), "配置信息不能为空");
        LoginBo loginBo = new LoginBo();
        if(null != pageManager) {
            loginBo.setPageId(pageManager.getId());
            loginBo.setStatus(pageManager.getStatus());
            loginBo.setVersion(pageManager.getVersionId());
            loginBo.setChannelNo(pageManager.getChannelNo());
        }
        loginBo.setWebJson(webJson);
        try {
            WebJsonBo webJsonBo = JSONObject.parseObject(webJson, WebJsonBo.class);
            if (webJsonBo.getData() == null ) {
                throw new ParameterException("login页面数据不能为空不能为空");
            }
            WebJsonBo.WebJsonDataBo loginData = webJsonBo.getData();
            loginBo.setId(webJsonBo.getId());
            loginBo.setModuleUniqueId(webJsonBo.getModuleUniqueId());
            loginBo.setTitleChinese(loginData.getTitleChinese());
            loginBo.setTitleEng(loginData.getTitleEng());
            loginBo.setAgreementUrlChinese(loginData.getAgreementUrlChinese());
            loginBo.setAgreementUrlEng(loginData.getAgreementUrlEng());
            loginBo.setPrivacyAgreementUrlChinese(loginData.getPrivacyAgreementUrlChinese());
            loginBo.setPrivacyAgreementUrlEng(loginData.getPrivacyAgreementUrlEng());
            loginBo.setSubtitleChinese(loginData.getSubtitleChinese());
            loginBo.setSubtitleEng(loginData.getSubtitleEng());
        } catch (JSONException e) {
            throw new ParameterException("webJson格式异常");
        } catch (Exception e) {
            log.error("ModuleService.parseLogin has exception", e);
            throw new ParameterException("webJson解析异常");
        }
        return loginBo;
    }

    public static BannerBo parseBanner(PageManager pageManager, String webJson){
        Assert.isTrue(StringUtils.isNotBlank(webJson), "配置信息不能为空");
        BannerBo bannerBo = new BannerBo();
        if(null != pageManager) {
            bannerBo.setChannelNo(pageManager.getChannelNo());
            bannerBo.setPageId(pageManager.getId());
            bannerBo.setStatus(pageManager.getStatus());
            bannerBo.setVersion(pageManager.getVersionId());
        }
        bannerBo.setWebJson(webJson);
        try {
            WebJsonBo webJsonBo = JSONObject.parseObject(webJson, WebJsonBo.class);
            if (webJsonBo.getData() == null || CollectionUtils.isEmpty(webJsonBo.getData().getImgList())) {
                throw new ParameterException("banner不能为空");
            }
            bannerBo.setId(webJsonBo.getId());
            bannerBo.setModuleUniqueId(webJsonBo.getModuleUniqueId());
            List<WebJsonBo.WebJsonImgBo> bannerWebJsonList = webJsonBo.getData().getImgList();
            List<BannerImgBo> BannerImgBos = IntStream.range(0, bannerWebJsonList.size()).mapToObj(idx -> {
                BannerImgBo bannerImgBo = new BannerImgBo();
                WebJsonBo.WebJsonImgBo bannerWebJson = bannerWebJsonList.get(idx);
                Long btnModuleUniqueId = bannerWebJson.getModuleUniqueId();
                String bannerName = bannerWebJson.getTitle();
                String bannerUrl = bannerWebJson.getImgUrl();
                List<Date> period = bannerWebJson.getPeriod();
                WebJsonBo.WebJsonLinkBo linkWebJson = bannerWebJson.getLink();
                LinkDto linkDto = null;
                if(null != linkWebJson){
                    linkDto = new LinkDto();
                    wrapLinkDto(linkWebJson, linkDto);
                }
                bannerImgBo.setName(bannerName);
                bannerImgBo.setUrl(bannerUrl);
                bannerImgBo.setSort(idx);
                bannerImgBo.setLinkDto(linkDto);
                if(!CollectionUtils.isEmpty(period)){
                    bannerImgBo.setShowStartTime(period.get(0));
                    bannerImgBo.setShowEndTime(period.size() > 1 ? period.get(1) : null);
                }
                //bannerImgBo.setWebJson(JSONObject.toJSONString(bannerWebJson));
                bannerImgBo.setModuleUniqueId(btnModuleUniqueId);
                bannerImgBo.setId(bannerWebJson.getId());
                return bannerImgBo;
            }).collect(Collectors.toList());

            bannerBo.setBannerImgs(BannerImgBos);

        } catch (JSONException e) {
            throw new ParameterException("webJson格式异常");
        } catch (Exception e) {
            log.error("ModuleService.parseBanner has exception", e);
            throw new ParameterException("webJson解析异常");
        }
        return bannerBo;
    }

    public static HotspotBo parseHotspot(PageManager pageManager, String webJson){
        Assert.isTrue(StringUtils.isNotBlank(webJson), "配置信息不能为空");
        HotspotBo hotspotBo = new HotspotBo();
        if(null != pageManager) {
            hotspotBo.setChannelNo(pageManager.getChannelNo());
            hotspotBo.setPageId(pageManager.getId());
            hotspotBo.setStatus(pageManager.getStatus());
            hotspotBo.setVersion(pageManager.getVersionId());
        }
        hotspotBo.setWebJson(webJson);
        try {
            WebJsonBo webJsonBo = JSONObject.parseObject(webJson, WebJsonBo.class);
            if (webJsonBo.getData() == null ) {
                throw new ParameterException("热区数据不能为空");
            }
            hotspotBo.setId(webJsonBo.getId());
            hotspotBo.setModuleUniqueId(webJsonBo.getModuleUniqueId());
            hotspotBo.setTitle(webJsonBo.getData().getTitle());
            hotspotBo.setUrl(webJsonBo.getData().getImgUrl());
            List<WebJsonBo.HotspotPartsBo> hotspotWebJsonList = webJsonBo.getData().getHotspotList();
            List<HotspotPartsBo> hotspotPartsBos = IntStream.range(0, hotspotWebJsonList.size()).mapToObj(idx -> {
                HotspotPartsBo hotspotPartsBo = new HotspotPartsBo();
                WebJsonBo.HotspotPartsBo webJsonPartsBo = hotspotWebJsonList.get(idx);
                Long partsModuleUniqueId = webJsonPartsBo.getModuleUniqueId();
                String name = webJsonPartsBo.getTitle();
                WebJsonBo.WebJsonLinkBo linkWebJson = webJsonPartsBo.getLink();
                LinkDto linkDto = null;
                if(null != linkWebJson){
                    linkDto = new LinkDto();
                    wrapLinkDto(linkWebJson, linkDto);
                }
                hotspotPartsBo.setName(name);
                hotspotPartsBo.setSort(idx);
                hotspotPartsBo.setLinkDto(linkDto);
                hotspotPartsBo.setModuleUniqueId(partsModuleUniqueId);
                hotspotPartsBo.setStyle(webJsonPartsBo);
                hotspotPartsBo.setId(webJsonPartsBo.getId());
                return hotspotPartsBo;
            }).collect(Collectors.toList());

            hotspotBo.setHotspotPartsBos(hotspotPartsBos);

        } catch (JSONException e) {
            throw new ParameterException("webJson格式异常");
        } catch (Exception e) {
            log.error("ModuleService.parseHotspot has exception", e);
            throw new ParameterException("webJson解析异常");
        }
        return hotspotBo;
    }

    public static NavigateBo parseNavigate(PageManager pageManager, String webJson){
        Assert.isTrue(StringUtils.isNotBlank(webJson), "配置信息不能为空");
        NavigateBo navigateBo = new NavigateBo();
        navigateBo.setWebJson(webJson);
        if(null != pageManager) {
            navigateBo.setChannelNo(pageManager.getChannelNo());
            navigateBo.setPageId(pageManager.getId());
            navigateBo.setStatus(pageManager.getStatus());
            navigateBo.setVersion(pageManager.getVersionId());
        }
        try {
            WebJsonBo webJsonBo = JSONObject.parseObject(webJson, WebJsonBo.class);
            if (webJsonBo.getData() == null || CollectionUtils.isEmpty(webJsonBo.getData().getImgList())) {
                throw new ParameterException("图片数据不能为空");
            }
            navigateBo.setId(webJsonBo.getId());
            navigateBo.setModuleUniqueId(webJsonBo.getModuleUniqueId());
            List<WebJsonBo.WebJsonImgBo> navigateWebJsonList = webJsonBo.getData().getImgList();
            List<NavigateImgBo> navigateImgBos = IntStream.range(0, navigateWebJsonList.size()).mapToObj(idx -> {
                NavigateImgBo navigateImgBo = new NavigateImgBo();
                WebJsonBo.WebJsonImgBo navigateWebJson = navigateWebJsonList.get(idx);
                Long btnModuleUniqueId = navigateWebJson.getModuleUniqueId();
                String bannerName = navigateWebJson.getTitle();
                String bannerUrl = navigateWebJson.getImgUrl();
                WebJsonBo.WebJsonLinkBo linkWebJson = navigateWebJson.getLink();
                LinkDto linkDto = null;
                if(null != linkWebJson){
                    linkDto = new LinkDto();
                    wrapLinkDto(linkWebJson, linkDto);
                }
                navigateImgBo.setName(bannerName);
                navigateImgBo.setUrl(bannerUrl);
                navigateImgBo.setSort(idx);
                navigateImgBo.setLinkDto(linkDto);
                navigateImgBo.setId(navigateWebJson.getId());
                //navigateImgBo.setWebJson(JSONObject.toJSONString(bannerWebJson));
                navigateImgBo.setModuleUniqueId(btnModuleUniqueId);
                return navigateImgBo;
            }).collect(Collectors.toList());

            navigateBo.setNavigateImgBos(navigateImgBos);

        } catch (JSONException e) {
            throw new ParameterException("webJson格式异常");
        } catch (Exception e) {
            log.error("ModuleService.parseNavigate has exception", e);
            throw new ParameterException("webJson解析异常");
        }
        return navigateBo;
    }

    public static ComboImgBo parseComboImg(PageManager pageManager, String webJson){
        Assert.isTrue(StringUtils.isNotBlank(webJson), "配置信息不能为空");
        ComboImgBo comboImgBo = new ComboImgBo();
        comboImgBo.setWebJson(webJson);
        if(null != pageManager) {
            comboImgBo.setChannelNo(pageManager.getChannelNo());
            comboImgBo.setPageId(pageManager.getId());
            comboImgBo.setStatus(pageManager.getStatus());
            comboImgBo.setVersion(pageManager.getVersionId());
        }
        try {
            WebJsonBo webJsonBo = JSONObject.parseObject(webJson, WebJsonBo.class);
            if (webJsonBo.getData() == null || CollectionUtils.isEmpty(webJsonBo.getData().getImgList())) {
                throw new ParameterException("图片数据不能为空");
            }
            comboImgBo.setId(webJsonBo.getId());
            comboImgBo.setModuleUniqueId(webJsonBo.getModuleUniqueId());
            List<WebJsonBo.WebJsonImgBo> comboWebJsonList = webJsonBo.getData().getImgList();
            List<ComboImgDetailBo> comboImgDetailBos = IntStream.range(0, comboWebJsonList.size()).mapToObj(idx -> {
                ComboImgDetailBo comboImgDetailBo = new ComboImgDetailBo();
                WebJsonBo.WebJsonImgBo comboWebJson = comboWebJsonList.get(idx);
                Long btnModuleUniqueId = comboWebJson.getModuleUniqueId();
                String name = comboWebJson.getTitle();
                String url = comboWebJson.getImgUrl();
                WebJsonBo.WebJsonLinkBo linkWebJson = comboWebJson.getLink();
                LinkDto linkDto = null;
                if(null != linkWebJson){
                    linkDto = new LinkDto();
                    wrapLinkDto(linkWebJson, linkDto);
                }
                comboImgDetailBo.setName(name);
                comboImgDetailBo.setUrl(url);
                comboImgDetailBo.setSort(idx);
                comboImgDetailBo.setLinkDto(linkDto);
                comboImgDetailBo.setId(comboWebJson.getId());
                //comboImgDetailBo.setWebJson(JSONObject.toJSONString(comboWebJson));
                comboImgDetailBo.setModuleUniqueId(btnModuleUniqueId);
                return comboImgDetailBo;
            }).collect(Collectors.toList());

            comboImgBo.setComboImgDetailBos(comboImgDetailBos);

        } catch (JSONException e) {
            throw new ParameterException("webJson格式异常");
        } catch (Exception e) {
            log.error("ModuleService.parseComboImg has exception", e);
            throw new ParameterException("webJson解析异常");
        }
        return comboImgBo;
    }

    public static void main(String[] args) {
        parseComboImg(null,"{\n" +
                "\t\"blockId\": \"6532c9b2-673f-4500-9844-99684e1d2f3f\",\n" +
                "\t\"belongType\": 0,\n" +
                "\t\"data\": {\n" +
                "\t\t\"imgList\": [{\n" +
                "\t\t\t\"imgUrl\": \"http://sckj-ygys.oss-cn-hangzhou.aliyuncs.com/pic/test/20201211155953381e8ae98274e0104479a0edc988e04a\",\n" +
                "\t\t\t\"imgId\": \"1337306095636475906\",\n" +
                "\t\t\t\"link\": {\n" +
                "\t\t\t\t\"linkType\": 0,\n" +
                "\t\t\t\t\"pageType\": 0,\n" +
                "\t\t\t\t\"pagePath\": \"/page-center/1338038404791324673\",\n" +
                "\t\t\t\t\"pageName\": \"新建页面6\",\n" +
                "\t\t\t\t\"params\": {\n" +
                "\t\t\t\t\t\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t\"pageUrl\": \"\"\n" +
                "\t\t\t},\n" +
                "\t\t\t\"moduleUniqueId\": \"1337306095636475906\",\n" +
                "\t\t\t\"id\": \"1338058885833609218\",\n" +
                "\t\t\t\"title\": \"\"\n" +
                "\t\t}]\n" +
                "\t},\n" +
                "\t\"moduleType\": 0,\n" +
                "\t\"name\": \"banner\",\n" +
                "\t\"layoutType\": 1,\n" +
                "\t\"style\": {\n" +
                "\t\t\"width\": 750,\n" +
                "\t\t\"height\": 300\n" +
                "\t},\n" +
                "\t\"id\": \"1338058885783277570\",\n" +
                "\t\"category\": 0,\n" +
                "\t\"status\": \"PREVIEW\"\n" +
                "}");
    }

    /**
     * 封装链接组件数据对象
     * @param linkWebJson
     * @param linkDto
     */
    public static void wrapLinkDto(WebJsonBo.WebJsonLinkBo linkWebJson, LinkDto linkDto) {
        linkDto.setId(linkWebJson.getId());
        linkDto.setModuleUniqueId(linkWebJson.getModuleUniqueId());
        linkDto.setName(linkWebJson.getPageName());
        linkDto.setType(getLinkTypeFromWeb(linkWebJson.getLinkType()));
        linkDto.setLinkType(Optional.ofNullable(linkWebJson.getBusinessType()).orElse(PageCenterConsts.BusinessType.COMMON.getCode()));
        linkDto.setLinkToId(linkWebJson.getPageId());
        linkDto.setLinkPath(linkWebJson.getRouterName());
        linkDto.setLinkUrl(linkWebJson.getPageUrl());
        linkDto.setParams(JSONObject.toJSONString(linkWebJson.getParams()));
    }

    /**
     * 根据前端传入的站内链接类型获取后端的站内链接类型
     * @param webPageType
     * @return
     * @deprecated
     */
    private static Integer getInSideLinkTypeFromWeb(Integer webPageType) {
        Integer insideLinkType = null;
        if (WEB_INSIDE_LINK_TYPE_PAGE.equals(webPageType)) {
            insideLinkType = ModuleConstant.INSIDE_LINK_TYPE.PAGE.getCode();
        }
        if (WEB_INSIDE_LINK_TYPE_ARTICLE.equals(webPageType)) {
            insideLinkType = ModuleConstant.INSIDE_LINK_TYPE.ARTICLE.getCode();
        }
        if (WEB_INSIDE_LINK_TYPE_VIDEO.equals(webPageType)) {
            insideLinkType = ModuleConstant.INSIDE_LINK_TYPE.VIDEO.getCode();
        }
        if (WEB_INSIDE_LINK_TYPE_HEALTH_SET_MEAL.equals(webPageType)) {
            insideLinkType = ModuleConstant.INSIDE_LINK_TYPE.HEALTH_SET_MEAL.getCode();
        }
        if (WEB_INSIDE_LINK_TYPE_HEALTH_HOSPITAL.equals(webPageType)) {
            insideLinkType = ModuleConstant.INSIDE_LINK_TYPE.HEALTH_HOSPITAL.getCode();
        }
        if (WEB_INSIDE_LINK_TYPE_GOODS.equals(webPageType)) {
            insideLinkType = ModuleConstant.INSIDE_LINK_TYPE.GOODS.getCode();
        }
        return insideLinkType;
    }

    /**
     * 根据前端传入的链接类型获取后端的链接类型
     * @param webLinkType
     * @return
     */
    private static Integer getLinkTypeFromWeb(Integer webLinkType) {
        return WEB_LINK_TYPE_OUTSIDE.equals(webLinkType) ? ModuleConstant.LINK_TYPE_OUTSIDE : ModuleConstant.LINK_TYPE_INSIDE;
    }

    /**
     * 个人信息组件解析
     * @param pageManager
     * @param webJson
     * @return
     */
    public static PersonalInfoBo parsePersonalInfo(PageManager pageManager, String webJson){
        Assert.isTrue(StringUtils.isNotBlank(webJson), "配置信息不能为空");
        PersonalInfoBo personalInfoBo = new PersonalInfoBo();
        if(null != pageManager) {
            personalInfoBo.setChannelNo(pageManager.getChannelNo());
            personalInfoBo.setPageId(pageManager.getId());
            personalInfoBo.setStatus(pageManager.getStatus());
            personalInfoBo.setVersion(pageManager.getVersionId());
        }
        personalInfoBo.setWebJson(webJson);
        try {
            WebJsonBo webJsonBo = JSONObject.parseObject(webJson, WebJsonBo.class);
            if (webJsonBo.getData() == null) {
                throw new ParameterException("个人信息不能为空");
            }
            personalInfoBo.setId(webJsonBo.getId());
            personalInfoBo.setModuleUniqueId(webJsonBo.getModuleUniqueId());
            List<WebJsonBo.WebJsonImgBo> imgList = webJsonBo.getData().getImgList();
            List<PersonalInfoImgBo> personalInfoImgBos = IntStream.range(0, imgList.size()).mapToObj(idx -> {
                PersonalInfoImgBo personalInfoImgBo = new PersonalInfoImgBo();
                WebJsonBo.WebJsonImgBo imgJson = imgList.get(idx);
                WebJsonBo.WebJsonLinkBo linkWebJson = imgJson.getLink();
                LinkDto linkDto = null;
                if(null != linkWebJson){
                    linkDto = new LinkDto();
                    wrapLinkDto(linkWebJson, linkDto);
                }
                personalInfoImgBo.setName(imgJson.getTitle());
                personalInfoImgBo.setUrl(imgJson.getImgUrl());
                personalInfoImgBo.setSort(idx);
                personalInfoImgBo.setLinkDto(linkDto);
                personalInfoImgBo.setModuleUniqueId(imgJson.getModuleUniqueId());
                personalInfoImgBo.setId(imgJson.getId());
                return personalInfoImgBo;
            }).collect(Collectors.toList());

            personalInfoBo.setPersonalInfoImgs(personalInfoImgBos);

        } catch (JSONException e) {
            throw new ParameterException("webJson格式异常");
        } catch (Exception e) {
            log.error("ModuleHandler.parsePersonalInfo has exception", e);
            throw new ParameterException("webJson解析异常");
        }
        return personalInfoBo;
    }

    /**
     * 解析订单管理组件
     * @param pageManager
     * @param webJson
     * @return
     */
    public static OrderManagementBo parseOrderManagement(PageManager pageManager, String webJson){
        Assert.isTrue(StringUtils.isNotBlank(webJson), "配置信息不能为空");
        OrderManagementBo orderManagementBo = new OrderManagementBo();
        if(null != pageManager) {
            orderManagementBo.setChannelNo(pageManager.getChannelNo());
            orderManagementBo.setPageId(pageManager.getId());
            orderManagementBo.setStatus(pageManager.getStatus());
            orderManagementBo.setVersion(pageManager.getVersionId());
        }
        orderManagementBo.setWebJson(webJson);
        try {
            WebJsonBo webJsonBo = JSONObject.parseObject(webJson, WebJsonBo.class);
            if (webJsonBo.getData() == null || CollectionUtils.isEmpty(webJsonBo.getData().getImgList())) {
                throw new ParameterException("图片数据不能为空");
            }
            orderManagementBo.setId(webJsonBo.getId());
            orderManagementBo.setModuleUniqueId(webJsonBo.getModuleUniqueId());
            List<WebJsonBo.WebJsonImgBo> imgList = webJsonBo.getData().getImgList();
            List<OrderMgtImgBo> orderMgtImgBos = IntStream.range(0, imgList.size()).mapToObj(idx -> {
                OrderMgtImgBo orderMgtImgBo = new OrderMgtImgBo();
                WebJsonBo.WebJsonImgBo imgJson = imgList.get(idx);
                WebJsonBo.WebJsonLinkBo linkWebJson = imgJson.getLink();
                LinkDto linkDto = null;
                if(null != linkWebJson){
                    linkDto = new LinkDto();
                    wrapLinkDto(linkWebJson, linkDto);
                }
                orderMgtImgBo.setName(imgJson.getTitle());
                orderMgtImgBo.setUrl(imgJson.getImgUrl());
                orderMgtImgBo.setSort(idx);
                orderMgtImgBo.setLinkDto(linkDto);
                orderMgtImgBo.setModuleUniqueId(imgJson.getModuleUniqueId());
                orderMgtImgBo.setId(imgJson.getId());
                return orderMgtImgBo;
            }).collect(Collectors.toList());

            orderManagementBo.setOrderMgtImgs(orderMgtImgBos);

        } catch (JSONException e) {
            throw new ParameterException("webJson格式异常");
        } catch (Exception e) {
            log.error("ModuleHandler.parseOrderManagement has exception", e);
            throw new ParameterException("webJson解析异常");
        }

        return orderManagementBo;
    }

    /**
     * 解析我的模块组件
     * @param pageManager
     * @param webJson
     * @return
     */
    public static MyModBo parseMyMod(PageManager pageManager, String webJson){
        Assert.isTrue(StringUtils.isNotBlank(webJson), "配置信息不能为空");
        MyModBo myModBo = new MyModBo();
        if(null != pageManager) {
            myModBo.setChannelNo(pageManager.getChannelNo());
            myModBo.setPageId(pageManager.getId());
            myModBo.setStatus(pageManager.getStatus());
            myModBo.setVersion(pageManager.getVersionId());
        }
        myModBo.setWebJson(webJson);
        try {
            WebJsonBo webJsonBo = JSONObject.parseObject(webJson, WebJsonBo.class);
            if (webJsonBo.getData() == null || CollectionUtils.isEmpty(webJsonBo.getData().getImgList())) {
                throw new ParameterException("图片数据不能为空");
            }
            myModBo.setId(webJsonBo.getId());
            myModBo.setModuleUniqueId(webJsonBo.getModuleUniqueId());
            List<WebJsonBo.WebJsonImgBo> imgList = webJsonBo.getData().getImgList();
            List<MyModImgBo> myModImgBos = IntStream.range(0, imgList.size()).mapToObj(idx -> {
                MyModImgBo myModImgBo = new MyModImgBo();
                WebJsonBo.WebJsonImgBo imgJson = imgList.get(idx);
                WebJsonBo.WebJsonLinkBo linkWebJson = imgJson.getLink();
                LinkDto linkDto = null;
                if(null != linkWebJson){
                    linkDto = new LinkDto();
                    wrapLinkDto(linkWebJson, linkDto);
                }
                myModImgBo.setName(imgJson.getTitle());
                myModImgBo.setUrl(imgJson.getImgUrl());
                myModImgBo.setSort(idx);
                myModImgBo.setLinkDto(linkDto);
                myModImgBo.setModuleUniqueId(imgJson.getModuleUniqueId());
                myModImgBo.setId(imgJson.getId());
                return myModImgBo;
            }).collect(Collectors.toList());

            myModBo.setMyModImgs(myModImgBos);

        } catch (JSONException e) {
            throw new ParameterException("webJson格式异常");
        } catch (Exception e) {
            log.error("ModuleHandler.parseMyMod has exception", e);
            throw new ParameterException("webJson解析异常");
        }

        return myModBo;
    }

}
