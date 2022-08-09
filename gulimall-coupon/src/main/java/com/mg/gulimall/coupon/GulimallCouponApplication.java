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

/**
 * 一、nacos配置中心统一管理配置
 * 1.引入依赖
 * spring-cloud-starter-alibaba-nacos-config
 * 如果读取不到bootstrap.properties配置文件则需要引入bootstrap依赖
 * 2.创建一个bootstrap.properties
 * spring.cloud.nacos.config.server-addr=
 * spring.application.name=
 * 3.需要给配置中心默认添加一个数据集(Data Id),默认规则：应用名.properties
 * 4.动态获取配置：
 * @RefreshScope：动态获取并刷新配置
 * @Value("${配置项的名}"):获取到配置
 * 如果配置中心和当前应用的配置文件中配置了相同的配置项，优先使用配置中的配置
 *
 *
 *  1.命名空间：配置隔离
 *  默认：public(保留空间)：默认新增的所有配置都在public空间
 *      1.开发、测试、生产，利用命名空间来做环境隔离
 *        注意：在bootstrap.properties配置上，需要使用哪个命名空间下的配置
 *        spring.cloud.nacos.config.namespace=f647e215-d857-4aec-974e-31247e592d94
 *      2.每一个微服务之间相互隔离配置，每一个微服务都创建自己的命名空间，只加载自己命名空间下的所有配置
 *
 *  2.配置集：一组相关或不相关的配置项的集合
 *
 *  3.配置集ID
 *   Data ID：类似文件名
 *
 *  4.配置分组：
 *    默认所有的配置集都属于：DEFAULT_GROUP
 *
 * 本项目使用：每一个微服务创建自己的命名空间，使用配置分组区分环境，dev、test、prod
 *
 *
 * 3.同时加载多个配置集
 *   微服务的任何配置信息，任何配置文件都可以放在配置中心中
 *   配置中心有的优先使用配置中心的
 */
@EnableDiscoveryClient
@SpringBootApplication
public class GulimallCouponApplication {
    public static void main(String[] args) {
        SpringApplication.run(GulimallCouponApplication.class, args);
    }

}
