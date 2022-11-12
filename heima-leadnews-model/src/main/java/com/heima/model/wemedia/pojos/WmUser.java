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
* wm_user 实体类
* </p>
*
* @author lenovo
* @since 2022-10-31 11:35:34
*/
@Getter
@Setter
@TableName("wm_user")
public class WmUser implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
    * 主键
    */
    @TableId
    private Long id;

    private Integer apUserId;

    private Long apAuthorId;

    /**
    * 登录用户名
    */
    private String name;

    /**
    * 登录密码
    */
    private String password;

    /**
    * 盐
    */
    private String salt;

    /**
    * 昵称
    */
    private String nickname;

    /**
    * 头像
    */
    private String image;

    /**
    * 归属地
    */
    private String location;

    /**
    * 手机号
    */
    private String phone;

    /**
    * 状态
            0 暂时不可用
            1 永久不可用
            9 正常可用
    */
    private Integer status;

    /**
    * 邮箱
    */
    private String email;

    /**
    * 账号类型
            0 个人 
            1 企业
            2 子账号
    */
    private Integer type;

    /**
    * 运营评分
    */
    private Integer score;

    /**
    * 最后一次登录时间
    */
    private LocalDateTime loginTime;

    /**
    * 创建时间
    */
    private LocalDateTime createdTime;


}
