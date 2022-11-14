package com.heima.model.searhc.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class SearchArticleVo {
    Long id;

    Date publishTime;

    Integer layout;

    String images;

    String staticUrl;

    Long authorId;

    String authorName;

    String title;

    String content;


}
