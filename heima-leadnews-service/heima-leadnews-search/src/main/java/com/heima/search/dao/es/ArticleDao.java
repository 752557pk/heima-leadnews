package com.heima.search.dao.es;


import com.heima.search.pojo.Article;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleDao extends ElasticsearchRepository<Article,Long> {
}
