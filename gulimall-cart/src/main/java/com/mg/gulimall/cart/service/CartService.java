package com.mg.gulimall.cart.service;

import com.mg.gulimall.cart.vo.CartItemVo;
import com.mg.gulimall.cart.vo.CartVo;

public interface CartService {

    CartItemVo addCartItem(Long skuId, Integer num);

    CartItemVo getCartItem(Long skuId);

    CartVo getCart();

    void checkCart(Long skuId, Integer isChecked);

    void changeItemCount(Long skuId, Integer num);

    void deleteItem(Long skuId);
}
