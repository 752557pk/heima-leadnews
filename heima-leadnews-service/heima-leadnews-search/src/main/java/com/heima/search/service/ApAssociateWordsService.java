package com.heima.search.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.searhc.dto.UserSearchDto;

public interface ApAssociateWordsService {
    ResponseResult search(UserSearchDto userSearchDto);
}
