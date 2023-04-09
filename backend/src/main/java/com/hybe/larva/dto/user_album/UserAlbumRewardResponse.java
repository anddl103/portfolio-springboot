package com.hybe.larva.dto.user_album;

import com.hybe.larva.entity.user_album.UserAlbum;
import com.hybe.larva.entity.user_album.UserAlbumAggregation;
import com.hybe.larva.enums.ContentsType;
import com.hybe.larva.util.CacheUtil;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
public class UserAlbumRewardResponse  {

    private String title;

    private String description;

    private String thumbnailKey;

    private ContentsType type;

    private String imageKey;

    private String videoKey;

    public UserAlbumRewardResponse(UserAlbumAggregation userAlbum, CacheUtil cacheUtil) {

        if (userAlbum.getAlbum() != null && userAlbum.isRewarded() ) {
            this.title = cacheUtil.getLanguageResponse(userAlbum.getAlbum().getReward().getTitle());
            this.description = cacheUtil.getLanguageResponse(userAlbum.getAlbum().getReward().getDescription());
            this.thumbnailKey = userAlbum.getAlbum().getReward().getThumbnailKey();
            this.type = userAlbum.getAlbum().getReward().getType();
            this.imageKey = userAlbum.getAlbum().getReward().getImageKey();
            this.videoKey = userAlbum.getAlbum().getReward().getVideoKey();
        }
    }
}
