package com.mg.gulimall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {

    @Bean
    public RedissonClient redissClient(){
        Config config = new Config();
        config.useSingleServer().setAddress("redis://123.60.98.9:6379");
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }
}
