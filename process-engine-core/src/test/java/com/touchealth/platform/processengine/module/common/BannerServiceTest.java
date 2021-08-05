package com.touchealth.platform.processengine.module.common;

import com.alibaba.fastjson.JSON;
import com.github.jsonzou.jmockdata.JMockData;
import com.github.jsonzou.jmockdata.MockConfig;
import com.github.pagehelper.PageInfo;
import com.touchealth.platform.processengine.BaseTest;
import com.touchealth.platform.processengine.pojo.bo.module.common.BannerBo;
import com.touchealth.platform.processengine.pojo.bo.module.common.BannerImgBo;
import com.touchealth.platform.processengine.pojo.bo.module.common.BtnBo;
import com.touchealth.platform.processengine.pojo.bo.module.common.BtnGroupBo;
import com.touchealth.platform.processengine.pojo.dto.PageData;
import com.touchealth.platform.processengine.pojo.dto.module.common.BannerDto;
import com.touchealth.platform.processengine.pojo.dto.module.common.BannerImgDto;
import com.touchealth.platform.processengine.pojo.dto.module.common.BtnGroupDto;
import com.touchealth.platform.processengine.pojo.dto.module.common.LinkDto;
import com.touchealth.platform.processengine.pojo.request.module.common.BannerImgRequest;
import com.touchealth.platform.processengine.service.module.common.BannerImgService;
import com.touchealth.platform.processengine.service.module.common.BannerService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class BannerServiceTest extends BaseTest {

    @Autowired
    BannerService bannerService;

    @Autowired
    BannerImgService bannerImgService;

    @Test
    public void saveTest() {
        BannerBo mock = JMockData.mock(BannerBo.class, MockConfig.newInstance()
                .excludes("id", "bannerImgs")
                .subConfig("version").sizeRange(6, 6)
                .subConfig("status").intRange(0, 4)
                .globalConfig()
        );
        mock.setBannerImgs(new ArrayList<>());
        for (int i = 0; i < 2; i++) {
            BannerImgBo mock1 = JMockData.mock(BannerImgBo.class, MockConfig.newInstance()
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
            mock.getBannerImgs().add(mock1);
        }
        BannerDto res = bannerService.save(mock);
        System.out.println(JSON.toJSONString(res));
        Assert.assertNotNull(res);
    }

    @Test
    public void updateTest() {
        BannerBo bo = new BannerBo();
        bo.setId(1331142023846596609L);
        bo.setVersion(3421L);
        List<BannerImgBo> bannerImgBos = new ArrayList<>();
        BannerImgBo b1 = new BannerImgBo();
        b1.setId(1331142024672874498L);
        b1.setName("取消");
        BannerImgBo b2 = new BannerImgBo();
        b2.setName("确定");
        b2.setUrl("123");
        b2.setModuleUniqueId(1231231231L);
        bannerImgBos.add(b1);
        bannerImgBos.add(b2);
        bo.setBannerImgs(bannerImgBos);
        String res = bannerService.update(bo);
        Assert.assertNotNull(res);
    }

    @Test
    public void queryImgList(){
        BannerImgRequest request = new BannerImgRequest();
        request.setChannelNo("4492");
        //request.setPageName("体检业务");
        //request.setBannerName("确定");
        //request.setDeleteFlag(0);
        request.setOverTime(1);
        BannerDto bannerDto = bannerService.queryBannerDetail(1331142023846596609L,0);
        System.out.println("1");
    }
}
