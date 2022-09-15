package com.mg.gulimall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.mg.common.constant.ProductConstant;
import com.mg.common.to.SkuModelVo;
import com.mg.common.to.SkuReductionVo;
import com.mg.common.to.SkuStacksVo;
import com.mg.common.to.SpuBoundsVo;
import com.mg.common.utils.R;
import com.mg.gulimall.product.entity.*;
import com.mg.gulimall.product.feign.CouponFeignService;
import com.mg.gulimall.product.feign.SearchFeignService;
import com.mg.gulimall.product.feign.WareFeignService;
import com.mg.gulimall.product.service.*;
import com.mg.gulimall.product.vo.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mg.common.utils.PageUtils;
import com.mg.common.utils.Query;

import com.mg.gulimall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    SpuInfoDescService spuInfoDescService;
    @Autowired
    SpuImagesService spuImagesService;
    @Autowired
    ProductAttrValueService productAttrValueService;
    @Autowired
    AttrService attrService;
    @Autowired
    SkuInfoService skuInfoService;
    @Autowired
    SkuImagesService skuImagesService;
    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    CouponFeignService couponFeignService;
    @Autowired
    WareFeignService wareFeignService;
    @Autowired
    BrandService brandService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    SearchFeignService searchFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveBathInfo(SpuInfoVo spuInfoVo) {
        //1.保存spu的基本信息  pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuInfoVo, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.saveSpuInfo(spuInfoEntity);

        //2.保存spu的描述信息  pms_spu_info_desc
        List<String> decripts = spuInfoVo.getDecript();
        SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
        descEntity.setSpuId(spuInfoEntity.getId());
        descEntity.setDecript(String.join(",", decripts));
        spuInfoDescService.saveDesc(descEntity);

        //3.保存spu的图片集   pms_spu_images
        List<String> images = spuInfoVo.getImages();
        spuImagesService.saveBatchImages(spuInfoEntity.getId(), images);

        //4.保存spu的规格参数   pms_product_attr_value
        List<BaseAttrs> baseAttrs = spuInfoVo.getBaseAttrs();
        List<ProductAttrValueEntity> collect = baseAttrs.stream().map(item -> {
            ProductAttrValueEntity attrValueEntity = new ProductAttrValueEntity();
            attrValueEntity.setAttrId(item.getAttrId());
            attrValueEntity.setSpuId(spuInfoEntity.getId());
            AttrRespVo attrInfo = attrService.getByInfoId(item.getAttrId());
            attrValueEntity.setAttrName(attrInfo.getAttrName());
            attrValueEntity.setAttrValue(item.getAttrValues());
            attrValueEntity.setQuickShow(item.getShowDesc());
            return attrValueEntity;
        }).collect(Collectors.toList());
        productAttrValueService.saveAttrValue(collect);

        //5.保存spu的积分信息   gulimall_sms->sms_spu_bounds
        Bounds bounds = spuInfoVo.getBounds();
        SpuBoundsVo spuBoundsVo = new SpuBoundsVo();
        BeanUtils.copyProperties(bounds, spuBoundsVo);
        spuBoundsVo.setSpuId(spuInfoEntity.getId());
        R saveBounds = couponFeignService.saveBounds(spuBoundsVo);
        if (saveBounds.getCode() != 0) {
            log.error("远远程保存spu积分信息失败");
        }

        //6.保存当前spu对应的所有sku信息
        //1).保存sku的基本信息   pms_sku_info
        List<Skus> skus = spuInfoVo.getSkus();
        if (skus != null && skus.size() != 0) {
            skus.forEach(sku -> {
                String defaultImg = "";
                List<Images> skuImages = sku.getImages();
                for (Images image : skuImages) {
                    if (image.getDefaultImg() == 1) {
                        defaultImg = image.getImgUrl();
                    }
                }
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(sku, skuInfoEntity);
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSkuDefaultImg(defaultImg);

                skuInfoService.save(skuInfoEntity);

                //2).保存sku的图片信息  pms_sku_image
                List<SkuImagesEntity> skuImagesEntities = skuImages.stream().map(img -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuInfoEntity.getSkuId());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    return skuImagesEntity;
                }).collect(Collectors.toList());
                skuImagesService.saveBatch(skuImagesEntities);

                //3).保存sku的销售属性信息    pms_sku_sale_attr_value
                List<Attr> skuAttr = sku.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = skuAttr.stream().map(attr -> {
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(sku, skuSaleAttrValueEntity);
                    skuSaleAttrValueEntity.setSkuId(skuInfoEntity.getSkuId());
                    return skuSaleAttrValueEntity;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);


                //4).sku的满减优惠等信息      gulimall_sms->sms_sku_ladder\sms_sku_full_reduction\sms_member_price
                SkuReductionVo skuReductionVo = new SkuReductionVo();
                BeanUtils.copyProperties(sku, skuReductionVo);
                skuReductionVo.setSkuId(skuInfoEntity.getSkuId());
                if (skuReductionVo.getFullCount() > 0 || skuReductionVo.getFullPrice().compareTo(new BigDecimal("0")) == 1) {
                    R saveInfo = couponFeignService.saveInfo(skuReductionVo);
                    if (saveInfo.getCode() != 0) {
                        log.error("远程保存sku优惠信息失败");
                    }
                }
            });


        }


    }

    @Override
    public PageUtils queryPageByParams(Map<String, Object> params) {

        QueryWrapper<SpuInfoEntity> queryWrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and(item -> {
                item.eq("spu_name", key).or().eq("id", key);
            });
        }

        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(catelogId) && !"0".equals(catelogId)) {
            queryWrapper.eq("catalog_id", catelogId);
        }

        String status = (String) params.get("status");
        if (!StringUtils.isEmpty(status)) {
            queryWrapper.eq("publish_status", status);
        }

        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equals(brandId)) {
            queryWrapper.eq("brand_id", brandId);
        }
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void up(Long spuId) {
        // 1 组装数据 查出当前spuId对应的所有sku信息
        List<SkuInfoEntity> skus = skuInfoService.getSkusById(spuId);
        // 查询这些sku是否有库存
        List<Long> skuIds = skus.stream().map(item -> item.getSkuId()).collect(Collectors.toList());

        // 2 封装每个sku的信息


        // 3.查询当前sku所有可以被用来检索的规格属性
        // 获取所有的spu商品的id 然后查询这些id中那些是可以被检索的 [数据库中目前 4、5、6、11不可检索]
        List<ProductAttrValueEntity> attrValueEntities = productAttrValueService.selectListAttrs(spuId);

        // 可检索的id集合
        List<Long> attIds = attrValueEntities.stream().map(item -> item.getAttrId()).collect(Collectors.toList());

        // 根据商品id 过滤不可检索的商品 最后映射号检索属性
        Set<Long> attSearchs = attrService.selectSearch(attIds);

        List<SkuModelVo.Attr> collect = attrValueEntities.stream().filter(item -> {
            return attSearchs.contains(item.getAttrId());
        }).map(attr -> {
            SkuModelVo.Attr attrs = new SkuModelVo.Attr();
            BeanUtils.copyProperties(attr, attrs);
            return attrs;
        }).collect(Collectors.toList());
        // skuId 对应 是否有库存
        //发送远程调用，查询是否有库存
        Map<Long,Boolean> stockMap = null;
        try {
            R skuStocks = wareFeignService.getSkuStocks(skuIds);
            stockMap = skuStocks.getData(new TypeReference<List<SkuStacksVo>>(){}).stream().collect(Collectors.toMap(SkuStacksVo::getSkuId,item->item.getHasStock()));
            log.warn("库存服务调用成功"+skuStocks);
        } catch (Exception e) {
            log.error("远程调用库存服务失败，原因:{}", e);
        }


        Map<Long, Boolean> finalStockMap = stockMap;
        List<SkuModelVo> skuModelVoList = skus.stream().map(sku -> {
            SkuModelVo skuModelVo = new SkuModelVo();
            BeanUtils.copyProperties(sku, skuModelVo);
            skuModelVo.setSkuPrice(sku.getPrice());
            skuModelVo.setSkuImg(sku.getSkuDefaultImg());
            //设置库存
            if (finalStockMap == null) {
                skuModelVo.setHasStock(true);
            } else {
                skuModelVo.setHasStock(finalStockMap.get(sku.getSkuId()));
            }
            // TODO 1.热度评分 0
            skuModelVo.setHotScore(0L);

            // brandName、brandImg
            BrandEntity brandEntity = brandService.getById(sku.getBrandId());
            skuModelVo.setBrandName(brandEntity.getName());
            skuModelVo.setBrandImg(brandEntity.getLogo());

            // 查询分类信息
            CategoryEntity categoryEntity = categoryService.getById(sku.getCatalogId());
            skuModelVo.setCatalogName(categoryEntity.getName());

            // 保存商品的属性
            skuModelVo.setAttrs(collect);
            return skuModelVo;
        }).collect(Collectors.toList());

        //发送ES进行保存
        R statusUp = searchFeignService.productStatusUp(skuModelVoList);
        if(statusUp.getCode()==0){
            baseMapper.updateSpuStatus(spuId, ProductConstant.StatusEnum.SPU_UP.getCode());
        } else {
            // 远程调用失败 TODO 接口幂等性 重试机制
            /**
             * Feign 的调用流程  Feign有自动重试机制
             * 1. 发送请求执行
             * 2.
             */
        }

    }

    private void saveSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);
    }

}