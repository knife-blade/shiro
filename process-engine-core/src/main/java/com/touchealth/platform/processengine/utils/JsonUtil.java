package com.touchealth.platform.processengine.utils;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class JsonUtil {

    private static ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new ParameterNamesModule())
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule())
            .enable(SerializationFeature.INDENT_OUTPUT)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);//忽略未知属性

    /**
     * 将对象序列化
     *
     * @param obj
     * @return
     */
    public static String getJsonFromObject(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 反序列化对象字符串
     *
     * @param json
     * @param clazz
     * @return
     */
    public static <T> T getObjectFromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 反序列化字符串成为对象
     * @param json
     * @param valueTypeRef
     * @return
     */
    public static <T> T getObjectFromJson(String json, TypeReference<T> valueTypeRef) {
        try {
            return objectMapper.readValue(json, valueTypeRef);
        } catch (JsonParseException e) {
            log.error(e.getMessage(), e);
        } catch (JsonMappingException e) {
            log.error(e.getMessage(), e);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 将json字符串转化为对象，并拦截转化错误信息
     * @param json json字符串
     * @param tClass 目标对象类
     * @param <T> 范型
     * @return 目标对象
     */
    public static <T> T parseJson(String json, Class<T> tClass) {
        try {
            return JSONObject.parseObject(json, tClass);
        } catch (Exception e) {
            log.error("parse json error:", e);
        }
        return null;
    }

    public static <T> List<T> getListFromJson(String json, Class<T> tClass) {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, tClass);
        try {
            return objectMapper.readValue(json, javaType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * json转带泛型的对象，如：Pager<T> parentClass:Pager childClass:T
     */
    public static <T,K> T getGenericFromJson(String json, Class<T> parentClass, Class<K> childClass) {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(parentClass, childClass);
        try {
            return objectMapper.readValue(json, javaType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
