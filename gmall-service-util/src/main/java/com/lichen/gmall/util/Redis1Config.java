package com.lichen.gmall.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Redis1Config {

    //读取配置文件中的redis的ip地址
    @Value("${spring.redis.host:disabled}")
    private String host;

    @Value("${spring.redis.port:6379}")
    private int port;

    @Value("${spring.redis.database:0}")
    private int database;

    @Bean
    public Redis1Util getRedis1Util(){
        if(host.equals("disabled")){
            return null;
        }
        Redis1Util redis1Util =new Redis1Util();
        redis1Util.initPool(host,port,database);
        return redis1Util;
    }

}
