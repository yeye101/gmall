package com.yeye.mall.ware.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.yeye.mall.ware.vo.MergeVo;
import com.yeye.mall.ware.vo.PurchaseFinishVo;
import com.yeye.mall.ware.entity.PurchaseEntity;
import com.yeye.mall.ware.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.R;


/**
 * 采购信息
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 19:49:41
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
  @Autowired
  private PurchaseService purchaseService;

  /**
   * 查询未领取的采购单 /ware/purchase/unreceive/list
   */
  @RequestMapping("/unreceive/list")
  public R unreceiveList(@RequestParam Map<String, Object> params) {
    PageUtils page = purchaseService.queryPageUnreceive(params);

    return R.ok().put("page", page);
  }

  /**
   * 合并采购需求 /ware/purchase/merge
   */
  @PostMapping("/merge")
  public R merge(@RequestBody MergeVo vo) {
    purchaseService.mergePurchase(vo);

    return R.ok();
  }


  /**
   * 领取采购单 /ware/purchase/received
   */
  @PostMapping("/received")
  public R received(@RequestBody List<Long> ids) {
    purchaseService.receivedPurchase(ids);

    return R.ok();
  }


  /**
   * 完成采购 /ware/purchase/done
   */
  @PostMapping("/done")
  public R finish(@RequestBody PurchaseFinishVo vo) {
    purchaseService.done(vo);

    return R.ok();
  }



  /**
   * 列表
   */
  @RequestMapping("/list")
  public R list(@RequestParam Map<String, Object> params) {
    PageUtils page = purchaseService.queryPage(params);

    return R.ok().put("page", page);
  }


  /**
   * 信息
   */
  @RequestMapping("/info/{id}")
  public R info(@PathVariable("id") Long id) {
    PurchaseEntity purchase = purchaseService.getById(id);

    return R.ok().put("purchase", purchase);
  }

  /**
   * 保存
   */
  @RequestMapping("/save")
  public R save(@RequestBody PurchaseEntity purchase) {
    purchase.setCreateTime(new Date());
    purchase.setUpdateTime(new Date());
    purchaseService.save(purchase);

    return R.ok();
  }

  /**
   * 修改
   */
  @RequestMapping("/update")
  public R update(@RequestBody PurchaseEntity purchase) {
    purchase.setUpdateTime(new Date());
    purchaseService.updateById(purchase);

    return R.ok();
  }

  /**
   * 删除
   */
  @RequestMapping("/delete")
  public R delete(@RequestBody Long[] ids) {
    purchaseService.removeByIds(Arrays.asList(ids));

    return R.ok();
  }

}
