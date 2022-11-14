package com.heima.search.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.searhc.dto.HistorySearchDto;

public interface ApUserSearchService {
    public void addApUserSearch( Long userId,String keyword);

    ResponseResult findByUserId();

    ResponseResult del(HistorySearchDto historySearchDto);
}
