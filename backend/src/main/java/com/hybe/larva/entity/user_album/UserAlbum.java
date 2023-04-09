package com.hybe.larva.entity.user_album;

import com.hybe.larva.dto.user_album.UserAlbumUpdateRequest;
import com.hybe.larva.entity.album.Album;
import com.hybe.larva.entity.album.AlbumCard;
import com.hybe.larva.entity.common.BaseEntity;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "userAlbums")
public class UserAlbum extends BaseEntity {

    public static final String UID = "uid";
    public static final String CARD_ID = "cards._id";
    public static final String ALBUM_ID = "album.$id";
    public static final String ARTIST_ID = "artistId";
    public static final String USER_ARTIST_ID = "userArtistId";
    public static final String UPDATED_AT = "updatedAt";
    public static final String ALBUM_SEARCH_TAGS = "album.$searchTags";

    // Aggregation 사용
    public static final String _ID = "$albumId";
    public static final String FROM_COLLECTION = "albums";
    public static final String ADD_FIELD = "albumObjectId";
    public static final String FOREIGN_FIELD = "_id";
    public static final String AS = "album";

    @Indexed
    private String userArtistId;

    @Indexed
    private String albumId;

    @Indexed
    @NotNull
    private String uid;

    private boolean completed;

    private boolean rewarded;

    private List<UserAlbumCard> cards;

    @LastModifiedDate
    protected LocalDateTime updatedAt;


    @Builder
    public UserAlbum(String userArtistId, String albumId, String uid, boolean completed, boolean rewarded,
                     List<UserAlbumCard> cards) {
        this.userArtistId = userArtistId;
        this.albumId = albumId;
        this.uid = uid;
        this.completed = completed;
        this.rewarded = rewarded;
        this.cards = cards;
    }

    public UserAlbum update(UserAlbumUpdateRequest request) {

        Optional.ofNullable(request.isCompleted()).ifPresent(v -> this.completed = v);
        Optional.ofNullable(request.isRewarded()).ifPresent(v -> this.rewarded = v);

        return this;
    }

    public UserAlbum update(List<UserAlbumCard> cards) {
        Optional.ofNullable(cards).ifPresent(v -> this.cards = v);
        return this;
    }

    public UserAlbum completed(boolean completed) {
        this.completed = completed;
        return this;
    }

    public UserAlbum rewarded(boolean rewarded) {
        this.rewarded = rewarded;
        return this;
    }

    public UserAlbum delete() {
        this.deleted = true;
        return this;
    }

}
