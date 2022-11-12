package com.heima.schedule.controller;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.schedule.pojos.Taskinfo;
import com.heima.schedule.service.TaskinfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
* <p>
* taskinfo 控制器实现
* </p>
*
* @author lenovo
* @since 2022-11-08 11:56:12
*/
@RestController
@RequestMapping("/api/v1/taskinfo")
public class TaskinfoController {

    @Autowired
    private TaskinfoService taskinfoService;

    @PostMapping("/addTask")
    public ResponseResult addTask(@RequestBody Taskinfo taskinfo) {
        return taskinfoService.addTask(taskinfo);
    }

    @PostMapping("/deleteTask/{id}")
    public ResponseResult deleteTask(@PathVariable("id") Long taskId) {
        return taskinfoService.deleteTask(taskId);
    }

    @PostMapping("/getTask/{type}/{priority}")
    public ResponseResult getTask(@PathVariable("type") Integer type,@PathVariable("priority") Integer priority) {
        return taskinfoService.getTask(type,priority);
    }

}
