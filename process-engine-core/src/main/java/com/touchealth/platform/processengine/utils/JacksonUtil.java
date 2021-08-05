package com.touchealth.platform.processengine.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * Description: jackson工具类
 *
 * @author lvx
 * @date 2020/10/23
 */
@Slf4j
public class JacksonUtil {

    private static ObjectMapper mapper = new ObjectMapper();

    public static String bean2Json(Object data) {
        try {
            return mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.error("bean2Json error:", e);
        }

        return null;
    }

    public static <T> T json2Bean(String jsonData, Class<T> beanType) {
        try {
            return mapper.readValue(jsonData, beanType);
        } catch (Exception e) {
            log.error("json2Bean error:", e);
        }

        return null;
    }

    public static <T> List<T> json2List(String jsonData, Class<T> beanType) {
        JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class, beanType);

        try {
            return mapper.readValue(jsonData, javaType);
        } catch (Exception e) {
            log.error("json2List error:", e);
        }

        return null;
    }

    public static <K, V> Map<K, V> json2Map(String jsonData, Class<K> keyType, Class<V> valueType) {
        JavaType javaType = mapper.getTypeFactory().constructMapType(Map.class, keyType, valueType);

        try {
            return mapper.readValue(jsonData, javaType);
        } catch (Exception e) {
            log.error("json2Map error:", e);
        }

        return null;
    }

}
