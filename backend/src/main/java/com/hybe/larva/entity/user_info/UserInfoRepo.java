package com.hybe.larva.entity.user_info;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserInfoRepo extends MongoRepository<UserInfo, String> {

    Optional<UserInfo> findByUid(String userUid);
}
