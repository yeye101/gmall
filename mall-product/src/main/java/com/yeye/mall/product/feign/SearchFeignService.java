package com.yeye.mall.product.feign;

import com.yeye.common.to.es.SkuEsModel;
import com.yeye.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("mall-search")
public interface SearchFeignService {

  @PostMapping("/search/upProduct")
  R upProduct(@RequestBody List<SkuEsModel> skuEsModels);

  // 下架商品
  @PostMapping("/search/downProduct/{spuId}")
  R downProduct(@PathVariable("spuId") Long spuId);

  // 商品
  @PostMapping("/search/product/{catelogId}")
  R getSpuProduct(@PathVariable("catelogId") Long catelogId);
}
