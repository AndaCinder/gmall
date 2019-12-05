package com.lichen.gmall.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class Redis2Util {

    private JedisPool jedisPool2;

    public void initPool(String host,int port ,int database){
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(200);
        poolConfig.setMaxIdle(30);
        poolConfig.setBlockWhenExhausted(true);
        poolConfig.setMaxWaitMillis(10*1000);
        poolConfig.setTestOnBorrow(true);
        jedisPool2=new JedisPool(poolConfig,host,port,20*1000);
    }

    public Jedis getJedis2(){
        Jedis jedis2 = jedisPool2.getResource();
        return jedis2;
    }

}
