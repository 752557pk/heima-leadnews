package com.heima.schedule;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.heima.schedule.mapper")
@SpringBootApplication
@EnableScheduling
public class ScheduleApp {

    public static void main(String[] args) {
        SpringApplication.run(ScheduleApp.class,args);
        System.out.println("ScheduleApp 启动成功");
    }
}
