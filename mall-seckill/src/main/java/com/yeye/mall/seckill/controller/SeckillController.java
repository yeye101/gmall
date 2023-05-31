package com.yeye.mall.seckill.controller;

import com.yeye.common.utils.R;
import com.yeye.mall.seckill.scheduled.SeckillSkuScheduled;
import com.yeye.mall.seckill.service.SeckillService;
import com.yeye.mall.seckill.to.SeckillSkuRedisTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class SeckillController {

  @Autowired
  private SeckillSkuScheduled seckillSkuScheduled;
  @Autowired
  private SeckillService seckillService;

  @ResponseBody
  @GetMapping("/test")
  public R test() throws InterruptedException {
    seckillSkuScheduled.uploadSeckillSku3Days();
    return R.ok();
  }

  @ResponseBody
  @GetMapping("/currentSeckillSkus")
  public R getCurrentSeckillSkus() {
    List<SeckillSkuRedisTo> redisTos = seckillService.getCurrentSeckillSkus();


    return R.ok().put("data", redisTos);
  }

  @ResponseBody
  @GetMapping("/seckill/skuSeckillInfo/{skuId}")
  public R getSkuSeckillInfo(@PathVariable("skuId") Long skuId) {
    SeckillSkuRedisTo redisTo = seckillService.getSkuSeckillInfo(skuId);


    return R.ok().put("data", redisTo);
  }

  @GetMapping("/kill")
  public String seckill(@RequestParam("id") String id,
                   @RequestParam("randomCode") String randomCode,
                   @RequestParam("num") Integer num,
                   Model model) {
    String orderSn = seckillService.seckill(id, randomCode, num);

    model.addAttribute("orderSn",orderSn);
    return "success";
  }
}
