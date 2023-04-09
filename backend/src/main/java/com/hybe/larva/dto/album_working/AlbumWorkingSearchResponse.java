package com.hybe.larva.dto.album_working;

import com.hybe.larva.dto.common.ArtistResponse;
import com.hybe.larva.dto.common.BaseResponse;
import com.hybe.larva.dto.common.CardContents;
import com.hybe.larva.entity.album.AlbumCard;
import com.hybe.larva.entity.album_working.AlbumWorkingAggregation;
import com.hybe.larva.enums.AlbumState;
import com.hybe.larva.util.CacheUtil;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@ToString(callSuper = true)
@Getter
public class AlbumWorkingSearchResponse extends BaseResponse {

    private final List<String> tags;

//    private final String artistId;

    // title, multi-language
    private final String title;

    // description, multi-language
    private final String description;

    // thumbnail image url
    private final String thumbnailKey;

    // theme info
    private final String headImageKey;

    private final List<AlbumCard> cards;

    private final CardContents reward;

    private ArtistResponse artist;

    private String titleKr;

    private int version;

    private AlbumState state;

    private boolean deployedFlag;

    public AlbumWorkingSearchResponse(AlbumWorkingAggregation aggregation, CacheUtil cacheUtil) {
        super(aggregation);
        this.tags = aggregation.getTags();
//        this.artistId = aggregation.getArtistId();
        this.title = aggregation.getTitle();
        this.description = aggregation.getDescription();
        this.thumbnailKey = aggregation.getThumbnailKey();
        this.headImageKey = aggregation.getHeadImageKey();
        this.cards = aggregation.getCards();
        this.reward = aggregation.getReward();
        this.version = aggregation.getVersion();
        this.state = aggregation.getState();

        this.deployedFlag = aggregation.isDeployedFlag();

        this.titleKr = cacheUtil.getLanguageResponse("ko", aggregation.getTitle());
        if (aggregation.getArtist() != null) {

            this.artist = new ArtistResponse(aggregation.getArtist());
        }
    }
}
