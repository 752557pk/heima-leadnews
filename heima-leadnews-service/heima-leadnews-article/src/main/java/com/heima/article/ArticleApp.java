package com.heima.article;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.heima.article.mapper")
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class ArticleApp {

    public static void main(String[] args) {
        SpringApplication.run(ArticleApp.class,args);
        System.out.println("ArticleApp 启动成功");
    }
}
