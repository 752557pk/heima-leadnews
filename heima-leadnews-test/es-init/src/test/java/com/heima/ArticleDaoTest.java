package com.heima;


import com.heima.dao.ArticleDao;
import com.heima.mapper.ApArticleMapper;
import com.heima.pojo.SearchArticleVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ArticleDaoTest {

    @Autowired
    ArticleDao articleDao;

    @Autowired
    ApArticleMapper apArticleMapper;

    @Test
    public void test() {
        //查出数据
        List<SearchArticleVo> searchArticleVos = apArticleMapper.loadArticleList();
        //导入数据到ES
        Iterable<SearchArticleVo> searchArticleVos1 = articleDao.saveAll(searchArticleVos);

    }

}
