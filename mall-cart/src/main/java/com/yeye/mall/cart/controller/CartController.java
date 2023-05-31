package com.yeye.mall.cart.controller;

import com.yeye.common.utils.R;
import com.yeye.mall.cart.service.CartService;
import com.yeye.mall.cart.vo.CartItem;
import com.yeye.mall.cart.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CartController {

  public static final String REDIRECT_CART_SUCCESS_HTML = "redirect:http://cart.mall-yeye.com/addToCartSuccess.html";


  @Autowired
  CartService cartService;

  @GetMapping({"/cart.html"})
  public String toCartListPage(Model model) {

    CartVo cartVo = cartService.queryCart();
    model.addAttribute("cart", cartVo);

    return "cartList";
  }


  @GetMapping("/addToCart")
  public String addToCart(@RequestParam("skuId") Long skuId,
                          @RequestParam("num") Integer num,
                          RedirectAttributes redirectAttributes) {

    CartItem cartItem = cartService.addToCart(skuId, num);

    redirectAttributes.addAttribute("skuId", skuId);

    return REDIRECT_CART_SUCCESS_HTML;
  }

  @GetMapping("/addToCartSuccess.html")
  public String addToCartSuccess(@RequestParam("skuId") Long skuId, Model model) {

    CartItem cartItem = cartService.getCartItem(skuId);

    model.addAttribute("item", cartItem);

    return "success";
  }

  @GetMapping("/changeItem")
  @ResponseBody
  public R changeItem(@RequestParam("skuId") Long skuId,
                      @RequestParam(value = "check", required = false) Boolean check,
                      @RequestParam(value = "count", required = false) Integer count) {

    cartService.changeItem(skuId, check, count);
    return R.ok();
  }

  @GetMapping("/deleteItem")
  @ResponseBody
  public R deleteItem(@RequestParam("skuId") Long skuId) {

    cartService.deleteItem(skuId);
    return R.ok();
  }

  @GetMapping("/userCartItems/{memberId}")
  @ResponseBody
  public List<CartItem> getUserCartItems(@PathVariable("memberId") Long memberId) {

    List<CartItem> data = cartService.getUserCatItems(memberId);
    return data;
  }

}
