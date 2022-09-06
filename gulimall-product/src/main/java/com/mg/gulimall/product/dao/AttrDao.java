package com.mg.gulimall.product.dao;

import com.mg.gulimall.product.entity.AttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 商品属性
 *
 * @author yusong
 * @email ysjh0014@gmail.com
 * @date 2022-06-16 20:34:51
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {

    Set<Long> selectSearch(@Param("attrIds") List<Long> attIds);
}
