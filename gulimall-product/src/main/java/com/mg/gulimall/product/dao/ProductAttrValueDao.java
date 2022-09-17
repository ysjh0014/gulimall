package com.mg.gulimall.product.dao;

import com.mg.gulimall.product.entity.ProductAttrValueEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mg.gulimall.product.vo.SpuItemAttrGroupVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * spu属性值
 *
 * @author yusong
 * @email ysjh0014@gmail.com
 * @date 2022-06-16 20:34:52
 */
@Mapper
public interface ProductAttrValueDao extends BaseMapper<ProductAttrValueEntity> {

    List<SpuItemAttrGroupVo> getProductGroupAttrsBySpuId(Long spuId, Long catalogId);
}
