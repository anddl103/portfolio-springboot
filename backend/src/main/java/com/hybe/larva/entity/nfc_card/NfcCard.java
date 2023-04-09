package com.hybe.larva.entity.nfc_card;

import com.hybe.larva.dto.nfc_card.NfcCardUpdateRequest;
import com.hybe.larva.entity.common.BaseEntity;
import com.hybe.larva.enums.NfcCardStatus;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Optional;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "nfcCards")
public class NfcCard extends BaseEntity {

    public static final String NFC_UID = "nfcUid";
    public static final String TAGS = "tags";
    public static final String COMMENT = "comment";
    public static final String CARD_IDS = "cardIds";
    public static final String ALBUM_ID = "albumId";
    public static final String ARTIST_ID = "artistId";

    // Aggregation 사용
    public static final String CARD_ID = "$cardIds";
    public static final String CARD_FOREIGN_FIELD = "_id";
    public static final String CARD_AS = "cards";

    // Aggregation 사용
//    public static final String ALBUM__ID = "$albumId";
    public static final String ALBUM_FROM_COLLECTION = "albums";
    public static final String ALBUM_ADD_FIELD = "albumObjectId";
    public static final String ALBUM_FOREIGN_FIELD = "_id";
    public static final String ALBUM_AS = "album";

    // Aggregation 사용
    public static final String AG_ARTIST_ID = "$artistId";
    public static final String ARTIST_FROM_COLLECTION = "artists";
    public static final String ARTIST_ADD_FIELD = "artistObjectId";
    public static final String ARTIST_FOREIGN_FIELD = "_id";
    public static final String ARTIST_AS = "artist";


    private List<String> tags;

    private String artistId;

    private String albumId;

    private String comment;

    @Indexed
    private List<String> cardIds;

    private NfcCardStatus status;

    @Builder
    public NfcCard(List<String> tags, String artistId, String albumId, String comment, List<String> cardIds, NfcCardStatus status) {
        this.tags = tags;
        this.artistId = artistId;
        this.albumId = albumId;
        this.comment = comment;
        this.cardIds = cardIds;
        this.status = status;
    }

    public NfcCard update(NfcCardUpdateRequest request) {
        Optional.ofNullable(request.getAlbumId()).ifPresent(v -> this.albumId = v);
        Optional.ofNullable(request.getTags()).ifPresent(v -> this.tags = v);
        Optional.ofNullable(request.getComment()).ifPresent(v -> this.comment = v);
        Optional.ofNullable(request.getCardIds()).ifPresent(v -> this.cardIds = v);

        return this;
    }

    public NfcCard afterOrder() {
        if (this.status == NfcCardStatus.BEFORE_ORDER) {
            this.status = NfcCardStatus.AFTER_ORDER;
        } else {
            throw new IllegalStateException("Cannot after order nfcCard: status=" + this.status);
        }
        return this;
    }

    public NfcCard delete() {
        this.deleted = true;
        return this;
    }

}
