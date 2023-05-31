package com.yeye.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.Query;
import com.yeye.mall.product.dao.AttrGroupDao;
import com.yeye.mall.product.entity.AttrEntity;
import com.yeye.mall.product.entity.AttrGroupEntity;
import com.yeye.mall.product.service.AttrGroupService;
import com.yeye.mall.product.service.AttrService;
import com.yeye.mall.product.vo.AttrGroupWithAttrsVo;
import com.yeye.mall.product.vo.web.SpuItemGroupVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

  @Autowired
  AttrService attrService;

  @Override
  public PageUtils queryPage(Map<String, Object> params) {
    IPage<AttrGroupEntity> page = this.page(
      new Query<AttrGroupEntity>().getPage(params),
      new QueryWrapper<AttrGroupEntity>()
    );

    return new PageUtils(page);
  }

  @Override
  public PageUtils queryPage(Map<String, Object> params, Long catelogId) {

    String key = (String) params.get("key");
    LambdaQueryWrapper<AttrGroupEntity> wrapper = new LambdaQueryWrapper<AttrGroupEntity>();
    if (!StringUtils.isEmpty(key)) {
      wrapper.eq(AttrGroupEntity::getAttrGroupId, key).or().like(AttrGroupEntity::getAttrGroupName, key);
    }

    if (catelogId != 0) {
      wrapper.eq(AttrGroupEntity::getCatelogId, catelogId);
    }

    IPage<AttrGroupEntity> page = this.page(
      new Query<AttrGroupEntity>().getPage(params),
      wrapper
    );
    return new PageUtils(page);
  }

  /**
   * 获取分类下所有分组&关联属性
   *
   * @param catelogId
   * @return
   */
  @Override
  public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId) {

    List<AttrGroupEntity> list = this.list(new LambdaQueryWrapper<AttrGroupEntity>()
      .eq(AttrGroupEntity::getCatelogId, catelogId));
    if (CollectionUtils.isEmpty(list)) {
      return null;
    }

    List<AttrGroupWithAttrsVo> attrsVoList = list.stream().map(item -> {
      AttrGroupWithAttrsVo vo = new AttrGroupWithAttrsVo();
      BeanUtils.copyProperties(item, vo);

      // 根据组id查出所有的关联子分组
      List<AttrEntity> relationAttrList = attrService.getRelationAttr(item.getAttrGroupId());
      vo.setAttrs(relationAttrList);

      return vo;
    }).collect(Collectors.toList());

    return attrsVoList;
  }

  @Override
  public List<SpuItemGroupVo> getGroupAttrsBySIdAndCId(Long spuId, Long catalogId) {
    List<SpuItemGroupVo> result = new ArrayList<>();

    result = this.baseMapper.getGroupAttrsBySIdAndCId(spuId, catalogId);

    return result;
  }

}