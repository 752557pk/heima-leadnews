package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.minio.service.FileStorageService;
import com.heima.minio.service.impl.MinIOFileStorageService;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import com.heima.utils.common.AppJwtUtil;
import com.heima.utils.common.UserThreadLocalUtil;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.service.WmMaterialService;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
* <p>
* wm_material Service 接口实现
* </p>
*
* @author lenovo
* @since 2022-10-31 14:39:51
*/
@Service
@Transactional
@Slf4j
public class WmMaterialServiceImpl extends ServiceImpl<WmMaterialMapper, WmMaterial> implements WmMaterialService {

    @Autowired
    private WmMaterialMapper wmMaterialMapper;
    @Autowired
    FileStorageService fileStorageService;

    @Override
    public ResponseResult list(WmMaterialDto wmMaterialDto) {
        // 参数校验
        wmMaterialDto.checkParam();
        // 业务实现
        LambdaQueryWrapper<WmMaterial> wrapper = new LambdaQueryWrapper<>();
        // 只能查看当前登录用户的素材
        wrapper.eq(WmMaterial::getUserId, UserThreadLocalUtil.get());
        // 如果是收藏则拼接收藏条件
        if(1== wmMaterialDto.getIsCollection()){
            wrapper.eq(WmMaterial::getIsCollection,wmMaterialDto.getIsCollection());
        }
        IPage<WmMaterial> page = Page.of(wmMaterialDto.getPage(),wmMaterialDto.getSize());
        wmMaterialMapper.selectPage(page, wrapper);
        // 结果封装
        PageResponseResult responseResult = new PageResponseResult(wmMaterialDto.getPage(), wmMaterialDto.getSize(), page.getTotal());
        responseResult.setData(page.getRecords());
        return responseResult;
    }

    @Override
    public ResponseResult uploadPicture(MultipartFile multipartFile) throws IOException {
        // 参数校验
        if(multipartFile==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        // 业务实现
        //  先上传minio   d/f/1.jpg
        String filename = multipartFile.getOriginalFilename();// 获取上传文件的文件名称
        filename = UUID.randomUUID().toString()+filename.substring(filename.lastIndexOf("."));
        String path = fileStorageService.uploadImgFile("", filename, multipartFile.getInputStream());
        // 再保存数据到数据
        // 业务实现
        WmMaterial wmMaterial =new WmMaterial();
        wmMaterial.setUrl(path);
        wmMaterial.setType(0);
        wmMaterial.setIsCollection(0);
        wmMaterial.setCreatedTime(LocalDateTime.now());
        wmMaterial.setUserId(UserThreadLocalUtil.get());
        wmMaterialMapper.insert(wmMaterial);
        // 结果封装
        return ResponseResult.okResult(wmMaterial);
    }
}