package com.touchealth.platform.processengine.module.common;

import com.github.jsonzou.jmockdata.JMockData;
import com.github.jsonzou.jmockdata.MockConfig;
import com.touchealth.platform.processengine.BaseTest;
import com.touchealth.platform.processengine.pojo.dto.module.common.LinkDto;
import com.touchealth.platform.processengine.service.module.common.LinkService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class LinkServiceTest extends BaseTest {

    @Autowired
    private LinkService linkService;

    @Test
    public void saveTest() {
        LinkDto mock = JMockData.mock(LinkDto.class, MockConfig.newInstance()
                .excludes("id", "buttons", "moduleUniqueId")
                .subConfig("version").sizeRange(6, 12)
                .subConfig("status").intRange(0, 6)
                .subConfig("type").intRange(0, 2)
                .globalConfig());
//        LinkDto res = linkService.saveModule(mock);
//        Assert.assertNotNull(res);
    }

    @Test
    public void updateTest() {
        LinkDto dto = new LinkDto();
        dto.setId(1329691357625774081L);
        dto.setName("百度外链接");
        dto.setLinkUrl("https://www.baidu.com");
//        Boolean res = linkService.updateModule(dto);
//        Assert.assertTrue(res);
    }

    @Test
    public void findByIdTest() {
//        LinkDto res = linkService.findModuleById(1329691357625774081L);
//        Assert.assertNotNull(res);
    }

    @Test
    public void findListByIdsTest() {
//        List<LinkDto> res = linkService.findModuleByIdList(Arrays.asList(1329691357625774081L));
//        Assert.assertFalse(CollectionUtils.isEmpty(res));
    }

    @Test
    public void countByVersionsAndToIdTest() {
        List<Long> versions = new ArrayList<>();
        versions.add(1343810479173373954L);
        versions.add(1343810300126924802L);
        Integer res = linkService.countByVersionsAndToId(versions, "1Bf0Fm5Pgv8");
        Assert.assertTrue(res > 0);
    }

}
