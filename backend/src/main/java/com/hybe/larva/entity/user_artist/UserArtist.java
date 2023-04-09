package com.hybe.larva.entity.user_artist;

import com.hybe.larva.entity.artist.Artist;
import com.hybe.larva.entity.common.BaseEntity;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "userArtists")
public class UserArtist extends BaseEntity {

    public static final String UID = "uid";
    public static final String ARTIST_ID = "artistId";

    // Aggregation 사용
    public static final String _ID = "$artistId";
    public static final String FROM_COLLECTION = "artists";
    public static final String ADD_FIELD = "artistObjectId";
    public static final String FOREIGN_FIELD = "_id";
    public static final String AS = "artist";


    @Indexed
    @NotNull
    private String uid;

    private String artistId;


    @Builder
    public UserArtist(String uid, String artistId) {
        this.uid = uid;
        this.artistId = artistId;
    }

    public UserArtist delete() {
        this.deleted = true;
        return this;
    }
}
