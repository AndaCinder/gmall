package com.lichen.gmall.annotation;

import java.lang.annotation.*;

/**
 * @author 李琛
 * 2020/4/14 - 16:06
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GmallCache {

    /**
     * 缓存的前缀
     * 备注索引结束位置不要加:
     */
    String prefix() default "";

    /**
     * 缓存的有效时间，单位：分钟
     */
    int timeout() default 1440;

    /**
     * 单位：分钟
     * 分布式锁的名称
     * 防止缓存击穿指定的
     */
    String lock() default "locks:";

    /**
     * 防止缓存雪崩  单位：分钟
     * 设置的缓存随机过期值范围
     */
    int random() default 10;



}
