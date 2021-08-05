package com.touchealth.platform.processengine.config;


import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Optional;

/**
 * 用于打印SQL的MyBatis插件
 */
@Intercepts({
        @Signature(
                type= Executor.class,
                method = "update",
                args = {MappedStatement.class,Object.class}),
        @Signature(
                type= Executor.class,
                method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class})
})
@Slf4j
public class PrintSqlInterceptor implements Interceptor {

    private static final String QUERY_METHOD = "query";
    private static final String UPDATE_METHOD = "update";
    private static final String SPLIT_FLAG = " ========> ";

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object returnObject;
        try {
            String currentTransactionName = Optional.ofNullable(TransactionSynchronizationManager.getCurrentTransactionName()).orElse("");
            Object[] args = invocation.getArgs();
            String method = invocation.getMethod().getName();
            String sql = "";
            long startTime = System.currentTimeMillis();
            if (UPDATE_METHOD.equalsIgnoreCase(method)) {
                if (args != null && args.length == 2 && args[0] instanceof MappedStatement) {
                    MappedStatement ms = (MappedStatement) args[0];
                    sql = ms.getBoundSql(args[1]).getSql()
                            .replaceAll("\n", " ")
                            .replaceAll("\\s{1,}", " ");
                }
            }
            if (QUERY_METHOD.equalsIgnoreCase(method)) {
                if (args != null && args.length == 6 && args[5] instanceof BoundSql) {
                    BoundSql boundSql = (BoundSql) args[5];
                    sql = boundSql.getSql()
                            .replaceAll("\n", " ")
                            .replaceAll("\\s{1,}", " ");
                }
            }
            returnObject = invocation.proceed();
            log.info(SPLIT_FLAG + (System.currentTimeMillis() - startTime) + SPLIT_FLAG + sql + SPLIT_FLAG + currentTransactionName);
        } catch (Exception e) {
            log.error("PrintSqlInterceptor.intercept has error", e);
            returnObject = invocation.proceed();
        }
        return returnObject;
    }

}
