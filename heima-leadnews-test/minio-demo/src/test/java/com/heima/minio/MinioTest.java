package com.heima.minio;

import com.alibaba.fastjson.JSON;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MinioTest {

    @Test
    public void upload() throws Exception {
        MinioClient minioClient = MinioClient.builder()
                .endpoint("http://localhost:9000")
                .credentials("minio","minio123").build();

        FileInputStream fileInputStream = new FileInputStream(new File("d://heima42.html"));

        PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                .bucket("heima42")//上传目录,bucket必须先存在
                .object("heima42.html")//设置文件名称;
                .contentType("text/html")//设置上传文件的类型，minio默认会按照此类型进行数据返回
                .stream(fileInputStream,fileInputStream.available(),-1)
                .build();
        ObjectWriteResponse response = minioClient.putObject(putObjectArgs);
        System.out.println(JSON.toJSONString(response));
    }

}
