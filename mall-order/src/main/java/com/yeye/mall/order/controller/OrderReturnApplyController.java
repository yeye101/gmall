package com.yeye.mall.order.controller;

import java.util.Arrays;
import java.util.Map;

import com.yeye.mall.order.entity.OrderReturnApplyEntity;
import com.yeye.mall.order.service.OrderReturnApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.R;


/**
 * 订单退货申请
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 19:48:25
 */
@RestController
@RequestMapping("order/orderreturnapply")
public class OrderReturnApplyController {
  @Autowired
  private OrderReturnApplyService orderReturnApplyService;

  /**
   * 列表
   */
  @RequestMapping("/list")

  public R list(@RequestParam Map<String, Object> params) {
    PageUtils page = orderReturnApplyService.queryPage(params);

    return R.ok().put("page", page);
  }


  /**
   * 信息
   */
  @RequestMapping("/info/{id}")

  public R info(@PathVariable("id") Long id) {
    OrderReturnApplyEntity orderReturnApply = orderReturnApplyService.getById(id);

    return R.ok().put("orderReturnApply", orderReturnApply);
  }

  /**
   * 保存
   */
  @RequestMapping("/save")

  public R save(@RequestBody OrderReturnApplyEntity orderReturnApply) {
    orderReturnApplyService.save(orderReturnApply);

    return R.ok();
  }

  /**
   * 修改
   */
  @RequestMapping("/update")

  public R update(@RequestBody OrderReturnApplyEntity orderReturnApply) {
    orderReturnApplyService.updateById(orderReturnApply);

    return R.ok();
  }

  /**
   * 删除
   */
  @RequestMapping("/delete")

  public R delete(@RequestBody Long[] ids) {
    orderReturnApplyService.removeByIds(Arrays.asList(ids));

    return R.ok();
  }

}
