package com.lichen.gmall.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Redis2Config {

    //读取配置文件中的redis的ip地址
    @Value("${spring.redis2.host:disabled}")
    private String host;

    @Value("${spring.redis2.port:6380}")
    private int port;

    @Value("${spring.redis2.database:0}")
    private int database;

    @Bean
    public Redis2Util getRedis2Util(){
        if(host.equals("disabled")){
            return null;
        }
        Redis2Util redis2Util =new Redis2Util();
        redis2Util.initPool(host,port,database);
        return redis2Util;
    }

}
