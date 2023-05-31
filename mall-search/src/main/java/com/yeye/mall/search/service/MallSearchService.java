package com.yeye.mall.search.service;

import com.yeye.mall.search.result.SearchResult;
import com.yeye.mall.search.vo.SearchVo;

public interface MallSearchService {

  SearchResult search(SearchVo searchVo);
}
