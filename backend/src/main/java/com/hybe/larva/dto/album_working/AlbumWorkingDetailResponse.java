package com.hybe.larva.dto.album_working;

import com.hybe.larva.dto.common.BaseResponse;
import com.hybe.larva.dto.common.CardContents;
import com.hybe.larva.dto.common.CardContentsResponse;
import com.hybe.larva.entity.album.AlbumCard;
import com.hybe.larva.entity.album_working.AlbumStateHistory;
import com.hybe.larva.entity.album_working.AlbumWorking;
import com.hybe.larva.entity.album_working.StateHistory;
import com.hybe.larva.enums.AlbumState;
import com.hybe.larva.util.CacheUtil;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ToString(callSuper = true)
@Getter
public class AlbumWorkingDetailResponse extends BaseResponse {

    private final List<String> tags;

    private final String artistId;

    // title, multi-language
    private final Map<String, Object> title;

    // description, multi-language
    private final Map<String, Object> description;

    // thumbnail image url
    private final String thumbnailKey;

    // theme info
    private final String headImageKey;

    private final List<AlbumCard> cards;

    private final CardContentsResponse reward;

    private int version;

    private AlbumState state;

    private StateHistory rejectInfo;

    public AlbumWorkingDetailResponse(AlbumWorking albumWorking, StateHistory stateHistory, CacheUtil cacheUtil) {
        super(albumWorking);
        this.tags = albumWorking.getTags();
        this.artistId = albumWorking.getArtistId();
        this.title = cacheUtil.getAllLanguageResponse(albumWorking.getTitle());
        this.description = cacheUtil.getAllLanguageResponse(albumWorking.getDescription());
        this.thumbnailKey = albumWorking.getThumbnailKey();
        this.headImageKey = albumWorking.getHeadImageKey();
        this.cards = albumWorking.getCards();
        this.reward = convertCardContents(albumWorking.getReward(), cacheUtil);
        this.version = albumWorking.getVersion();
        this.state = albumWorking.getState();


        if (stateHistory != null) {
            this.rejectInfo = stateHistory;
        }
    }

    private CardContentsResponse convertCardContents(CardContents cardContents, CacheUtil cacheUtil) {
        return CardContentsResponse.builder()
                .imageKey(cardContents.getImageKey())
                .type(cardContents.getType())
                .videoKey(cardContents.getVideoKey())
                .thumbnailKey(cardContents.getThumbnailKey())
                .title(cacheUtil.getAllLanguageResponse(cardContents.getTitle()))
                .description(cacheUtil.getAllLanguageResponse(cardContents.getDescription()))
                .build();
    }
}
