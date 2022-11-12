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
* wm_channel 实体类
* </p>
*
* @author lenovo
* @since 2022-10-31 16:55:54
*/
@Getter
@Setter
@TableName("wm_channel")
public class WmChannel implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;

    /**
    * 频道名称
    */
    private String name;

    /**
    * 频道描述
    */
    private String description;

    /**
    * 是否默认频道
    */
    private Integer isDefault;

    private Integer status;

    /**
    * 默认排序
    */
    private Integer ord;

    /**
    * 创建时间
    */
    private LocalDateTime createdTime;


}
