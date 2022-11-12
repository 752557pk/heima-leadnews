package com.heima.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.article.dtos.ArticleListDto;
import com.heima.model.article.pojos.ApArticle;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* <p>
* ap_article Mapper 接口
* </p>
*
* @author lenovo
* @since 2022-10-29 11:29:44
*/
public interface ApArticleMapper extends BaseMapper<ApArticle> {

    public List<ApArticle> load(@Param("articleListDto") ArticleListDto articleListDto);

}