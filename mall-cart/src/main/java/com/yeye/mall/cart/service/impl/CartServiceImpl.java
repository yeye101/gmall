package com.yeye.mall.cart.service.impl;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.yeye.common.constant.CartConstant;
import com.yeye.common.utils.R;
import com.yeye.mall.cart.feign.ProductFeignService;
import com.yeye.mall.cart.interceptor.CartInterceptor;
import com.yeye.mall.cart.service.CartService;
import com.yeye.mall.cart.util.RedisUtil;
import com.yeye.mall.cart.vo.CartItem;
import com.yeye.mall.cart.vo.CartVo;
import com.yeye.mall.cart.vo.SkuInfoTo;
import com.yeye.mall.cart.vo.UserInfoTo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

  private final static Log log = LogFactory.get();

  @Autowired
  ThreadPoolExecutor executor;

  @Autowired
  private RedisUtil redisUtil;

  @Autowired
  private ProductFeignService productFeignService;

  @Override
  public CartItem addToCart(Long skuId, Integer num) {
    BoundHashOperations<String, Object, Object> cartOps = getCartOps();


    String res = (String) cartOps.get(skuId.toString());

    if (StringUtils.isNotEmpty(res)) {
      CartItem cartItem = JSON.parseObject(res, CartItem.class);
      cartItem.setCount(cartItem.getCount() + num);

      cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));
      return cartItem;
    }


    CartItem cartItem = new CartItem();
    CompletableFuture<Void> getSkuInfo = CompletableFuture.runAsync(() -> {
      R info = productFeignService.info(skuId);
      if (info.getCode() != 0) {
        log.error("远程调用报错，{}", info.getMsg());
      }
      SkuInfoTo skuInfo = info.getData("skuInfo", new TypeReference<SkuInfoTo>() {
      });
      cartItem.setSkuId(skuId);
      cartItem.setCount(num);
      cartItem.setImage(skuInfo.getSkuDefaultImg());
      cartItem.setTitle(skuInfo.getSkuTitle());
      cartItem.setChecked(Boolean.TRUE);
      cartItem.setPrice(skuInfo.getPrice());
      cartItem.setTotalPrice(skuInfo.getPrice().multiply(new BigDecimal("" + cartItem.getCount())));
    }, executor);

    CompletableFuture<Void> getSkuSaleAttrStr = CompletableFuture.runAsync(() -> {
      R r = productFeignService.querySaleAttrListById(skuId);
      if (r.getCode() != 0) {
        log.error("远程调用报错，{}", r.getMsg());
      }
      List<String> data = r.getData("data", new TypeReference<List<String>>() {
      });
      cartItem.setSkuAttr(data);

    }, executor);

    try {
      CompletableFuture.allOf(getSkuInfo, getSkuSaleAttrStr).get();
    } catch (InterruptedException | ExecutionException e) {
      log.error("异步编排获取sku信息失败{}", e);
    }

    cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));

    return cartItem;
  }

  @Override
  public CartItem getCartItem(Long skuId) {
    BoundHashOperations<String, Object, Object> cartOps = getCartOps();
    CartItem cartItem = new CartItem();
    String res = (String) cartOps.get(skuId.toString());

    if (StringUtils.isNotEmpty(res)) {
      cartItem = JSON.parseObject(res, CartItem.class);
      cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));
    }
    return cartItem;
  }

  @Override
  public CartVo queryCart() {
    CartVo cartVo = new CartVo();
    UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
    // 查询临时购物车
    String tempCartKey = CartConstant.CART_PREFIX + userInfoTo.getUserKey();
    cartVo = getCartVO(tempCartKey);

    if (userInfoTo.getUserId() == null) {
      return cartVo;
    }
    // 合并购物车
    if (!CollectionUtils.isEmpty(cartVo.getCartItems())) {
      cartVo.getCartItems().forEach(item -> this.addToCart(item.getSkuId(), item.getCount()));
      this.deleteCart(tempCartKey);
    }

    cartVo = getCartVO(CartConstant.CART_PREFIX + userInfoTo.getUserId());


    return cartVo;
  }

  @Override
  public void deleteCart(String cartKey) {
    redisUtil.delete(cartKey);

  }

  @Override
  public void changeItem(Long skuId, Boolean check, Integer count) {
    BoundHashOperations<String, Object, Object> ops = this.getCartOps();
    CartItem cartItem = this.getCartItem(skuId);

    if (check != null) {
      cartItem.setChecked(check);
    }
    if (count != null) {
      cartItem.setCount(count);
      cartItem.setTotalPrice(cartItem.getPrice().multiply(new BigDecimal("" + cartItem.getCount())));
    }

    ops.put(skuId.toString(), JSON.toJSONString(cartItem));
  }

  @Override
  public void deleteItem(Long skuId) {
    BoundHashOperations<String, Object, Object> ops = this.getCartOps();
    ops.delete(skuId.toString());
  }

  @Override
  public List<CartItem> getUserCatItems(Long memberId) {

    CartVo cartVO = getCartVO(CartConstant.CART_PREFIX + memberId);


    // 查出所有的sku信息
    List<Long> skuIds = cartVO.getCartItems().stream()
      .filter(CartItem::getChecked)
      .map(CartItem::getSkuId)
      .collect(Collectors.toList());
    if (CollectionUtils.isEmpty(skuIds)) {
      log.info("skuIds为空");
      return null;
    }
    R skuInfoRes = productFeignService.getSkuInfoByIds(skuIds);
    if (skuInfoRes.getCode() != 0) {
      log.error("远程方法调用错误--[productFeignService:getSkuInfoByIds]");
    }

    List<SkuInfoTo> skuInfoVoList = skuInfoRes.getData("data", new TypeReference<List<SkuInfoTo>>() {
    });
    Map<Long, SkuInfoTo> skuInfoVoMap = skuInfoVoList.stream().collect(Collectors.toMap(SkuInfoTo::getSkuId, v -> v));

    // 获取被选中的购物项
    List<CartItem> data = cartVO.getCartItems().stream()
      .filter(CartItem::getChecked)
      .map(item -> {

        if (skuInfoVoMap.containsKey(item.getSkuId())) {
          SkuInfoTo skuInfo = skuInfoVoMap.get(item.getSkuId());
          item.setTotalPrice(skuInfo.getPrice().multiply(new BigDecimal("" + item.getCount())));
          item.setImage(skuInfo.getSkuDefaultImg());
          item.setTitle(skuInfo.getSkuTitle());
          item.setPrice(skuInfo.getPrice());
          item.setSkuId(item.getSkuId());
          item.setCount(item.getCount());
          item.setChecked(Boolean.TRUE);
        }

        return item;
      }).collect(Collectors.toList());


    return data;
  }

  /**
   * 获取购物车
   *
   * @return
   */
  private BoundHashOperations<String, Object, Object> getCartOps() {
    UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
    String cartKey = "";
    if (userInfoTo.getUserId() != null) {
      cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserId();
    } else {
      cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserKey();
    }
    BoundHashOperations<String, Object, Object> ops = redisUtil.getStringRedisTemplate().boundHashOps(cartKey);
    return ops;
  }


  /**
   * 获取购物车
   *
   * @param cartKey
   * @return
   */
  private CartVo getCartVO(String cartKey) {
    CartVo cartVo = new CartVo();
    BoundHashOperations<String, Object, Object> ops = redisUtil.getStringRedisTemplate().boundHashOps(cartKey);
    List<Object> values = ops.values();

    if (CollectionUtils.isEmpty(values)) {
      cartVo.setReduce(BigDecimal.ZERO);
      cartVo.setTotalAmountPrice(BigDecimal.ZERO);
      return cartVo;
    }

    List<CartItem> cartItems = values.stream().map(item -> {
      String cartItem = (String) item;
      return JSON.parseObject(cartItem, CartItem.class);
    }).collect(Collectors.toList());

    Integer skuSum = cartItems.stream().map(CartItem::getCount).reduce(0, Integer::sum);

    cartVo.setCartItems(cartItems);
    cartVo.setCountNum(skuSum);
    cartVo.setCountType(cartItems.size());
    cartVo.setReduce(BigDecimal.ZERO);
    BigDecimal totalPrice = cartItems.stream().filter(CartItem::getChecked)
      .map(CartItem::getTotalPrice).reduce(cartVo.getReduce().negate(), BigDecimal::add);
    cartVo.setTotalAmountPrice(totalPrice);

    return cartVo;
  }
}
