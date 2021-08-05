package com.touchealth.platform.processengine.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.CollectionUtils;

import java.beans.PropertyDescriptor;
import java.util.*;

/**
 * @author liufengqiang
 * @date 2020-11-13 16:37:25
 */
@Slf4j
public class BaseHelper {

    /**
     * @param resource
     * @param target
     * @param <T>
     * @param <R>
     * @return
     * @apiNote resource to target
     */
    public static <T, R> T r2t(R resource, Class<T> target) {
        if (resource == null) {
            return null;
        }
        T tt = null;
        try {
            tt = (T) target.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            log.error(e.getMessage(), e);
        }
        BeanUtils.copyProperties(resource, tt);
        return tt;
    }

    /**
     * @param resourceList
     * @param target
     * @param <T>
     * @param <R>
     * @return
     * @apiNote resourceList to target type list
     */
    public static <T, R> List<T> r2t(List<R> resourceList, Class<T> target) {
        List<T> tList = new ArrayList<>();
        if (CollectionUtils.isEmpty(resourceList)) {
            return tList;
        }
        resourceList.forEach(resource -> {
            T t1 = BaseHelper.r2t(resource, target);
            tList.add(t1);
        });
        return tList;
    }

    /**
     * 拷贝对象非空属性
     * @param source
     * @param target
     */
    public static void copyNotNullProperties(Object source, Object target) {
        BeanUtils.copyProperties(source, target, getNullField(source));
    }

    /**
     * 获取对象空值属性名集合
     * @param obj
     * @return
     */
    private static String[] getNullField(Object obj) {
        BeanWrapper beanWrapper = new BeanWrapperImpl(obj);
        PropertyDescriptor[] propertyDescriptors = beanWrapper.getPropertyDescriptors();
        Set<String> nullFieldSet = new HashSet<>();
        if (propertyDescriptors.length > 0) {
            for (PropertyDescriptor p : propertyDescriptors) {
                String name = p.getName();
                Object value = beanWrapper.getPropertyValue(name);
                if (Objects.isNull(value)) {
                    nullFieldSet.add(name);
                }
            }
        }
        String[] notNullField = new String[nullFieldSet.size()];
        return nullFieldSet.toArray(notNullField);
    }

}
