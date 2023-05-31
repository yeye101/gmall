package com.yeye.mall.order.controller;

import java.util.Arrays;
import java.util.Map;

import com.yeye.mall.order.entity.OrderOperateHistoryEntity;
import com.yeye.mall.order.service.OrderOperateHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.R;


/**
 * 订单操作历史记录
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 19:48:25
 */
@RestController
@RequestMapping("order/orderoperatehistory")
public class OrderOperateHistoryController {
  @Autowired
  private OrderOperateHistoryService orderOperateHistoryService;

  /**
   * 列表
   */
  @RequestMapping("/list")

  public R list(@RequestParam Map<String, Object> params) {
    PageUtils page = orderOperateHistoryService.queryPage(params);

    return R.ok().put("page", page);
  }


  /**
   * 信息
   */
  @RequestMapping("/info/{id}")

  public R info(@PathVariable("id") Long id) {
    OrderOperateHistoryEntity orderOperateHistory = orderOperateHistoryService.getById(id);

    return R.ok().put("orderOperateHistory", orderOperateHistory);
  }

  /**
   * 保存
   */
  @RequestMapping("/save")

  public R save(@RequestBody OrderOperateHistoryEntity orderOperateHistory) {
    orderOperateHistoryService.save(orderOperateHistory);

    return R.ok();
  }

  /**
   * 修改
   */
  @RequestMapping("/update")

  public R update(@RequestBody OrderOperateHistoryEntity orderOperateHistory) {
    orderOperateHistoryService.updateById(orderOperateHistory);

    return R.ok();
  }

  /**
   * 删除
   */
  @RequestMapping("/delete")

  public R delete(@RequestBody Long[] ids) {
    orderOperateHistoryService.removeByIds(Arrays.asList(ids));

    return R.ok();
  }

}
