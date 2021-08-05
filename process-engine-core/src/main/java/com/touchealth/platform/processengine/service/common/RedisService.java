package com.touchealth.platform.processengine.service.common;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author liufengqiang
 * @date 2020-11-26 18:07:55
 */
public interface RedisService {

    /**
     * 新增key-value
     *
     * @param k
     * @param v
     * @param <T>
     */
    <T> void setValue(String k, T v);

    /**
     * 新增key-value
     * @param k
     * @param v
     * @param l
     * @param <T>
     */
    <T> void setValue(String k, T v, long l);

    /**
     * 新增key-value
     * @param k
     * @param v
     * @param l
     * @param timeUnit
     * @param <T>
     */
    <T> void setValue(String k, T v, long l, TimeUnit timeUnit);

    /**
     * 根据key获取value
     *
     * @param k
     * @param <T>
     * @return
     */
    <T> T getValue(String k);

    /**
     * 新增集合
     * @param key
     * @param values
     * @param <T>
     * @return
     */
    <T> Long rightPushAllList(String key, Collection<T> values);

    <T> void putMap(String h, String hk, T t);

    <T extends Map> void putMap(String h, T t);

    /**
     * 删除key
     * @param key
     */
    void del(String key);

    /**
     * 自增一次
     * @param key 对应的key
     * @return
     */
    long incr(String key);

    /**
     * 自增指定的步长
     * @param key key
     * @param delta 步长
     * @return
     */
    long incr(String key, long delta);

    long decr(String key);

    long decr(String key, long delta);

    /**
     * 实现命令：HSET key field value，将哈希表 key中的域 field的值自增
     * @param key
     * @param field
     */
    void hsetIncr(String key, String field);

    /**
     * 实现命令：HSET key field value，将哈希表 key中的域 field的值自增delta
     * @param key
     * @param field
     * @param delta
     */
    void hsetIncr(String key, String field, long delta);

    <T> T hget(String key, String field);

    /**
     * 获取指定hash key的数量
     * @param key key
     * @return
     */
    long hlen(String key);

    Map<Object, Object> hgetAll(String key);

    void pfadd(String key, Object val);

    long pfcount(String key);

    /**
     * HyperLogLog  PFMERGE命令
     * 将多个key并集
     * @param key 指定key
     * @param sourceKey 需要合并的key
     */
    void union(String key, String... sourceKey);

    void sadd(String key, Object... values);

    boolean isMember(String key, Object value);
}
