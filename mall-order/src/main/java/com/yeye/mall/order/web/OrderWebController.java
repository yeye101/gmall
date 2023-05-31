package com.yeye.mall.order.web;

import com.alibaba.fastjson.TypeReference;
import com.yeye.common.utils.R;
import com.yeye.mall.order.service.OrderService;
import com.yeye.mall.order.to.OrderCreateTo;
import com.yeye.mall.order.vo.OrderConfirmVo;
import com.yeye.mall.order.vo.OrderSubmitVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class OrderWebController {

  @Autowired
  OrderService orderService;

  @GetMapping("/toTrade")
  public String toTrade(Model model) {
    OrderConfirmVo confirmVo = orderService.getConfirmOrder();
    model.addAttribute("orderConfirmData", confirmVo);
    return "confirm";
  }

  @PostMapping("/submitOrder")
  public String submitOrder(OrderSubmitVo vo, Model model, RedirectAttributes attributes) {

    R r = null;
    try {
      r = orderService.submitOrder(vo);
    } catch (Exception e) {
      e.printStackTrace();
      r = R.error(e.getMessage());

    }

    if (r.getCode() != 0) {
      attributes.addFlashAttribute("errorMsg", r.getMsg());
      return "redirect:http://order.mall-yeye.com/toTrade";
    }

    model.addAttribute("responseVo", r.getData("data", new TypeReference<OrderCreateTo>() {
    }));
    return "pay";
  }

  @GetMapping("/detail/{orderSn}")
  public String orderDetail(@PathVariable("orderSn") String orderSn,
                            Model model,
                            RedirectAttributes attributes) {

    R r = orderService.orderDetail(orderSn);


    OrderCreateTo data = r.getData("data", new TypeReference<OrderCreateTo>() {
    });
    model.addAttribute("res", data);
    return "detail";
  }

  @GetMapping("/order/recive")
  @ResponseBody
  public R reciveOrder(@RequestParam("orderSn") String orderSn) {
    return orderService.reciveOrder(orderSn);
  }

  @GetMapping("/order/cancle")
  @ResponseBody
  public R cancleOrder(@RequestParam("orderSn") String orderSn) {
    return orderService.cancleOrder(orderSn);
  }



}
