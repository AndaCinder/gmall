package com.lichen.gmall.cart.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author 李琛
 * 2020/4/30 - 20:43
 */
@SpringBootConfiguration
public class ThreadPoolExecutorConfig {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor(@Value("${thread.pool.coreSize}") Integer coreSize,
                                                 @Value("${thread.pool.maxSize}") Integer maxSize,
                                                 @Value("${thread.pool.keepAliveTime}") Long keepAlive,
                                                 @Value("${thread.pool.queueSize}") Integer queueSize){
        return new ThreadPoolExecutor(coreSize,maxSize,keepAlive,TimeUnit.SECONDS,new ArrayBlockingQueue<>(queueSize));
    }
}
