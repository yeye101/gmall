package com.yeye.mall.product.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.Query;
import com.yeye.common.utils.R;
import com.yeye.mall.product.dao.SkuInfoDao;
import com.yeye.mall.product.entity.SkuImagesEntity;
import com.yeye.mall.product.entity.SkuInfoEntity;
import com.yeye.mall.product.entity.SpuInfoDescEntity;
import com.yeye.mall.product.feign.SeckillFeignService;
import com.yeye.mall.product.service.*;
import com.yeye.mall.product.vo.SeckillInfoVo;
import com.yeye.mall.product.vo.web.SkuItemSaleAttrsVo;
import com.yeye.mall.product.vo.web.SkuItemVo;
import com.yeye.mall.product.vo.web.SpuItemGroupVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

  @Autowired
  private SkuImagesService skuImagesService;

  @Autowired
  private SpuInfoDescService spuInfoDescService;

  @Autowired
  private AttrGroupService attrGroupService;

  @Autowired
  private SkuSaleAttrValueService skuSaleAttrValueService;

  @Autowired
  private SeckillFeignService seckillFeignService;

  @Autowired
  private ThreadPoolExecutor executor;

  @Override
  public PageUtils queryPage(Map<String, Object> params) {
    IPage<SkuInfoEntity> page = this.page(
      new Query<SkuInfoEntity>().getPage(params),
      new QueryWrapper<SkuInfoEntity>()
    );

    return new PageUtils(page);
  }

  @Override
  public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
    this.baseMapper.insert(skuInfoEntity);
  }

  @Override
  public PageUtils queryPageByCondition(Map<String, Object> params) {


    LambdaQueryWrapper<SkuInfoEntity> wrapper = new LambdaQueryWrapper<>();
    String key = (String) params.get("key");
    if (!StringUtils.isEmpty(key)) {
      wrapper.eq(SkuInfoEntity::getSkuId, key).or().like(SkuInfoEntity::getSkuName, key);
    }

    String brandId = (String) params.get("brandId");
    if (!StringUtils.isEmpty(brandId) && !brandId.equals("0")) {
      wrapper.eq(SkuInfoEntity::getBrandId, brandId);
    }

    String catelogId = (String) params.get("catelogId");
    if (!StringUtils.isEmpty(catelogId) && !catelogId.equals("0")) {
      wrapper.eq(SkuInfoEntity::getCatalogId, catelogId);
    }

    String min = (String) params.get("min");
    if (!StringUtils.isEmpty(min) && !min.equals("0")) {
      wrapper.ge(SkuInfoEntity::getPrice, min);
    }
    String max = (String) params.get("max");
    if (!StringUtils.isEmpty(max) && !max.equals("0")) {
      wrapper.le(SkuInfoEntity::getPrice, max);
    }

    IPage<SkuInfoEntity> page = this.page(
      new Query<SkuInfoEntity>().getPage(params),
      wrapper
    );

    return new PageUtils(page);
  }

  @Override
  public List<SkuInfoEntity> getSkusBySpuId(Long spuId) {

    List<SkuInfoEntity> list = this.list(new LambdaQueryWrapper<SkuInfoEntity>().eq(SkuInfoEntity::getSpuId, spuId));
    return list;
  }

  @Override
  public SkuItemVo queryItem(Long skuId) {

    SkuItemVo skuItemVo = new SkuItemVo();

    CompletableFuture<SkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(() -> {
      // sku的信息 pms_sku_info
      SkuInfoEntity skuInfoEntity = this.getById(skuId);
      skuItemVo.setInfo(skuInfoEntity);
      return skuInfoEntity;
    }, executor);


    CompletableFuture<Void> imageFuture = CompletableFuture.runAsync(() -> {
      // sku图片 pms_sku_images
      List<SkuImagesEntity> skuImagesEntities = skuImagesService.list(new LambdaQueryWrapper<SkuImagesEntity>()
        .eq(SkuImagesEntity::getSkuId, skuId));
      if (!CollectionUtils.isEmpty(skuImagesEntities)) {
        skuItemVo.setImages(skuImagesEntities);
      }
    }, executor);

    CompletableFuture<Void> saleAttrFuture = infoFuture.thenAcceptAsync(res -> {
      // spu销售组合
      List<SkuItemSaleAttrsVo> saleAttrsVo = skuSaleAttrValueService.getSaleAttrsVoBySId(res.getSpuId());
      skuItemVo.setSaleAttrsVo(saleAttrsVo);
    }, executor);
    CompletableFuture<Void> descFuture = infoFuture.thenAcceptAsync(res -> {
      // spu信息
      SpuInfoDescEntity spuInfoDesc = spuInfoDescService.getById(res.getSpuId());
      skuItemVo.setSpuInfoDesc(spuInfoDesc);
    }, executor);
    CompletableFuture<Void> baseAttrFuture = infoFuture.thenAcceptAsync(res -> {
      // spu属性和规格
      List<SpuItemGroupVo> groupVos = attrGroupService.getGroupAttrsBySIdAndCId(res.getSpuId(), res.getCatalogId());
      skuItemVo.setGroupVos(groupVos);
    }, executor);

    CompletableFuture<Void> seckillInfoFuture = CompletableFuture.runAsync(() -> {
      // 秒杀信息
      R seckillInfoRes = seckillFeignService.getSkuSeckillInfo(skuId);
      if (seckillInfoRes.getCode() != 0) {
        log.error("远程方法调用失败--[seckillFeignService:getSkuSeckillInfo]");
      }
      SeckillInfoVo data = seckillInfoRes.getData("data", new TypeReference<SeckillInfoVo>() {
      });
      if (Objects.nonNull(data.getId())) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        data.setStartTimeDesc(DateUtil.format(new Date(data.getStartTime()), formatter));
        data.setEndTimeDesc(DateUtil.format(new Date(data.getEndTime()), formatter));
        skuItemVo.setSeckillInfoVo(data);
      }
    }, executor);


    try {
      CompletableFuture.allOf(imageFuture, saleAttrFuture, baseAttrFuture, descFuture, seckillInfoFuture).get();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
      log.error("异步编排获取sku信息失败{}", e);
    }


    return skuItemVo;
  }


}