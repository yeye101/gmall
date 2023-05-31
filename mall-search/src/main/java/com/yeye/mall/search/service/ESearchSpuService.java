package com.yeye.mall.search.service;


import com.yeye.common.to.es.SkuEsModel;
import com.yeye.common.utils.R;

import java.util.List;

public interface ESearchSpuService {

  Boolean upProduct(List<SkuEsModel> skuEsModels);

  Boolean downProduct(Long spuId);

  R getSpuProduct(Long catelogId);
}
