package com.hybe.larva.entity.banner;

import com.hybe.larva.entity.artist.Artist;
import com.hybe.larva.entity.common.BaseEntity;
import lombok.*;

import java.util.List;
import java.util.Map;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BannerAggregation extends BaseEntity {

    private List<String> tags;

    private String title;

    private String albumId;

    private String albumTitle;

    private String thumbnailKey;

    private String artistId;

    private String artistName;

    // link, imageUrl
    private Map<String, BannerContents> contents;

    private int sortOrder;

    private boolean display;

    private Artist artist;

}
