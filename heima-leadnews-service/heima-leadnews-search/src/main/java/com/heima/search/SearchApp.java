package com.heima.search;

import com.baomidou.mybatisplus.autoconfigure.IdentifierGeneratorAutoConfiguration;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude ={ DataSourceAutoConfiguration.class , MybatisPlusAutoConfiguration.class , IdentifierGeneratorAutoConfiguration.class})
public class SearchApp {
    public static void main(String[] args) {
        SpringApplication.run(SearchApp.class);
        System.out.println("SearchApp 启动成功");
    }
}
