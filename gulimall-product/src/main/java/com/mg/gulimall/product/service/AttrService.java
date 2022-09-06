package com.mg.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mg.common.utils.PageUtils;
import com.mg.gulimall.product.entity.AttrEntity;
import com.mg.gulimall.product.vo.AttrGroupRelationVo;
import com.mg.gulimall.product.vo.AttrRespVo;
import com.mg.gulimall.product.vo.AttrVo;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 商品属性
 *
 * @author yusong
 * @email ysjh0014@gmail.com
 * @date 2022-06-16 20:34:51
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils findAttrList(Long catelogId, Map<String, Object> params, String attrType);

    void saveAttr(AttrVo attr);

    AttrRespVo getByInfoId(Long attrId);

    void updateAttrById(AttrVo attr);

    List<AttrEntity> getRelationAttr(Long attrgroupId);

    PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId);

    void delRelation(List<AttrGroupRelationVo> attrGroupRelationVo);

    Set<Long> selectSearch(List<Long> attIds);
}

