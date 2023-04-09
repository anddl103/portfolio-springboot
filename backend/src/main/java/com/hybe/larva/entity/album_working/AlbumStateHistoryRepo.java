package com.hybe.larva.entity.album_working;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AlbumStateHistoryRepo extends MongoRepository<AlbumStateHistory, String> {

    List<AlbumStateHistory> findByAlbumId(String albumId);

}
