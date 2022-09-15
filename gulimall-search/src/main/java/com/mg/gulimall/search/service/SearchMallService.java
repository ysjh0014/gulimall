package com.mg.gulimall.search.service;

import com.mg.gulimall.search.vo.SearchParam;
import com.mg.gulimall.search.vo.SearchResult;

public interface SearchMallService {
    SearchResult getSearchResult(SearchParam searchParam);
}
