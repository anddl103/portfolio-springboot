package com.hybe.larva.dto.album;

import com.hybe.larva.dto.common.BaseResponse;
import com.hybe.larva.dto.common.CardContents;
import com.hybe.larva.dto.common.CardContentsResponse;
import com.hybe.larva.entity.album.AlbumAggregation;
import com.hybe.larva.entity.album.AlbumCard;
import com.hybe.larva.util.CacheUtil;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@ToString(callSuper = true)
@Getter
public class AlbumDetailResponse extends BaseResponse {
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

    private final int version;

    public AlbumDetailResponse(AlbumAggregation aggregation, CacheUtil cacheUtil) {
        super(aggregation);
        this.tags = aggregation.getTags();
        this.artistId = aggregation.getArtistId();
        this.title = cacheUtil.getAllLanguageResponse(aggregation.getTitle());
        this.description = cacheUtil.getAllLanguageResponse(aggregation.getDescription());
        this.thumbnailKey = aggregation.getThumbnailKey();
        this.headImageKey = aggregation.getHeadImageKey();
        this.cards = aggregation.getCards();
        this.reward = convertCardContents(aggregation.getReward(), cacheUtil);
        this.version = aggregation.getVersion();
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
