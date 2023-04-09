package com.hybe.larva.dto.banner;

import com.hybe.larva.dto.common.BaseResponse;
import com.hybe.larva.entity.banner.Banner;
import com.hybe.larva.entity.banner.BannerContents;
import com.hybe.larva.util.CacheUtil;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@ToString(callSuper = true)
@Getter
public class BannerDetailResponse extends BaseResponse {

    private List<String> tags;

    private Map<String, Object> title;

    private String albumId;

    private String albumTitle;

    private String thumbnailKey;

    private String artistId;

    private String artistName;

    // link, imageUrl
    private Map<String, BannerContents> contents;

    private int sortOrder;

    private boolean display;


    public BannerDetailResponse(Banner banner, CacheUtil cacheUtil) {
        super(banner);
        this.tags = banner.getTags();
        this.title = cacheUtil.getAllLanguageResponse(banner.getTitle());
        this.albumId = banner.getAlbumId();
        this.albumTitle = banner.getAlbumTitle();
        this.thumbnailKey = banner.getThumbnailKey();
        this.artistId = banner.getArtistId();
        this.artistName = banner.getArtistName();
        this.contents = banner.getContents();
        this.sortOrder = banner.getSortOrder();
        this.display = banner.isDisplay();
    }
}
