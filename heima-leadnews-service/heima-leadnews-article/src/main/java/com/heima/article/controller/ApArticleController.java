package com.heima.article.controller;

import com.heima.article.service.ApArticleService;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.ArticleListDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* <p>
* ap_article 控制器实现
* </p>
*
* @author lenovo
* @since 2022-10-29 11:29:44
*/
@RestController
@RequestMapping("/api/v1/article")
public class ApArticleController {

    @Autowired
    private ApArticleService apArticleService;

    @PostMapping("/load")
    public ResponseResult load(@RequestBody ArticleListDto articleListDto) {
        return apArticleService.load(articleListDto,0);
    }

    @PostMapping("/loadnew")
    public ResponseResult loadNew(@RequestBody ArticleListDto articleListDto) {
        return apArticleService.load(articleListDto,1);
    }

    @PostMapping("/loadmore")
    public ResponseResult loadMore(@RequestBody ArticleListDto articleListDto) {
        return apArticleService.load(articleListDto,2);
    }

    /**
     * 接收自媒体系统中的数据，保存到app文章信息表中
     * @param articleDto
     * @return
     */
    @PostMapping("/saveOrUpdate")
    public ResponseResult saveOrUpdate(@RequestBody ArticleDto articleDto) throws Exception {
        return apArticleService.saveOrUpdate(articleDto);
    }



}
