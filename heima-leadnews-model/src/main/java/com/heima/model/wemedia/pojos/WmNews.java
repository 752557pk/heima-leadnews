package com.heima.model.wemedia.pojos;
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
* wm_news 实体类
* </p>
*
* @author lenovo
* @since 2022-10-31 16:56:43
*/
@Getter
@Setter
@TableName("wm_news")
public class WmNews implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
    * 主键
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 自媒体用户ID
    */
    private Long userId;

    /**
    * 标题
    */
    private String title;

    /**
    * 图文内容
    */
    private String content;

    /**
    * 文章布局
            0 无图文章
            1 单图文章
            3 多图文章
    */
    private Integer type;

    /**
    * 图文频道ID
    */
    private Long channelId;

    private String labels;

    /**
    * 创建时间
    */
    private LocalDateTime createdTime;

    /**
    * 提交时间
    */
    private LocalDateTime submitedTime;

    /**
    * 当前状态
            0 草稿
            1 提交（待审核）
            2 审核失败
            3 人工审核
            4 人工审核通过
            8 审核通过（待发布）
            9 已发布
    */
    private Integer status;

    /**
    * 定时发布时间，不定时则为空
    */
    private Date publishTime;

    /**
    * 拒绝理由
    */
    private String reason;

    /**
    * 发布库文章ID
    */
    private Long articleId;

    /**
    * //图片用逗号分隔
    */
    private String images;

    private Integer enable;


}
