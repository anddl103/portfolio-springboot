package com.hybe.larva.dto.banner;

import com.hybe.larva.dto.common.BaseResponse;
import com.hybe.larva.entity.banner.Banner;
import com.hybe.larva.entity.banner.BannerContents;
import com.hybe.larva.util.CacheUtil;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@ToString(callSuper = true)
@Getter
public class BannerSearchResponse extends BaseResponse {

    private List<String> tags;

    private String title;

    private String albumId;

    private String albumTitle;

    private String thumbnailKey;

    private String artistId;

    private String artistName;

    // link, imageUrl
    private BannerContents contents;

    private int sortOrder;

    private boolean display;


    public BannerSearchResponse(Banner banner, CacheUtil cacheUtil) {
        super(banner);
        this.tags = banner.getTags();
        this.albumId = banner.getAlbumId();
        this.title = cacheUtil.getLanguageResponse(banner.getTitle());
        this.albumTitle = cacheUtil.getLanguageResponse(banner.getAlbumTitle());
        this.thumbnailKey = banner.getThumbnailKey();
        this.artistId = banner.getArtistId();
        this.artistName = banner.getArtistName();

        if (banner.getContents().containsKey(cacheUtil.getLanguage())) {
            this.contents = banner.getContents().get(cacheUtil.getLanguage());
        } else if (banner.getContents().containsKey(cacheUtil.DEFAULT_COUNTRY_CODE)) {
            this.contents = banner.getContents().get(cacheUtil.DEFAULT_COUNTRY_CODE);
        }

        this.sortOrder = banner.getSortOrder();
        this.display = banner.isDisplay();
    }
}
