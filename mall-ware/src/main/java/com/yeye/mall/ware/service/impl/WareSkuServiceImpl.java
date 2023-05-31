package com.yeye.mall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeye.common.to.mq.OrderTo;
import com.yeye.common.to.mq.StockLockTo;
import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.Query;
import com.yeye.common.utils.R;
import com.yeye.mall.ware.dao.WareSkuDao;
import com.yeye.mall.ware.entity.WareOrderTaskDetailEntity;
import com.yeye.mall.ware.entity.WareOrderTaskEntity;
import com.yeye.mall.ware.entity.WareSkuEntity;
import com.yeye.mall.ware.feign.OrderFeignService;
import com.yeye.mall.ware.feign.ProductFeignService;
import com.yeye.mall.ware.service.WareOrderTaskDetailService;
import com.yeye.mall.ware.service.WareOrderTaskService;
import com.yeye.mall.ware.service.WareSkuService;
import com.yeye.mall.ware.vo.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

  @Autowired
  private WareSkuDao wareSkuDao;

  @Autowired
  private ProductFeignService productFeignService;

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Autowired
  private WareOrderTaskService wareOrderTaskService;

  @Autowired
  private WareOrderTaskDetailService wareOrderTaskDetailService;

  @Autowired
  private OrderFeignService orderFeignService;

  @Override
  public PageUtils queryPage(Map<String, Object> params) {
    LambdaQueryWrapper<WareSkuEntity> wrapper = new LambdaQueryWrapper<>();

    String skuId = (String) params.get("skuId");
    if (!StringUtils.isEmpty(skuId)) {
      wrapper.eq(WareSkuEntity::getSkuId, skuId);
    }

    String wareId = (String) params.get("wareId");
    if (!StringUtils.isEmpty(wareId)) {
      wrapper.eq(WareSkuEntity::getWareId, wareId);
    }

    IPage<WareSkuEntity> page = this.page(
      new Query<WareSkuEntity>().getPage(params),
      wrapper
    );

    return new PageUtils(page);
  }

  @Transactional
  @Override
  public void handleStockLockedRelease(StockLockTo content) {


    Long taskId = content.getTaskId();
    Long detailId = content.getId();
    WareOrderTaskDetailEntity taskDetail = wareOrderTaskDetailService.getOne(
      new LambdaQueryWrapper<WareOrderTaskDetailEntity>()
        .eq(WareOrderTaskDetailEntity::getId, detailId)
        .eq(WareOrderTaskDetailEntity::getLockStatus, 1));
    if (Objects.isNull(taskDetail)) {
      return;
    }
    // 库存锁定成功了
    WareOrderTaskEntity task = wareOrderTaskService.getById(taskId);

    String orderSn = task.getOrderSn();
    R r = orderFeignService.getOrderInfoByOrderSn(orderSn);
    if (r.getCode() != 0) {
      log.error("远程方法调用报错--[getOrderInfoByOrderSn]");
      throw new RuntimeException("远程方法调用报错");
    }

    OrderVo orderVo = r.getData("data", new TypeReference<OrderVo>() {
    });
    // 不为null 且已完成已付款待发货的直接返回
    if (Objects.nonNull(orderVo) && (orderVo.getStatus() == 1 || orderVo.getStatus() == 2 || orderVo.getStatus() == 3)) {
      WareOrderTaskDetailEntity entity = new WareOrderTaskDetailEntity();
      entity.setId(detailId);
      entity.setLockStatus(3);
      this.wareOrderTaskDetailService.updateById(entity);
      return;
    }
    // 解锁库存(订单服务回滚或者订单关闭才进行关单服务)
    if (Objects.isNull(orderVo) || orderVo.getStatus() == 4) {
      if (taskDetail.getLockStatus() == 1) {
        stockLockedRelease(taskDetail.getSkuId(), taskDetail.getWareId(), taskDetail.getSkuNum(), detailId);
      }
    }

  }

  /**
   * 为了防止机器故障导致的消息延迟
   *
   * @param orderTo
   */
  @Transactional
  @Override
  public void handleOrderCloseRelease(OrderTo orderTo) {
    // 订单锁定成功了
    String orderSn = orderTo.getOrderSn();
    WareOrderTaskEntity task = wareOrderTaskService.getOrderTaskEntityBySn(orderSn);
    if (Objects.isNull(task)) {
      return;
    }
    List<WareOrderTaskDetailEntity> taskDetailList = wareOrderTaskDetailService.list(new LambdaQueryWrapper<WareOrderTaskDetailEntity>()
      .eq(WareOrderTaskDetailEntity::getTaskId, task.getId())
      .eq(WareOrderTaskDetailEntity::getLockStatus, 1));
    if (CollectionUtils.isEmpty(taskDetailList)) {
      return;
    }
    for (WareOrderTaskDetailEntity detailEntity : taskDetailList) {
      this.stockLockedRelease(detailEntity.getSkuId(), detailEntity.getWareId(), detailEntity.getSkuNum(), detailEntity.getId());
    }


  }

  private void stockLockedRelease(Long skuId, Long wareId, Integer skuNum, Long detailId) {
    this.wareSkuDao.stockLockedRelease(skuId, wareId, skuNum);

    WareOrderTaskDetailEntity entity = new WareOrderTaskDetailEntity();
    entity.setId(detailId);
    entity.setLockStatus(2);
    this.wareOrderTaskDetailService.updateById(entity);
  }


  @Transactional
  @Override
  public void addStock(Long skuId, Long wareId, Integer skuNum) {

    Integer integer = wareSkuDao.selectCount(new LambdaQueryWrapper<WareSkuEntity>()
      .eq(WareSkuEntity::getSkuId, skuId)
      .eq(WareSkuEntity::getWareId, wareId));

    if (integer == 0) {
      WareSkuEntity wareSkuEntity = new WareSkuEntity();
      wareSkuEntity.setSkuId(skuId);
      wareSkuEntity.setWareId(wareId);
      wareSkuEntity.setStock(skuNum);
      wareSkuEntity.setStockLocked(0);
      // 查sku信息，而且无需回滚
      // todo 全局熔断
      try {
        R info = productFeignService.info(skuId);
        if (info.getCode() == 0) {
          Map<String, Object> skuInfo = (Map<String, Object>) info.get("skuInfo");

          wareSkuEntity.setSkuName((String) skuInfo.get("skuName"));
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      wareSkuDao.insert(wareSkuEntity);
    } else {
      wareSkuDao.addStock(skuId, wareId, skuNum);
    }
  }

  @Override
  public List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds) {
    //todo 未完待续
    if (CollectionUtils.isEmpty(skuIds)) {
      return new ArrayList<>();
    }

    List<SkuHasStockVo> vos = this.baseMapper.getSkuStock(skuIds);

    return vos;
  }

  @Transactional
  @Override
  public R lockOrder(WareSkuLockVo vo) {
    if (CollectionUtils.isEmpty(vo.getOrderItems())) {
      return R.ok();
    }

    /**
     * 保存库存工作单
     * 追溯仓库
     */
    WareOrderTaskEntity taskEntity = new WareOrderTaskEntity();
    taskEntity.setOrderSn(vo.getOrderSn());
    taskEntity.setCreateTime(new Date());
    taskEntity.setPaymentWay(1);
    wareOrderTaskService.save(taskEntity);


    // 1.找到库存
    List<OrderItemVo> orderItems = vo.getOrderItems();

    List<WareSkuStock> skuStockList = orderItems.stream().map(item -> {
      WareSkuStock wareSkuStock = new WareSkuStock();
      wareSkuStock.setSkuId(item.getSkuId());
      wareSkuStock.setNum(item.getCount());
      // 查询仓库地址
      List<Long> wareIds = wareSkuDao.selectWareIdsBySkuId(item.getSkuId());
      wareSkuStock.setWareId(wareIds);
      return wareSkuStock;
    }).collect(Collectors.toList());

    for (WareSkuStock wareSkuStock : skuStockList) {
      Boolean skuStock = Boolean.FALSE;

      Long skuId = wareSkuStock.getSkuId();

      List<Long> wareIds = wareSkuStock.getWareId();
      if (CollectionUtils.isEmpty(wareIds)) {
        throw new RuntimeException("商品id为" + skuId + "，无库存");
      }

      for (Long wareId : wareIds) {
        Integer count = wareSkuDao.lockSkuStock(wareId, skuId, wareSkuStock.getNum());
        if (count != 0) {
          skuStock = Boolean.TRUE;

          // 锁成功的消息
          WareOrderTaskDetailEntity taskDetailEntity = new WareOrderTaskDetailEntity();
          taskDetailEntity.setSkuId(skuId);
          taskDetailEntity.setSkuName(null);
          taskDetailEntity.setSkuNum(wareSkuStock.getNum());
          taskDetailEntity.setTaskId(taskEntity.getId());
          taskDetailEntity.setWareId(wareId);
          taskDetailEntity.setLockStatus(1);
          boolean save = wareOrderTaskDetailService.save(taskDetailEntity);


          StockLockTo stockLockTo = new StockLockTo();
          BeanUtils.copyProperties(taskDetailEntity, stockLockTo);
          rabbitTemplate.convertAndSend("stock-event-exchange", "stock.locked", stockLockTo);

          break;
        }
      }

      if (!skuStock) {
        throw new RuntimeException("商品id为" + skuId + "，无库存");
      }

    }


    return R.ok().put("data", Boolean.TRUE);
  }

}