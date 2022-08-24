package com.user9527.shopping.search;


import com.alibaba.fastjson.JSON;


import com.user9527.shopping.search.config.ShoppingElasticsearchConfig;
import lombok.Data;
import lombok.ToString;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.naming.directory.SearchResult;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.user9527.shopping.search.config.ShoppingElasticsearchConfig.COMMON_OPTIONS;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ShoppingSearchApplicationTests {

    @Autowired
    private RestHighLevelClient client;

    @Test
    public void searchData() throws IOException {

        SearchRequest searchRequest = new SearchRequest("bank");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 搜索address中包含mill的所有人的年龄分布以及平均薪资
        searchSourceBuilder.query(QueryBuilders.matchQuery("address", "mill"));
        // 1，按照年龄的值分布进行聚合
        TermsAggregationBuilder ageAgg = AggregationBuilders.terms("ageAgg").field("age").size(10);
        searchSourceBuilder.aggregation(ageAgg);
        // 2, 平均薪资
        AvgAggregationBuilder ageAvg = AggregationBuilders.avg("ageAvg").field("balance");
        searchSourceBuilder.aggregation(ageAvg);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = client.search(searchRequest, COMMON_OPTIONS);

        System.out.println(response);
        // 访问返回的文档
        SearchHits hits = response.getHits();
        TotalHits totalHits = hits.getTotalHits();
        long value = totalHits.value;
        System.out.println("命中数量：" + value);
        TotalHits.Relation relation = totalHits.relation;
        System.out.println("关系：" + relation);

        SearchHit[] hits1 = hits.getHits();
        for (SearchHit hit : hits1) {
            String string = hit.getSourceAsString();
            Accout accout = JSON.parseObject(string, Accout.class);
            System.out.println("accout：" + accout);
        }

        // 获取聚合数据
        Aggregations aggregations = response.getAggregations();
        Terms ageAgg1 = aggregations.get("ageAgg");
        for (Terms.Bucket bucket : ageAgg1.getBuckets()) {
            String keyAsString = bucket.getKeyAsString();
            System.out.println("年龄：" + keyAsString);
        }

        Avg ageAvg1 = aggregations.get("ageAvg");
        System.out.println("平均薪资：" + ageAvg1.getValue());
    }

    @Test
    public void indexData() throws IOException {
        IndexRequest request = new IndexRequest("posts");
        request.id("1");
        User user = new User();
        user.setUserName("李四");
        user.setAge(18);
        user.setGender("男");
        String jsonString = JSON.toJSONString(user);
        request.source(jsonString, XContentType.JSON);
        IndexResponse response = client.index(request, COMMON_OPTIONS);

        System.out.println(response);
    }

    @ToString
    @Data
    static class Accout{
        private int account_number;

        private int balance;

        private String firstname;

        private String lastname;

        private int age;

        private String gender;

        private String address;

        private String employer;

        private String email;

        private String city;

        private String state;
    }

    @Data
    class User{
        private String userName;
        private String gender;
        private Integer age;
    }

    @Test
    public void contextLoads() {
        List<User> userList = new ArrayList<>();
        User user = new User();
        user.setAge(18);
        userList.add(user);
        Accout accout = new Accout();
        accout.setCity(userList.get(0).getGender());

    }

}
