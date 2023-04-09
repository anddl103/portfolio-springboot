package com.hybe.larva.entity.album_working;

import com.hybe.larva.enums.AlbumState;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AlbumWorkingRepo extends MongoRepository<AlbumWorking, String> {

    List<AlbumWorking> findAllByState(AlbumState state);

}
