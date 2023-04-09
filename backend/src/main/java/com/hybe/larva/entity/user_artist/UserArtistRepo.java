package com.hybe.larva.entity.user_artist;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserArtistRepo extends MongoRepository<UserArtist, String> {

//    UserArtist findByArtistIdAndUid(String cardId, String userUid);

    UserArtist findByUidAndArtistId(String uid, String artistId);

    Optional<UserArtist> findByIdAndUid(String id, String uid);
}
