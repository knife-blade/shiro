package com.touchealth.platform.processengine.service.impl.user;

import com.touchealth.platform.processengine.BaseTest;
import com.touchealth.platform.processengine.constant.PermsConstant;
import com.touchealth.platform.processengine.entity.user.Perms;
import com.touchealth.platform.processengine.handler.WebPermsHandler;
import com.touchealth.platform.processengine.pojo.dto.user.PermDto;
import com.touchealth.platform.processengine.service.user.PermsService;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class PermsServiceTest extends BaseTest {

    @Autowired
    private PermsService permsService;

    @Test
    public void listUserAllPermsTest() {
        List<PermDto> permDtos = permsService.listUserAllPerms("", 1L);
        Assert.assertTrue(CollectionUtils.isNotEmpty(permDtos));
    }


    /**
     * 同步权限资源到数据库中，一次性的操作<br>
     * 若数据库中存在，那么会被更新。<br>
     * <font color=red>不要随意调用。</font>
     * @return
     */
    @Test
    public void tmpPermsSync() {
        boolean updAlreadyExits = false;
        List<Perms> allPerms = permsService.getAll();
        Map<String, Perms> permMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(allPerms)) {
            permMap = allPerms.stream().collect(Collectors.toMap(Perms::getPermsCode, o -> o));
        }
        List<Perms> saveOrUpdList = new ArrayList<>();

        Class permsConstantClass = PermsConstant.class;
        Field[] fields = permsConstantClass.getDeclaredFields();
        for (Field field : fields) {
            String name = field.getName();
            try {
                String value = field.get(new PermsConstant() {
                    @Override
                    public int hashCode() {
                        return super.hashCode();
                    }
                }).toString();

                Perms perms = permMap.get(value);
                if (perms == null) {
                    perms = new Perms();
                    String webName = WebPermsHandler.getPermWebName(value);
                    perms.setCode(name);
                    perms.setName(StringUtils.isEmpty(webName) ? name : webName);
                    perms.setType(getPermType(name));
                    perms.setPermsCode(value);
                    perms.setPermsGroup("");
                    perms.setPId(Optional.ofNullable(perms.getPId()).orElse(-1L)); // TODO 修改数据库中的父子关系
                    saveOrUpdList.add(perms);
                } else if (updAlreadyExits) {
                    String webName = WebPermsHandler.getPermWebName(value);
                    perms.setCode(name);
                    perms.setName(StringUtils.isEmpty(webName) ? name : webName);
                    perms.setType(getPermType(name));
                    perms.setPermsCode(value);
                    saveOrUpdList.add(perms);
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
                Assert.assertTrue(false);
            }
        }
        Assert.assertTrue(permsService.saveOrUpdateBatch(saveOrUpdList));
    }

    private Integer getPermType(String name) {
        if (name.startsWith("MENU_")) {
            return 0;
        }
        if (name.startsWith("OP_")) {
            return 1;
        }
        if (name.startsWith("DATA_")) {
            return 2;
        }
        if (name.startsWith("FIELD_")) {
            return 3;
        }
        if (name.startsWith("API_")) {
            return 4;
        }
        return 0;
    }

}
