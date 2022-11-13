package com.heima.search.controller;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.searhc.dto.UserSearchDto;
import com.heima.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/article/search")
public class ArticleSearchController {

    @Autowired
    SearchService searchService;
    @PostMapping("/search")
    public ResponseResult search(@RequestBody UserSearchDto dto) throws IOException {
        return searchService.search(dto);
    }
}
