package com.touchealth.platform.processengine.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.touchealth.platform.processengine.constant.RedisConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

    /**
     * 重写缓存Key生成策略
     * 缓存的key是包名+方法名+参数列表,防止缓存Key冲突
     *
     * @return
     */
    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder("cache:key:");
            sb.append(target.getClass().getName()).append(".");//执行方法所在的类
            sb.append(method.getName()).append("(");//执行的方法名称
            StringBuilder sb2 = new StringBuilder();
            for (Object param : params) {
                if (null == param){
                    sb2.append("java.lang.Object[null],");
                }
                else {
                    sb2.append(param.getClass().getName()).append("[").append(String.valueOf(param)).append("],");
                }
            }
            if (StringUtils.isNotBlank(sb2.toString())){
                sb.append(sb2.substring(0, sb2.length() - 1));
            }
            return sb.append(")").toString();
        };
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        Map<String, RedisCacheConfiguration> configurationMap = new HashMap<>();
        configurationMap.put(RedisConstant.REDIS_CACHE_30_SECOND, RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(30)).serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer())).serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer())));
        configurationMap.put(RedisConstant.REDIS_CACHE_10_MINUTE, RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(10)).serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer())).serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer())));
        configurationMap.put(RedisConstant.REDIS_CACHE_30_MINUTE, RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(30)).serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer())).serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer())));
        configurationMap.put(RedisConstant.REDIS_CACHE_2_HOUR, RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(2)).serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer())).serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer())));
        configurationMap.put(RedisConstant.REDIS_CACHE_12_HOUR, RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(12)).serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer())).serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer())));
        configurationMap.put(RedisConstant.REDIS_CACHE_24_HOUR, RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(24)).serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer())).serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer())));

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofDays(7))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer()))
                .disableCachingNullValues();

        return RedisCacheManager.builder(factory)
                .initialCacheNames(configurationMap.keySet())
                .withInitialCacheConfigurations(configurationMap)
                .cacheDefaults(config)
                .build();
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(keySerializer());
        template.setValueSerializer(valueSerializer());

        template.setHashKeySerializer(keySerializer());
        template.setHashValueSerializer(valueSerializer());
        template.afterPropertiesSet();
        return template;
    }

    private RedisSerializer<String> keySerializer() {
        return new StringRedisSerializer();
    }

    private RedisSerializer<Object> valueSerializer() {
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//        om.activateDefaultTyping(om.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        // 日期和时间格式化
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
        om.registerModule(javaTimeModule);

        jackson2JsonRedisSerializer.setObjectMapper(om);

        return jackson2JsonRedisSerializer;
    }

}
