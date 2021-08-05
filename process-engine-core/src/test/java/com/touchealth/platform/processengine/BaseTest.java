package com.touchealth.platform.processengine;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProcessEngineApplication.class)
@ActiveProfiles("dev")
//@SpringBootTest(classes = ProcessEngineApplication.class, args = "--mpw.key=xxx")
//@ActiveProfiles("test")
public class BaseTest {

    @Autowired
    protected Environment environment;

}
