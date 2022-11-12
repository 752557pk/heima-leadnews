package com.heima.sdk;

import cn.hutool.core.io.FileUtil;
import com.heima.aliyun.service.AliyunService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.UnsupportedEncodingException;

@SpringBootTest
@RunWith(SpringRunner.class)
public class StarterTest {

    @Autowired
    AliyunService aliyunService;

    @Test
    public void testText() throws UnsupportedEncodingException {
        String result = aliyunService.scanText("大麻是毒品");
        System.out.println(result);
    }

    @Test
    public void testImage() throws UnsupportedEncodingException {
        byte[] bytes = FileUtil.readBytes("D:\\123\\bbb.jpg");
        String result = aliyunService.scanImage(bytes);
        System.out.println(result);
    }

}
