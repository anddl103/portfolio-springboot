package com.hybe.larva.entity.album_working;

import com.hybe.larva.dto.common.CardContents;
import com.hybe.larva.entity.album.AlbumCard;
import com.hybe.larva.entity.artist.Artist;
import com.hybe.larva.entity.common.BaseEntity;
import com.hybe.larva.enums.AlbumState;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AlbumWorkingAggregation extends BaseEntity {

    private List<String> tags;

    private String artistId;

    private String title;

    private String description;

    private String thumbnailKey;

    private String headImageKey;

    private List<AlbumCard> cards;

    private CardContents reward;

    private int version;

    private AlbumState state;

    private Artist artist;

    private LocalDateTime updatedAt;

    private boolean deployedFlag;
}
