package com.yeye.mall.order.web;

import com.yeye.mall.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class OrderPayController {

  @Autowired
  private OrderService orderService;

  @GetMapping(value = "/payOrder", produces = "text/html")
  @ResponseBody
  public String payOrder(@RequestParam("orderSn") String orderSn, Model model) {

    String payRes = orderService.payOrder(orderSn);

    return payRes;
  }
}
