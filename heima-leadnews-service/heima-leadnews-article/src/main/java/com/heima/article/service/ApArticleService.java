package com.heima.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.ArticleListDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.mess.ArticleVisitStreamMess;

import java.io.IOException;

/**
* <p>
* ap_article Service 接口
* </p>
*
* @author lenovo
* @since 2022-10-29 11:29:44
*/
public interface ApArticleService extends IService<ApArticle> {

    ResponseResult load(ArticleListDto articleListDto,int type);

    ResponseResult saveOrUpdate(ArticleDto articleDto) throws Exception;

    void cacheArticle();
    /**
     * 更新文章的分值  同时更新缓存中的热点文章数据
     * @param mess
     */
    public void updateScore(ArticleVisitStreamMess mess);
}
