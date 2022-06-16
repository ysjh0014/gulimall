package com.mg.gulimall.product.dao;

import com.mg.gulimall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 *
 * @author yusong
 * @email ysjh0014@gmail.com
 * @date 2022-06-16 20:34:52
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {

}
