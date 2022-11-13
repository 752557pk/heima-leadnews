package com.heima.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AppGateway {

    public static void main(String[] args) {
        SpringApplication.run(AppGateway.class,args);
        System.out.println("AppGateway 启动成功");
    }

}
