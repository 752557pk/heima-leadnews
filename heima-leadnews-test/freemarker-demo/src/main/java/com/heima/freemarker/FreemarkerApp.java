package com.heima.freemarker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FreemarkerApp {

    public static void main(String[] args) {
        SpringApplication.run(FreemarkerApp.class,args);
        System.out.println("FreemarkerApp 启动成功");
    }
}
