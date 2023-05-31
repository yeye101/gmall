package com.yeye.mall.product.web;

import com.yeye.mall.product.entity.CategoryEntity;
import com.yeye.mall.product.service.CategoryService;
import com.yeye.mall.product.vo.web.Catalog2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class IndexController {

  @Autowired
  CategoryService categoryService;

  @GetMapping({"/", "/index.html"})
  public String indexPage(Model model) {

    List<CategoryEntity> categoryEntityList1 = categoryService.getCategoryLeval1();

    model.addAttribute("categorys", categoryEntityList1);
    return "index";
  }

  // 子菜单
  @ResponseBody
  @GetMapping("index/catalogJson")
  public Map<String, List<Catalog2Vo>> getCatalogJson() {

    Map<String, List<Catalog2Vo>> map = categoryService.getCatalogJson();


    return map;
  }
}
