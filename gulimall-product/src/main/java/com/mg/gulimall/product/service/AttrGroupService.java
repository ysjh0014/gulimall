package com.mg.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mg.common.utils.PageUtils;
import com.mg.gulimall.product.entity.AttrEntity;
import com.mg.gulimall.product.entity.AttrGroupEntity;
import com.mg.gulimall.product.vo.AttrGroupWithAttrsVo;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author yusong
 * @email ysjh0014@gmail.com
 * @date 2022-06-16 20:34:51
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, Long catelogId);

    List<AttrGroupWithAttrsVo> getWithAttr(Long catelogId);
}

