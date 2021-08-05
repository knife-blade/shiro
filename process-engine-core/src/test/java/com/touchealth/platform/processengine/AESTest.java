package com.touchealth.platform.processengine;

import com.baomidou.mybatisplus.core.toolkit.AES;
import org.junit.Test;

import java.util.Arrays;

public class AESTest {

    @Test
    public void generatorKey() {
        String[] values = new String[] {
                "jdbc:mysql://192.168.137.151:3306/process_engine?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf8&allowMultiQueries=true&zeroDateTimeBehavior=convertToNull",
                "root",
                "123456"
        }; // 需要加密的字符串
        String key = AES.generateRandomKey();
        System.out.println(key); // 随机字符串  7552915d62837249
        Arrays.stream(values).forEach(value -> {
            String serect = AES.encrypt(value, key);
            System.out.println(serect); // 加密后的值
        });
    }

    @Test
    public void decryptKey() {
        String mpwKey = "";
        String secret = "KJ935RX0OWp3H4VDahFd1yA9IJ7nkk4aZHpvVS1tW8fDt0OHVq2vilG1GxZStlmkJUffxjCROBBg1GHuxUhEIAQXAxCp7u25fzf6gTNM4c8XN6bvO2sC6Mbef9tRzZaNyqnLbMOoq1g38cQqazkqmum6dguaxLedUyjAw4OK5gu+sIIy760PWJ3lUuohuCLvtrazy6rPXZMJbE8/kIUpGt++h+Zup3cIVrA8Jc1IvvE=";
        String decrypt = AES.decrypt(secret, mpwKey);
        System.out.println(decrypt);
    }

}
