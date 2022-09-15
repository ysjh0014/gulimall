package com.mg.gulimall.search.service.impl;

import com.mg.gulimall.search.GulimallSearchApplication;
import com.mg.gulimall.search.config.ElasticSearchConfig;
import com.mg.gulimall.search.service.SearchMallService;
import com.mg.gulimall.search.vo.SearchParam;
import com.mg.gulimall.search.vo.SearchResult;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SearchMallServiceImpl implements SearchMallService {
    @Autowired
    RestHighLevelClient restHighLevelClient;
    @Override
    public SearchResult getSearchResult(SearchParam searchParam) {
        SearchResult result = null;
        //通过查询参数构造查询请求
        SearchRequest request = bulidSearchRequest(searchParam);

        try {
            SearchResponse search = restHighLevelClient.search(request, ElasticSearchConfig.COMMON_OPTIONS);
            //将es的返回结果进行封装
            result = bulidSearchResult(searchParam,search);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    private SearchResult bulidSearchResult(SearchParam searchParam, SearchResponse search) {
        SearchSourceBuilder searchRequest = new SearchSourceBuilder();
        //构建bool query
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        //1.bool must
        if(!StringUtils.isEmpty(searchParam.getKeyword())){
            boolQueryBuilder.must(QueryBuilders.matchQuery("skuTitle",searchParam.getKeyword()));
        }
        //2.bool filter
        //2.1 catalogId  分类id
        if(searchParam.getCatalog3Id()!=null){
            boolQueryBuilder.filter(QueryBuilders.termQuery("catalogId",searchParam.getCatalog3Id()));
        }
        //2.2 brandId   品牌id
        if(searchParam.getBrandId()!=null){
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId",searchParam.getBrandId()));
        }
        //2.3  hasStock 是否有库存
        if(searchParam.getHasStock()!=null){
            boolQueryBuilder.filter(QueryBuilders.termQuery("hasStock",searchParam.getHasStock()==1));
        }
        //2.4  skuPrice
        RangeQueryBuilder skuPrice = QueryBuilders.rangeQuery("skuPrice");
        String paramSkuPrice = searchParam.getSkuPrice();
        if(!StringUtils.isEmpty(paramSkuPrice)){
            String[] price = paramSkuPrice.split("_");
            if(price.length==1){
                if(paramSkuPrice.startsWith("_")){
                    skuPrice.lte(Integer.parseInt(price[0]));
                } else{
                    skuPrice.gte(Integer.parseInt(price[0]));
                }
            } else if(price.length==2) {
                //_6000会被分割为["","6000"]
                if(!price[0].isEmpty()){
                    skuPrice.gte(price[0]);
                }
                skuPrice.lte(price[1]);
            }
            boolQueryBuilder.filter(skuPrice);

            // attrs-nested

        }



        return new SearchResult();
    }

    private SearchRequest bulidSearchRequest(SearchParam searchParam) {


        return null;
    }
}
