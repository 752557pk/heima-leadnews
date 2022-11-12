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
* ap_article_config 实体类
* </p>
*
* @author lenovo
* @since 2022-10-29 11:28:50
*/
@Getter
@Setter
@TableName("ap_article_config")
public class ApArticleConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
    * 主键
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 文章ID
    */
    private Long articleId;

    /**
    * 是否可评论
    */
    private Integer isComment;

    /**
    * 是否已删除
    */
    private Integer isDelete;

    /**
    * 是否下架
    */
    private Integer isDown;

    /**
    * 是否转发
    */
    private Integer isForward;


}
