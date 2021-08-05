package com.touchealth.platform.processengine.example;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.touchealth.platform.processengine.BaseTest;
import com.touchealth.platform.processengine.entity.Example;
import com.touchealth.platform.processengine.service.ExampleService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class ExampleTest extends BaseTest {

    @Autowired
    private ExampleService exampleService;

    @Test
    public void addExampleTest() {
        Example mock = new Example();
        mock.setId(null);
        mock.setType(2);
        mock.setContent("hahaha");
        boolean save = exampleService.save(mock);
        Assert.isTrue(save);
    }

    @Test
    public void findAllExampleTest() {
        List<Example> examples = exampleService.list();
        System.out.println(environment.getProperty("spring.datasource.type"));
        System.out.println(environment.getProperty("spring.datasource.url"));
        Assert.isTrue(!CollectionUtils.isEmpty(examples));
    }

    @Test
    public void updateExampleTest() {
        // 第一个参数不传或者为空，不会调用com.touchealth.platform.processengine.handler.CustomMetaObjectHandler.updateFill方法，导致更新时间等字段填充失败
        boolean res = exampleService.update(new Example(), Wrappers.<Example>lambdaUpdate()
                .in(Example::getId, 1326786164666998786L)
                .set(Example::getContent, "haha").set(Example::getType, 1));
//        Example updObj = new Example();
//        updObj.setId(1326786164666998786L);
//        updObj.setContent("heihei");
//        boolean res = exampleService.saveOrUpdate(updObj);
        Assert.isTrue(res);
    }

}
