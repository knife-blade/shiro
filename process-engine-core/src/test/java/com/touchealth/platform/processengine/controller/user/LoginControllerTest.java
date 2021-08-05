package com.touchealth.platform.processengine.controller.user;

import com.touchealth.platform.processengine.ProcessEngineApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProcessEngineApplication.class)
public class LoginControllerTest {

    @Resource
    private LoginController loginController;

    @Test
    public void login() {
    }
}