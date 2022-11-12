package com.heima.model.schedule.pojos;
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
* taskinfo 实体类
* </p>
*
* @author lenovo
* @since 2022-11-08 11:28:20
*/
@Getter
@Setter
@TableName("taskinfo")
public class Taskinfo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 任务id
     */
    @TableId
    private Long taskId;

    /**
     * 执行时间
     */
    private Date executeTime;

    /**
     * 参数
     */
    private byte[] parameters;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 任务类型
     */
    private Integer taskType;

}
