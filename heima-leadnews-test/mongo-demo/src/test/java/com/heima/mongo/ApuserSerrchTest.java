package com.heima.mongo;

import com.heima.mongo.dao.ApUserSearchDao;
import com.heima.mongo.pojo.ApUserSearch;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Date;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ApuserSerrchTest {
    @Autowired
    ApUserSearchDao apUserSearchDao;

    @Test
    public void test() {
        ApUserSearch apUserSearch = new ApUserSearch();
        apUserSearch.setUserId(1);
        apUserSearch.setKeyword("测试");
        apUserSearch.setCreatedTime(new Date());
        apUserSearchDao.save(apUserSearch);
    }
}
