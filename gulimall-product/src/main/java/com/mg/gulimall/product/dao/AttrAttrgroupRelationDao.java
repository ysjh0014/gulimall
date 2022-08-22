package com.mg.gulimall.product.dao;

import com.mg.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 属性&属性分组关联
 *
 * @author yusong
 * @email ysjh0014@gmail.com
 * @date 2022-06-16 20:34:51
 */
@Mapper
public interface AttrAttrgroupRelationDao extends BaseMapper<AttrAttrgroupRelationEntity> {

    void deleteBatchRelation(@Param("collects") List<AttrAttrgroupRelationEntity> collects);
}
