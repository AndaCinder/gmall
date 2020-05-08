package com.lichen.gmall.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;


/**
 * @author 李琛
 * 2020/5/8 - 17:58
 */
@SpringBootConfiguration
public class RedissonConfig {

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.226.128:6379");
        return Redisson.create(config);
    }
}
