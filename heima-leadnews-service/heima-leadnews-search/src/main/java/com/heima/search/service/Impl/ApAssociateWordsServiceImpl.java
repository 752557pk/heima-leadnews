package com.heima.search.service.Impl;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.searhc.dto.UserSearchDto;
import com.heima.model.searhc.pojo.ApAssociateWords;
import com.heima.search.service.ApAssociateWordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApAssociateWordsServiceImpl implements ApAssociateWordsService {
    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public ResponseResult search(UserSearchDto userSearchDto) {

        Criteria associateWords = Criteria.where("associateWords")
                .regex(".*?\\" + userSearchDto.getSearchWords() + ".*");

        Query query = Query.query(associateWords);

        List<ApAssociateWords> associateWordsList = mongoTemplate.find(query, ApAssociateWords.class);
        return ResponseResult.okResult(associateWordsList);

        //Criteria associateWords = Criteria.where("associateWords").regex(".*?\\" + userSearchDto.getSearchWords() + ".*");
        //mongoTemplate.find(Query.query(associateWords),ApAssociateWords.class);
    }
}
