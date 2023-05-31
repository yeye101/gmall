package com.yeye.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.Query;
import com.yeye.mall.product.dao.AttrAttrgroupRelationDao;
import com.yeye.mall.product.entity.AttrAttrgroupRelationEntity;
import com.yeye.mall.product.service.AttrAttrgroupRelationService;
import com.yeye.mall.product.vo.AttrGroupRelationVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrAttrgroupRelationService")
public class AttrAttrgroupRelationServiceImpl extends ServiceImpl<AttrAttrgroupRelationDao, AttrAttrgroupRelationEntity> implements AttrAttrgroupRelationService {

  @Override
  public PageUtils queryPage(Map<String, Object> params) {
    IPage<AttrAttrgroupRelationEntity> page = this.page(
      new Query<AttrAttrgroupRelationEntity>().getPage(params),
      new QueryWrapper<AttrAttrgroupRelationEntity>()
    );

    return new PageUtils(page);
  }

  /**
   * 批量保存关联关系
   *
   * @param vos
   */
  @Override
  public void saveBatch(List<AttrGroupRelationVo> vos) {
    List<AttrAttrgroupRelationEntity> relationEntityList = vos.stream().map(item -> {
      AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
      BeanUtils.copyProperties(item, relationEntity);
      return relationEntity;
    }).collect(Collectors.toList());

    this.saveBatch(relationEntityList);
  }

}