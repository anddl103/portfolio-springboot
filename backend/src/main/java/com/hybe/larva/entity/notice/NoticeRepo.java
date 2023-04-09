package com.hybe.larva.entity.notice;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface NoticeRepo extends MongoRepository<Notice, String> {
}
