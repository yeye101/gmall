package com.yeye.mall.coupon.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.yeye.mall.coupon.entity.SeckillSessionEntity;
import com.yeye.mall.coupon.service.SeckillSessionService;
import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.R;


/**
 * 秒杀活动场次
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 19:30:03
 */
@RestController
@RequestMapping("coupon/seckillsession")
public class SeckillSessionController {
  @Autowired
  private SeckillSessionService seckillSessionService;


  @GetMapping("/lates3DaysSession")
  public R getLates3DaysSession() {
    List<SeckillSessionEntity> data = seckillSessionService.getLates3DaysSession();

    return R.ok().put("data", data);
  }


  /**
   * 列表
   */
  @RequestMapping("/list")
  public R list(@RequestParam Map<String, Object> params) {
    PageUtils page = seckillSessionService.queryPage(params);

    return R.ok().put("page", page);
  }


  /**
   * 信息
   */
  @RequestMapping("/info/{id}")

  public R info(@PathVariable("id") Long id) {
    SeckillSessionEntity seckillSession = seckillSessionService.getById(id);

    return R.ok().put("seckillSession", seckillSession);
  }

  /**
   * 保存
   */
  @RequestMapping("/save")

  public R save(@RequestBody SeckillSessionEntity seckillSession) {
    seckillSessionService.save(seckillSession);

    return R.ok();
  }

  /**
   * 修改
   */
  @RequestMapping("/update")

  public R update(@RequestBody SeckillSessionEntity seckillSession) {
    seckillSessionService.updateById(seckillSession);

    return R.ok();
  }

  /**
   * 删除
   */
  @RequestMapping("/delete")

  public R delete(@RequestBody Long[] ids) {
    seckillSessionService.removeByIds(Arrays.asList(ids));

    return R.ok();
  }

}
