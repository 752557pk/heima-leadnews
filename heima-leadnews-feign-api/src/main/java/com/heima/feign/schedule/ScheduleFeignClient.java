package com.heima.feign.schedule;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.schedule.pojos.Taskinfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("heima-leadnews-schedule")
public interface ScheduleFeignClient {

    @PostMapping("/api/v1/taskinfo/addTask")
    public ResponseResult addTask(@RequestBody Taskinfo taskinfo);

    @PostMapping("/api/v1/taskinfo/deleteTask/{id}")
    public ResponseResult deleteTask(@PathVariable("id") Long taskId);

    @PostMapping("/api/v1/taskinfo/getTask/{type}/{priority}")
    public ResponseResult getTask(@PathVariable("type") Integer type,@PathVariable("priority") Integer priority);
}
