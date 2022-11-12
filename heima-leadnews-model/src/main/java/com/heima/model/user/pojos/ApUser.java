package com.heima.model.user.pojos;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
* <p>
* ap_user 实体类
* </p>
*
* @author lenovo
* @since 2022-10-28 14:55:34
*/
@Getter
@Setter
@TableName("ap_user")
public class ApUser implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
    * 主键
    */
    @TableId
    private Long id;

    /**
    * 注册时间
    */
    private LocalDateTime createdTime;

    /**
    * 0 普通用户
            1 自媒体人
            2 大V
    */
    private Integer flag;

    /**
    * 头像
    */
    private String image;

    /**
    * 0 未
            1 是
    */
    private Integer isCertification;

    /**
    * 是否身份认证
    */
    private Integer isIdentityAuthentication;

    /**
    * 用户名
    */
    private String name;

    /**
    * 密码,md5加密
    */
    private String password;

    /**
    * 手机号
    */
    private String phone;

    /**
    * 密码、通信等加密盐
    */
    private String salt;

    /**
    * 0 男
            1 女
            2 未知
    */
    private Integer sex;

    /**
    * 0正常
            1锁定
    */
    private Integer status;


}
