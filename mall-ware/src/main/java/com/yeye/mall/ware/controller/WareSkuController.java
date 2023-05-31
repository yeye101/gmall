package com.yeye.mall.ware.controller;

import com.yeye.common.exception.BizCodeEnum;
import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.R;
import com.yeye.mall.ware.entity.WareSkuEntity;
import com.yeye.mall.ware.service.WareSkuService;
import com.yeye.mall.ware.vo.SkuHasStockVo;
import com.yeye.mall.ware.vo.WareSkuLockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 商品库存
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 19:49:41
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
  @Autowired
  private WareSkuService wareSkuService;

  //查询sku是否有库存
  @PostMapping("/lock/order")
  public R lockOrder(@RequestBody WareSkuLockVo vo) {

    R r = null;
    try {
      r = wareSkuService.lockOrder(vo);
    } catch (RuntimeException e) {
      e.printStackTrace();
      r = R.error(BizCodeEnum.WARE_NO_SOCK_EXCEPTION.getCode(), BizCodeEnum.WARE_NO_SOCK_EXCEPTION.getMsg());
    }
    return r;
  }


  //查询sku是否有库存
  @PostMapping("/hasstock")
  public R getSkuHasStock(@RequestBody List<Long> skuIds) {
    List<SkuHasStockVo> vos = wareSkuService.getSkuHasStock(skuIds);

    return R.ok().put("data", vos);
  }

  /**
   * 列表
   */
  @RequestMapping("/list")

  public R list(@RequestParam Map<String, Object> params) {
    PageUtils page = wareSkuService.queryPage(params);

    return R.ok().put("page", page);
  }


  /**
   * 信息
   */
  @RequestMapping("/info/{id}")

  public R info(@PathVariable("id") Long id) {
    WareSkuEntity wareSku = wareSkuService.getById(id);

    return R.ok().put("wareSku", wareSku);
  }

  /**
   * 保存
   */
  @RequestMapping("/save")

  public R save(@RequestBody WareSkuEntity wareSku) {
    wareSkuService.save(wareSku);

    return R.ok();
  }

  /**
   * 修改
   */
  @RequestMapping("/update")

  public R update(@RequestBody WareSkuEntity wareSku) {
    wareSkuService.updateById(wareSku);

    return R.ok();
  }

  /**
   * 删除
   */
  @RequestMapping("/delete")

  public R delete(@RequestBody Long[] ids) {
    wareSkuService.removeByIds(Arrays.asList(ids));

    return R.ok();
  }

}
