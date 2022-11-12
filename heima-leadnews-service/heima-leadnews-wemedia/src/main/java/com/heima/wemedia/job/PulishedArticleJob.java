package com.heima.wemedia.job;

import com.alibaba.fastjson.JSON;
import com.heima.feign.schedule.ScheduleFeignClient;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.schedule.pojos.Taskinfo;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.wemedia.service.AutoAuthNewsService;
import com.heima.wemedia.service.WmNewsService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class PulishedArticleJob {

    @Autowired
    ScheduleFeignClient scheduleFeignClient;
    @Autowired
    WmNewsService wmNewsService;
    @Autowired
    AutoAuthNewsService autoAuthNewsService;

    /**
     * 每秒钟执行,拉取需要发布文章的任务
     */
//    @Scheduled(fixedRate = 1000)
    @Scheduled(cron = "0/1 * * * * ?")
    public void pulishedArticle(){
        ResponseResult task = scheduleFeignClient.getTask(100, 100);
        if(task.getData()!=null){
            LinkedHashMap data = (LinkedHashMap) task.getData();
            System.out.println(System.currentTimeMillis()/1000+"\t:"+data);
            Taskinfo taskinfo = JSON.parseObject(JSON.toJSONString(data),Taskinfo.class);
            WmNews wmNews = wmNewsService.getById(taskinfo.getTaskId());
            autoAuthNewsService.saveToApp(wmNews);//保存到app
        }
    }

}
