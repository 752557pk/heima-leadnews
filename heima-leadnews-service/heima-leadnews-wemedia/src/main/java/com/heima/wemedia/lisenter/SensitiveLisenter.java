package com.heima.wemedia.lisenter;

import com.heima.model.wemedia.pojos.WmSensitive;
import com.heima.utils.common.SensitiveWordUtil;
import com.heima.wemedia.mapper.WmSensitiveMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SensitiveLisenter implements ApplicationRunner {

    @Autowired
    WmSensitiveMapper wmSensitiveMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("-================= 初始化敏感词库==============");
        List<WmSensitive> wmSensitives = wmSensitiveMapper.selectList(null);
        List<String> list = wmSensitives.stream().map(WmSensitive::getSensitives).collect(Collectors.toList());
        SensitiveWordUtil.initMap(list);
    }
}
