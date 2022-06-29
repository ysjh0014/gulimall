package com.mg.gulimall.order.dao;

import com.mg.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 *
 * @author yusong
 * @email ysjh0014@gmail.com
 * @date 2022-06-21 17:40:17
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {

}
