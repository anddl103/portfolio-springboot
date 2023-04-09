package com.hybe.larva.dto.banner;

import com.hybe.larva.dto.common.AlbumResponse;
import com.hybe.larva.dto.common.ArtistResponse;
import com.hybe.larva.dto.common.BaseResponse;
import com.hybe.larva.entity.banner.BannerAggregation;
import com.hybe.larva.entity.banner.BannerContents;
import com.hybe.larva.util.CacheUtil;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@ToString(callSuper = true)
@Getter
public class BannerResponse extends BaseResponse {

    private List<String> tags;

    private String title;

    private String thumbnailKey;

    private AlbumResponse album;

    private ArtistResponse artist;

    // link, imageUrl
    private Map<String, BannerContents> contents;

    private int sortOrder;

    private boolean display;

    private String titleKr;


    public BannerResponse(BannerAggregation aggregation, CacheUtil cacheUtil) {
        super(aggregation);
        this.tags = aggregation.getTags();
        this.title = aggregation.getTitle();
        this.thumbnailKey = aggregation.getThumbnailKey();
        this.contents = aggregation.getContents();
        this.sortOrder = aggregation.getSortOrder();
        this.display = aggregation.isDisplay();

        this.album = new AlbumResponse(aggregation.getAlbumId(), aggregation.getAlbumTitle(), aggregation.getThumbnailKey(), cacheUtil);

        this.artist = new ArtistResponse(aggregation.getArtist());

        this.titleKr = cacheUtil.getLanguageResponse("ko", aggregation.getTitle());
    }
}
