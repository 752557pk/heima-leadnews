package com.heima.dao;


import com.heima.pojo.SearchArticleVo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleDao extends ElasticsearchRepository<SearchArticleVo,Long> {
}
