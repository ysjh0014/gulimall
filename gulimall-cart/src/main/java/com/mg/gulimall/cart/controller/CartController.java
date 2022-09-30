package com.mg.gulimall.cart.controller;

import com.mg.gulimall.cart.service.CartService;
import com.mg.gulimall.cart.vo.CartItemVo;
import com.mg.gulimall.cart.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CartController {

    @Autowired
    CartService cartService;

    /**
     * 添加商品到购物车
     * RedirectAttributes.addFlashAttribute():将数据放在session中，可以在页面中取出，但是只能取一次
     * RedirectAttributes.addAttribute():将数据拼接在url后面，?skuId=xxx
     *
     * @return
     */
    @GetMapping("/addToCart")
    public String addCartItem(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num, RedirectAttributes attributes) {
        cartService.addCartItem(skuId, num);
        attributes.addAttribute("skuId", skuId);
        //为了防止成功页刷新可以重复提交添加商品，我们不直接转到成功页
        return "redirect:http://localhost:40000/addCartItemSuccess";
    }

    @GetMapping("/addCartItemSuccess")
    public String addCartItemSuccess(@RequestParam("skuId") Long skuId, Model model) {
        CartItemVo cartItemVo = cartService.getCartItem(skuId);
        model.addAttribute("cartItem", cartItemVo);
        return "index";
    }

    @RequestMapping("/cart.html")
    public String getCartList(Model model) {
        CartVo cartVo=cartService.getCart();
        model.addAttribute("cart", cartVo);
        return "cart";
    }
}
