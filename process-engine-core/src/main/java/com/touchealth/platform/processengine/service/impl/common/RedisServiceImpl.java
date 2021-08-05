package com.touchealth.platform.processengine.service.impl.common;

import com.touchealth.platform.processengine.service.common.RedisService;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author liufengqiang
 * @date 2020-11-26 18:08:45
 */
@Service
public class RedisServiceImpl implements RedisService {

    @Resource(name = "redisTemplate")
    private ValueOperations valueOperations;
    @Resource(name = "redisTemplate")
    private ListOperations listOperations;
    @Resource(name = "redisTemplate")
    private HashOperations hashOperations;

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public <T> void setValue(String k, T v) {
        this.valueOperations.set(k, v);
    }

    @Override
    public <T> void setValue(String k, T v, long l) {
        this.valueOperations.set(k, v, l, TimeUnit.SECONDS);
    }

    @Override
    public <T> void setValue(String k, T v, long l, TimeUnit timeUnit) {
        this.valueOperations.set(k, v, l, timeUnit);
    }

    @Override
    public <T> T getValue(String k) {
        return (T) this.valueOperations.get(k);
    }

    @Override
    public <T> Long rightPushAllList(String key, Collection<T> values) {
        return this.listOperations.rightPush(key, values);
    }

    @Override
    public <T> void putMap(String h, String hk, T t) { this.hashOperations.put(h, hk, t);
    }

    @Override
    public <T extends Map> void putMap(String h, T t) {
        this.hashOperations.putAll(h, t);
    }

    @Override
    public void del(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public long incr(String key) {
        return valueOperations.increment(key);
    }

    @Override
    public long incr(String key, long delta) {
        return valueOperations.increment(key, delta);
    }

    @Override
    public long decr(String key) {
        return valueOperations.decrement(key);
    }

    @Override
    public long decr(String key, long delta) {
        return valueOperations.decrement(key, delta);
    }

    @Override
    public void hsetIncr(String key, String field) {
        hsetIncr(key, field, 1L);
    }

    @Override
    public void hsetIncr(String key, String field, long delta) {
        hashOperations.increment(key, field, delta);
    }

    @Override
    public <T> T hget(String key, String field) {
        return (T) hashOperations.get(key, field);
    }

    @Override
    public long hlen(String key) {
        return hashOperations.size(key);
    }

    @Override
    public Map<Object, Object> hgetAll(String key) {
        return hashOperations.entries(key);
    }

    @Override
    public void pfadd(String key, Object val) {
        redisTemplate.opsForHyperLogLog().add(key, val);
    }

    @Override
    public long pfcount(String key) {
        Long size = redisTemplate.opsForHyperLogLog().size(key);
        return size == null ? 0 : size;
    }

    @Override
    public void union(String key, String... sourceKey) {
        redisTemplate.opsForHyperLogLog().union(key, sourceKey);
    }

    @Override
    public void sadd(String key, Object... values) {
        redisTemplate.opsForSet().add(key, values);
    }

    @Override
    public boolean isMember(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

}
