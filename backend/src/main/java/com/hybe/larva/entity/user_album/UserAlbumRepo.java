package com.hybe.larva.entity.user_album;


import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserAlbumRepo extends MongoRepository<UserAlbum, String> {

    Optional<UserAlbum> findByIdAndUid(String id, String uid);

    Optional<UserAlbum> findByUidAndAlbumId(String uid, String albumId);
}
