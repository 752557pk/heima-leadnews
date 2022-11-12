package com.heima.freemarker;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Controller
public class FreemarkerController {

    @GetMapping("/test")
    public String test(Model model){
        model.addAttribute("name","张三");
        model.addAttribute("null",null);
        model.addAttribute("point", 102920122);
        Map<String,String> map = new HashMap<>();
        map.put("name","java");
        map.put("age","28");

        model.addAttribute("map",map);
        List<Student> students = new ArrayList<>();

        Student student1 = new Student();
        student1.setAge(18);
        student1.setName("张小三");
        student1.setBirthday(new Date());
        student1.setMoney(100F);

        model.addAttribute("stu",student1);
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

        model.addAttribute("students",students);

        return "test";
    }

}
