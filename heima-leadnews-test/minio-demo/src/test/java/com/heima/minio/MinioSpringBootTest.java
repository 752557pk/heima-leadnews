package com.heima.minio;

import com.heima.minio.service.impl.MinIOFileStorageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MinioSpringBootTest {

    @Autowired
    MinIOFileStorageService minIOFileStorageService;

    @Test
    public void test() throws FileNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(new File("d://heima42.html"));
        String path = minIOFileStorageService.uploadHtmlFile("", "a.html", fileInputStream);
        System.out.println(path);
    }

}
