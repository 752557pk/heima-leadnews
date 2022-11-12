package com.heima.model.wemedia.dtos;

import com.heima.model.common.dtos.PageRequestDto;
import lombok.Data;

import java.util.Date;

@Data
public class NewsListDto extends PageRequestDto {

    Date beginPubDate;
    Integer channelId;
    Date endPubDate;
    String keyword;
    Integer status;

}
