package com.heima.article.job;

import com.heima.article.service.ApArticleService;
import com.heima.article.service.impl.CacheService;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 缓存每天的热点文章数据
 */
@Component
public class ArticleCacheJob {

    @Autowired
    ApArticleService apArticleService;
    @Autowired
    CacheService cacheService;

    /**
     * 每天早上两点执行
     */
//    @Scheduled(cron = "0 1 * * * ?")
    @XxlJob("my-xiaojiba")
    public void cacheArticle() {
        System.out.println("==========开始任务调度");
        String article_hot = cacheService.tryLock("article_hot", 30000);
        if (StringUtils.isNotEmpty(article_hot)) {

            apArticleService.cacheArticle();
        }
        System.out.println("==========结束任务调度");

    }
}
