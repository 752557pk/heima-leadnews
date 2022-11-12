package com.heima.wemedia.controller;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.Serializable;

import com.heima.wemedia.service.WmMaterialService;
import org.springframework.web.multipart.MultipartFile;

/**
* <p>
* wm_material 控制器实现
* </p>
*
* @author lenovo
* @since 2022-10-31 14:40:07
*/
@RestController
@RequestMapping("/api/v1/material")
public class WmMaterialController {

    @Autowired
    private WmMaterialService wmMaterialService;

    @PostMapping("/list")
    public ResponseResult list(@RequestBody WmMaterialDto wmMaterialDto) {
       return wmMaterialService.list(wmMaterialDto);
    }

    @PostMapping("/upload_picture")
    public ResponseResult uploadPicture(MultipartFile multipartFile) throws IOException {
        return wmMaterialService.uploadPicture(multipartFile);
    }

}
