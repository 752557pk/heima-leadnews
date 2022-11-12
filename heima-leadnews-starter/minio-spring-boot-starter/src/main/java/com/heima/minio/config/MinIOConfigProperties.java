package com.heima.minio.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "minio")
public class MinIOConfigProperties {

    String bucket;
    String readPath;//访问文件的服务器地址
    String endpoint;// 文件上传地址

    String username;
    String password;

}
