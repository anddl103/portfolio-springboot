package com.hybe.larva.entity.question;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface QuestionRepo extends MongoRepository<Question, String> {

    Optional<Question> findByIdAndCreatedBy(String id, String uid);

}
