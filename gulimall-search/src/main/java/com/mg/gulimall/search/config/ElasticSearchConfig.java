package com.mg.gulimall.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 1.导入依赖
 * 2.编写配置，给容器中注入一个RestHighLevelClient
 * 3.参照API
 */
@Configuration
public class ElasticSearchConfig {
    @Value("ipAddr")
    public String host;

    public static final RequestOptions COMMON_OPTIONS;
    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        COMMON_OPTIONS = builder.build();
    }


    @Bean
     public RestHighLevelClient esRestClient(){
         RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost(host, 9200, "http")));
         return client;
     }

}
