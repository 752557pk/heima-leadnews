package com.heima;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EsApp {
    public static void main(String[] args) {
        SpringApplication.run(EsApp.class);
        System.out.println("EsApp 启动成功");
    }
}
