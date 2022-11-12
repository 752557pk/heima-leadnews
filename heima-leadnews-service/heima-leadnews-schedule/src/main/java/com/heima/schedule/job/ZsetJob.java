package com.heima.schedule.job;

import com.heima.schedule.service.impl.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ZsetJob {

    @Autowired
    CacheService cacheService;

    /**
     * 每秒钟执行
     * 扫描zset集合中时间到的任务，并移动list中
     */
    @Scheduled(cron = "*/1 * * * * ?")
    public void zsetJob(){
        String zsetJob = cacheService.tryLock("zset_job", 30);
        if(zsetJob!=null) {
            // 找到redis中所有的zset
            Set<String> scans = cacheService.scan("TASK_ZSET_*");
            // 循环zset,把每个zset集合中时间到的任务，并移动list中
//            System.out.println("移动01" + "\t:" + (System.currentTimeMillis() / 1000));
            for (String zsetKey : scans) {
                // 区时间到期的任务
                long time = System.currentTimeMillis();
                Set<String> tasks = cacheService.zRangeByScore(zsetKey, 0, time);
                if (tasks != null && !tasks.isEmpty()) {
                    // 通过管道技术进行数据从zset中移动到list
                    String listKey = zsetKey.replace("TASK_ZSET_", "TASK_LIST_");
                    cacheService.refreshWithPipeline(zsetKey, listKey, tasks);
                }
            }
        }
    }

}
