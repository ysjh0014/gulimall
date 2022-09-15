package com.mg.gulimall.search.vo;

import lombok.Data;

import java.util.List;

/**
 * 全文检索：skuTitle-》keyword
 *
 * 排序：saleCount（销量）、hotScore（热度分）、skuPrice（价格）
 *
 * 过滤：hasStock、skuPrice区间、brandId、catalog3Id、attrs
 *
 * 聚合：attrs
 */
@Data
public class SearchParam {

    //页面传递过来的全文匹配关键字
    private String keyword;
    //三级分类id
    private Long catalog3Id;
    //品牌id,可以多选
    private List<Long> brandId;
    /**
     * 排序条件
     *  sort = price_desc/asc
     *  sort = salecount_desc_asc
     *  sort = hotscore_desc/asc
     */
    private String sort;
    //是否显示有货 0/1
    private Integer hasStock;
    //价格区间查询 1_500/_500/1_
    private String skuPrice;
    //按照属性进行筛选
    private List<String> attrs;
    //页码
    private Integer pageNum = 1;
    //原生的所有查询条件
    private String _queryString;
}
