package com.heima.model.article.pojos;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
* <p>
* ap_article_content 实体类
* </p>
*
* @author lenovo
* @since 2022-10-29 11:28:50
*/
@Getter
@Setter
@TableName("ap_article_content")
public class ApArticleContent implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
    * 主键
    */
    @TableId
    private Long id;

    /**
    * 文章ID
    */
    private Long articleId;

    /**
    * 文章内容
    */
    private String content;


}
