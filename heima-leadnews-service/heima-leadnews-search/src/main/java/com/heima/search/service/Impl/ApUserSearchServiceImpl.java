package com.heima.search.service.Impl;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.searhc.dto.HistorySearchDto;
import com.heima.model.searhc.pojo.ApUserSearch;
import com.heima.search.dao.mongodb.ApUserSearchDao;
import com.heima.search.service.ApUserSearchService;
import com.heima.utils.common.UserThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ApUserSearchServiceImpl implements ApUserSearchService {

    @Autowired
    ApUserSearchDao apUserSearchDao;

    //添加一个搜索词
    @Async
    @Override
    public void addApUserSearch( Long userId,String keyword) {

        //一个用户最多有10条关键字，多余的则删除最久的历史记录
        // 先拿出用户所有的关键字集合
        List<ApUserSearch> userSearchList = apUserSearchDao.findByUserId(userId);

        //查询添加的关键是否已经在集合中:
        List<ApUserSearch> temp = userSearchList.stream()
                .filter(item -> item.getKeyword().equals(keyword))
                .collect(Collectors.toList());
        // 在集合中:更新对应数据的时间
        if (temp.size() > 0) {

            temp.forEach(apUserSearch -> {
                apUserSearch.setCreatedTime(new Date());
                apUserSearchDao.save(apUserSearch);
            });
        } else {
            //不在集合:添加到集合中，整个集合倒排序
            ApUserSearch apUserSearch = new ApUserSearch();
            apUserSearch.setUserId(userId);
            apUserSearch.setKeyword(keyword);
            apUserSearch.setCreatedTime(new Date());
            apUserSearchDao.save(apUserSearch);
            userSearchList.add(apUserSearch);
        }

        //判断集合是否有10条记录
        if (temp.size() > 10) {
            temp.sort((o1, o2) -> (int) (o1.getCreatedTime().getTime() - o2.getCreatedTime().getTime()));
            ApUserSearch apUserSearch = temp.get(temp.size() - 1);
            apUserSearchDao.delete(apUserSearch);

        }

        //如果有则删除集合中的最后一条记录


    }

    @Override
    public ResponseResult findByUserId() {
        Long userId = UserThreadLocalUtil.get();
        List<ApUserSearch> byUserId = apUserSearchDao.findByUserId(userId);

        return ResponseResult.okResult(byUserId);
    }

    @Override
    public ResponseResult del(HistorySearchDto historySearchDto) {
        Optional<ApUserSearch> byId = apUserSearchDao.findById(historySearchDto.getId());
        if (!byId.isPresent()) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        } else {
            Long userId = UserThreadLocalUtil.get();
            ApUserSearch apUserSearch = byId.get();
            if (apUserSearch.getUserId().equals(userId)) {
                return ResponseResult.okResult("");
            } else {
                return ResponseResult.errorResult(AppHttpCodeEnum.NO_OPERATOR_AUTH);
            }
        }
    }
}
