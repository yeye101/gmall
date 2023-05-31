package com.yeye.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.Query;
import com.yeye.mall.product.dao.SpuImagesDao;
import com.yeye.mall.product.entity.SpuImagesEntity;
import com.yeye.mall.product.service.SpuImagesService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("spuImagesService")
public class SpuImagesServiceImpl extends ServiceImpl<SpuImagesDao, SpuImagesEntity> implements SpuImagesService {

  @Override
  public PageUtils queryPage(Map<String, Object> params) {
    IPage<SpuImagesEntity> page = this.page(
      new Query<SpuImagesEntity>().getPage(params),
      new QueryWrapper<SpuImagesEntity>()
    );

    return new PageUtils(page);
  }

  @Override
  public void saveImages(Long id, List<String> images) {
    if (CollectionUtils.isEmpty(images)) {
      return;
    }

    List<SpuImagesEntity> collect = images.stream().map(item -> {
      SpuImagesEntity spuImagesEntity = new SpuImagesEntity();
      spuImagesEntity.setSpuId(id);
      spuImagesEntity.setImgUrl(item);
      return spuImagesEntity;
    }).collect(Collectors.toList());

    this.saveBatch(collect);
  }

}