package com.heima.schedule.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.schedule.pojos.Taskinfo;
import com.heima.model.schedule.pojos.TaskinfoLogs;
import com.heima.schedule.mapper.TaskinfoLogsMapper;
import com.heima.schedule.mapper.TaskinfoMapper;
import com.heima.schedule.service.TaskinfoService;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Date;

/**
* <p>
* taskinfo Service 接口实现
* </p>
*
* @author lenovo
* @since 2022-11-08 11:30:54
*/
@Service
@Transactional
@Slf4j
public class TaskinfoServiceImpl extends ServiceImpl<TaskinfoMapper, Taskinfo> implements TaskinfoService {

    @Autowired
    private TaskinfoMapper taskinfoMapper;
    @Autowired
    private TaskinfoLogsMapper taskinfoLogsMapper;
    @Autowired
    private CacheService cacheService;

    @Override
    public ResponseResult addTask(Taskinfo taskinfo) {
        // 插入数据库,并且插入日志
        taskinfoMapper.insert(taskinfo);
        TaskinfoLogs taskinfoLogs = new TaskinfoLogs();
        BeanUtils.copyProperties(taskinfo,taskinfoLogs);
        taskinfoLogs.setVersion(1);
        taskinfoLogs.setStatus(0);
        taskinfoLogsMapper.insert(taskinfoLogs);
        cacheToRedis(taskinfo);
        return ResponseResult.okResult(taskinfo);
    }

    public void cacheToRedis(Taskinfo taskinfo){
        // 判断时间放置redis
        Date executeTime = taskinfo.getExecuteTime();
        Long now = System.currentTimeMillis();
        Long five = now+ 5*60*1000;
        if(executeTime.getTime()<=now){
            // 时间到了
            String key = listName(taskinfo.getTaskType(),taskinfo.getPriority());
            cacheService.lLeftPush(key, JSON.toJSONString(taskinfo));
        }else if(executeTime.getTime()<=five){
            // 未来5分钟执行的时间
            String key = zsetName(taskinfo.getTaskType(),taskinfo.getPriority());
            cacheService.zAdd(key, JSON.toJSONString(taskinfo),executeTime.getTime());
        }
    }

    @Override
    public ResponseResult deleteTask(Long taskId) {
        // 删除数据库中任务
        Taskinfo taskinfo = taskinfoMapper.selectById(taskId);
        taskinfoMapper.deleteById(taskId);
        // 更新任务日志
        TaskinfoLogs taskinfoLogs = new TaskinfoLogs();
        taskinfoLogs.setStatus(2);
        taskinfoLogs.setTaskId(taskId);
        taskinfoLogsMapper.updateById(taskinfoLogs);
        // 删除redis的数据
        String json = JSON.toJSONString(taskinfo);
        cacheService.lRemove(listName(taskinfo.getTaskType(),taskinfo.getPriority()),0,json);
        cacheService.zRemove(zsetName(taskinfo.getTaskType(),taskinfo.getPriority()),json);
        return ResponseResult.okResult(taskId);
    }

    @Override
    public ResponseResult getTask(Integer type, Integer priority) {
        String key = listName(type, priority);
        String json = cacheService.lLeftPop(key);
        if(StringUtils.isNotEmpty(json)){
            // json
            Taskinfo taskinfo = JSON.parseObject(json, Taskinfo.class);
            // 把任务的日志更新，并删除数据库的任务数据
            taskinfoMapper.deleteById(taskinfo.getTaskId());
            // 更新任务日志
            TaskinfoLogs taskinfoLogs = new TaskinfoLogs();
            taskinfoLogs.setStatus(1);
            taskinfoLogs.setTaskId(taskinfo.getTaskId());
            taskinfoLogsMapper.updateById(taskinfoLogs);
            return ResponseResult.okResult(taskinfo);
        }
        return ResponseResult.okResult(null);
    }

    /**
     * 生成zset名称
     * @param type
     * @param priority
     * @return
     */
    public String zsetName(Integer type,Integer priority){
        return String.format("TASK_ZSET_%s_%s",type,priority);
    }

    /**
     * 生成list名称
     * @param type
     * @param priority
     * @return
     */
    public String listName(Integer type,Integer priority){
        return String.format("TASK_LIST_%s_%s",type,priority);
    }
}