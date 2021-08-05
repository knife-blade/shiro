package com.touchealth.platform.processengine.service.impl.user;

import com.touchealth.platform.processengine.BaseTest;
import com.touchealth.platform.processengine.entity.user.Department;
import com.touchealth.platform.processengine.pojo.dto.PageData;
import com.touchealth.platform.processengine.pojo.dto.user.DepartmentTreeDto;
import com.touchealth.platform.processengine.pojo.request.user.DepartmentOpRequest;
import com.touchealth.platform.processengine.service.user.DepartmentService;
import com.touchealth.platform.processengine.service.user.PostJobService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class DepartmentServiceTest extends BaseTest {

    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private PostJobService postJobService;

    @Test
    public void pageByDeptIdAndPostJobIdTest() {
        String channelNo = "CN59964";
        Long departmentId = null;
        Long postJobId = null;
        Long pageNo = 1L;
        Long pageSize = 10L;
        PageData<DepartmentTreeDto> res = departmentService.pageTreeByDeptIdAndPostJobId(channelNo,0, departmentId, postJobId, pageNo, pageSize);
        Assert.assertNotNull(res);
    }

    @Test
    public void saveAndCacheTest() {
        DepartmentOpRequest req = new DepartmentOpRequest();
        req.setChannelNo("CN59964");
        req.setName("财务部");
        Department res = departmentService.saveAndCache(req);
        Assert.assertNotNull(res);
    }

    @Test
    public void updateAndCacheTest() {
        DepartmentOpRequest req = new DepartmentOpRequest();
        req.setId(1346368217600339969L);
        req.setChannelNo("CN59964");
        req.setName("客服部");
        Department res = departmentService.updateAndCache(req);
        Assert.assertNotNull(res);
    }

    @Test
    public void getCacheByIdTest() {
        Department res = departmentService.getCacheById(1346368217600339969L);
        Assert.assertNotNull(res);
    }

}
