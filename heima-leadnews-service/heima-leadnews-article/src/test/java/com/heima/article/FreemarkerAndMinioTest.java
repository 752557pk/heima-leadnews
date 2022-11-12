package com.heima.article;

import com.alibaba.fastjson.JSON;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.minio.service.FileStorageService;
import com.heima.minio.service.impl.MinIOFileStorageService;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleContent;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class FreemarkerAndMinioTest {

    @Autowired
    Configuration configuration;
    @Autowired
    ApArticleContentMapper apArticleContentMapper;
    @Autowired
    ApArticleMapper articleMapper;
    @Autowired
    FileStorageService fileStorageService;

    @Test
    public void test() throws IOException, TemplateException {
        // 获取模板，并传入数据，生成静态页面保存到d盘
        Template template = configuration.getTemplate("article.ftl");
        // 封装数据- 文章内容，并内容转换成集合
        ApArticleContent apArticleContent = apArticleContentMapper.selectById("1302865476799447041");
        String content = apArticleContent.getContent();
        List<HashMap> list = JSON.parseArray(content, HashMap.class);
        Map<String,Object> data = new HashMap<>();
        data.put("content",list);
        // 生成结果
        StringWriter stringWriter = new StringWriter();
        template.process(data,stringWriter);

        // 上传minio
        ByteArrayInputStream inputStream = new ByteArrayInputStream(stringWriter.toString().getBytes());
        String path = fileStorageService.uploadHtmlFile("", apArticleContent.getArticleId() + ".html", inputStream);
        System.out.println(path);
        // 回填地址数据
        ApArticle apArticle = new ApArticle();
        apArticle.setStaticUrl(path);
        apArticle.setId(apArticleContent.getArticleId());
        articleMapper.updateById(apArticle);
    }

}
