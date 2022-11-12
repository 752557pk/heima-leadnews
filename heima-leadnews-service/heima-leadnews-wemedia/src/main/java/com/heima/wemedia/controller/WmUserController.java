package com.heima.wemedia.controller;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;

import com.heima.wemedia.service.WmUserService;

/**
* <p>
* wm_user 控制器实现
* </p>
*
* @author lenovo
* @since 2022-10-31 11:38:00
*/
@RestController
public class WmUserController {

    @Autowired
    private WmUserService wmUserService;

    @PostMapping("/login/in")
    public ResponseResult login(@RequestBody WmUser wmUser) {
        return wmUserService.login(wmUser);
    }
}
