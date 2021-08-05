package com.touchealth.platform.processengine.module.common;

import com.alibaba.fastjson.JSON;
import com.github.jsonzou.jmockdata.JMockData;
import com.github.jsonzou.jmockdata.MockConfig;
import com.touchealth.platform.processengine.BaseTest;
import com.touchealth.platform.processengine.pojo.bo.CompareBo;
import com.touchealth.platform.processengine.pojo.bo.WebJsonBo;
import com.touchealth.platform.processengine.pojo.dto.module.common.DelimiterDto;
import com.touchealth.platform.processengine.service.module.common.DelimiterService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;

public class DelimiterServiceTest extends BaseTest {

    @Autowired
    private DelimiterService delimiterService;

    @Test
    public void saveTest() {
        DelimiterDto mock = JMockData.mock(DelimiterDto.class, MockConfig.newInstance()
                        .excludes("id", "buttons", "moduleUniqueId")
                        .subConfig("version").sizeRange(6, 12)
                        .subConfig("status").intRange(0, 4)
                        .globalConfig()
        );
        DelimiterDto res = delimiterService.save(mock);
        Assert.assertNotNull(res);
    }

    @Test
    public void updateTest() {
        DelimiterDto dto = new DelimiterDto();
        dto.setId(1329681038866198530L);
        dto.setVersion(10000010001L);
        dto.setName("超大分隔符");
        dto.setWebJson("{weight: 1000px}");
        DelimiterDto res = delimiterService.update(dto);
        Assert.assertNotNull(res);
    }

    @Test
    public void findByIdTest() {
        DelimiterDto res = delimiterService.findById(1329681038866198530L);
        Assert.assertNotNull(res);
    }

    @Test
    public void findListByIdsTest() {
        List<DelimiterDto> res = delimiterService.findByIdList(Arrays.asList(1329681038866198530L, 1329688572884762625L, 1329688733593743362L));
        Assert.assertFalse(CollectionUtils.isEmpty(res));
    }

    @Test
    public void savePageModuleTest() {
        WebJsonBo webJsonBo = new WebJsonBo();
        webJsonBo.setLayoutType(1);
        WebJsonBo.WebJsonStyleBo style = new WebJsonBo.WebJsonStyleBo();
        style.setHeight(201);
        webJsonBo.setStyle(style);
        String webJson = JSON.toJSONString(webJsonBo);
        String res = delimiterService.savePageModule(webJson, 1334422502285717505L);
        System.out.println(res);
        Assert.assertNotNull(res);
    }

    @Test
    public void findPageModuleByIdTest() {
        String res = delimiterService.findPageModuleById(1333947953776168962L);
        System.out.println(res);
        Assert.assertNotNull(res);
    }

    @Test
    public void updatePageModuleTest() {
        String webJson = "{\"id\":1333947953776168962,\"layoutType\":1,\"style\":{\"height\":\"108px\"}}";
        String res = delimiterService.updatePageModule(webJson);
        Assert.assertNotNull(res);
    }

    @Test
    public void clonePageModuleTest() {
        String res = delimiterService.clonePageModule(1333953590186844162L, 1329620006031749122L);
        System.out.println(res);
        Assert.assertNotNull(res);
    }

    @Test
    public void compareTest() {
        String webJson = "{\"id\":1333954256464572417,\"layoutType\":2,\"moduleUniqueId\":1333953590186844162,\"style\":{\"height\":\"109px\"}}";
        List<CompareBo> res = delimiterService.compare(webJson, 1333954256464572417L);
        System.out.println(JSON.toJSONString(res, true));
        Assert.assertFalse(CollectionUtils.isEmpty(res));
    }

}
