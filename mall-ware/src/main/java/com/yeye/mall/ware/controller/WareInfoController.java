package com.yeye.mall.ware.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

import com.yeye.mall.ware.entity.WareInfoEntity;
import com.yeye.mall.ware.service.WareInfoService;
import com.yeye.mall.ware.vo.FareVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.R;


/**
 * 仓库信息
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 19:49:41
 */
@RestController
@RequestMapping("ware/wareinfo")
public class WareInfoController {
  @Autowired
  private WareInfoService wareInfoService;

  @GetMapping("/getFare")
  public R getFare(@RequestParam("addrId")  Long addrId){
    FareVo data = wareInfoService.getFare(addrId);

    return R.ok().put("data",data);
  }

  /**
   * 列表
   */
  @RequestMapping("/list")

  public R list(@RequestParam Map<String, Object> params) {
    PageUtils page = wareInfoService.queryPage(params);

    return R.ok().put("page", page);
  }


  /**
   * 信息
   */
  @RequestMapping("/info/{id}")

  public R info(@PathVariable("id") Long id) {
    WareInfoEntity wareInfo = wareInfoService.getById(id);

    return R.ok().put("wareInfo", wareInfo);
  }

  /**
   * 保存
   */
  @RequestMapping("/save")

  public R save(@RequestBody WareInfoEntity wareInfo) {
    wareInfoService.save(wareInfo);

    return R.ok();
  }

  /**
   * 修改
   */
  @RequestMapping("/update")

  public R update(@RequestBody WareInfoEntity wareInfo) {
    wareInfoService.updateById(wareInfo);

    return R.ok();
  }

  /**
   * 删除
   */
  @RequestMapping("/delete")

  public R delete(@RequestBody Long[] ids) {
    wareInfoService.removeByIds(Arrays.asList(ids));

    return R.ok();
  }

}
