package com.heima.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.dtos.LoginDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.user.mapper.ApUserMapper;
import com.heima.user.service.ApUserService;
import com.heima.utils.common.AppJwtUtil;
import com.heima.utils.common.MD5Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
* <p>
* ap_user Service 接口实现
* </p>
*
* @author lenovo
* @since 2022-10-28 14:58:09
*/
@Service
@Transactional
@Slf4j
public class ApUserServiceImpl extends ServiceImpl<ApUserMapper, ApUser> implements ApUserService {

    @Autowired
    private ApUserMapper apUserMapper;

    @Override
    public ResponseResult login(LoginDto loginDto) {
        // 判断登录的方式
        if(StringUtils.isNotBlank(loginDto.getPhone())&&StringUtils.isNotBlank(loginDto.getPassword())){//账户登录
            // 查询用户
//            LambdaQueryWrapper<ApUser> wrapper = Wrappers.<ApUser>lambdaQuery().eq(ApUser::getPhone, loginDto.getPhone());
//            LambdaQueryWrapper<ApUser> wrapper = Wrappers.<ApUser>lambdaQuery();
            LambdaQueryWrapper<ApUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ApUser::getPhone,loginDto.getPhone());
            ApUser apUser = apUserMapper.selectOne(wrapper);
            if(apUser==null){
                return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
            }
            // 生成盐密码
            String salt = apUser.getSalt();
            String tempPass = loginDto.getPassword()+salt;
            tempPass = MD5Utils.encode(tempPass);
            // 比对密码
            if(tempPass.equals(apUser.getPassword())){//登录成功
                String token = AppJwtUtil.getToken(apUser.getId());//生成jwt字符串
                Map<String,Object> map = new HashMap<>();
                map.put("token",token);
                map.put("user",apUser);
                // apUser 不能返回盐和密码
                apUser.setPassword("");
                apUser.setSalt("");
                return ResponseResult.okResult(map);
            }else{
                return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
            }
        }else{// 游客登录
            String token = AppJwtUtil.getToken(0L);//生成jwt字符串
            Map<String,Object> map = new HashMap<>();
            map.put("token",token);
            return ResponseResult.okResult(map);
        }
    }
}