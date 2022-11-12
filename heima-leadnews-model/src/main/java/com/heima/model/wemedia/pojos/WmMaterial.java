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

/**
* <p>
* wm_material 实体类
* </p>
*
* @author lenovo
* @since 2022-10-31 14:38:51
*/
@Getter
@Setter
@TableName("wm_material")
public class WmMaterial implements Serializable {
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
    * 图片地址
    */
    private String url;

    /**
    * 素材类型
            0 图片
            1 视频
    */
    private Integer type;

    /**
    * 是否收藏
    */
    private Integer isCollection;

    /**
    * 创建时间
    */
    private LocalDateTime createdTime;


}
