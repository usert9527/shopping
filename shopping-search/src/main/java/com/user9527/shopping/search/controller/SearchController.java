package com.user9527.shopping.search.controller;

import com.user9527.shopping.search.service.MallSearchService;
import com.user9527.shopping.search.vo.SearchParam;
import com.user9527.shopping.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author yangtao
 * @version 1.0
 * @date 2021/6/28 16:31
 */
@Controller
public class SearchController {

    @Autowired
    private MallSearchService mallSearchService;

    @GetMapping("/list.html")
    public String listPage(SearchParam paramVO, Model model, HttpServletRequest request) {

        //拿到后面的完整的查询字符串
        String queryString = request.getQueryString();
        paramVO.set_queryString(queryString);

        SearchResult result = mallSearchService.search(paramVO);
        model.addAttribute("result", result);
        return "list";
    }
}
