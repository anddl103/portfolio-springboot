package com.hybe.larva.dto.user_info;

import com.hybe.larva.dto.common.BaseResponse;
import com.hybe.larva.entity.user_album.UserAlbum;
import com.hybe.larva.entity.user_album.UserAlbumAggregation;
import com.hybe.larva.util.CacheUtil;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
public class UserInfoAlbumResponse extends BaseResponse {

    private String albumId;

    private boolean completed;

    private boolean rewarded;

    private String artistId;

    private String title;

    private String description;

    // thumbnail image url
    private String thumbnailKey;

    public UserInfoAlbumResponse(UserAlbumAggregation userAlbum, CacheUtil cacheUtil) {
        super(userAlbum);

        this.completed = userAlbum.isCompleted();
        this.rewarded = userAlbum.isRewarded();

        if (userAlbum.getAlbum() != null) {
            this.albumId = userAlbum.getAlbum().getId();
            this.artistId = userAlbum.getAlbum().getArtistId();
            this.title = getLocaleCodeValue(userAlbum.getAlbum().getTitle(), cacheUtil);
            this.description = getLocaleCodeValue(userAlbum.getAlbum().getDescription(), cacheUtil);
            this.thumbnailKey = userAlbum.getAlbum().getThumbnailKey();
        }
    }

    private String getLocaleCodeValue(String key, CacheUtil cacheUtil) {
        if (key == null) {
            return null;
        }

        return cacheUtil.getLanguageResponse("ko", key);
    }
}
