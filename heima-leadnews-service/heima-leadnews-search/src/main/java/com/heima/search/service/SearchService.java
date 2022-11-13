package com.heima.search.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.searhc.dto.UserSearchDto;
import org.springframework.stereotype.Service;


public interface SearchService {
    ResponseResult search(UserSearchDto dto);
}
