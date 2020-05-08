package com.lichen.gmall.aspect;

import com.alibaba.fastjson.JSON;
import com.lichen.gmall.annotation.GmallCache;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author 李琛
 * 2020/5/8 - 18:07
 */
@Aspect
@Component
public class GmallCacheAspect {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Around("@annotation(com.lichen.gmall.annotation.GmallCache)")
    public Object cacheAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;
        // 编写
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 获取目标方法的入参
        Object[] args = joinPoint.getArgs();
        // 获取目标方法的返回值
        Method method = signature.getMethod();
        Class<?> returnType = method.getReturnType();
        GmallCache gmallCache = method.getAnnotation(GmallCache.class);
        // 拼接前缀
        String tempPrefix = gmallCache.prefix();
        String prefix = tempPrefix.substring(0, tempPrefix.lastIndexOf(":") == tempPrefix.length() - 1 ? tempPrefix.lastIndexOf(":") : tempPrefix.length());;
        //缓存的key值
        String key = prefix + this.toCustomString(args);

        int timeout = gmallCache.timeout();
        int random = gmallCache.random();
        String lockKey = gmallCache.lock() + args[0];
        // DCL
        result = cacheHit(key, returnType);
        if(result != null)
            return result;
        RLock lock = this.redissonClient.getLock(lockKey);
        lock.lock(10L, TimeUnit.SECONDS);

        result = cacheHit(key, returnType);
        if(result != null) {
            lock.unlock();
            return result;
        }

        result = joinPoint.proceed(joinPoint.getArgs());

        if (result == null){
            this.redisTemplate.opsForValue().set(key,null,1,TimeUnit.MINUTES);
        }else{
            this.redisTemplate.opsForValue().set(key,JSON.toJSONString(result),timeout + new Random().nextInt(random),TimeUnit.MINUTES);
        }
        lock.unlock();
        return result;
    }

    private Object cacheHit(String key, Class<?> returnType){
        // 缓存判断
        String catesJson = this.redisTemplate.opsForValue().get(key);
        if (StringUtils.isNotBlank(catesJson)){
            return JSON.parseObject(catesJson, returnType);
        }
        return null;
    }

    /**
     * 为了适应redis的索引结构写的toString方法把入参数组转为String
     * @param args
     * @return
     */
    private String toCustomString(Object[] args){
        StringBuilder sb = new StringBuilder();
        List<Object> objects = Arrays.asList(args);
        Iterator<Object> it = objects.iterator();
        if (!it.hasNext())
            return "[]";
        sb.append(":");
        while (true){
            Object next = it.next();
            sb.append(next instanceof Collection ? "(the Collection)": next);
            if (! it.hasNext())
                return sb.toString();
            sb.append(":");
        }

    }

}
