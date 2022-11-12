package com.heima.article.listener;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.heima.article.mapper.ApArticleConfigMapper;
import com.heima.model.article.pojos.ApArticleConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class DownOrUpListener {

    @Autowired
    ApArticleConfigMapper apArticleConfigMapper;

    @KafkaListener(topics = "article_down_or_up")
    public void downOrUp(String message) {
        HashMap hashMap = JSON.parseObject(message, HashMap.class);
        String temp = (String) hashMap.get("articleId");
        Long articleId = Long.valueOf(temp);
        Integer enable = Integer.valueOf((String) hashMap.get("enable"));
        ApArticleConfig apArticleConfig = new ApArticleConfig();
        apArticleConfig.setIsDown(enable == 1 ? 0 : 1);//1是下架 0是上架
        LambdaQueryWrapper<ApArticleConfig> wrapper = new LambdaQueryWrapper();
        wrapper.eq(ApArticleConfig::getArticleId, articleId);
        apArticleConfigMapper.update(apArticleConfig, wrapper);
    }
}
