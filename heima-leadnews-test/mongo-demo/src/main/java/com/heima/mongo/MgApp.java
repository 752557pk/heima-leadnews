package com.heima.mongo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MgApp {
    public static void main(String[] args) {
        SpringApplication.run(MgApp.class, args);
        System.out.println("MgApp.class+启动成功");
    }
}
