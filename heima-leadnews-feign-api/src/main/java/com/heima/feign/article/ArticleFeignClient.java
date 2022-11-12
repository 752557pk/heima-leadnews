package com.heima.feign.article;

import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "heima-leadnews-article")
public interface ArticleFeignClient {

    /**
     * 接收自媒体系统中的数据，保存到app文章信息表中
     * @param articleDto
     * @return
     */
    @PostMapping("/api/v1/article/saveOrUpdate")
    public ResponseResult saveOrUpdate(ArticleDto articleDto);

}
