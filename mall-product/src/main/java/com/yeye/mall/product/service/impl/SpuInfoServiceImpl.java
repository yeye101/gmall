package com.yeye.mall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeye.common.constant.ProductConstant;
import com.yeye.common.to.SkuHasStockVo;
import com.yeye.common.to.SkuReductionTo;
import com.yeye.common.to.SpuBoundTo;
import com.yeye.common.to.es.Attrs;
import com.yeye.common.to.es.SkuEsModel;
import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.Query;
import com.yeye.common.utils.R;
import com.yeye.mall.product.dao.SpuInfoDao;
import com.yeye.mall.product.entity.*;
import com.yeye.mall.product.feign.CouponFeignService;
import com.yeye.mall.product.feign.SearchFeignService;
import com.yeye.mall.product.feign.WareFeignService;
import com.yeye.mall.product.service.*;
import com.yeye.mall.product.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

  @Autowired
  SpuInfoDescService spuInfoDescService;

  @Autowired
  SpuImagesService spuImagesService;

  @Autowired
  AttrService attrService;

  @Autowired
  ProductAttrValueService productAttrValueService;

  @Autowired
  SkuInfoService skuInfoService;

  @Autowired
  SkuImagesService skuImagesService;

  @Autowired
  SkuSaleAttrValueService skuSaleAttrValueService;

  @Autowired
  CouponFeignService couponFeignService;

  @Autowired
  WareFeignService wareFeignService;

  @Autowired
  SearchFeignService searchFeignService;

  @Autowired
  BrandService brandService;

  @Autowired
  CategoryService categoryService;

  @Override
  public PageUtils queryPage(Map<String, Object> params) {
    IPage<SpuInfoEntity> page = this.page(
      new Query<SpuInfoEntity>().getPage(params),
      new QueryWrapper<SpuInfoEntity>()
    );

    return new PageUtils(page);
  }

  /**
   * todo 分布式事务
   *
   * @param vo
   */
  @Transactional
  @Override
  public void saveSpuInfo(SpuSaveVo vo) {
    //1. 保存spu基本信息 pms_sku_info
    SpuInfoEntity infoEntity = new SpuInfoEntity();
    BeanUtils.copyProperties(vo, infoEntity);
    infoEntity.setCreateTime(new Date());
    infoEntity.setUpdateTime(new Date());
    this.saveBaseSpuInfo(infoEntity);

    //2. 保存spu描述图片 pms_spu_info_desc
    List<String> decript = vo.getDecript();
    SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
    descEntity.setSpuId(infoEntity.getId());
    descEntity.setDecript(String.join(",", decript));
    spuInfoDescService.saveSpuInfoDesc(descEntity);


    //3. 保存spu图片集 pms_spu_images
    List<String> images = vo.getImages();
    spuImagesService.saveImages(infoEntity.getId(), images);


    //4. 保存spu规格参数base pms_product_attr_value
    List<BaseAttrs> baseAttrs = vo.getBaseAttrs();

    List<ProductAttrValueEntity> productAttrValueEntities = baseAttrs.stream().map(attr -> {
      ProductAttrValueEntity valueEntity = new ProductAttrValueEntity();
      valueEntity.setAttrId(attr.getAttrId());
      valueEntity.setAttrValue(attr.getAttrValues());
      valueEntity.setQuickShow(attr.getShowDesc());
      valueEntity.setSpuId(infoEntity.getId());

      // 从attrService里面获取
      AttrEntity attrEntity = attrService.getById(attr.getAttrId());
      valueEntity.setAttrName(attrEntity.getAttrName());

      return valueEntity;
    }).collect(Collectors.toList());

    productAttrValueService.saveProductAttr(productAttrValueEntities);


    //5. 保存spu积分信息 sms_spu_bounds
    Bounds bounds = vo.getBounds();
    SpuBoundTo spuBoundTo = new SpuBoundTo();
    BeanUtils.copyProperties(bounds, spuBoundTo);
    spuBoundTo.setSpuId(infoEntity.getId());

    R r1 = couponFeignService.saveSpuBounds(spuBoundTo);
    if (r1.getCode() != 0) {
      log.error("远程调用积分失败");
    }


    //6. 保存spu的所有的 skus  pms_sku_images

    List<Skus> skus = vo.getSkus();
    if (CollectionUtils.isEmpty(skus)) {
      return;
    }
    skus.forEach(item -> {
      String defaultImag = "";
      for (Images image : item.getImages()) {
        if (image.getDefaultImg() == 1) {
          defaultImag = image.getImgUrl();
        }
      }
      SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
      BeanUtils.copyProperties(item, skuInfoEntity);
      skuInfoEntity.setBrandId(infoEntity.getBrandId());
      skuInfoEntity.setCatalogId(infoEntity.getCatalogId());
      skuInfoEntity.setSaleCount(0L);
      skuInfoEntity.setSpuId(infoEntity.getId());
      skuInfoEntity.setSkuDefaultImg(defaultImag);

      // 5.1sku基本信息 pms_sku_info
      skuInfoService.saveSkuInfo(skuInfoEntity);

      Long skuId = skuInfoEntity.getSkuId();


      List<SkuImagesEntity> imagesEntities = item.getImages().stream().map(img -> {
        SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
        skuImagesEntity.setSkuId(skuId);
        skuImagesEntity.setImgUrl(img.getImgUrl());
        skuImagesEntity.setDefaultImg(img.getDefaultImg());

        return skuImagesEntity;
      }).filter(entity -> {
        return !StringUtils.isEmpty(entity.getImgUrl());
      }).collect(Collectors.toList());

      // 5.2sku图片信息 pms_sku_images
      //  没有图片路径的不许保存
      skuImagesService.saveBatch(imagesEntities);


      List<Attr> attr = item.getAttr();
      List<SkuSaleAttrValueEntity> saleAttrValueEntities = attr.stream().map(a -> {
        SkuSaleAttrValueEntity attrValueEntity = new SkuSaleAttrValueEntity();
        BeanUtils.copyProperties(a, attrValueEntity);
        attrValueEntity.setSkuId(skuId);
        return attrValueEntity;
      }).collect(Collectors.toList());
      // 5.3sku销售属性信息 pms_sku_sale_attr_value
      skuSaleAttrValueService.saveBatch(saleAttrValueEntities);

      SkuReductionTo skuReductionTo = new SkuReductionTo();

      BeanUtils.copyProperties(item, skuReductionTo);
      skuReductionTo.setSkuId(skuId);

      // 5.4sku的优惠满减信息 sms_sku_ladder  sms_sku_full_reduction  sms_member_price
      R r = couponFeignService.saveSkuReduction(skuReductionTo);

      if (r.getCode() != 0) {
        log.error("远程调用积分和优惠信息失败");
      }


    });


  }

  @Override
  public void saveBaseSpuInfo(SpuInfoEntity infoEntity) {
    this.baseMapper.insert(infoEntity);

  }

  @Override
  public PageUtils queryPageByCondition(Map<String, Object> params) {

    LambdaQueryWrapper<SpuInfoEntity> wrapper = new LambdaQueryWrapper<>();
    String key = (String) params.get("key");
    if (!StringUtils.isEmpty(key)) {
      wrapper.eq(SpuInfoEntity::getId, key).or().like(SpuInfoEntity::getSpuName, key);
    }

    String brandId = (String) params.get("brandId");
    if (!StringUtils.isEmpty(brandId) && !brandId.equals("0")) {
      wrapper.eq(SpuInfoEntity::getBrandId, brandId);
    }

    String catelogId = (String) params.get("catelogId");
    if (!StringUtils.isEmpty(catelogId) && !catelogId.equals("0")) {
      wrapper.eq(SpuInfoEntity::getCatalogId, catelogId);
    }

    String status = (String) params.get("status");
    if (!StringUtils.isEmpty(status)) {
      wrapper.eq(SpuInfoEntity::getPublishStatus, status);
    }
    wrapper.orderByDesc(SpuInfoEntity::getId);

    IPage<SpuInfoEntity> page = this.page(
      new Query<SpuInfoEntity>().getPage(params),
      wrapper
    );

    return new PageUtils(page);
  }

  /**
   * 商品上架
   *
   * @param spuId
   */
  @Override
  public void up(Long spuId) {

    List<SkuInfoEntity> skus = skuInfoService.getSkusBySpuId(spuId);

    if (CollectionUtils.isEmpty(skus)) {
      return;
    }
    List<Long> skuIdList = skus.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());

    List<SkuHasStockVo> skuHasStockVo = new ArrayList<>();

    // 远程调用，库存系统查询是否有库存
    try {
      R skuHasStock = wareFeignService.getSkuHasStock(skuIdList);
      if (skuHasStock.getCode() == 0) {
        List<Map<String, Object>> data = (List<Map<String, Object>>) skuHasStock.get("data");

        skuHasStockVo = JSONArray.parseArray(JSON.toJSONString(data), SkuHasStockVo.class);
      }
    } catch (Exception e) {
      log.error("库存查询异常：Exception->{}", e);
    }

    Map<Long, Boolean> skuStockMap = new HashMap<>();

    if (!CollectionUtils.isEmpty(skuHasStockVo)) {
      skuStockMap = skuHasStockVo.stream().collect
        (Collectors.toMap(SkuHasStockVo::getSkuId,
          item -> item.getHasStock() > 0 ? Boolean.TRUE : Boolean.FALSE));
    }


    BrandEntity brandEntity = brandService.getById(skus.get(0).getBrandId());
    CategoryEntity categoryEntity = categoryService.getById(skus.get(0).getCatalogId());

    // 查出可以被检索的规格属性
    List<ProductAttrValueEntity> baseSpuAttrList = productAttrValueService.baseSpuAttrList(spuId);
    if (CollectionUtils.isEmpty(baseSpuAttrList)) {
      return;
    }

    List<Attrs> searchAttrList = new ArrayList<>();

    // 获取可以检索的基础属性
    List<Long> searchAttrIds = attrService.selectSearchAttrIds();

    if (!CollectionUtils.isEmpty(searchAttrIds)) {
      // 做筛选
      Set<Long> searchAttrIdsSet = new HashSet<>(searchAttrIds);
      // 不为空，则得出可筛选的的基础属性列表
      searchAttrList = baseSpuAttrList.stream().map(item -> {
          Attrs attrs = new Attrs();
          BeanUtils.copyProperties(item, attrs);
          return attrs;
        }).filter(item -> searchAttrIdsSet.contains(item.getAttrId()))    // 过滤可检索的属性
        .collect(Collectors.toList());
    }


    List<Attrs> finalSearchAttrList = searchAttrList;
    Map<Long, Boolean> finalSkuStockMap = skuStockMap;

    List<SkuEsModel> esProducts = skus.stream().map(item -> {
      SkuEsModel skuEsModel = new SkuEsModel();
      BeanUtils.copyProperties(item, skuEsModel);
      skuEsModel.setSkuPrice(item.getPrice());
      skuEsModel.setSkuImg(item.getSkuDefaultImg());

      skuEsModel.setHasStock(finalSkuStockMap.getOrDefault(item.getSkuId(), Boolean.FALSE));

      //todo 热度评分 默认0；需要算法支撑
      skuEsModel.setHotScore(0L);
      //品牌、分类信息；
      skuEsModel.setBrandName(brandEntity.getName());
      skuEsModel.setBrandImg(brandEntity.getLogo());
      skuEsModel.setCatalogName(categoryEntity.getName());

      // 查出可以被检索的规格属性
      skuEsModel.setAttrs(finalSearchAttrList);
      return skuEsModel;
    }).collect(Collectors.toList());


    // 发送保存到es

    try {
      R r = searchFeignService.upProduct(esProducts);
      if (r.getCode() == 0) {
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        spuInfoEntity.setId(spuId);
        spuInfoEntity.setUpdateTime(new Date());
        spuInfoEntity.setPublishStatus(ProductConstant.PublishStatusEnum.SPU_UP.getCode());
        this.updateById(spuInfoEntity);

      } else {
        // todo 调用失败,测试接口幂等 Feign自带五次重试机制，可以满足大多数应用场景
        log.error("上架商品失败");
      }

    } catch (Exception e) {
      log.error("上架商品异常：Exception->{}", e);
    }

  }

  @Override
  public SpuInfoVO getSpuInfo(Long skuId) {
    SpuInfoVO spuInfoVO = new SpuInfoVO();
    SkuInfoEntity skuInfo = skuInfoService.getById(skuId);
    SpuInfoEntity spuInfo = this.getById(skuInfo.getSpuId());
    BrandEntity brand = brandService.getById(spuInfo.getBrandId());
    List<SpuImagesEntity> spuImagesList = spuImagesService
      .list(new LambdaQueryWrapper<SpuImagesEntity>().eq(SpuImagesEntity::getSpuId, spuInfo.getId()));
    BeanUtils.copyProperties(spuInfo, spuInfoVO);
    spuInfoVO.setBrandName(brand.getName());
    if (!CollectionUtils.isEmpty(spuImagesList)){
      spuInfoVO.setSpuPic(spuImagesList.get(0).getImgUrl());
    }
    return spuInfoVO;
  }

  @Override
  public void down(Long spuId) {
    // 发送保存到es
    try {
      R r = searchFeignService.downProduct(spuId);
      if (r.getCode() == 0) {
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        spuInfoEntity.setId(spuId);
        spuInfoEntity.setUpdateTime(new Date());
        spuInfoEntity.setPublishStatus(ProductConstant.PublishStatusEnum.SPU_DOWN.getCode());
        this.updateById(spuInfoEntity);
      } else {
        log.error("下架商品失败");
      }
    } catch (Exception e) {
      log.error("下架商品异常：Exception->{}", e);
    }
  }

}