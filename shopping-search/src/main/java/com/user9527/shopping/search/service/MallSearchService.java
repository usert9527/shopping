package com.user9527.shopping.search.service;

import com.user9527.shopping.search.vo.SearchParam;
import com.user9527.shopping.search.vo.SearchResult;

import java.io.IOException;

/**
 * @author yangtao
 * @version 1.0
 * @date 2021/6/28 16:45
 */
public interface MallSearchService {

    /**
     * @param paramVO 检索的所有参数
     * @return 返回检索的结果
     */
    SearchResult search(SearchParam paramVO);
}
