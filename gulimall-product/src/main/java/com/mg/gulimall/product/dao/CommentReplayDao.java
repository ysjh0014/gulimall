package com.mg.gulimall.product.dao;

import com.mg.gulimall.product.entity.CommentReplayEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品评价回复关系
 *
 * @author yusong
 * @email ysjh0014@gmail.com
 * @date 2022-06-16 20:34:52
 */
@Mapper
public interface CommentReplayDao extends BaseMapper<CommentReplayEntity> {

}
