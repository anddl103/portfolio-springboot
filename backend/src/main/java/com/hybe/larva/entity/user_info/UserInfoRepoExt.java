package com.hybe.larva.entity.user_info;

import com.hybe.larva.dto.common.CommonUser;
import com.hybe.larva.entity.artist.Artist;
import com.hybe.larva.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserInfoRepoExt {

    private final UserInfoRepo repo;
    private final MongoOperations mongo;

    public UserInfo insert(UserInfo userInfo) {  return repo.insert(userInfo);  }

    public void deleteById(String id) {  repo.deleteById(id);  }

    public void delete(UserInfo userInfo) {  repo.delete(userInfo);  }

    public UserInfo save(UserInfo userInfo) {  return repo.save(userInfo);  }

    public UserInfo findById(String id) {
        return repo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Cannot find user: id=" + id)
        );
    }

    public Page<UserInfo> search(Criteria criteria, Pageable pageable) {
        Query query = new Query(criteria).with(pageable);
        List<UserInfo> userInfos = mongo.find(query, UserInfo.class);
        return PageableExecutionUtils.getPage(userInfos, pageable,
                () -> mongo.count(new Query(criteria), UserInfo.class)
        );
    }

    public UserInfo findByUid(String id) {
        return repo.findByUid(id).orElseThrow(() ->
                new ResourceNotFoundException("Cannot find user: uid=" + id)
        );
    }

    public UserInfo updateDeviceTokensUid(String deviceToken) {
        Query query = Query.query(Criteria.where(UserInfo.USER_UID).is(CommonUser.getUid())
                .and(UserInfo.DELETED).is(false));
        Update update = new Update().set(UserInfo.DEVICE_TOKENS+"."+deviceToken, true);
        FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);
        return mongo.findAndModify(query, update, options, UserInfo.class);
    }


//    public UserInfo incPoint(String id, int point) {
//        Query query = Query.query(Criteria.where(UserInfo.USER_UID).is(id)
//                .and(UserInfo.DELETED).is(false));
//        Update update = new Update().inc(UserInfo.POINT, point);
//        FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);
//        return mongo.findAndModify(query, update, options, UserInfo.class);
//    }
//
//    public UserInfo decPoint(String id, int point) {
//        Query query = Query.query(Criteria.where(UserInfo.USER_UID).is(id)
//                .and(UserInfo.DELETED).is(false));
//        Update update = new Update().inc(UserInfo.POINT, (point * -1));
//        FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);
//        return mongo.findAndModify(query, update, options, UserInfo.class);
//    }
}
