package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
* <p>
* wm_material Service 接口
* </p>
*
* @author lenovo
* @since 2022-10-31 14:39:40
*/
public interface WmMaterialService extends IService<WmMaterial> {

    ResponseResult list(WmMaterialDto wmMaterialDto);

    ResponseResult uploadPicture(MultipartFile multipartFile) throws IOException;
}