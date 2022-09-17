package com.mg.gulimall.product.vo;

import com.mg.gulimall.product.entity.SkuImagesEntity;
import com.mg.gulimall.product.entity.SkuInfoEntity;
import com.mg.gulimall.product.entity.SpuInfoDescEntity;
import lombok.Data;
import java.util.List;

@Data
public class SkuItemVo {
    //1、sku基本信息的获取  pms_sku_info
    private SkuInfoEntity info;

    private boolean hasStock = true;

    //2、sku的图片信息    pms_sku_images
    private List<SkuImagesEntity> images;

    //3、获取spu的销售属性组合
    private List<SkuItemSaleAttrVo> saleAttr;

    //4、获取spu的介绍
    private SpuInfoDescEntity desc;

    //5、获取spu的规格参数信息
    private List<SpuItemAttrGroupVo> groupAttrs;
    /**
     * 秒杀信息
     */
    SeckillInfoVo seckillSkuVo;
}
