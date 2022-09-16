package com.mg.gulimall.search.service.impl;

import com.mg.gulimall.search.GulimallSearchApplication;
import com.mg.gulimall.search.config.ElasticSearchConfig;
import com.mg.gulimall.search.constant.EsConstant;
import com.mg.gulimall.search.service.SearchMallService;
import com.mg.gulimall.search.vo.SearchParam;
import com.mg.gulimall.search.vo.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
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
            result = bulidSearchRequest(searchParam,search);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    private SearchRequest bulidSearchRequest(SearchParam searchParam) {
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
            // attrs=1_5寸:8寸&2_16G:8G
            List<String> attrs = searchParam.getAttrs();
            BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
            if(attrs!=null&&attrs.size()!=0){
                attrs.forEach(attr->{
                    String[] attrIds = attr.split("_");
                    queryBuilder.must(QueryBuilders.termQuery("attr.attrId",attrIds[0]));
                    String[] attrValues = attrIds[1].split(":");
                    queryBuilder.must(QueryBuilders.termsQuery("attr.attrValue",attrValues));
                });
            }
            NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("attrs", queryBuilder, ScoreMode.None);
            boolQueryBuilder.filter(nestedQueryBuilder);
            // bool query构建完成
            searchRequest.query(boolQueryBuilder);
        }

        // sort sort=saleCount_desc/asc
        String sort = searchParam.getSort();
        if(!StringUtils.isEmpty(sort)){
            String[] sorts = sort.split("_");
            searchRequest.sort(sorts[0],sorts[1].equalsIgnoreCase("asc")? SortOrder.ASC : SortOrder.DESC);
        }

        //分页
        searchRequest.from(searchParam.getPageNum() * EsConstant.PRODUCT_PAGESIZE);
        searchRequest.size(EsConstant.PRODUCT_PAGESIZE);

        //TODO 高亮


        //聚合
        //1.按照brand聚合
        TermsAggregationBuilder brandId = AggregationBuilders.terms("brandAgg").field("brandId");
        TermsAggregationBuilder brandName = AggregationBuilders.terms("brandNameAgg").field("brandName");
        TermsAggregationBuilder brandImg = AggregationBuilders.terms("brandImgAgg").field("brandImg");
        brandId.subAggregation(brandName);
        brandId.subAggregation(brandImg);
        searchRequest.aggregation(brandId);

        //2.按照catalog聚合
        TermsAggregationBuilder catalogId = AggregationBuilders.terms("catalogAgg").field("catalogId");
        TermsAggregationBuilder catalogName = AggregationBuilders.terms("catalogNamAgg").field("catalogName");
        catalogId.subAggregation(catalogName);
        searchRequest.aggregation(catalogId);

        //3.按照attrs聚合
        NestedAggregationBuilder nestedAggregationBuilder = new NestedAggregationBuilder("attrs", "attrs");
        TermsAggregationBuilder attrId = AggregationBuilders.terms("attrIdAgg").field("attrs.attrId");
        TermsAggregationBuilder attrName = AggregationBuilders.terms("attsNameAgg").field("attrs.attrName");
        TermsAggregationBuilder attrValue = AggregationBuilders.terms("attsValueAgg").field("attrs.attrValue");
        attrId.subAggregation(attrName);
        attrId.subAggregation(attrValue);
        nestedAggregationBuilder.subAggregation(attrId);
        searchRequest.aggregation(nestedAggregationBuilder);

        log.debug("构建的DSL语句 {}",searchRequest.toString());

        SearchRequest request = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, searchRequest);
        return request;
    }

    private SearchResult bulidSearchRequest(SearchParam searchParam, SearchResponse search) {


        return null;
    }
}
