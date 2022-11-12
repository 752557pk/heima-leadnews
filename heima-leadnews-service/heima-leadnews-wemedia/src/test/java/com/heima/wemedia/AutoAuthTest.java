package com.heima.wemedia;

import com.alibaba.fastjson.JSON;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.wemedia.service.AutoAuthNewsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class AutoAuthTest {

    @Autowired
    AutoAuthNewsService autoAuthNewsService;

    @Test
    public void test() throws Exception {
        ResponseResult responseResult = autoAuthNewsService.autoAuthNews(6233L);
        System.out.println(JSON.toJSONString(responseResult));
    }

}
