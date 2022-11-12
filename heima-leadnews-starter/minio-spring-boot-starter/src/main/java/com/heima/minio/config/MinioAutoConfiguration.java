package com.heima.minio.config;


import com.heima.minio.service.impl.MinIOFileStorageService;
import io.minio.MinioClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MinIOConfigProperties.class)
public class MinioAutoConfiguration {

    @Bean
    public MinioClient minioClient(MinIOConfigProperties minIOConfigProperties){
        MinioClient minioClient = MinioClient.builder()
                .endpoint(minIOConfigProperties.getEndpoint())
                .credentials(minIOConfigProperties.getUsername(),minIOConfigProperties.getPassword()).build();
        return minioClient;
    }


    @Bean
    public MinIOFileStorageService minIOFileStorageService(){
        return new MinIOFileStorageService();
    }

}
