package com.yeye.mall.product.controller;

import com.yeye.common.utils.PageUtils;
import com.yeye.common.utils.R;
import com.yeye.mall.product.entity.AttrEntity;
import com.yeye.mall.product.entity.AttrGroupEntity;
import com.yeye.mall.product.service.AttrAttrgroupRelationService;
import com.yeye.mall.product.service.AttrGroupService;
import com.yeye.mall.product.service.AttrService;
import com.yeye.mall.product.service.CategoryService;
import com.yeye.mall.product.vo.AttrGroupRelationVo;
import com.yeye.mall.product.vo.AttrGroupWithAttrsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 属性分组
 *
 * @author yeye
 * @email 582326542@qq.com
 * @date 2022-10-07 13:33:44
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
  @Autowired
  private AttrGroupService attrGroupService;

  @Autowired
  private CategoryService categoryService;

  @Autowired
  private AttrService attrService;

  @Autowired
  private AttrAttrgroupRelationService relationService;


  /**
   * 获取分类下所有分组&关联属性 /product/attrgroup/{catelogId}/withattr
   *
   */
  @GetMapping("/{catelogId}/withattr")
  public R getAttrGroupWithAttrs(@PathVariable("catelogId") Long catelogId) {

    List<AttrGroupWithAttrsVo> data =attrGroupService.getAttrGroupWithAttrsByCatelogId(catelogId);

    return R.ok().put("data",data);
  }


  /**
   * 添加关联关系 /product/attrgroup/attr/relation
   */
  @PostMapping("/attr/relation")
  public R addRelation(@RequestBody List<AttrGroupRelationVo> vos) {

    relationService.saveBatch(vos);

    return R.ok();
  }

  /**
   * 获取当前组的所有子列表 /product/attrgroup/{attrgroupId}/attr/relation
   */
  @GetMapping("/{attrgroupId}/attr/relation")
  public R attrRelation(@PathVariable("attrgroupId") Long attrgroupId) {

    List<AttrEntity> entities = attrService.getRelationAttr(attrgroupId);

    return R.ok().put("data", entities);
  }

  /**
   * 获取当前组的没有关联的子列表 /product/attrgroup/{attrgroupId}/noattr/relation
   */
  @GetMapping("/{attrgroupId}/noattr/relation")
  public R attrNoRelation(@RequestParam Map<String, Object> params,
                          @PathVariable("attrgroupId") Long attrgroupId) {

    PageUtils page = attrService.getNoRelationAttr(params, attrgroupId);

    return R.ok().put("page", page);
  }


  /**
   * 获取分组列表
   */
  @RequestMapping("/list/{catelogId}")
  public R list(@RequestParam Map<String, Object> params,
                @PathVariable("catelogId") Long catelogId) {

    PageUtils page = attrGroupService.queryPage(params, catelogId);

    return R.ok().put("page", page);
  }


  /**
   * 找出分类的完全路径id,能够刷新后停留在原分类处
   */
  @RequestMapping("/info/{attrGroupId}")
  public R info(@PathVariable("attrGroupId") Long attrGroupId) {
    AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);

    Long[] catelogPath = categoryService.findCatelogPath(attrGroup.getCatelogId());
    attrGroup.setCatelogPath(catelogPath);

    return R.ok().put("attrGroup", attrGroup);
  }

  /**
   * 保存
   */
  @RequestMapping("/save")
  public R save(@RequestBody AttrGroupEntity attrGroup) {
    attrGroupService.save(attrGroup);

    return R.ok();
  }

  /**
   * 修改
   */
  @RequestMapping("/update")
  public R update(@RequestBody AttrGroupEntity attrGroup) {
    attrGroupService.updateById(attrGroup);

    return R.ok();
  }

  /**
   * 删除
   */
  @RequestMapping("/delete")
  public R delete(@RequestBody Long[] attrGroupIds) {
    attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

    return R.ok();
  }


  /**
   * 删除关系 /product/attrgroup/attr/relation/delete
   */
  @PostMapping("/attr/relation/delete")
  public R deleteRelation(@RequestBody AttrGroupRelationVo[] attrGroupRelationVo) {

    attrService.deleteRelation(attrGroupRelationVo);

    return R.ok();
  }

}
