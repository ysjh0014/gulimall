package com.mg.gulimall.search.vo;

import com.mg.common.to.SkuModelVo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class SearchResult {

    //查询到的所有商品信息
    private List<SkuModelVo> skuModelVoList;

    //当前页码
    private Integer pageNum;

    //总记录数
    private Long total;

    //总页码
    private Integer totalPages;
    //页码遍历结果集(分页)
    private List<Integer> pageNavs;

    //当前查询到的结果，所有涉及到的品牌
    private List<BrandVo> brands;

    //当前查询到的结果，所有涉及到的所有属性
    private List<AttrVo> attrs;

    //当前查询到的结果，所有涉及到的所有分类
    private List<CatalogVo> catalogs;

    @Data
    @AllArgsConstructor
    public static class BrandVo{
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    @Data
    @AllArgsConstructor
    public static class AttrVo{
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }

    @Data
    @AllArgsConstructor
    public static class CatalogVo{
        private Long catalogId;
        private String catalogName;
    }

}
