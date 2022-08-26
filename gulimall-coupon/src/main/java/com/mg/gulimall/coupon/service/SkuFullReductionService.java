package com.mg.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mg.common.to.SkuReductionVo;
import com.mg.common.utils.PageUtils;
import com.mg.gulimall.coupon.entity.SkuFullReductionEntity;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author yusong
 * @email ysjh0014@gmail.com
 * @date 2022-06-17 21:46:27
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveInfo(SkuReductionVo skuReductionVo);
}

