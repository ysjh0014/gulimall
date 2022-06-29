package com.mg.gulimall.order.dao;

import com.mg.gulimall.order.entity.OrderItemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单项信息
 *
 * @author yusong
 * @email ysjh0014@gmail.com
 * @date 2022-06-21 17:40:17
 */
@Mapper
public interface OrderItemDao extends BaseMapper<OrderItemEntity> {

}
