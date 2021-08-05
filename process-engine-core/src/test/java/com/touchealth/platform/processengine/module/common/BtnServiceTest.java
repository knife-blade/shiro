package com.touchealth.platform.processengine.module.common;

import com.alibaba.fastjson.JSONObject;
import com.github.jsonzou.jmockdata.JMockData;
import com.github.jsonzou.jmockdata.MockConfig;
import com.touchealth.platform.processengine.BaseTest;
import com.touchealth.platform.processengine.constant.CommonConstant;
import com.touchealth.platform.processengine.pojo.bo.CompareBo;
import com.touchealth.platform.processengine.pojo.bo.WebJsonBo;
import com.touchealth.platform.processengine.pojo.bo.module.common.BtnBo;
import com.touchealth.platform.processengine.pojo.bo.module.common.BtnGroupBo;
import com.touchealth.platform.processengine.pojo.dto.module.common.BtnGroupDto;
import com.touchealth.platform.processengine.service.module.common.BtnGroupService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 按钮组件测试类
 *
 * @author SunYang
 */
public class BtnServiceTest extends BaseTest {

    @Autowired
    private BtnGroupService btnGroupService;

    @Test
    public void saveTest() {
        BtnGroupBo mock = JMockData.mock(BtnGroupBo.class, MockConfig.newInstance()
                .excludes("id", "buttons")
                .subConfig("version").sizeRange(6, 6)
                .subConfig("status").intRange(0, 4)
                .globalConfig()
        );
        mock.setButtons(new ArrayList<>());
        for (int i = 0; i < 2; i++) {
            BtnBo mock1 = JMockData.mock(BtnBo.class, MockConfig.newInstance()
                    .excludes("id")
                    .subConfig("sort").sizeRange(0, 100)
                    .globalConfig()
            );
            mock.getButtons().add(mock1);
        }
        BtnGroupDto res = btnGroupService.save(mock);
        Assert.assertNotNull(res);
    }

    @Test
    public void updateTest() {
        BtnGroupBo bo = new BtnGroupBo();
        bo.setId(1328308542002761729L);
        bo.setVersion(001001001L);
        List<BtnBo> btns = new ArrayList<>();
        BtnBo b1 = new BtnBo();
        b1.setId(1328308542250225666L);
        b1.setName("取消");
        BtnBo b2 = new BtnBo();
        b2.setName("确定");
        b2.setLinkDto(null);
        b2.setWebJson("{\"weight\": 10px}");
        btns.add(b1);
        btns.add(b2);
        bo.setButtons(btns);
        BtnGroupDto res = btnGroupService.update(bo);
        Assert.assertNotNull(res);
    }

    @Test
    public void findByIdTest() {
        String res = btnGroupService.findPageModuleById(1333705391622672385L);
        System.out.println(res);
        Assert.assertNotNull(res);
    }

    @Test
    public void findByIdsTest() {
        List<String> res = btnGroupService.findPageModuleByIdList(Arrays.asList(1328308542002761729L, 1329256707595407362L, 1329621249129754626L));
        System.out.println(JSONObject.toJSONString(res, true));
        Assert.assertFalse(CollectionUtils.isEmpty(res));
    }

    @Test
    public void savePageModuleTest() {
        WebJsonBo webJsonBo = new WebJsonBo();
        webJsonBo.setLayoutType(2);
        List<WebJsonBo.WebJsonButtonBo> buttonBos = new ArrayList<>();
        WebJsonBo.WebJsonButtonBo buttonBo = new WebJsonBo.WebJsonButtonBo();
        buttonBo.setTitle("取消");
        buttonBo.setColor("red");
        buttonBo.setBgColor("light-red");
        WebJsonBo.WebJsonLinkBo link = new WebJsonBo.WebJsonLinkBo();
        link.setLinkType(0);
        //link.setPagePath("1329620006031749122");
        link.setPageName("体检业务");
        link.setParams(new HashMap<String, Object>() {{
            put("pageParam", "hahaha~~~");
        }});
        buttonBo.setLink(link);
        buttonBos.add(buttonBo);
        WebJsonBo.WebJsonButtonBo buttonBo1 = new WebJsonBo.WebJsonButtonBo();
        buttonBo1.setTitle("保存");
        buttonBo1.setColor("green");
        buttonBo1.setBgColor("light-green");
        WebJsonBo.WebJsonLinkBo link1 = new WebJsonBo.WebJsonLinkBo();
        link1.setLinkType(0);
        //link1.setPagePath("1334745646192103426");
        link1.setPageName("首页2");
        link1.setParams(new HashMap<String, Object>() {{
            put("pageParam1", "heiheihei~~~");
        }});
        buttonBo1.setLink(link1);
        buttonBos.add(buttonBo1);
        WebJsonBo.WebJsonDataBo data = new WebJsonBo.WebJsonDataBo();
        data.setButtonList(buttonBos);
        webJsonBo.setData(data);

        String webJson = JSONObject.toJSONString(webJsonBo);
        String res = btnGroupService.savePageModule(webJson, 1334422502285717505L);
        System.out.println(res);
        Assert.assertNotNull(res);
    }

    @Test
    public void findPageModuleByIdTest() {
        String res = btnGroupService.findPageModuleById(1336146517814173698L);
        System.out.println(res);
        Assert.assertNotNull(res);
    }

    @Test
    public void updatePageModuleTest() {
        String webJson = "{\"data\":{\"buttonList\":[{\"bgColor\":\"light-blue\",\"color\":\"blue\",\"link\":{\"linkType\":0,\"pageName\":\"首页3\",\"pagePath\":\"1334747537311502337\",\"pageType\":0,\"params\":{\"pageParam\":\"hahaha~~~\"}},\"title\":\"取消\"},{\"bgColor\":\"light-green\",\"color\":\"green\",\"id\":1336146518753697794,\"link\":{\"id\":1336146518292324354,\"linkType\":0,\"moduleUniqueId\":1336146518292324354,\"pageName\":\"首页2\",\"pagePath\":\"1334745646192103426\",\"pageType\":0,\"params\":{\"pageParam1\":\"heiheihei~~~\"}},\"moduleUniqueId\":1336146518753697794,\"title\":\"保存\"}]},\"id\":1336146517814173698,\"layoutType\":2,\"moduleUniqueId\":1336146517814173698}";
//        String webJson = "{\"data\":{\"buttonList\":[{\"bgColor\":\"light-green\",\"color\":\"green\",\"id\":1336146518753697794,\"link\":{\"linkType\":0,\"pageName\":\"首页2\",\"pagePath\":\"1334745646192103426\",\"pageType\":0,\"params\":{\"pageParam1\":\"heiheihei~~~\"}},\"moduleUniqueId\":1336146518753697794,\"title\":\"保存\"}]},\"id\":1336146517814173698,\"layoutType\":1,\"moduleUniqueId\":1336146517814173698}";
        String res = btnGroupService.updatePageModule(webJson);
        Assert.assertNotNull(res);
    }

    @Test
    public void compareTest() {
        String webJson = "{\"data\":{\"buttonList\":[{\"bgColor\":\"light-blue\",\"color\":\"blue\",\"id\":1336146518728531969,\"link\":{\"id\":1336146518145523714,\"linkType\":0,\"moduleUniqueId\":1336146518145523714,\"pageName\":\"首页3\",\"pagePath\":\"1334747537311502337\",\"pageType\":0,\"params\":{\"pageParam\":\"hahaha~~~\"}},\"moduleUniqueId\":1336146518728531969,\"title\":\"取消\"},{\"bgColor\":\"light-green\",\"color\":\"green\",\"id\":1336146518753697794,\"link\":{\"id\":1336146518292324354,\"linkType\":0,\"moduleUniqueId\":1336146518292324354,\"pageName\":\"首页2\",\"pagePath\":\"1334745646192103426\",\"pageType\":0,\"params\":{\"pageParam1\":\"heiheihei~~~\"}},\"moduleUniqueId\":1336146518753697794,\"title\":\"保存\"}]},\"id\":1336146517814173698,\"layoutType\":2,\"moduleUniqueId\":1336146517814173698}";
        List<CompareBo> res = btnGroupService.compare(webJson, 1336146517814173698L);
        System.out.println(JSONObject.toJSONString(res, true));
        Assert.assertNotNull(res);
    }

    @Test
    public void clonePageModuleTest() {
        String res = btnGroupService.clonePageModule(1336146517814173698L, 1336147775314092033L);
        System.out.println(res);
        Assert.assertNotNull(res);
    }

    @Test
    public void updateModuleStatusTest() {
        Boolean res = btnGroupService.updateModuleStatus(1333945324425764865L, CommonConstant.STATUS.PUBLISHED.getCode());
        Assert.assertTrue(res);
    }

}
