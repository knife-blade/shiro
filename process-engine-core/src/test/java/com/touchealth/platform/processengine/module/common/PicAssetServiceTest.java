package com.touchealth.platform.processengine.module.common;

import com.touchealth.platform.processengine.BaseTest;
import com.touchealth.platform.processengine.constant.ModuleConstant;
import com.touchealth.platform.processengine.pojo.dto.PageData;
import com.touchealth.platform.processengine.pojo.dto.module.common.PicAssetDto;
import com.touchealth.platform.processengine.pojo.dto.module.common.PicAssetOssDto;
import com.touchealth.platform.processengine.pojo.request.module.common.PicAssetPageRequest;
import com.touchealth.platform.processengine.service.module.common.PicAssetService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

public class PicAssetServiceTest extends BaseTest {

    @Autowired
    private PicAssetService picAssetService;

    @Test
    public void saveTest() {
        PicAssetOssDto dto = new PicAssetOssDto();
        dto.setChannelNo("123");
        dto.setName("测试图");
        dto.setUrl("https://www.baidu.com/1.jpg");
        dto.setType(ModuleConstant.PIC_TYPE);
        PicAssetDto res = picAssetService.save(dto);
        Assert.assertNotNull(res);
    }

    @Test
    public void updateNameTest() {
        Boolean res = picAssetService.updateName(1330044196013264898L, "第一张测试图片");
        Assert.assertTrue(res);
    }

    @Test
    public void pageListTest() {
        PicAssetPageRequest query = new PicAssetPageRequest();
        query.setPageNo(1L);
        query.setPageSize(10L);
        query.setChannelNo("CN19842");
//        query.setFolderId(-1L);
        PageData<PicAssetDto> page = picAssetService.pageList(query);
        Assert.assertNotNull(page);
        Assert.assertFalse(CollectionUtils.isEmpty(page.getData()));
    }

}
