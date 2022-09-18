package com.mg.gulimall.cart.service;

import com.mg.gulimall.cart.vo.CartItemVo;

public interface CartService {

    CartItemVo addCartItem(Long skuId, Integer num);

    CartItemVo getCartItem(Long skuId);
}
