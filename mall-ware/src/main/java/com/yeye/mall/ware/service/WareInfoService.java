package com.yeye.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yeye.common.utils.PageUtils;
import com.yeye.mall.ware.entity.WareInfoEntity;
import com.yeye.mall.ware.vo.FareVo;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 仓库信息
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 19:49:41
 */
public interface WareInfoService extends IService<WareInfoEntity> {

  PageUtils queryPage(Map<String, Object> params);

  /**
   * 获取运费
   * @param addrId
   * @return
   */
  FareVo getFare(Long addrId);
}

