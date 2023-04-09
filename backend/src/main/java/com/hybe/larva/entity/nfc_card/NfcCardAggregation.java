package com.hybe.larva.entity.nfc_card;

import com.hybe.larva.entity.album.Album;
import com.hybe.larva.entity.album.AlbumCard;
import com.hybe.larva.entity.artist.Artist;
import com.hybe.larva.entity.common.BaseEntity;
import com.hybe.larva.enums.NfcCardStatus;
import lombok.*;

import java.util.List;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NfcCardAggregation extends BaseEntity {

    private List<String> tags;

    private String artistId;

    private String albumId;

    private String comment;

    private List<String> cardIds;

    private NfcCardStatus status;

    private List<AlbumCard> cards;

    private Album album;

    private Artist artist;

    @Setter
    private int count;
}
