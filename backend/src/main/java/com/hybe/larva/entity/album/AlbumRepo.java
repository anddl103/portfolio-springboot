package com.hybe.larva.entity.album;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AlbumRepo extends MongoRepository<Album, String> {

    List<Album> findByArtistId(String artistId);
}
