package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.utils.common.AppJwtUtil;
import com.heima.utils.common.MD5Utils;
import com.heima.wemedia.mapper.WmUserMapper;
import com.heima.wemedia.service.WmUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
* <p>
* wm_user Service 接口实现
* </p>
*
* @author lenovo
* @since 2022-10-31 11:37:39
*/
@Service
@Transactional
@Slf4j
public class WmUserServiceImpl extends ServiceImpl<WmUserMapper, WmUser> implements WmUserService {

    @Autowired
    private WmUserMapper wmUserMapper;

    @Override
    public ResponseResult login(WmUser wmUser) {
        // 判断登录的方式
        if(StringUtils.isNotBlank(wmUser.getName())&&StringUtils.isNotBlank(wmUser.getPassword())){//账户登录
            LambdaQueryWrapper<WmUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(WmUser::getName,wmUser.getName());
            WmUser user = wmUserMapper.selectOne(wrapper);
            if(user==null){
                return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
            }
            // 生成盐密码
            String salt = user.getSalt();
            String tempPass = wmUser.getPassword()+salt;
            tempPass = MD5Utils.encode(tempPass);
            // 比对密码
            if(tempPass.equals(user.getPassword())){//登录成功
                String token = AppJwtUtil.getToken(user.getId());//生成jwt字符串
                Map<String,Object> map = new HashMap<>();
                map.put("token",token);
                map.put("user",user);
                // apUser 不能返回盐和密码
                user.setPassword("");
                user.setSalt("");
                return ResponseResult.okResult(map);
            }else{
                return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
            }
        }else{// 游客登录
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE);
        }
    }
}