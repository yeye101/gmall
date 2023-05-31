package com.yeye.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.Query;
import com.yeye.mall.product.dao.CategoryDao;
import com.yeye.mall.product.entity.CategoryEntity;
import com.yeye.mall.product.service.CategoryBrandRelationService;
import com.yeye.mall.product.service.CategoryService;
import com.yeye.mall.product.util.RedisUtil;
import com.yeye.mall.product.vo.web.Catalog2Vo;
import com.yeye.mall.product.vo.web.Catalog3Vo;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

  public static final int EXPIRED_TIME = 10 * 60;

  public static final String LOCK_PRODUCT_CATALOG_JSON = "|lock|product|category|";

  @Autowired
  CategoryBrandRelationService categoryBrandRelationService;

  @Autowired
  RedisUtil redisUtil;

  @Autowired
  RedissonClient redissonClient;

  @Override
  public PageUtils queryPage(Map<String, Object> params) {
    IPage<CategoryEntity> page = this.page(
      new Query<CategoryEntity>().getPage(params),
      new QueryWrapper<CategoryEntity>()
    );

    return new PageUtils(page);
  }

  @Override
  public List<CategoryEntity> listWithTree() {
    //查出所有
    List<CategoryEntity> entities = baseMapper.selectList(null);

    //一级分类
    List<CategoryEntity> level1Menus = entities.stream().filter(categoryEntity ->
        categoryEntity.getParentCid() == 0
      ).map(menu -> {
        //组成二级
        menu.setChildren(getChildrens(menu, entities));
        return menu;
      }).sorted(Comparator.comparingInt(menu -> (Optional.ofNullable(menu.getSort()).orElse(0))))
      .collect(Collectors.toList());


    return level1Menus;
  }

  @CacheEvict(value = "category", allEntries = true)
  @Override
  public void removeMenuByIds(List<Long> ids) {
    //todo 检查当前引用菜单是否被用
    this.baseMapper.deleteBatchIds(ids);

  }

  @Override
  public Long[] findCatelogPath(Long catelogId) {
    List<Long> paths = new ArrayList<>();
    List<Long> parentPath = findParentPath(catelogId, paths);
    return parentPath.toArray(new Long[parentPath.size()]);
  }

  /**
   * 开启事务统一管理
   *
   * @param category
   */
  //  @Caching(evict = {
  //    @CacheEvict(value = {"category"},key = "'getCategoryLeval1'"),
  //    @CacheEvict(value = {"category"},key = "'getCatalogJson'")
  //  })

  //删缓存
  @CacheEvict(value = "category", allEntries = true)
  @Transactional
  @Override
  public void updateCascade(CategoryEntity category) {
    this.updateById(category);

    if (!StringUtils.isEmpty(category.getName())) {
      categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }


  }

  @Cacheable(value = {"category"}, key = "#root.methodName") //加入缓存，需要指定放到哪一个名字的区域
  @Override
  public List<CategoryEntity> getCategoryLeval1() {

    List<CategoryEntity> list = this.list(new LambdaQueryWrapper<CategoryEntity>().eq(CategoryEntity::getCatLevel, 1));

    return list;

  }

  /**
   * 处理缓存
   * 1.缓存穿透  添加null值
   * 2.缓存雪崩 添加随机过期时间
   * 3.缓存击穿 加分布式锁保持原子性
   *
   * sync 加上本地方法的锁，足够使用
   */
  @Cacheable(value = "category", key = "#root.methodName" ,sync = true)
  @Override
  public Map<String, List<Catalog2Vo>> getCatalogJson() {

    Map<String, List<Catalog2Vo>> result = new HashMap<>();

    try {
      //查出所有
      List<CategoryEntity> entities = baseMapper.selectList(null);

      // 查出一级分类
      List<CategoryEntity> leval1List = getChildrenByParentCid(entities, 0L);

      result = leval1List.stream().collect(Collectors.toMap(itemk1 -> itemk1.getCatId().toString(), itemv1 -> {
        // 查出子分类
        List<Catalog2Vo> catalog2VoList = getChildrenByParentCid(entities, itemv1.getCatId()).stream()
          .map(item2 -> {
            Catalog2Vo catalog2Vo = new Catalog2Vo(itemv1.getCatId().toString(), null, item2.getCatId().toString(), item2.getName());

            // 获取l3的标题和对象
            List<Catalog3Vo> catalog3VoList = getChildrenByParentCid(entities, item2.getCatId()).stream()
              .map(item3 -> new Catalog3Vo(item2.getCatId().toString(), item3.getCatId().toString(), item3.getName())).collect(Collectors.toList());
            catalog2Vo.setCatalog3List(catalog3VoList);

            return catalog2Vo;
          }).collect(Collectors.toList());

        return catalog2VoList;

      }));

    } catch (Exception e) {
      log.error("getCatalogJson报错{}", e);
    }

    return result;
  }


  // 优化，聚合公共方法
  private List<CategoryEntity> getChildrenByParentCid(List<CategoryEntity> entities, Long parentCid) {
    return entities.stream().filter(categoryEntity -> categoryEntity.getParentCid().equals(parentCid)).collect(Collectors.toList());
  }


  //递归查找父节点id
  public List<Long> findParentPath(Long catelogId, List<Long> paths) {
    //1、收集当前节点id
    CategoryEntity byId = this.getById(catelogId);
    if (byId.getParentCid() != 0) {
      findParentPath(byId.getParentCid(), paths);
    }
    paths.add(catelogId);
    return paths;
  }

  private List<CategoryEntity> getChildrens(CategoryEntity root,
                                            List<CategoryEntity> all) {
    List<CategoryEntity> children = all.stream().
      filter(categoryEntity -> categoryEntity.getParentCid().equals(root.getCatId()))
      .map(menu -> {
        menu.setChildren(getChildrens(menu, all));
        return menu;
      }).sorted(Comparator.comparingInt(menu -> (Optional.ofNullable(menu.getSort()).orElse(0))))
      .collect(Collectors.toList());
    return children;
  }

}