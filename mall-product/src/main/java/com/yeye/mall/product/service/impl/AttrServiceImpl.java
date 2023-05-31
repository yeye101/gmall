package com.yeye.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeye.common.constant.ProductConstant;
import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.Query;
import com.yeye.mall.product.dao.AttrAttrgroupRelationDao;
import com.yeye.mall.product.dao.AttrDao;
import com.yeye.mall.product.dao.AttrGroupDao;
import com.yeye.mall.product.dao.CategoryDao;
import com.yeye.mall.product.entity.AttrAttrgroupRelationEntity;
import com.yeye.mall.product.entity.AttrEntity;
import com.yeye.mall.product.entity.AttrGroupEntity;
import com.yeye.mall.product.entity.CategoryEntity;
import com.yeye.mall.product.service.AttrService;
import com.yeye.mall.product.service.CategoryService;
import com.yeye.mall.product.vo.AttrGroupRelationVo;
import com.yeye.mall.product.vo.AttrRespVo;
import com.yeye.mall.product.vo.AttrVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

  @Autowired
  AttrAttrgroupRelationDao relationDao;

  @Autowired
  AttrGroupDao attrGroupDao;

  @Autowired
  CategoryDao categoryDao;

  @Autowired
  CategoryService categoryService;


  @Override
  public PageUtils queryPage(Map<String, Object> params) {
    IPage<AttrEntity> page = this.page(
      new Query<AttrEntity>().getPage(params),
      new QueryWrapper<AttrEntity>()
    );

    return new PageUtils(page);
  }

  @Transactional
  @Override
  public void saveAttr(AttrVo attr) {
    AttrEntity attrEntity = new AttrEntity();
    BeanUtils.copyProperties(attr, attrEntity);
    this.save(attrEntity);

    if (attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() && attr.getAttrGroupId() != null) {
      // 属性和属性分组关系图
      AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
      relationEntity.setAttrGroupId(attr.getAttrGroupId());
      relationEntity.setAttrId(attrEntity.getAttrId());
      relationDao.insert(relationEntity);
    }

  }

  @Override
  public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String attrType) {
    String key = (String) params.get("key");
    LambdaQueryWrapper<AttrEntity> wrapper = new LambdaQueryWrapper<AttrEntity>();
    // 加上初始判断，是否为销售或者规格属性(1,0)
    wrapper.eq(AttrEntity::getAttrType, "base".equalsIgnoreCase(attrType) ?
      ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() : ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());

    if (!StringUtils.isEmpty(key)) {
      wrapper.eq(AttrEntity::getAttrId, key).or().like(AttrEntity::getAttrName, key);
    }

    if (catelogId != 0) {
      wrapper.eq(AttrEntity::getCatelogId, catelogId);
    }

    IPage<AttrEntity> page = this.page(
      new Query<AttrEntity>().getPage(params),
      wrapper
    );

    // 抽取元素，变换实体类
    PageUtils pageUtils = new PageUtils(page);
    List<AttrEntity> records = page.getRecords();


    List<AttrRespVo> respVos = records.stream().map(attrEntity -> {
      AttrRespVo attrRespVo = new AttrRespVo();
      BeanUtils.copyProperties(attrEntity, attrRespVo);
      // 只有基本（base）属性才有分组，销售（sale）属性无分组
      if ("base".equalsIgnoreCase(attrType)) {
        //设置所属分类名字 和所属分组名字
        AttrAttrgroupRelationEntity relationEntity = relationDao.selectOne(
          new LambdaQueryWrapper<AttrAttrgroupRelationEntity>().
            eq(AttrAttrgroupRelationEntity::getAttrId, attrEntity.getAttrId()));

        if (null != relationEntity) {
          AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
          attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
        }
      }
      if (null != attrEntity.getCatelogId()) {
        CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
        attrRespVo.setCatelogName(categoryEntity.getName());
      }
      return attrRespVo;
    }).collect(Collectors.toList());

    pageUtils.setList(respVos);

    return pageUtils;
  }

  @Override
  public AttrRespVo getAttrInfo(Long attrId) {
    AttrRespVo respVo = new AttrRespVo();

    // 拷贝属性
    AttrEntity attr = this.getById(attrId);
    BeanUtils.copyProperties(attr, respVo);

    if (attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
      //设置所属分类名字 和所属分组名字
      AttrAttrgroupRelationEntity relationEntity = relationDao.selectOne(
        new LambdaQueryWrapper<AttrAttrgroupRelationEntity>().
          eq(AttrAttrgroupRelationEntity::getAttrId, attr.getAttrId()));
      //  若无分组关系，则不赋值(补充信息)
      if (null != relationEntity) {
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
        respVo.setGroupName(attrGroupEntity.getAttrGroupName());
        respVo.setAttrGroupId(relationEntity.getAttrGroupId());
      }
    }
    if (null != attr.getCatelogId()) {
      CategoryEntity categoryEntity = categoryDao.selectById(attr.getCatelogId());
      Long[] catelogPath = categoryService.findCatelogPath(attr.getCatelogId());
      respVo.setCatelogName(categoryEntity.getName());
      respVo.setCatelogPath(catelogPath);
    }

    return respVo;
  }

  @Override
  public void updateAttr(AttrVo attr) {
    AttrEntity attrEntity = new AttrEntity();
    BeanUtils.copyProperties(attr, attrEntity);
    // 修改原本的属性
    this.updateById(attrEntity);


    if (attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
      // 修改连带的表的属性
      AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
      relationEntity.setAttrGroupId(attr.getAttrGroupId());
      relationEntity.setAttrId(attr.getAttrId());

      Integer count = relationDao.selectCount(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>()
        .eq(AttrAttrgroupRelationEntity::getAttrId, attr.getAttrId()));
      if (count > 0) {
        relationDao.update(relationEntity, new LambdaUpdateWrapper<AttrAttrgroupRelationEntity>()
          .eq(AttrAttrgroupRelationEntity::getAttrId, attr.getAttrId()));
      } else {
        relationDao.insert(relationEntity);
      }
    }
  }


  /**
   * 根据组id查出所有的关联子分组
   *
   * @param attrgroupId
   * @return 列表
   */
  @Transactional
  @Override
  public List<AttrEntity> getRelationAttr(Long attrgroupId) {

    List<AttrAttrgroupRelationEntity> relationEntities = relationDao.selectList(new LambdaQueryWrapper<AttrAttrgroupRelationEntity>().
      eq(AttrAttrgroupRelationEntity::getAttrGroupId, attrgroupId));

    if (0 == relationEntities.size()) {
      return null;
    }
    List<Long> attrIdList = relationEntities.stream().map(attr -> attr.getAttrId()).collect(Collectors.toList());

    List<AttrEntity> list = this.listByIds(attrIdList);
    return list;
  }

  @Override
  public void deleteRelation(AttrGroupRelationVo[] attrGroupRelationVo) {

    List<AttrAttrgroupRelationEntity> entities = Arrays.asList(attrGroupRelationVo).stream().map(item -> {
      AttrAttrgroupRelationEntity entity = new AttrAttrgroupRelationEntity();
      BeanUtils.copyProperties(item, entity);
      return entity;
    }).collect(Collectors.toList());

    // 使用手写sql实现批量删除二条件的记录
    relationDao.deleteBatchRelation(entities);
  }

  /**
   * 获取当前组的没有关联的子列表
   * 1.查询当前分组所在分类。当前分组只能关联自己所属分类里面的所有属性。
   * 2.查询此分类下的。当前分组只能关联同分类下，别的分组没有引用的属性。
   * 2.1.查询当前分类下的其他属性分组ids
   * 2.2.查询这些分组中已关联的属性ids
   * 2.3.从当前分类的所有属性中排除查询这些分组中已关联的属性list
   *
   * @param params
   * @param attrgroupId
   * @return
   */
  @Override
  public PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId) {
    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);
    Long catelogId = attrGroupEntity.getCatelogId();
    List<AttrGroupEntity> attrGroupList = attrGroupDao.selectList(new LambdaQueryWrapper<AttrGroupEntity>()
      .eq(AttrGroupEntity::getCatelogId, catelogId));
    List<Long> attrGroupIds = attrGroupList.stream().map(AttrGroupEntity::getAttrGroupId).collect(Collectors.toList());

    List<AttrAttrgroupRelationEntity> relationList = relationDao.selectList(
      new LambdaQueryWrapper<AttrAttrgroupRelationEntity>().in(AttrAttrgroupRelationEntity::getAttrGroupId, attrGroupIds));
    List<Long> attrIds = relationList.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());

    String key = (String) params.get("key");

    // 设置查询参数
    LambdaQueryWrapper<AttrEntity> wrapper = new LambdaQueryWrapper<AttrEntity>()
      .eq(AttrEntity::getCatelogId, catelogId)
      .eq(AttrEntity::getAttrType, ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());

    if (null != attrIds && attrIds.size() > 0) {
      wrapper.notIn(AttrEntity::getAttrId, attrIds);
    }
    if (!StringUtils.isEmpty(key)) {
      wrapper.eq(AttrEntity::getAttrId, key).or().like(AttrEntity::getAttrName, key);
    }

    IPage<AttrEntity> page = this.page(
      new Query<AttrEntity>().getPage(params),
      wrapper
    );
    return new PageUtils(page);

  }

  @Override
  public List<Long> selectSearchAttrIds() {

    List<AttrEntity> list = this.list(new LambdaQueryWrapper<AttrEntity>().eq(AttrEntity::getSearchType, 1));
    
    if (CollectionUtils.isEmpty(list)){
      return new ArrayList<>();
    }
    List<Long> searchAttrIds = list.stream().map(AttrEntity::getAttrId).collect(Collectors.toList());
    return searchAttrIds;
    
  }


}