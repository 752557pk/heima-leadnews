package com.heima.freemarker;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class FreemarkerTest {

    @Autowired
    Configuration configuration;

    @Test
    public void test() throws IOException, TemplateException {
        // 获取模板，并传入数据，生成静态页面保存到d盘
        Template template = configuration.getTemplate("test.ftl");
        // 封装数据
        Map<String, Object> data = new HashMap<>();
        data.put("name1","圣诞节");

        List<Student> students = new ArrayList<>();

        Student student1 = new Student();
        student1.setAge(18);
        student1.setName("张小三");
        student1.setBirthday(new Date());
        student1.setMoney(100F);

        students.add(student1);


        Student student2 = new Student();
        student2.setAge(21);
        student2.setName("李小四");
        student2.setBirthday(new Date());
        student2.setMoney(100F);

        students.add(student2);

        Student student3 = new Student();
        student3.setAge(21);
        student3.setName("王小五");
        student3.setBirthday(new Date());
        student3.setMoney(100F);

        students.add(student3);
        data.put("students",students);
        // 生成结果
        FileWriter fileWriter = new FileWriter("d:/heima42.html");
        template.process(data,fileWriter);
    }

}
