package com.heima.search.dao.mongodb;


import com.heima.model.searhc.pojo.ApUserSearch;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApUserSearchDao extends MongoRepository<ApUserSearch, String> {

    /**
     * 通过用户id查询所有的搜索关键字
     *
     * @param userId
     * @return
     */
    List<ApUserSearch> findByUserId(Long userId);

}
