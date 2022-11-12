package com.heima.schedule.job;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.heima.model.schedule.pojos.Taskinfo;
import com.heima.schedule.service.TaskinfoService;
import com.heima.schedule.service.impl.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

@Component
public class ScanDbJob {

    @Autowired
    TaskinfoService taskinfoService;
    @Autowired
    CacheService cacheService;

    /**
     * 每5分钟钟执行
     * 扫描数据库中的任务，并移动list/zset中
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void scanDbJob(){
        String scanDbJob = cacheService.tryLock("scan_db_job", 30);
        if(scanDbJob!=null) {
            // 重新插入数据之前，情况redis中的数据
            clearRedis();
            // 从数据库中查询未来5分钟要执行的任务——执行时间小于未来5分钟
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, 5);//在当前时间上增加5分钟
            LambdaQueryWrapper<Taskinfo> wrapper = new LambdaQueryWrapper();
            wrapper.le(Taskinfo::getExecuteTime, calendar.getTime());
            List<Taskinfo> list = taskinfoService.list(wrapper);
            System.out.println("开始执行拉~~~~~~~" + list.size());
            // 判断放入list或zset
            for (Taskinfo taskinfo : list) {
                taskinfoService.cacheToRedis(taskinfo);
            }
        }
    }

    public void clearRedis(){
        Set<String> zsets = cacheService.scan("TASK_ZSET_*");
        Set<String> zlists = cacheService.scan("TASK_LIST_*");
        zsets.addAll(zlists);
        cacheService.delete(zsets);
    }

}
