package com.yeye.mall.search.web;

import com.yeye.mall.search.result.SearchResult;
import com.yeye.mall.search.service.MallSearchService;
import com.yeye.mall.search.vo.SearchVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class SearchController {

  @Autowired
  MallSearchService mallSearchService;

  @GetMapping("/list.html")
  public String search(SearchVo searchVo, Model model, HttpServletRequest request) {

    searchVo.set_queryString(request.getQueryString());
    SearchResult result = mallSearchService.search(searchVo);

    model.addAttribute("result", result);
    return "list";
  }
}
