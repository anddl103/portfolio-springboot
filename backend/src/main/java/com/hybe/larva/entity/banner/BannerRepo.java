package com.hybe.larva.entity.banner;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BannerRepo extends MongoRepository<Banner, String> {

    List<Banner> findAllByAlbumId(String albumId);
}
