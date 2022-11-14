package com.heima.mongo.dao;

import com.heima.mongo.pojo.ApUserSearch;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApUserSearchDao extends MongoRepository<ApUserSearch,String> {
}
