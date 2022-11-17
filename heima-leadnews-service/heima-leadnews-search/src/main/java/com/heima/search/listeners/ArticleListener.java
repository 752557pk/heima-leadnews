package com.heima.search.listeners;

import com.alibaba.fastjson.JSON;
import com.heima.model.searhc.pojo.SearchArticleVo;

import com.heima.search.dao.es.ArticleDao;
import com.heima.search.pojo.Article;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ArticleListener {
    @Autowired
    ArticleDao articleDao;

    @KafkaListener(topics = {"es_index_update"})
    public void updateIndex(String messages) {
        System.out.println("kafka接受消息:" + messages);
        SearchArticleVo searchArticleVo = JSON.parseObject(messages, SearchArticleVo.class);
        Article article = new Article();
        BeanUtils.copyProperties(searchArticleVo,article);
        articleDao.save(article);
    }
}
