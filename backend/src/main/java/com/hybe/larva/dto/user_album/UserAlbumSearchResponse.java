package com.hybe.larva.dto.user_album;

import com.hybe.larva.dto.common.BaseResponse;
import com.hybe.larva.dto.common.CardContents;
import com.hybe.larva.entity.user_album.UserAlbum;
import com.hybe.larva.entity.user_album.UserAlbumAggregation;
import com.hybe.larva.util.CacheUtil;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
public class UserAlbumSearchResponse extends BaseResponse {

//    private String albumId;

    private boolean completed;

    private boolean rewarded;

//    private Album album;

    private String artistId;

    private String title;

    private String description;

    private String thumbnailKey;

    private CardContents reward;

//    private List<AlbumCard> cards;


    public UserAlbumSearchResponse(UserAlbumAggregation userAlbum, CacheUtil cacheUtil) {
        super(userAlbum);

//        this.albumId = aggregation.getAlbumId();
        this.completed = userAlbum.isCompleted();
        this.rewarded = userAlbum.isRewarded();

        if (userAlbum.getAlbum() != null) {
            // 다국어 처리
            userAlbum = setCountryCode(userAlbum, cacheUtil);

            this.artistId = userAlbum.getAlbum().getArtistId();
            this.title = userAlbum.getAlbum().getTitle();
            this.description = userAlbum.getAlbum().getDescription();
            this.thumbnailKey = userAlbum.getAlbum().getThumbnailKey();

            if (userAlbum.isRewarded()) {
                this.reward = userAlbum.getAlbum().getReward();
            }

        }
    }

    private UserAlbumAggregation setCountryCode(UserAlbumAggregation userAlbum, CacheUtil cacheUtil) {
        // cacheUtil.getLanguageResponse("609e34d24471ac5aba63339b")
        userAlbum.getAlbum().setDescription(
                cacheUtil.getLanguageResponse(userAlbum.getAlbum().getDescription()));
        userAlbum.getAlbum().setTitle(
                cacheUtil.getLanguageResponse(userAlbum.getAlbum().getTitle()));
        if (userAlbum.isRewarded()) {
            userAlbum.getAlbum().getReward().setTitle(
                    cacheUtil.getLanguageResponse(userAlbum.getAlbum().getReward().getTitle()));

            userAlbum.getAlbum().getReward().setDescription(
                    cacheUtil.getLanguageResponse(userAlbum.getAlbum().getReward().getDescription()));
        }

        return userAlbum;
    }
}
