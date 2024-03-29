package com.mg.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mg.common.utils.PageUtils;
import com.mg.gulimall.product.entity.ProductAttrValueEntity;
import com.mg.gulimall.product.vo.SpuItemAttrGroupVo;

import java.util.List;
import java.util.Map;

/**
 * spu属性值
 *
 * @author yusong
 * @email ysjh0014@gmail.com
 * @date 2022-06-16 20:34:52
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttrValue(List<ProductAttrValueEntity> collect);

    List<ProductAttrValueEntity> selectListAttrs(Long spuId);

    List<SpuItemAttrGroupVo> getProductGroupAttrsBySpuId(Long spuId, Long catalogId);
}

