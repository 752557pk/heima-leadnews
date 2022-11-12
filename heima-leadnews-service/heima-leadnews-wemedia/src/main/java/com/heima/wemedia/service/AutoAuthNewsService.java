package com.heima.wemedia.service;


import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmNews;

import java.io.UnsupportedEncodingException;

public interface AutoAuthNewsService {

    /**
     * 自动审核谋篇文章
     *
     * @param articleId
     * @return
     */
    public ResponseResult autoAuthNews(Long articleId) throws Exception;

    public ResponseResult saveToApp(WmNews wmNews);
}
