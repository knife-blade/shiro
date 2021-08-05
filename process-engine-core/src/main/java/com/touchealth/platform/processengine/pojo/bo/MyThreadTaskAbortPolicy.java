package com.touchealth.platform.processengine.pojo.bo;

import org.slf4j.Logger;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 用于处理记录线程池任务超限信息
 */
public class MyThreadTaskAbortPolicy implements RejectedExecutionHandler {
    private Logger logger;
    private String poolName;

    public String getPoolName() {
        return poolName;
    }

    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    public MyThreadTaskAbortPolicy(Logger logger, String poolName) {
        this.poolName = poolName;
        this.logger = logger;
    }


    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {


        logger.warn("queue task blocked!!! poolName=" + poolName +
                ",queue task =" + executor.getQueue().size() +
                ",completed tasks=" + executor.getCompletedTaskCount() +
                ",active threads=" + executor.getActiveCount() +
                ",corePool size=" + executor.getCorePoolSize());
        if (!executor.isShutdown()) {
            executor.getQueue().poll();
            executor.execute(r);
        }
    }

}
