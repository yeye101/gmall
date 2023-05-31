package com.yeye.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yeye.common.utils.PageUtils;
import com.yeye.mall.product.entity.SpuImagesEntity;

import java.util.List;
import java.util.Map;

/**
 * spu图片
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 13:33:44
 */
public interface SpuImagesService extends IService<SpuImagesEntity> {

  PageUtils queryPage(Map<String, Object> params);

  void saveImages(Long id, List<String> images);
}

