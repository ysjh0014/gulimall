package com.mg.gulimall.search.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {


    @GetMapping({"/", "/search.html"})
    public String index(Model model) {
        return "search";
    }

}
