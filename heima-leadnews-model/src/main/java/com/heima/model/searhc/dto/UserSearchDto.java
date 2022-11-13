package com.heima.model.searhc.dto;

import lombok.Data;

import java.util.Date;


@Data
public class UserSearchDto {

    /**
    * 搜索关键字
    */
    String searchWords;

    /**
    * 分页条数
    */
    Integer pageSize;
    /**
    * 最小时间
    */
    Date minBehotTime;

    public void checkParam() {
        if (this.pageSize == null || this.pageSize < 0 || this.pageSize > 100) {
            setPageSize(10);
        }
    }

}
