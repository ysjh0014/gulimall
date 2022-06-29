package com.mg.gulimall.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


/**
 * nacos注册中心
 * 1.首先创建nacos-server
 * 2.将微服务注册到nacos中
 *   1).首先引入依赖spring-cloud-starter-alibaba-nacos-discovery
 *   2).在配置文件application.yml中配置nacos-server地址spring.cloud.nacos.discovery.server-addr:
 *   3).使用@EnableDiscoveryClient注解开启服务注册发现功能，但是从 Spring Cloud Edgware 版本开始，
 *   实际上已经不需要添加 @EnableDiscoveryClient 注解，
 *   只需要引入 Spring Cloud 注册发现组件，就会自动开启注册发现的功能。
 *   例如说，我们这里已经引入了 spring-cloud-starter-alibaba-nacos-discovery 依赖，
 *   就不用再添加 @EnableDiscoveryClient 注解了
 */

@SpringBootApplication
public class GulimallCouponApplication {
    public static void main(String[] args) {
        SpringApplication.run(GulimallCouponApplication.class, args);
    }

}
