package com.mg.gulimall.product.service.impl;

import com.mg.gulimall.product.entity.SkuImagesEntity;
import com.mg.gulimall.product.entity.SpuInfoDescEntity;
import com.mg.gulimall.product.service.*;
import com.mg.gulimall.product.vo.SkuItemVo;
import com.mg.gulimall.product.vo.SkuItemSaleAttrVo;
import com.mg.gulimall.product.vo.SpuItemAttrGroupVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mg.common.utils.PageUtils;
import com.mg.common.utils.Query;

import com.mg.gulimall.product.dao.SkuInfoDao;
import com.mg.gulimall.product.entity.SkuInfoEntity;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {
    @Autowired
    SkuImagesService skuImagesService;
    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    SpuInfoDescService spuInfoDescService;
    @Autowired
    ProductAttrValueService productAttrValueService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageByParams(Map<String, Object> params) {

        QueryWrapper<SkuInfoEntity> queryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and(item -> {
                item.eq("sku_name", key).or().eq("sku_id", key);
            });
        }

        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(catelogId) && !"0".equals(catelogId)) {
            queryWrapper.eq("catalog_id", catelogId);
        }

        String min = (String) params.get("min");
        if (!StringUtils.isEmpty(min)) {
            queryWrapper.ge("price", min);
        }

        String max = (String) params.get("max");
        if (!StringUtils.isEmpty(max)) {
            BigDecimal bigDecimal = new BigDecimal(max);
            if (bigDecimal.compareTo(new BigDecimal("0")) == 1) {
                queryWrapper.le("price", max);
            }
        }

        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equals(brandId)) {
            queryWrapper.eq("brand_id", brandId);
        }


        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> getSkusById(Long spuId) {
        return this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id",spuId));
    }

    @Override
    public SkuItemVo item(Long skuId) {

        SkuItemVo skuItemVo = new SkuItemVo();
        //1、sku基本信息的获取  pms_sku_info
        SkuInfoEntity skuInfoEntity = this.getById(skuId);
        skuItemVo.setInfo(skuInfoEntity);
        Long spuId = skuInfoEntity.getSpuId();
        Long catalogId = skuInfoEntity.getCatalogId();


        //2、sku的图片信息    pms_sku_images
        List<SkuImagesEntity> skuImagesEntities = skuImagesService.list(new QueryWrapper<SkuImagesEntity>().eq("sku_id", skuId));
        skuItemVo.setImages(skuImagesEntities);

        //3、获取spu的销售属性组合-> 依赖1 获取spuId
        List<SkuItemSaleAttrVo> saleAttrVos=skuSaleAttrValueService.listSaleAttrs(spuId);
        skuItemVo.setSaleAttr(saleAttrVos);

        //4、获取spu的介绍-> 依赖1 获取spuId
        SpuInfoDescEntity byId = spuInfoDescService.getById(spuId);
        skuItemVo.setDesc(byId);

        //5、获取spu的规格参数信息-> 依赖1 获取spuId catalogId
        List<SpuItemAttrGroupVo> spuItemAttrGroupVos=productAttrValueService.getProductGroupAttrsBySpuId(spuId, catalogId);
        skuItemVo.setGroupAttrs(spuItemAttrGroupVos);
        //TODO 6、秒杀商品的优惠信息

        return skuItemVo;
    }

}