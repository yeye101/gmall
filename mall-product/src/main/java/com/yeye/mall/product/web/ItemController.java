package com.yeye.mall.product.web;

import com.yeye.mall.product.service.SkuInfoService;
import com.yeye.mall.product.vo.web.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ItemController {

  @Autowired
  SkuInfoService skuInfoService;

  @GetMapping("/{skuId}.html")
  public String skuItem(@PathVariable("skuId") Long skuId, Model model) {


    SkuItemVo skuItemVo = skuInfoService.queryItem(skuId);
    model.addAttribute("item",skuItemVo);
    return "item";
  }

}
