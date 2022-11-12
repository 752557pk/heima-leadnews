package com.heima.feign;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@EnableFeignClients("com.heima.feign")
@Configuration
public class FeignApiAutoConfiguration {

}
