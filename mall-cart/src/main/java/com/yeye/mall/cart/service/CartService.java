package com.yeye.mall.cart.service;

import com.yeye.mall.cart.vo.CartItem;
import com.yeye.mall.cart.vo.CartVo;

import java.util.List;

public interface CartService {
  CartItem addToCart(Long skuId, Integer num);

  CartItem getCartItem(Long skuId);

  CartVo queryCart();


  void deleteCart(String cartKey);

  void changeItem(Long skuId, Boolean check, Integer count);

  void deleteItem(Long skuId);

  List<CartItem> getUserCatItems(Long memberId);

}
