package com.mg.gulimall.product.service.impl;

import com.mg.common.to.SkuReductionVo;
import com.mg.common.to.SpuBoundsVo;
import com.mg.common.utils.R;
import com.mg.gulimall.product.entity.*;
import com.mg.gulimall.product.feign.CouponFeignService;
import com.mg.gulimall.product.service.*;
import com.mg.gulimall.product.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
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
        BeanUtils.copyProperties(bounds,spuBoundsVo);
        spuBoundsVo.setSpuId(spuInfoEntity.getId());
        R saveBounds = couponFeignService.saveBounds(spuBoundsVo);
        if(saveBounds.getCode()!=0){
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
            });
            //4).sku的满减优惠等信息      gulimall_sms->sms_sku_ladder\sms_sku_full_reduction\sms_member_price
            SkuReductionVo skuReductionVo = new SkuReductionVo();





        }


    }

    private void saveSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);
    }

}