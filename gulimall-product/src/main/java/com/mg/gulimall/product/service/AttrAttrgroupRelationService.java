package com.mg.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mg.common.utils.PageUtils;
import com.mg.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.mg.gulimall.product.vo.AttrGroupRelationVo;

import java.util.List;
import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author yusong
 * @email ysjh0014@gmail.com
 * @date 2022-06-16 20:34:51
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveBatchs(List<AttrGroupRelationVo> attrGroupRelationVo);
}

