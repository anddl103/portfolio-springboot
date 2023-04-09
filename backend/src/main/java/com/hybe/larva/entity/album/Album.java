package com.hybe.larva.entity.album;

import com.hybe.larva.dto.album.AlbumUpdateRequest;
import com.hybe.larva.dto.common.CardContents;
import com.hybe.larva.entity.common.BaseEntity;
import lombok.*;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Document(collection = "albums")
@CompoundIndexes({
    @CompoundIndex(def = "{'cards.cardId' : 1}" )
})
public class Album extends BaseEntity {

    public static final String ARTIST_ID = "artistId";
    public static final String TAGS = "tags";
    public static final String CARDS_CARD_ID = "cards.cardId";

    // Aggregation 사용
    public static final String _ID = "$artistId";
    public static final String FROM_COLLECTION = "artists";
    public static final String ADD_FIELD = "artistObjectId";
    public static final String FOREIGN_FIELD = "_id";
    public static final String AS = "artist";

    private List<String> tags;

    private String artistId;

    // title, multi-language
    @Setter
    private String title;

    // description, multi-language
    @Setter
    private String description;

    private String thumbnailKey;

    private String headImageKey;

    private List<AlbumCard> cards;

    private CardContents reward;

    private int version;

    @Builder
    public Album(String id, List<String> tags, String artistId, String title, String description, String thumbnailKey,
                 String headImageKey, List<AlbumCard> cards, CardContents reward, int version) {
        this.id = id;
        this.tags = tags;
        this.artistId = artistId;
        this.title = title;
        this.description = description;
        this.thumbnailKey = thumbnailKey;
        this.headImageKey = headImageKey;
        this.cards = cards;
        this.reward = reward;
        this.version = version;
        this.createdAt = LocalDateTime.now();
    }

    public Album update(AlbumUpdateRequest request) {
        Optional.ofNullable(request.getTags()).ifPresent(v -> this.tags = v);
        Optional.ofNullable(request.getTitle()).ifPresent(v -> this.title = v);
        Optional.ofNullable(request.getDescription()).ifPresent(v -> this.description = v);
        Optional.ofNullable(request.getThumbnailKey()).ifPresent(v -> this.thumbnailKey = v);
        Optional.ofNullable(request.getReward()).ifPresent(v -> this.reward = v);
        return this;
    }

    public Album update(List<AlbumCard> albumCards) {
        Optional.ofNullable(albumCards).ifPresent(v -> this.cards = v);
        return this;
    }

    public Album delete() {
        this.deleted = true;
        return this;
    }
}
