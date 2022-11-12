package com.heima.model.article.dtos;

import lombok.Data;

import java.util.Date;

@Data
public class ArticleListDto {
    Integer size;
    String tag;
    Date maxBehotTime;
    Date minBehotTime;
}
