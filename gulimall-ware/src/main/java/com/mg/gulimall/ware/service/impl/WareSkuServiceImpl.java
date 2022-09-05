package com.mg.gulimall.ware.service.impl;

import com.mg.common.to.SkuStacksVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mg.common.utils.PageUtils;
import com.mg.common.utils.Query;

import com.mg.gulimall.ware.dao.WareSkuDao;
import com.mg.gulimall.ware.entity.WareSkuEntity;
import com.mg.gulimall.ware.service.WareSkuService;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();
        String skuId = (String)params.get("skuId");
        if(!StringUtils.isEmpty(skuId)){
            queryWrapper.eq("sku_id",skuId);
        }
        String wareId = (String)params.get("wareId");
        if(!StringUtils.isEmpty(wareId)){
            queryWrapper.eq("ware_id",wareId);
        }
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuStacksVo> getSkuStocks(List<Long> ids) {
        List<SkuStacksVo> collect = ids.stream().map(item -> {
            SkuStacksVo skuStacksVo = new SkuStacksVo();
            skuStacksVo.setSkuId(item);
            skuStacksVo.setHasStock(baseMapper.getSkuStock(item) == null ? false : true);
            return skuStacksVo;
        }).collect(Collectors.toList());
        return collect;
    }

}