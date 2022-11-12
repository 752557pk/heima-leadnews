package com.heima.aliyun.service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "aliyun")
public class AliyunProperties {

    String accessKeyId;
    String accessKeySecret;

}
