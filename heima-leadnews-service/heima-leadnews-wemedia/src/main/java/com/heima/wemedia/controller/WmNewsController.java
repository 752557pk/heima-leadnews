package com.heima.wemedia.controller;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.DownOrUpDto;
import com.heima.model.wemedia.dtos.NewsListDto;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.pojos.WmNews;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;

import com.heima.wemedia.service.WmNewsService;

/**
* <p>
* wm_news 控制器实现
* </p>
*
* @author lenovo
* @since 2022-10-31 16:57:37
*/
@RestController
@RequestMapping("/api/v1/news")
public class WmNewsController {

    @Autowired
    private WmNewsService wmNewsService;

    @PostMapping("/list")
    public ResponseResult list(@RequestBody NewsListDto newsListDto) {
        return wmNewsService.list(newsListDto);
    }


    @PostMapping("/submit")
    public ResponseResult submitNews(@RequestBody WmNewsDto dto){
        return wmNewsService.addOrUpdateNews(dto);
    }

    @PostMapping("/down_or_up")
    public ResponseResult downOrUp(@RequestBody DownOrUpDto dto){
        return wmNewsService.downOrUp(dto);
    }

    @GetMapping("/one/{newId}")
    public ResponseResult submitNews(@PathVariable("newId") Long newId){
        return ResponseResult.okResult(wmNewsService.getById(newId));
    }

}
