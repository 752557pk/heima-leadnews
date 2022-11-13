package com.heima.search.service.Impl;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.searhc.dto.UserSearchDto;
import com.heima.search.service.SearchService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Override
    public ResponseResult search(UserSearchDto dto) {
        //参数校验
        dto.checkParam();
        //拼条件
        SearchRequest searchRequest = new SearchRequest("leadnews");
        SearchSourceBuilder source = searchRequest.source();


        //模糊搜索
        MultiMatchQueryBuilder multiMatchQuery = QueryBuilders.multiMatchQuery(dto.getSearchWords(), "title");
        //时间范围
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("publishTime").lt(dto.getMinBehotTime());


        //组合所有的条件
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.must(multiMatchQuery);
        query.filter(rangeQueryBuilder);
        source.query(query);

        //分页
        source.size(dto.getPageSize());
        //排序
        source.sort("publishTime", SortOrder.DESC);
        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.preTags("<font style='color: red; font-size: inherit;'>");
        highlightBuilder.postTags("</font>");
        highlightBuilder.requireFieldMatch(false);
        source.highlighter(highlightBuilder);

        //执行搜索
        SearchResponse searchResponse = null;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //解析结果
        SearchHits hits = Objects.requireNonNull(searchResponse).getHits();
        SearchHit[] hitsHits = hits.getHits();
        List<Map> ListMaps = new ArrayList<>();
        for (SearchHit hitsHit : hitsHits) {
            Map<String, Object> sourceAsMap = hitsHit.getSourceAsMap();
            String titleString = sourceAsMap.get("title").toString();
            Map<String, HighlightField> highlightFields = hitsHit.getHighlightFields();
            if (highlightFields != null) {
                HighlightField title = highlightFields.get("title");
                if (title != null) {
                    titleString = title.getFragments()[0].string();

                }
            }
            sourceAsMap.put("h_title", titleString);
            ListMaps.add(sourceAsMap);
        }
        return ResponseResult.okResult(ListMaps);
    }
}
