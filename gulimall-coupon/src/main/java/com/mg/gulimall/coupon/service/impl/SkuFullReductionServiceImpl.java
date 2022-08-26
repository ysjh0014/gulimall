package com.mg.gulimall.coupon.service.impl;

import com.mg.common.to.MemberPrice;
import com.mg.common.to.SkuReductionVo;
import com.mg.gulimall.coupon.entity.MemberPriceEntity;
import com.mg.gulimall.coupon.entity.SkuLadderEntity;
import com.mg.gulimall.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Member;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mg.common.utils.PageUtils;
import com.mg.common.utils.Query;

import com.mg.gulimall.coupon.dao.SkuFullReductionDao;
import com.mg.gulimall.coupon.entity.SkuFullReductionEntity;
import com.mg.gulimall.coupon.service.SkuFullReductionService;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    SkuLadderService skuLadderService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveInfo(SkuReductionVo skuReductionVo) {
        //sku的优惠、满减等信息；gulimall_sms->sms_sku_ladder\sms_sku_full_reduction\sms_member_price
        //sms_sku_ladder
        SkuLadderEntity skuLadder = new SkuLadderEntity();
        skuLadder.setFullCount(skuReductionVo.getFullCount());
        skuLadder.setSkuId(skuReductionVo.getSkuId());
        skuLadder.setDiscount(skuReductionVo.getDiscount());
        skuLadder.setAddOther(skuReductionVo.getCountStatus());
        if (skuLadder.getFullCount() > 0) {
            skuLadderService.save(skuLadder);
        }

        //sms_sku_full_reduction
        SkuFullReductionEntity reductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuReductionVo, reductionEntity);
        if (reductionEntity.getFullPrice().compareTo(new BigDecimal("0")) == 1) {
            this.save(reductionEntity);
        }

        //sms_member_price
        List<MemberPrice> prices = skuReductionVo.getMemberPrice();
        prices.stream().map(price->{
            MemberPriceEntity memberPrice = new MemberPriceEntity();
            memberPrice.setSkuId(skuReductionVo.getSkuId());
            memberPrice.setMemberPrice(price.getPrice());
            memberPrice.setMemberLevelName(price.getName());
            memberPrice.setMemberLevelId(price.getId());
            return memberPrice;
        }).collect(Collectors.toList());


        List<MemberPrice> memberPrice = skuReductionVo.getMemberPrice();
    }

}