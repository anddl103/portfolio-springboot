package com.hybe.larva.entity.user_album;

import com.hybe.larva.entity.album.Album;
import com.hybe.larva.entity.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserAlbumAggregation extends BaseEntity {

    private String userArtistId;

    private String albumId;

    private String uid;

    private boolean completed;

    private boolean rewarded;

    private Album album;


}
