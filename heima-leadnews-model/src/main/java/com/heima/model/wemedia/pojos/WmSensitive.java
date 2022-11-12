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
* wm_sensitive 实体类
* </p>
*
* @author lenovo
* @since 2022-11-08 09:32:26
*/
@Getter
@Setter
@TableName("wm_sensitive")
public class WmSensitive implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
    * 主键
    */
    @TableId
    private Long id;

    /**
    * 创建时间
    */
    private LocalDateTime createdTime;

    /**
    * 敏感词
    */
    private String sensitives;


}
