package com.mg.gulimall.search.web;

import com.mg.gulimall.search.service.SearchMallService;
import com.mg.gulimall.search.vo.SearchParam;
import com.mg.gulimall.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class IndexController {

    @Autowired
    SearchMallService searchService;

    @GetMapping({"/", "/search.html"})
    @ResponseBody
    public String index(SearchParam searchParam, Model model, HttpServletRequest request) {
        searchParam.set_queryString(request.getQueryString());
        SearchResult result = searchService.getSearchResult(searchParam);
        model.addAttribute("result",result);
        return "search";
    }

    @GetMapping({"/", "/list.html"})
    public String list(Model model) {
        return "search";
    }

}
