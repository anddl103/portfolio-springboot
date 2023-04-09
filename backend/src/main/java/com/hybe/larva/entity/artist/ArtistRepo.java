package com.hybe.larva.entity.artist;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ArtistRepo extends MongoRepository<Artist, String> {
}
