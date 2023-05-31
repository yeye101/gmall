package com.yeye.mall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeye.common.constant.WareConstant;
import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.Query;
import com.yeye.mall.ware.dao.PurchaseDao;
import com.yeye.mall.ware.entity.PurchaseDetailEntity;
import com.yeye.mall.ware.entity.PurchaseEntity;
import com.yeye.mall.ware.service.PurchaseDetailService;
import com.yeye.mall.ware.service.PurchaseService;
import com.yeye.mall.ware.service.WareSkuService;
import com.yeye.mall.ware.vo.MergeVo;
import com.yeye.mall.ware.vo.PurchaseFinishVo;
import com.yeye.mall.ware.vo.PurchaseItemFinishVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

  @Autowired
  PurchaseDetailService purchaseDetailService;

  @Autowired
  WareSkuService wareSkuService;

  @Override
  public PageUtils queryPage(Map<String, Object> params) {

    LambdaQueryWrapper<PurchaseEntity> wrapper = new LambdaQueryWrapper<>();
    String key = (String) params.get("key");
    if (!StringUtils.isEmpty(key)) {
      wrapper.eq(PurchaseEntity::getId, key).or().like(PurchaseEntity::getAssigneeName, key);
    }

    String status = (String) params.get("status");
    if (!StringUtils.isEmpty(status)) {
      wrapper.eq(PurchaseEntity::getStatus, status);
    }

    wrapper.orderByDesc(PurchaseEntity::getId);


    IPage<PurchaseEntity> page = this.page(
      new Query<PurchaseEntity>().getPage(params),
      wrapper
    );

    return new PageUtils(page);
  }

  @Override
  public PageUtils queryPageUnreceive(Map<String, Object> params) {
    LambdaQueryWrapper<PurchaseEntity> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(PurchaseEntity::getStatus, WareConstant.PurchaseStatusEnum.CREATED.getCode())
      .or().eq(PurchaseEntity::getStatus, WareConstant.PurchaseStatusEnum.ASSIGNED.getCode());

    IPage<PurchaseEntity> page = this.page(
      new Query<PurchaseEntity>().getPage(params),
      wrapper
    );

    return new PageUtils(page);

  }

  @Transactional
  @Override
  public void mergePurchase(MergeVo vo) {
    Long purchaseId = vo.getPurchaseId();

    PurchaseEntity purchaseEntity = new PurchaseEntity();
    purchaseEntity.setId(purchaseId);
    purchaseEntity.setUpdateTime(new Date());
    if (purchaseId == null) {
      purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
      purchaseEntity.setCreateTime(new Date());
      this.save(purchaseEntity);
      // 获取新增的ID
      purchaseId = purchaseEntity.getId();
    }
    this.updateById(purchaseEntity);

    List<Long> items = vo.getItems();
    Long finalPurchaseId = purchaseId;
    List<PurchaseDetailEntity> detailEntityList = items.stream().map(item -> {
      PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
      detailEntity.setId(item);
      detailEntity.setPurchaseId(finalPurchaseId);
      detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
      return detailEntity;

    }).collect(Collectors.toList());
    // 修改状态
    purchaseDetailService.updateBatchById(detailEntityList);

  }

  @Transactional
  @Override
  public void receivedPurchase(List<Long> ids) {
    //1. 确认采购单单状态
    if (ids == null) {
      return;
    }
    List<PurchaseEntity> purchaseEntityList = this.list(new LambdaQueryWrapper<PurchaseEntity>().in(PurchaseEntity::getId, ids));

    if (CollectionUtils.isEmpty(purchaseEntityList)) {
      return;
    }
    List<PurchaseEntity> collect = purchaseEntityList.stream().filter(item -> {
      if (item.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode() ||
        item.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode()) {
        return true;
      } else {
        return false;
      }
    }).peek(item -> {
      item.setStatus(WareConstant.PurchaseStatusEnum.RECEIVE.getCode());
      item.setUpdateTime(new Date());
    }).collect(Collectors.toList());
    //2. 改变采购单状态
    this.updateBatchById(collect);

    //3. 改变采购需求的状态
    List<PurchaseDetailEntity> detailEntityList = purchaseDetailService.list(
      new LambdaQueryWrapper<PurchaseDetailEntity>().in(PurchaseDetailEntity::getPurchaseId, ids));

    List<PurchaseDetailEntity> detailEntities = detailEntityList.stream().peek(item -> {
      item.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
    }).collect(Collectors.toList());

    purchaseDetailService.updateBatchById(detailEntities);

  }


  @Transactional
  @Override
  public void done(PurchaseFinishVo vo) {
    // todo 库存完善

    // 1.改变采购单状态
    Long id = vo.getId();
    Boolean aBoolean = Boolean.TRUE;
    // 2.采购项
    List<PurchaseItemFinishVo> items = vo.getItems();

    List<PurchaseDetailEntity> updates = new ArrayList<>();
    for (PurchaseItemFinishVo item : items) {
      PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
      if (item.getStatus() == WareConstant.PurchaseDetailStatusEnum.HASERROR.getCode()) {
        aBoolean = Boolean.FALSE;
        detailEntity.setStatus(item.getStatus());
      } else {
        detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.FINISH.getCode());
        // 3.成功的入库
        // 采购项的详细信息
        PurchaseDetailEntity entity = purchaseDetailService.getById(item.getItemId());
        if (entity != null){
          wareSkuService.addStock(entity.getSkuId(), entity.getWareId(), entity.getSkuNum());
        }
      }
      detailEntity.setId(item.getItemId());
      updates.add(detailEntity);
    }
    purchaseDetailService.updateBatchById(updates);

    PurchaseEntity purchaseEntity = new PurchaseEntity();
    purchaseEntity.setId(id);
    purchaseEntity.setUpdateTime(new Date());
    purchaseEntity.setStatus(aBoolean ?
      WareConstant.PurchaseStatusEnum.FINISH.getCode() : WareConstant.PurchaseStatusEnum.HASERROR.getCode());
    // 改采购单
    this.updateById(purchaseEntity);

  }

}