package com.hybe.larva.dto.user_album;

import com.hybe.larva.dto.common.BaseResponse;
import com.hybe.larva.entity.user_album.UserAlbum;
import com.hybe.larva.entity.user_album.UserAlbumAggregation;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
public class UserAlbumResponse extends BaseResponse {

    private final String albumId;

    private final String userUid;

    private final boolean completed;

    private final boolean rewarded;

    public UserAlbumResponse(UserAlbumAggregation userAlbum) {
        super(userAlbum);

        this.albumId = userAlbum.getAlbum().getId();
        this.userUid = userAlbum.getUid();
        this.completed = userAlbum.isCompleted();
        this.rewarded = userAlbum.isRewarded();
    }
}
