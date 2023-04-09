package com.hybe.larva.entity.nfc_card;


import com.hybe.larva.entity.banner.Banner;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NfcCardRepo extends MongoRepository<NfcCard, String> {

    List<NfcCard> findAllByAlbumId(String albumId);
}
