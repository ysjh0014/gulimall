package com.mg.gulimall.search;


import com.alibaba.fastjson.JSON;
import com.mg.gulimall.search.config.ElasticSearchConfig;
import lombok.Data;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallSearchApplicationTests {

    @Autowired
    private RestHighLevelClient client;


    /**
     * DSL检索
     */
    @Test
    public void searchData() throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        //指定索引
        searchRequest.indices("bank");
        //指定DSL 检索条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //构造检索条件
        sourceBuilder.query(QueryBuilders.matchQuery("address","mill"));
        //按照年龄分布进行聚合
        TermsAggregationBuilder age = AggregationBuilders.terms("aggAgg").field("age").size(20);
        sourceBuilder.aggregation(age);
        //计算平均薪资
        AvgAggregationBuilder balance = AggregationBuilders.avg("balanceAvg").field("balance");
        sourceBuilder.aggregation(balance);
        searchRequest.source(sourceBuilder);
        //检索条件
        System.out.println("检索条件"+sourceBuilder.toString());

        SearchResponse searchResponse = client.search(searchRequest, ElasticSearchConfig.COMMON_OPTIONS);

        //分析结果
        System.out.println(searchResponse.toString());

        //获取所有查询到的数据
        SearchHit[] hits = searchResponse.getHits().getHits();
        for(SearchHit hit:hits){
            String sourceAsString = hit.getSourceAsString();
            Account account = JSON.parseObject(sourceAsString, Account.class);
            System.out.println("account"+account.toString());
        }

        //获取检索到的分析信息
        Aggregations aggregations = searchResponse.getAggregations();
        Terms aggAgg = aggregations.get("aggAgg");
        for(Terms.Bucket buket:aggAgg.getBuckets()){
            String keyAsString = buket.getKeyAsString();
            System.out.println("年龄："+keyAsString+"人数："+buket.getDocCount());
        }
        //平均薪资
        Avg balanceAvg = aggregations.get("balanceAvg");
        System.out.println("平均薪资："+balanceAvg.getValue());
    }





    /**
     * 测试保存与更新索引到es
     */
    @Test
    public void indexData() throws IOException {
        IndexRequest indexRequest = new IndexRequest("user");
        indexRequest.id("1");
        User user = new User();
        user.setUsername("lisi");
        user.setAge(19);
        user.setGender("男");
        String jsonString = JSON.toJSONString(user);
        indexRequest.source(jsonString, XContentType.JSON);
        //执行操作
        IndexResponse index = client.index(indexRequest, ElasticSearchConfig.COMMON_OPTIONS);

        System.out.println(index);
    }

    @Data
    class User{
        private String username;
        private Integer age;
        private String gender;
    }

    @Data
    static class Account{
        private int account_number;

        private int balance;

        private String firstname;

        private String lastname;

        private int age;

        private String gender;

        private String address;

        private String employer;

        private String email;

        private String city;

        private String state;
    }
    @Test
    public void contextLoads() {
        System.out.println(client);
    }

}
