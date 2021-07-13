package com.user9527.shopping.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.user9527.common.to.es.SkuEsModel;
import com.user9527.common.utils.R;
import com.user9527.shopping.search.config.ShoppingElasticsearchConfig;
import com.user9527.shopping.search.constant.EsConstant;
import com.user9527.shopping.search.feign.ProductFeignService;
import com.user9527.shopping.search.service.MallSearchService;
import com.user9527.shopping.search.vo.AttrResponseVo;
import com.user9527.shopping.search.vo.BrandVo;
import com.user9527.shopping.search.vo.SearchParam;
import com.user9527.shopping.search.vo.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.TotalHits;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yangtao
 * @version 1.0
 * @date 2021/6/28 16:45
 */
@Service
public class MallSearchServiceimpl implements MallSearchService {

    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private ProductFeignService productFeignService;

    @Override
    public SearchResult search(SearchParam paramVO) {
        SearchResult result = null;
        // 1 动态构建出查询需要的dsl语句
        SearchRequest searchRequest = buildSearchRequest(paramVO);

        try {
            SearchResponse searchResponse = client.search(searchRequest, ShoppingElasticsearchConfig.COMMON_OPTIONS);
            result = buildSearchResponse(searchResponse, paramVO);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 构建查询条件
     *
     * @param paramVO
     * @return
     */
    private SearchRequest buildSearchRequest(SearchParam paramVO) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 构建bool查询
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if (StringUtils.isNotEmpty(paramVO.getKeyword())) {
            boolQuery.must(QueryBuilders.matchQuery("skuTitle", paramVO.getKeyword()));
        }
        if (paramVO.getCatalog3Id() != null) {
            boolQuery.filter(QueryBuilders.termQuery("catalogId", paramVO.getCatalog3Id()));
        }
        if (paramVO.getBrandId() != null && paramVO.getBrandId().size() > 0) {
            boolQuery.filter(QueryBuilders.termsQuery("brandId", paramVO.getBrandId()));
        }
        if (paramVO.getHasStock() != null) {
            boolQuery.filter(QueryBuilders.termQuery("hasStock", paramVO.getHasStock() == 1));
        }
        if (paramVO.getAttrs() != null && paramVO.getAttrs().size() > 0) {
            // attrs=1_5寸：8寸&attrs=2_16G：8G
            for (String attrStr : paramVO.getAttrs()) {
                BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
                String[] s = attrStr.split("_");
                String attrId = s[0];
                String[] attrValues = s[1].split(":");
                queryBuilder.must(QueryBuilders.termsQuery("attrs.attrId", attrId));
                queryBuilder.must(QueryBuilders.termsQuery("attrs.attrValue", attrValues));
                boolQuery.filter(QueryBuilders.nestedQuery("attrs", queryBuilder, ScoreMode.None));
            }

        }
        if (StringUtils.isNotEmpty(paramVO.getSkuPrice())) {
            // 1_500 _500 500_
            String[] s = paramVO.getSkuPrice().split("_");
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("skuPrice");
            if (s.length == 2) {
                rangeQueryBuilder.gte(s[0]).lte(s[1]);

            } else {
                if (paramVO.getSkuPrice().startsWith("_")) {
                    rangeQueryBuilder.lte(paramVO.getSkuPrice());
                }
                if (paramVO.getSkuPrice().endsWith("_")) {
                    rangeQueryBuilder.gte(paramVO.getSkuPrice());
                }
            }
            boolQuery.filter(rangeQueryBuilder);
        }

        // 排序条件
        if (StringUtils.isNotEmpty(paramVO.getSort())) {
            // saleCount_acs/desc
            String[] s = paramVO.getSort().split("_");
            SortOrder order = s[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC;
            searchSourceBuilder.sort(s[0], order);
        }

        // 分页
        searchSourceBuilder.from((paramVO.getPageNum() - 1) * EsConstant.PRODUCT_PAGESIZE);
        searchSourceBuilder.size(EsConstant.PRODUCT_PAGESIZE);

        // 高亮
        if (StringUtils.isNotEmpty(paramVO.getKeyword())) {
            HighlightBuilder builder = new HighlightBuilder();
            builder.field("skuTitle");
            builder.preTags("<b style='color:red'>");
            builder.postTags("</b>");
            searchSourceBuilder.highlighter(builder);
        }

        // 聚合
        TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg");
        brand_agg.field("brandId").size(50);
        brand_agg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));
        brand_agg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1));
        searchSourceBuilder.aggregation(brand_agg);

        TermsAggregationBuilder catalog_agg = AggregationBuilders.terms("catalog_agg");
        catalog_agg.field("catalogId").size(50);
        catalog_agg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName").size(1));
        searchSourceBuilder.aggregation(catalog_agg);

        NestedAggregationBuilder nested = AggregationBuilders.nested("attrs_agg", "attrs");
        TermsAggregationBuilder attrId_agg = AggregationBuilders.terms("attrId_agg").field("attrs.attrId");
        attrId_agg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
        attrId_agg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue")).size(100);

        nested.subAggregation(attrId_agg);
        searchSourceBuilder.aggregation(nested);

        searchSourceBuilder.query(boolQuery);

        String s = searchSourceBuilder.toString();
        System.out.println("构建的DSL" + s);
        return new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, searchSourceBuilder);
    }

    /**
     * 构建查询结果
     *
     * @param searchResponse
     * @return
     */
    private SearchResult buildSearchResponse(SearchResponse searchResponse, SearchParam param) {
        SearchResult result = new SearchResult();

        SearchHits hits = searchResponse.getHits();
        List<SkuEsModel> products = new ArrayList<>();
        for (SearchHit hit : hits.getHits()) {
            SkuEsModel esModel = JSON.parseObject(hit.getSourceAsString(), SkuEsModel.class);
            if(StringUtils.isNotEmpty(param.getKeyword())){
                HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
                String string = skuTitle.getFragments()[0].string();
                esModel.setSkuTitle(string);
            }
            products.add(esModel);
        }
        result.setProducts(products);

        // 属性
        List<SearchResult.AttrVO> attrs = new ArrayList<>();
        ParsedNested attrs_agg = searchResponse.getAggregations().get("attrs_agg");
        ParsedLongTerms attrId_agg = attrs_agg.getAggregations().get("attrId_agg");
        for (Terms.Bucket bucket : attrId_agg.getBuckets()) {
            SearchResult.AttrVO attrVO = new SearchResult.AttrVO();

            long attrId = bucket.getKeyAsNumber().longValue();
            String attr_name_agg = ((ParsedStringTerms) bucket.getAggregations().get("attr_name_agg")).getBuckets().get(0).getKeyAsString();
            List<String> attrValues = ((ParsedStringTerms) bucket.getAggregations().get("attr_value_agg")).getBuckets().stream().map(item -> {
                String keyAsString = ((Terms.Bucket) item).getKeyAsString();
                return keyAsString;
            }).collect(Collectors.toList());

            attrVO.setAttrId(attrId);
            attrVO.setAttrName(attr_name_agg);
            attrVO.setAttrValue(attrValues);

            attrs.add(attrVO);
        }
        result.setAttrs(attrs);

        // 分类
        List<SearchResult.CatalogVO> catalogVOList = new ArrayList<>();
        ParsedLongTerms catalog_agg = searchResponse.getAggregations().get("catalog_agg");
        for (Terms.Bucket bucket : catalog_agg.getBuckets()) {
            SearchResult.CatalogVO catalogVO = new SearchResult.CatalogVO();
            String keyAsString = bucket.getKeyAsString();
            String catalog_name_agg = ((ParsedStringTerms) bucket.getAggregations().get("catalog_name_agg")).getBuckets().get(0).getKeyAsString();

            catalogVO.setCatalogId(Long.parseLong(keyAsString));
            catalogVO.setCatalogName(catalog_name_agg);

            catalogVOList.add(catalogVO);
        }
        result.setCatalogs(catalogVOList);

        // 品牌
        List<SearchResult.BrandVO> brandVOList = new ArrayList<>();
        ParsedLongTerms brand_agg = searchResponse.getAggregations().get("brand_agg");
        for (Terms.Bucket bucket : brand_agg.getBuckets()) {
            SearchResult.BrandVO brandVO = new SearchResult.BrandVO();
            // 1 品牌id
            long brandId = bucket.getKeyAsNumber().longValue();
            // 2 品牌名
            String brand_name_agg = ((ParsedStringTerms) bucket.getAggregations().get("brand_name_agg")).getBuckets().get(0).getKeyAsString();
            // 3 品牌图片
            String brand_img_agg = ((ParsedStringTerms) bucket.getAggregations().get("brand_img_agg")).getBuckets().get(0).getKeyAsString();
            brandVO.setBrandId(brandId);
            brandVO.setBrandName(brand_name_agg);
            brandVO.setBrandImg(brand_img_agg);
            brandVOList.add(brandVO);
        }
        result.setBrands(brandVOList);

        // 分页
        result.setPageNum(param.getPageNum());
        long total = hits.getTotalHits().value;
        result.setTotal(total);
        int totalPages = (int) total % EsConstant.PRODUCT_PAGESIZE == 0 ? (int) total / EsConstant.PRODUCT_PAGESIZE : (int) (total / EsConstant.PRODUCT_PAGESIZE) + 1;
        result.setTotalPages(totalPages);
        //页码导航
        ArrayList<Integer> pageNavs = new ArrayList<>();
        for (int i = 1; i <= totalPages; i++) {
            pageNavs.add(i);
        }
        result.setPageNavs(pageNavs);

        //6 构建面包屑导航功能 属性
        if (param.getAttrs() != null && param.getAttrs().size() > 0) {
            List<SearchResult.NavVo> navVos = param.getAttrs().stream().map(attr -> {
                SearchResult.NavVo navVo = new SearchResult.NavVo();
                //1 分析每个attrs传过来的查询参数值
                //attrs=2_5寸:6寸
                String[] s = attr.split("_");
                navVo.setNavValue(s[1]);
                R r = productFeignService.attrInfo(Long.parseLong(s[0]));
                result.getAttrIds().add(Long.parseLong(s[0]));
                if (r.getCode() == 0) {
                    //正常返回
                    AttrResponseVo data = r.getData("attr", new TypeReference<AttrResponseVo>() {});
                    navVo.setNavName(data.getAttrName());
                } else {
                    //如果失败
                    navVo.setNavName(s[0]);
                }

                //2 取消了面包屑以后 我们要跳转到哪个地方 将请求地址的url里面的当前请求参数置空
                //拿到所有的查询条件 去掉当前
                String replace = replaceQueryString(param, attr, "attrs");
                navVo.setLink("http://localhost:12000/list.html?" + replace);
                return navVo;
            }).collect(Collectors.toList());
            result.setNavs(navVos);
        }

        //品牌，分类 面包屑
        if(param.getBrandId() != null && param.getBrandId().size()>0) {
            List<SearchResult.NavVo> navs = result.getNavs();
            SearchResult.NavVo navVo = new SearchResult.NavVo();
            navVo.setNavName("品牌");
            //TODO 远程查询
            R r = productFeignService.brandsInfo(param.getBrandId());
            if (r.getCode() == 0) {
                List<BrandVo> brands = r.getData("brand", new TypeReference<List<BrandVo>>() {});
                StringBuffer buffer = new StringBuffer();
                String replace = "";
                for (BrandVo brandVo : brands) {
                    buffer.append(brandVo.getName() + ";");
                    replace = replaceQueryString(param, brandVo.getBrandId()+"", "brandId");
                }
                navVo.setNavValue(buffer.toString());
                navVo.setLink("http://localhost:12000/list.html?" + replace);
            }
            navs.add(navVo);
        }

        return result;
    }
    //编写面包屑的功能时，删除指定请求
    private String replaceQueryString(SearchParam param, String value,String key) {
        String encode = "";
        try {
            encode = URLEncoder.encode(value, "UTF-8");
            //+ 对应浏览器的%20编码
            encode = encode.replace("+","%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return  param.get_queryString().replace("&" + key + "=" + encode, "");
    }

}
