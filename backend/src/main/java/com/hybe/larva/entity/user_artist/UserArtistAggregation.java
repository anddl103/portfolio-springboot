package com.hybe.larva.entity.user_artist;

import com.hybe.larva.entity.artist.Artist;
import com.hybe.larva.entity.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserArtistAggregation extends BaseEntity {

    private String uid;

    private String artistId;

    private Artist artist;
}
