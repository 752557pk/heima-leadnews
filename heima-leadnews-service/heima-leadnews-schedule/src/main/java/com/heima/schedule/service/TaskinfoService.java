package com.heima.schedule.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.schedule.pojos.Taskinfo;

/**
* <p>
* taskinfo Service 接口
* </p>
*
* @author lenovo
* @since 2022-11-08 11:30:28
*/
public interface TaskinfoService extends IService<Taskinfo> {

    /**
     * 添加一个任务
     * @param taskinfo
     * @return
     */
    public ResponseResult addTask(Taskinfo taskinfo);

    /**
     * 删除一个任务
     */
    public ResponseResult deleteTask(Long taskId);

    /**
     * 类型和优先级
     * @param type
     * @param priority
     * @return
     */
    public ResponseResult getTask(Integer type,Integer priority);

    /**
     * 缓存任务
     * @param taskinfo
     */
    public void cacheToRedis(Taskinfo taskinfo);
}