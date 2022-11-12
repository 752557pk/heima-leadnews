package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmUser;

/**
* <p>
* wm_user Service 接口
* </p>
*
* @author lenovo
* @since 2022-10-31 11:37:22
*/
public interface WmUserService extends IService<WmUser> {

    ResponseResult login(WmUser wmUser);
}