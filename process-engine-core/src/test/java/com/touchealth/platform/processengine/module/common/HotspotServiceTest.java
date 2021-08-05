package com.touchealth.platform.processengine.module.common;

import com.github.jsonzou.jmockdata.JMockData;
import com.github.jsonzou.jmockdata.MockConfig;
import com.touchealth.platform.processengine.BaseTest;
import com.touchealth.platform.processengine.pojo.bo.module.common.HotspotBo;
import com.touchealth.platform.processengine.pojo.bo.module.common.HotspotPartsBo;
import com.touchealth.platform.processengine.pojo.dto.module.common.LinkDto;
import com.touchealth.platform.processengine.service.module.common.HotspotService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

public class HotspotServiceTest extends BaseTest {

    @Autowired
    HotspotService hotspotService;


    @Test
    public void saveTest() {
        HotspotBo mock = JMockData.mock(HotspotBo.class, MockConfig.newInstance()
                .excludes("id", "hotspotPartsBos")
                .subConfig("version").sizeRange(6, 6)
                .subConfig("status").intRange(0, 4)
                .globalConfig()
        );
        mock.setHotspotPartsBos(new ArrayList<>());
        for (int i = 0; i < 2; i++) {
            HotspotPartsBo mock1 = JMockData.mock(HotspotPartsBo.class, MockConfig.newInstance()
                    .excludes("id","linkDto")
                    .subConfig("sort").sizeRange(0, 100)
                    .globalConfig()
            );
            LinkDto linkDto = JMockData.mock(LinkDto.class, MockConfig.newInstance()
                    .excludes("id")
                    .subConfig("type").intRange(0, 1)
                    .globalConfig()
            );
            mock1.setLinkDto(linkDto);
            mock.getHotspotPartsBos().add(mock1);
        }
//        HotspotDto res = hotspotService.saveModule(mock);
//        Assert.assertNotNull(res);
    }
}
