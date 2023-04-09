package com.hybe.larva.entity.view_user_card;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ViewUserCardRepo extends MongoRepository<ViewUserCard, String> {

    ViewUserCard findByCardIdAndUid(String cardId, String userUid);
}
