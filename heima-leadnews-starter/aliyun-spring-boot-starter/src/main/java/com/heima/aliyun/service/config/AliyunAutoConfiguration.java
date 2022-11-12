package com.heima.aliyun.service.config;

import com.heima.aliyun.service.AliyunService;
import com.heima.aliyun.service.impl.AliyunServiceImpl;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AliyunProperties.class)
public class AliyunAutoConfiguration {

    @Bean
    public AliyunService aliyunService(){
        return new AliyunServiceImpl();
    }

}
