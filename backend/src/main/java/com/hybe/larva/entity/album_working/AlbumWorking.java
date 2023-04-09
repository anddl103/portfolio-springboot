package com.hybe.larva.entity.album_working;

import com.hybe.larva.dto.album.AlbumUpdateRequest;
import com.hybe.larva.dto.common.CardContents;
import com.hybe.larva.entity.album.AlbumCard;
import com.hybe.larva.entity.common.BaseEntity;
import com.hybe.larva.entity.user_album.UserAlbum;
import com.hybe.larva.enums.AlbumState;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Document(collection = "albums_working")
@CompoundIndexes({
    @CompoundIndex(def = "{'cards.cardId' : 1}" )
})
public class AlbumWorking extends BaseEntity {

    public static final String ARTIST_ID = "artistId";
    public static final String TAGS = "tags";
    public static final String CARDS_CARD_ID = "cards.cardId";
    public static final String VERSION = "version";
    public static final String STATE = "state";
    public static final String UPDATED_AT = "updatedAt";


    // Aggregation 사용
    public static final String AG_ARTIST_ID = "$artistId";
    public static final String AG_ARTIST_FROM_COLLECTION = "artists";
    public static final String AG_ARTIST_ADD_FIELD = "artistObjectId";
    public static final String AG_ARTIST_FOREIGN_FIELD = "_id";
    public static final String AG_ARTIST_AS = "artist";

    public static final String AG_ALBUM_FROM_COLLECTION = "albums";
    public static final String AG_ALBUM_FOREIGN_FIELD = "_id";
    public static final String AG_ALBUM_AS = "album";
    public static final String AG_ALBUM_ARRAY_OF = "$album._id";

    public static final String AG_ALBUM_DEPLOYED_FLAG = "deployedFlag";

//    public static final String AG_INFO_ID = "$_id";
//    public static final String AG_INFO_FROM_COLLECTION = "albums_additional_info";
//    public static final String AG_INFO_ADD_FIELD = "albumStringId";
//    public static final String AG_INFO_FOREIGN_FIELD = "albumId";
//    public static final String AG_INFO_AS = "info";
//    public static final String AG_INFO_FILTER_FIELD = "albumVersion";

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

    private AlbumState state;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder
    public AlbumWorking(List<String> tags, String artistId, String title, String description, String thumbnailKey,
                        String headImageKey, List<AlbumCard> cards, CardContents reward, int version, AlbumState state) {
        this.tags = tags;
        this.artistId = artistId;
        this.title = title;
        this.description = description;
        this.thumbnailKey = thumbnailKey;
        this.headImageKey = headImageKey;
        this.cards = cards;
        this.reward = reward;
        this.version = version;
        this.state = state;
    }

    public AlbumWorking update(AlbumUpdateRequest request) {
        Optional.ofNullable(request.getTags()).ifPresent(v -> this.tags = v);
        Optional.ofNullable(request.getTitle()).ifPresent(v -> this.title = v);
        Optional.ofNullable(request.getDescription()).ifPresent(v -> this.description = v);
        Optional.ofNullable(request.getThumbnailKey()).ifPresent(v -> this.thumbnailKey = v);
        Optional.ofNullable(request.getReward()).ifPresent(v -> this.reward = v);
        return this;
    }

    public AlbumWorking update(List<AlbumCard> albumCards) {
        Optional.ofNullable(albumCards).ifPresent(v -> this.cards = v);
        return this;
    }

    public AlbumWorking setState(AlbumState state) {
        this.state = state;
        return this;
    }

    public AlbumWorking delete() {
        this.deleted = true;
        return this;
    }
}
