package com.mg.gulimall.product.service.impl;

import com.mg.gulimall.product.vo.SpuItemAttrGroupVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mg.common.utils.PageUtils;
import com.mg.common.utils.Query;

import com.mg.gulimall.product.dao.ProductAttrValueDao;
import com.mg.gulimall.product.entity.ProductAttrValueEntity;
import com.mg.gulimall.product.service.ProductAttrValueService;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveAttrValue(List<ProductAttrValueEntity> collect) {
        this.saveBatch(collect);
    }

    @Override
    public List<ProductAttrValueEntity> selectListAttrs(Long spuId) {
        return this.baseMapper.selectList(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id",spuId));
    }

    @Override
    public List<SpuItemAttrGroupVo> getProductGroupAttrsBySpuId(Long spuId, Long catalogId) {
        return baseMapper.getProductGroupAttrsBySpuId(spuId, catalogId);
    }

}