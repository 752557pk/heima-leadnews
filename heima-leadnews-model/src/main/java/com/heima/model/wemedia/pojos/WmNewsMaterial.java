package com.heima.model.wemedia.pojos;
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
* wm_news_material 实体类
* </p>
*
* @author lenovo
* @since 2022-11-01 10:12:20
*/
@Getter
@Setter
@TableName("wm_news_material")
public class WmNewsMaterial implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
    * 主键
    */
    @TableId
    private Long id;

    /**
    * 素材ID
    */
    private Long materialId;

    /**
    * 图文ID
    */
    private Long newsId;

    /**
    * 引用排序
    */
    private Integer ord;

    /**
    * 引用类型
            0 内容引用
            1 主图引用
    */
    private Integer type;


}
