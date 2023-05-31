package com.yeye.mall.order.controller;

import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.R;
import com.yeye.mall.order.entity.OrderEntity;
import com.yeye.mall.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 订单
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 19:48:25
 */
@RestController
@RequestMapping("order/order")
public class OrderController {
  @Autowired
  private OrderService orderService;

  @GetMapping("/getOrderInfoByOrderSn/{orderSn}")
  public R getOrderInfoByOrderSn(@PathVariable("orderSn") String orderSn) {
    OrderEntity order = orderService.getOrderInfoByOrderSn(orderSn);

    return R.ok().put("data", order);
  }

  /**
   * 列表详细列表
   */
  @PostMapping("/listWithItems")
  public R listWithItems(@RequestBody Map<String, Object> params) {
    PageUtils data = orderService.listWithItems(params);

    return R.ok().put("data", data);
  }


  /**
   * 列表
   */
  @RequestMapping("/list")
  public R list(@RequestParam Map<String, Object> params) {
    PageUtils page = orderService.queryPage(params);

    return R.ok().put("page", page);
  }


  /**
   * 信息
   */
  @RequestMapping("/info/{id}")

  public R info(@PathVariable("id") Long id) {
    OrderEntity order = orderService.getById(id);

    return R.ok().put("order", order);
  }

  /**
   * 保存
   */
  @RequestMapping("/save")

  public R save(@RequestBody OrderEntity order) {
    orderService.save(order);

    return R.ok();
  }

  /**
   * 修改
   */
  @RequestMapping("/update")

  public R update(@RequestBody OrderEntity order) {
    orderService.updateById(order);

    return R.ok();
  }

  /**
   * 删除
   */
  @RequestMapping("/delete")

  public R delete(@RequestBody Long[] ids) {
    orderService.removeByIds(Arrays.asList(ids));

    return R.ok();
  }

}
