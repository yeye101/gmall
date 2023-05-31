package com.yeye.mall.order.service.impl;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alipay.api.AlipayApiException;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeye.common.exception.BizCodeEnum;
import com.yeye.common.to.MemberRes;
import com.yeye.common.to.SeckillOrderTo;
import com.yeye.common.to.mq.OrderTo;
import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.Query;
import com.yeye.common.utils.R;
import com.yeye.mall.order.constant.OrderConstant;
import com.yeye.mall.order.dao.OrderDao;
import com.yeye.mall.order.entity.OrderEntity;
import com.yeye.mall.order.entity.OrderItemEntity;
import com.yeye.mall.order.entity.PaymentInfoEntity;
import com.yeye.mall.order.enume.OrderStatusEnum;
import com.yeye.mall.order.feign.*;
import com.yeye.mall.order.interceptor.LoginUserInterceptor;
import com.yeye.mall.order.service.OrderItemService;
import com.yeye.mall.order.service.OrderService;
import com.yeye.mall.order.service.PaymentInfoService;
import com.yeye.mall.order.to.OrderCreateTo;
import com.yeye.mall.order.util.AlipayTemplate;
import com.yeye.mall.order.util.RedisUtil;
import com.yeye.mall.order.vo.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {


  @Autowired
  private MemberFeignService memberFeignService;

  @Autowired
  private CartFeignService cartFeignService;

  @Autowired
  private ThreadPoolExecutor executor;

  @Autowired
  private WareFeignService wareFeignService;

  @Autowired
  private ProductFeignService productFeignService;

  @Autowired
  private CouponFeignService couponFeignService;

  @Autowired
  private RedisUtil redisUtil;

  @Autowired
  private OrderItemService orderItemService;

  @Autowired
  private RabbitTemplate rabbitTemplate;


  @Autowired
  private AlipayTemplate alipayTemplate;

  @Autowired
  private PaymentInfoService paymentInfoService;


  @Override
  public PageUtils queryPage(Map<String, Object> params) {
    IPage<OrderEntity> page = this.page(
      new Query<OrderEntity>().getPage(params),
      new QueryWrapper<OrderEntity>()
    );

    return new PageUtils(page);
  }

  /**
   * 获取用户的子订单信息
   *
   * @param params
   * @return
   */
  @Override
  public PageUtils listWithItems(Map<String, Object> params) {
    // 获取用户信息
    MemberRes user = LoginUserInterceptor.loginUser.get();


    Wrapper<OrderEntity> queryWrapper = new LambdaQueryWrapper<OrderEntity>()
      .eq(OrderEntity::getMemberId, user.getId())
      .orderByDesc(OrderEntity::getId);

    IPage<OrderEntity> page = this.page(
      new Query<OrderEntity>().getPage(params),
      queryWrapper
    );
    if (CollectionUtils.isEmpty(page.getRecords())) {
      return new PageUtils(page);
    }

    // 获取子订单的消息数据
    List<Long> orderIds = page.getRecords().stream().map(OrderEntity::getId).collect(Collectors.toList());
    List<OrderItemEntity> orderItemEntities = this.orderItemService.list(
      new LambdaQueryWrapper<OrderItemEntity>()
        .in(OrderItemEntity::getOrderId, orderIds));
    HashMap<Long, List<OrderItemEntity>> orderItemsMap = new HashMap<>();
    for (Long orderId : orderIds) {
      List<OrderItemEntity> orderItemEntityList = orderItemEntities.stream()
        .filter(k -> StringUtils.equals(orderId.toString(), k.getOrderId().toString()))
        .collect(Collectors.toList());
      orderItemsMap.put(orderId, orderItemEntityList);
    }


    List<OrderEntity> orderWithItemsVos = page.getRecords().stream().peek(item -> {
      if (orderItemsMap.containsKey(item.getId())) {
        item.setOrderItemEntityList(orderItemsMap.get(item.getId()));
      }
    }).collect(Collectors.toList());

    page.setRecords(orderWithItemsVos);
    return new PageUtils(page);
  }

  @Override
  public String handleOrderPayAlipaySuccessRes(PayAsyncVo asyncVo) {


    if (StringUtils.equals("TRADE_SUCCESS", asyncVo.getTrade_status()) ||
      StringUtils.equals("TRADE_FINISHED", asyncVo.getTrade_status())) {
      OrderEntity update = new OrderEntity();
      update.setStatus(OrderStatusEnum.PAYED.getCode());
      update.setPaymentTime(new Date());
      this.update(update, new LambdaQueryWrapper<OrderEntity>()
        .eq(OrderEntity::getOrderSn, asyncVo.getOut_trade_no())
        .eq(OrderEntity::getStatus, OrderStatusEnum.CREATE_NEW.getCode()));
    }

    OrderEntity order = this.getOne(new LambdaQueryWrapper<OrderEntity>().eq(OrderEntity::getOrderSn, asyncVo.getOut_trade_no()));
    if (Objects.isNull(order)) {
      log.error("订单不存在");
      return "fail";
    }

    PaymentInfoEntity paymentInfoEntity = new PaymentInfoEntity();
    paymentInfoEntity.setOrderId(order.getId());
    paymentInfoEntity.setAlipayTradeNo(asyncVo.getTrade_no());
    paymentInfoEntity.setOrderSn(asyncVo.getOut_trade_no());
    paymentInfoEntity.setPaymentStatus(asyncVo.getTrade_status());
    paymentInfoEntity.setCallbackTime(asyncVo.getNotify_time());
    paymentInfoEntity.setConfirmTime(asyncVo.getNotify_time());
    paymentInfoEntity.setTotalAmount(new BigDecimal(asyncVo.getTotal_amount()));
    paymentInfoEntity.setSubject(asyncVo.getSubject());
    paymentInfoEntity.setCreateTime(new Date());
    paymentInfoEntity.setCallbackContent(asyncVo.getBody());
    this.paymentInfoService.save(paymentInfoEntity);

    return "success";
  }

  @Override
  public OrderConfirmVo getConfirmOrder() {
    OrderConfirmVo confirmVo = new OrderConfirmVo();
    // 获取用户信息
    MemberRes user = LoginUserInterceptor.loginUser.get();

    // 防止不同的线程之间丢失请求头的问题
    RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
    CompletableFuture<Void> addressF = CompletableFuture.runAsync(() -> {
      RequestContextHolder.setRequestAttributes(attributes);
      // 1.地址消息
      List<MemberAddressVo> addressById = memberFeignService.getAddressById(user.getId());
      confirmVo.setAddressVos(addressById);
    }, executor);


    CompletableFuture<Void> cartItemsF = CompletableFuture.runAsync(() -> {
      // 2.获取checked商品数据
      List<OrderItemVo> userCartItems = cartFeignService.getUserCartItems(user.getId());
      confirmVo.setOrderItemVos(userCartItems);

      // 3.查询库存
      List<OrderItemVo> orderItemVos = confirmVo.getOrderItemVos();
      List<Long> skuIds = orderItemVos.stream().map(OrderItemVo::getSkuId).collect(Collectors.toList());
      R skuHasStock = wareFeignService.getSkuHasStock(skuIds);
      List<SkuHasStockVo> data = skuHasStock.getData("data", new TypeReference<List<SkuHasStockVo>>() {
      });
      if (!CollectionUtils.isEmpty(data)) {
        Map<Long, Boolean> stockMap = data.stream()
          .collect(Collectors.toMap(SkuHasStockVo::getSkuId, v -> v.getHasStock() > 0 ? Boolean.TRUE : Boolean.FALSE));

        List<OrderItemVo> cartVos = orderItemVos.stream().peek(item -> {
          if (stockMap.containsKey(item.getSkuId())) {
            item.setStock(stockMap.get(item.getSkuId()));
          }
        }).collect(Collectors.toList());
        confirmVo.setOrderItemVos(cartVos);
      }
    }, executor);


    // 3.用户积分
    Integer integration = user.getIntegration();
    confirmVo.setIntegration(integration);

    try {
      CompletableFuture.allOf(addressF, cartItemsF).get();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
      log.error("确认页异步编排发生错误{}", e);
    }

    // 计算总价
    BigDecimal total = new BigDecimal(BigInteger.ZERO);
    Integer count = 0;
    if (Objects.nonNull(confirmVo.getOrderItemVos())) {
      for (OrderItemVo item : confirmVo.getOrderItemVos()) {
        total = total.add(item.getTotalPrice());
        count += item.getCount();
      }
    }

    total = total.max(BigDecimal.ZERO);

    //  数量
    confirmVo.setOrderItemCount(count);

    // 实际应付价格 total - 优惠 暂无
    confirmVo.setTotal(total);
    confirmVo.setPayPrice(total);


    // 4.幂等性放重 令牌
    String orderToken = IdUtil.simpleUUID();
    confirmVo.setOrderToken(orderToken);

    redisUtil.setEx(OrderConstant.USER_ORDER_TOKEN_PREFIX + user.getId(), orderToken, 30, TimeUnit.MINUTES);

    return confirmVo;
  }

  /**
   * GlobalTransactional为seata分布式事务
   *
   * @param vo
   * @return
   */
  @Transactional
  @Override
  public R submitOrder(OrderSubmitVo vo) {
    OrderCreateTo createTo = null;

    //下单：验证令牌、创建订单、验证价格、锁定库存
    // 下单成功到支付
    // 获取用户信息
    MemberRes user = LoginUserInterceptor.loginUser.get();

    // 1.验证令牌
    // 0失败/1成功
    String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

    Long res = redisUtil.getStringRedisTemplate().execute(new DefaultRedisScript<Long>(script, Long.class),
      Arrays.asList(OrderConstant.USER_ORDER_TOKEN_PREFIX + user.getId()),
      vo.getOrderToken());
    // 原子验证失败，则直接返回
    if (res == 0) {
      throw new RuntimeException(BizCodeEnum.ORDER_SUBMIT_COMMIT_EXCEPTION.getMsg());
    }

    // 2. 创建订单
    createTo = createOrder(vo, user);

    // 3.验证价格
    BigDecimal payPrice = vo.getPayPrice();
    BigDecimal payAmount = createTo.getOrder().getPayAmount();
    if (Math.abs(payAmount.subtract(payPrice).doubleValue()) >= 0.01) {
      throw new RuntimeException(BizCodeEnum.ORDER_SUBMIT_PRICE_EXCEPTION.getMsg());
    }

    // 4.保存订单
    saveOrder(createTo);

    // 5.锁定库存
    WareSkuLockVo lockVo = new WareSkuLockVo();
    List<OrderItemVo> orderItemVos = createTo.getOrderItems().stream().map(item -> {
      OrderItemVo orderItemVo = new OrderItemVo();
      orderItemVo.setSkuId(item.getSkuId());
      orderItemVo.setCount(item.getSkuQuantity());
      orderItemVo.setTitle(item.getSkuName());
      return orderItemVo;
    }).collect(Collectors.toList());
    lockVo.setOrderSn(createTo.getOrder().getOrderSn());
    lockVo.setOrderItems(orderItemVos);

    R r = wareFeignService.lockOrder(lockVo);
    if (r.getCode() != 0) {
      log.error("远程方法调用报错--[saveBatch]");
      throw new RuntimeException(BizCodeEnum.WARE_NO_SOCK_EXCEPTION.getMsg());
    }

    // 订单创建成功发消息
    rabbitTemplate.convertAndSend("order-event-exchange", "order.create.order", createTo.getOrder());

    return R.ok().put("data", createTo);
  }

  @Override
  public OrderEntity getOrderInfoByOrderSn(String orderSn) {
    OrderEntity orderEntity = this.getOne(new LambdaQueryWrapper<OrderEntity>().eq(OrderEntity::getOrderSn, orderSn));

    return orderEntity;
  }

  @Override
  public void handleOrderClose(OrderEntity content) {

    System.out.println("收到订单的关闭信息" + content);
    OrderEntity orderEntity = this.getById(content.getId());
    if (!StringUtils.equals(OrderStatusEnum.CREATE_NEW.getCode().toString(), orderEntity.getStatus().toString())) {
      return;
    }
    OrderEntity entity = new OrderEntity();
    entity.setId(content.getId());
    entity.setStatus(OrderStatusEnum.CANCLED.getCode());
    this.updateById(entity);
    OrderTo orderTo = new OrderTo();
    BeanUtils.copyProperties(orderEntity, orderTo);
    // 订单创建成功发消息
    rabbitTemplate.convertAndSend("order-event-exchange", "order.release.other.#", orderTo);

  }

  @Override
  public String payOrder(String orderSn) {
    String payRes = null;

    try {
      PayVo payVo = new PayVo();
      OrderEntity orderInfo = this.getOrderInfoByOrderSn(orderSn);
      String totalPayAmount = orderInfo.getPayAmount().setScale(2, BigDecimal.ROUND_UP).toString();
      List<OrderItemEntity> list = this.orderItemService.list(
        new LambdaQueryWrapper<OrderItemEntity>().eq(OrderItemEntity::getOrderSn, orderSn));
      if (CollectionUtils.isEmpty(list)) {
        throw new RuntimeException("订单为空");
      }

      payVo.setTotal_amount(totalPayAmount);
      payVo.setOut_trade_no(orderInfo.getOrderSn());
      payVo.setSubject(list.get(0).getSpuName());
      payVo.setBody(list.get(0).getSkuName());
      payRes = alipayTemplate.pay(payVo);
      System.out.println(payRes);


    } catch (AlipayApiException e) {
      e.printStackTrace();
      log.error("支付出现异常--", e);
    }

    return payRes;
  }

  @Override
  public void handleOrderSeckill(SeckillOrderTo content) {

    OrderItemEntity orderItemEntity = new OrderItemEntity();
    orderItemEntity.setOrderSn(content.getOrderSn());
    // 1.订单信息 插入的时候补全
    // 2.商品的spu信息
    R res = productFeignService.getSpuInfo(content.getSkuId());
    if (res.getCode() != 0) {
      log.error("远程方法调用报错--[productFeignService:getSpuInfo]");
    }
    SpuInfoVO spuInfoVO = res.getData("data", new TypeReference<SpuInfoVO>() {
    });

    R skuRes = productFeignService.getSkuInfo(content.getSkuId());
    if (skuRes.getCode() != 0) {
      log.error("远程方法调用报错--[productFeignService:getSkuInfo]");
    }
    SkuInfoVo skuInfo = skuRes.getData("skuInfo", new TypeReference<SkuInfoVo>() {
    });
    // attr
    R saleAttrRes = productFeignService.querySaleAttrListById(content.getSkuId());
    if (saleAttrRes.getCode() != 0) {
      log.error("远程方法调用报错--[productFeignService:getSkuInfo]");
    }
    List<String> data = saleAttrRes.getData("data", new TypeReference<List<String>>() {
    });

    // spu信息
    orderItemEntity.setSkuAttrsVals(JSON.toJSONString(data));
    orderItemEntity.setSpuId(spuInfoVO.getId());
    orderItemEntity.setSpuPic(spuInfoVO.getSpuPic());
    orderItemEntity.setSpuName(spuInfoVO.getSpuName());
    orderItemEntity.setSpuBrand(spuInfoVO.getBrandName());
    orderItemEntity.setCategoryId(spuInfoVO.getCatalogId());

    // 3.商品的sku信息
    orderItemEntity.setSkuId(skuInfo.getSkuId());
    orderItemEntity.setSkuName(skuInfo.getSkuTitle());
    orderItemEntity.setSkuPic(skuInfo.getSkuDefaultImg());
    orderItemEntity.setSkuPrice(skuInfo.getPrice());
    orderItemEntity.setSkuQuantity(content.getNum());
    // 4.优惠信息
    //  5.积分信息
    R boundsRes = couponFeignService.getBoundsBySpuId(spuInfoVO.getId());
    if (boundsRes.getCode() != 0) {
      log.error("远程方法调用报错--[getBoundsBySpuId]");
    }
    SpuBoundsVO boundsVO = boundsRes.getData("data", new TypeReference<SpuBoundsVO>() {
    });

    // 积分呢
    BigDecimal growBounds = boundsVO.getGrowBounds().multiply(new BigDecimal(content.getNum().toString()));
    BigDecimal buyBounds = boundsVO.getBuyBounds().multiply(new BigDecimal(content.getNum().toString()));

    orderItemEntity.setGiftGrowth(growBounds.intValue());
    orderItemEntity.setGiftIntegration(buyBounds.intValue());

    // 6.价格信息
    orderItemEntity.setIntegrationAmount(BigDecimal.ZERO);
    orderItemEntity.setCouponAmount(BigDecimal.ZERO);
    orderItemEntity.setPromotionAmount(BigDecimal.ZERO);
    // 总价
    BigDecimal totalAmount = content.getSeckillPrice().multiply(new BigDecimal(content.getNum().toString()));
    totalAmount = totalAmount.subtract(orderItemEntity.getCouponAmount())
      .subtract(orderItemEntity.getIntegrationAmount())
      .subtract(orderItemEntity.getPromotionAmount());
    totalAmount = totalAmount.max(BigDecimal.ZERO);
    orderItemEntity.setRealAmount(totalAmount);


    // 主订单
    R info = memberFeignService.getMemberInfo(content.getMemberId());

    if (info.getCode() != 0) {
      log.error("远程方法调用报错--[memberFeignService:getMemberInfo]");
      return;
    }
    MemberRes member = info.getData("member", new TypeReference<MemberRes>() {
    });

    List<MemberAddressVo> addressById = memberFeignService.getAddressById(member.getId());

    List<MemberAddressVo> defaultAddress = addressById.stream()
      .filter(v -> v.getDefaultStatus() == 1)
      .collect(Collectors.toList());
    BigDecimal fare = BigDecimal.ONE;

    OrderEntity orderEntity = new OrderEntity();
    orderEntity.setOrderSn(content.getOrderSn());
    // 运费信息
    orderEntity.setFreightAmount(fare);
    // 收货人信息
    if (!CollectionUtils.isEmpty(defaultAddress)) {
      orderEntity.setReceiverCity(defaultAddress.get(0).getCity());
      orderEntity.setReceiverDetailAddress(defaultAddress.get(0).getDetailAddress());
      orderEntity.setReceiverName(defaultAddress.get(0).getName());
      orderEntity.setReceiverPhone(defaultAddress.get(0).getPhone());
      orderEntity.setReceiverPostCode(defaultAddress.get(0).getPostCode());
      orderEntity.setReceiverProvince(defaultAddress.get(0).getProvince());
      orderEntity.setReceiverRegion(defaultAddress.get(0).getRegion());
    }

    orderEntity.setMemberId(content.getMemberId());
    orderEntity.setMemberUsername(member.getUsername());


    orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
    orderEntity.setTotalAmount(totalAmount);
    orderEntity.setPayAmount(totalAmount);
    orderEntity.setIntegrationAmount(BigDecimal.ZERO);
    orderEntity.setCouponAmount(BigDecimal.ZERO);
    orderEntity.setPromotionAmount(BigDecimal.ZERO);
    //订单设置积分
    orderEntity.setGrowth(growBounds.intValue());
    orderEntity.setIntegration(buyBounds.intValue());
    orderEntity.setDeleteStatus(0);
    orderEntity.setAutoConfirmDay(7);


    orderEntity.setCreateTime(new Date());
    orderEntity.setModifyTime(new Date());
    this.save(orderEntity);

    // 补齐订单id
    orderItemEntity.setOrderId(orderEntity.getId());
    this.orderItemService.save(orderItemEntity);

  }

  @Override
  public R orderDetail(String orderSn) {

    OrderCreateTo createTo = new OrderCreateTo();
    OrderEntity orderEntity = this.getOne(new LambdaQueryWrapper<OrderEntity>().eq(OrderEntity::getOrderSn, orderSn));
    if (Objects.isNull(orderEntity)) {
      return R.error();
    }
    createTo.setOrder(orderEntity);
    List<OrderItemEntity> orderItemEntityList = orderItemService.list(new LambdaQueryWrapper<OrderItemEntity>()
      .eq(OrderItemEntity::getOrderId, orderEntity.getId()));
    createTo.setOrderItems(orderItemEntityList);

    return R.ok().put("data", createTo);

  }

  @Override
  public R reciveOrder(String orderSn) {

    OrderEntity orderEntity = this.getOne(new LambdaQueryWrapper<OrderEntity>().eq(OrderEntity::getOrderSn, orderSn));
    if (!Objects.equals(orderEntity.getStatus(), OrderStatusEnum.PAYED.getCode())){
      return R.error();
    }
    orderEntity.setStatus(OrderStatusEnum.RECIEVED.getCode());
    orderEntity.setReceiveTime(new Date());

    this.update(orderEntity,new LambdaQueryWrapper<OrderEntity>()
      .eq(OrderEntity::getOrderSn,orderSn)
      .eq(OrderEntity::getStatus,OrderStatusEnum.PAYED.getCode()));
    return R.ok();
  }

  @Override
  public R cancleOrder(String orderSn) {
    OrderEntity orderEntity = this.getOne(new LambdaQueryWrapper<OrderEntity>().eq(OrderEntity::getOrderSn, orderSn));
    if (Objects.equals(orderEntity.getStatus(), OrderStatusEnum.CANCLED.getCode())){
      return R.error();
    }
    orderEntity.setStatus(OrderStatusEnum.CANCLED.getCode());

    this.update(orderEntity,new LambdaQueryWrapper<OrderEntity>()
      .eq(OrderEntity::getOrderSn,orderSn));
    return R.ok();
  }


  private void saveOrder(OrderCreateTo createTo) {
    OrderEntity order = createTo.getOrder();


    int insert = this.baseMapper.insert(order);
    if (insert == 0) {
      log.error("订单创建失败");
    }
    List<OrderItemEntity> orderItems = createTo.getOrderItems().stream()
      .peek(item -> item.setOrderId(order.getId()))
      .collect(Collectors.toList());

    for (OrderItemEntity orderItem : orderItems) {
      boolean save = orderItemService.save(orderItem);
      if (!save) {
        log.error("子订单创建失败");
      }
    }


  }

  /**
   * 创建订单和子订单
   *
   * @param vo
   * @param user
   * @return
   */
  private OrderCreateTo createOrder(OrderSubmitVo vo, MemberRes user) {
    OrderCreateTo createTo = new OrderCreateTo();

    OrderEntity orderEntity = buildOrder(vo);

    List<OrderItemEntity> orderItemList = buildOrderItems(user);

    // 较价
    comparePrice(orderEntity, orderItemList);

    createTo.setOrder(orderEntity);
    createTo.setOrderItems(orderItemList);

    return createTo;
  }

  private void comparePrice(OrderEntity orderEntity, List<OrderItemEntity> orderItemList) {
    // 计算总价
    BigDecimal totalAmount = new BigDecimal(BigInteger.ZERO);
    BigDecimal payAmount = new BigDecimal(BigInteger.ZERO);

    // 折价
    BigDecimal promotionAmount = new BigDecimal(BigInteger.ZERO);
    BigDecimal couponAmount = new BigDecimal(BigInteger.ZERO);
    BigDecimal integrationAmount = new BigDecimal(BigInteger.ZERO);

    // 积分和成长值
    BigDecimal integration = new BigDecimal(BigInteger.ZERO);
    BigDecimal growth = new BigDecimal(BigInteger.ZERO);

    if (Objects.nonNull(orderItemList)) {
      for (OrderItemEntity item : orderItemList) {
        totalAmount = totalAmount.add(item.getRealAmount());
        integrationAmount = integrationAmount.add(item.getIntegrationAmount());
        couponAmount = couponAmount.add(item.getCouponAmount());
        promotionAmount = promotionAmount.add(item.getPromotionAmount());
        integration = integration.add(new BigDecimal(item.getGiftIntegration().toString()));
        growth = growth.add(new BigDecimal(item.getGiftGrowth().toString()));

      }
    }
    // 设置金额
    totalAmount = totalAmount.max(BigDecimal.ZERO);
    orderEntity.setTotalAmount(totalAmount);

    // 设置金额
    payAmount = totalAmount.add(orderEntity.getFreightAmount());
    orderEntity.setPayAmount(payAmount);
    orderEntity.setPromotionAmount(promotionAmount);
    orderEntity.setCouponAmount(couponAmount);
    orderEntity.setIntegrationAmount(integrationAmount);

    //订单设置积分
    orderEntity.setGrowth(growth.intValue());
    orderEntity.setIntegration(integration.intValue());
    orderEntity.setDeleteStatus(0);


    // 子订单
    orderItemList = orderItemList.stream()
      .peek(item -> item.setOrderSn(orderEntity.getOrderSn()))
      .collect(Collectors.toList());

  }

  /**
   * 构建子订单实体list
   *
   * @param user
   * @return
   */
  private List<OrderItemEntity> buildOrderItems(MemberRes user) {
    // 2.获取所有的订单项
    List<OrderItemVo> cartItems = cartFeignService.getUserCartItems(user.getId());
    if (CollectionUtils.isEmpty(cartItems)) {
      log.error("远程方法调用报错--[购物车数据为空]");
    }


    List<OrderItemEntity> orderItemList = cartItems.stream().map(item -> {
      OrderItemEntity orderItemEntity = buildOrderItem(item);
      return orderItemEntity;
    }).collect(Collectors.toList());
    return orderItemList;
  }

  /**
   * 构建子订单实体
   *
   * @param item
   * @return
   */
  private OrderItemEntity buildOrderItem(OrderItemVo item) {
    OrderItemEntity orderItemEntity = new OrderItemEntity();
    // 1.订单信息 插入的时候补全
    // 2.商品的spu信息
    R res = productFeignService.getSpuInfo(item.getSkuId());
    if (res.getCode() != 0) {
      log.error("远程方法调用报错--[getSpuInfo]");
    }
    SpuInfoVO spuInfoVO = res.getData("data", new TypeReference<SpuInfoVO>() {
    });
    // spu信息
    orderItemEntity.setSpuId(spuInfoVO.getId());
    orderItemEntity.setSpuPic(spuInfoVO.getSpuPic());
    orderItemEntity.setSpuName(spuInfoVO.getSpuName());
    orderItemEntity.setSpuBrand(spuInfoVO.getBrandName());
    orderItemEntity.setCategoryId(spuInfoVO.getCatalogId());

    // 3.商品的sku信息
    orderItemEntity.setSkuId(item.getSkuId());
    orderItemEntity.setSkuName(item.getTitle());
    orderItemEntity.setSkuPic(item.getImage());
    orderItemEntity.setSkuPrice(item.getPrice());
    orderItemEntity.setSkuQuantity(item.getCount());
    orderItemEntity.setSkuAttrsVals(JSON.toJSONString(item.getSkuAttr()));
    // 4.优惠信息
    //  5.积分信息
    R boundsRes = couponFeignService.getBoundsBySpuId(spuInfoVO.getId());
    if (boundsRes.getCode() != 0) {
      log.error("远程方法调用报错--[getBoundsBySpuId]");
    }
    SpuBoundsVO boundsVO = boundsRes.getData("data", new TypeReference<SpuBoundsVO>() {
    });

    // 积分呢
    BigDecimal growBounds = boundsVO.getGrowBounds().multiply(new BigDecimal(item.getCount().toString()));
    BigDecimal buyBounds = boundsVO.getBuyBounds().multiply(new BigDecimal(item.getCount().toString()));

    orderItemEntity.setGiftGrowth(growBounds.intValue());
    orderItemEntity.setGiftIntegration(buyBounds.intValue());

    // 6.价格信息
    orderItemEntity.setIntegrationAmount(BigDecimal.ZERO);
    orderItemEntity.setCouponAmount(BigDecimal.ZERO);
    orderItemEntity.setPromotionAmount(BigDecimal.ZERO);
    // 总价
    BigDecimal totalAmount = item.getPrice().multiply(new BigDecimal(item.getCount().toString()));
    totalAmount = totalAmount.subtract(orderItemEntity.getCouponAmount())
      .subtract(orderItemEntity.getIntegrationAmount())
      .subtract(orderItemEntity.getPromotionAmount());
    totalAmount = totalAmount.max(BigDecimal.ZERO);
    orderItemEntity.setRealAmount(totalAmount);
    return orderItemEntity;
  }

  /**
   * 构建订单实体
   *
   * @return
   */
  private OrderEntity buildOrder(OrderSubmitVo vo) {
    MemberRes user = LoginUserInterceptor.loginUser.get();

    OrderEntity orderEntity = new OrderEntity();
    // 1.生成订单号
    String orderSn = IdUtil.getSnowflakeNextIdStr();
    orderEntity.setOrderSn(orderSn);

    R res = null;
    try {
      res = wareFeignService.getFare(vo.getAddrId());
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(BizCodeEnum.ORDER_SUBMIT_ADDRESS_EXCEPTION.getMsg());
    }
    if (res.getCode() != 0) {
      log.error("远程方法调用报错--[getFare]");
    }
    FareVo fareData = res.getData("data", new TypeReference<FareVo>() {
    });
    // 运费信息
    orderEntity.setFreightAmount(fareData.getFare());
    // 收货人信息
    orderEntity.setReceiverCity(fareData.getAddressVo().getCity());
    orderEntity.setReceiverDetailAddress(fareData.getAddressVo().getDetailAddress());
    orderEntity.setReceiverName(fareData.getAddressVo().getName());
    orderEntity.setReceiverPhone(fareData.getAddressVo().getPhone());
    orderEntity.setReceiverPostCode(fareData.getAddressVo().getPostCode());
    orderEntity.setReceiverProvince(fareData.getAddressVo().getProvince());
    orderEntity.setReceiverRegion(fareData.getAddressVo().getRegion());

    // 设置订单状态
    orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
    orderEntity.setAutoConfirmDay(7);

    // 会员信息
    orderEntity.setMemberId(user.getId());
    orderEntity.setMemberUsername(user.getUsername());

    orderEntity.setCreateTime(new Date());
    orderEntity.setModifyTime(new Date());

    return orderEntity;
  }
}