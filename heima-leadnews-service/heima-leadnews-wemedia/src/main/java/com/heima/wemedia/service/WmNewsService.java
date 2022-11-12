package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.DownOrUpDto;
import com.heima.model.wemedia.dtos.NewsListDto;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.pojos.WmNews;

/**
* <p>
* wm_news Service 接口
* </p>
*
* @author lenovo
* @since 2022-10-31 16:57:11
*/
public interface WmNewsService extends IService<WmNews> {

    ResponseResult list(NewsListDto newsListDto);

    ResponseResult addOrUpdateNews(WmNewsDto dto);

    ResponseResult downOrUp(DownOrUpDto dto);
}