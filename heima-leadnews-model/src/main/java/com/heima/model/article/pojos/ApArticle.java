package com.heima.model.article.pojos;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
* <p>
* ap_article 实体类
* </p>
*
* @author lenovo
* @since 2022-10-29 11:28:50
*/
@Getter
@Setter
@TableName("ap_article")
public class ApArticle implements Serializable {
    private static final long serialVersionUID = 1L;

    // 雪花算法Id
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
    * 文章作者的ID
    */
    private Long authorId;

    /**
    * 作者昵称
    */
    private String authorName;

    /**
    * 文章所属频道ID
    */
    private Long channelId;

    /**
    * 频道名称
    */
    private String channelName;

    /**
    * 市区
    */
    private Long cityId;

    /**
    * 收藏数量
    */
    private Long collection;

    /**
    * 评论数量
    */
    private Long comment;

    /**
    * 区县
    */
    private Long countyId;

    /**
    * 创建时间
    */
    private LocalDateTime createdTime;

    /**
    * 文章标记
            0 普通文章
            1 热点文章
            2 置顶文章
            3 精品文章
            4 大V 文章
    */
    private Integer flag;

    /**
    * 文章图片
            多张逗号分隔
    */
    private String images;

    /**
    * 文章标签最多3个 逗号分隔
    */
    private String labels;

    /**
    * 文章布局
            0 无图文章
            1 单图文章
            2 多图文章
    */
    private Integer layout;

    /**
    * 点赞数量
    */
    private Long likes;

    /**
    * 来源
    */
    private Integer origin;

    /**
    * 省市
    */
    private Long provinceId;

    /**
    * 发布时间
    */
    private Date publishTime;

    private String staticUrl;

    /**
    * 同步状态
    */
    private Integer syncStatus;

    /**
    * 标题
    */
    private String title;

    /**
    * 阅读数量
    */
    private Long views;


}
