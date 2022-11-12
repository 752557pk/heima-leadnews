package com.heima.wemedia.controller;

import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;

import com.heima.wemedia.service.WmChannelService;

/**
* <p>
* wm_channel 控制器实现
* </p>
*
* @author lenovo
* @since 2022-10-31 16:57:37
*/
@RestController
@RequestMapping("/api/v1/channel")
public class WmChannelController {

    @Autowired
    private WmChannelService wmChannelService;


    @GetMapping("/channels")
    public ResponseResult channels() {
        return ResponseResult.okResult( wmChannelService.list());
    }
}
