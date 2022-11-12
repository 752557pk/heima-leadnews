package com.heima.wemedia.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.wemedia.pojos.WmNewsMaterial;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* <p>
* wm_news_material Mapper 接口
* </p>
*
* @author lenovo
* @since 2022-11-01 10:12:45
*/
public interface WmNewsMaterialMapper extends BaseMapper<WmNewsMaterial> {

    public void insertBatch(@Param("list") List<WmNewsMaterial> list);
}