package com.heima.search.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Data
@Document(indexName = "leadnews")
public class Article {
    @Id
    Long id;

    Date publishTime;

    @Field(type = FieldType.Keyword, index = false)
    Integer layout;

    String images;

    @Field(type = FieldType.Keyword, index = false)
    String staticUrl;

    Long authorId;

    @Field(type = FieldType.Keyword)
    String authorName;

    @Field(type = FieldType.Text,analyzer = "ik_smart")
    String title;

    @Field(type = FieldType.Text,analyzer = "ik_smart")
    String content;


}
