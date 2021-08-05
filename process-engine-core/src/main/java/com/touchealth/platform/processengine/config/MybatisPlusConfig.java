package com.touchealth.platform.processengine.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.OptimisticLockerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPlusConfig {
    /**
     * 新的分页插件,一缓和二缓遵循mybatis的规则
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    /**
     * 乐观锁
     */
    @Bean
    public OptimisticLockerInterceptor OptimisticLockerInnerInterceptor() {
        return new OptimisticLockerInterceptor();
    }

    /**
     * MyBatis SQL打印插件
     * @return
     */
    @Bean
    public PrintSqlInterceptor printSqlInterceptor() {
        return new PrintSqlInterceptor();
    }

}
