package com.mg.gulimall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.mg.common.to.SkuModelVo;
import com.mg.gulimall.search.config.ElasticSearchConfig;
import com.mg.gulimall.search.constant.EsConstant;
import com.mg.gulimall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@Service
public class ProductSaveServiceImpl implements ProductSaveService {
    @Autowired
    RestHighLevelClient client;
    @Override
    public boolean productStatusUp(List<SkuModelVo> skuModelVoList) throws IOException {
        // 1.给ES建立一个索引 product
        BulkRequest bulkRequest = new BulkRequest();
        skuModelVoList.forEach(skuModelVo -> {
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            //设置索引id
            indexRequest.id(skuModelVo.getSkuId().toString());
            String jsonString = JSON.toJSONString(skuModelVo);
            indexRequest.source(jsonString, XContentType.JSON);
            bulkRequest.add(indexRequest);
        });
        BulkResponse bulk = client.bulk(bulkRequest, ElasticSearchConfig.COMMON_OPTIONS);

        boolean hasFailures = bulk.hasFailures();
        List<String> collect = Arrays.stream(bulk.getItems()).map(item -> {
            return item.getId();
        }).collect(Collectors.toList());

        log.info("商品上架完成:{}",collect);
        return !hasFailures;
    }
}
