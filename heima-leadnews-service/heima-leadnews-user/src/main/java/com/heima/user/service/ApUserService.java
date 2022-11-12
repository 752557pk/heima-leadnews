package com.heima.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.LoginDto;
import com.heima.model.user.pojos.ApUser;

/**
* <p>
* ap_user Service 接口
* </p>
*
* @author lenovo
* @since 2022-10-28 14:57:39
*/
public interface ApUserService extends IService<ApUser> {

    ResponseResult login(LoginDto loginDto);
}