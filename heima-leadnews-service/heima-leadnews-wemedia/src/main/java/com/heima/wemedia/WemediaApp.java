package com.heima.wemedia;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.heima.wemedia.mapper")
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class WemediaApp {
    public static void main(String[] args) {
        SpringApplication.run(WemediaApp.class,args);
    }
}
