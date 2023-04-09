package com.hybe.larva.dto.album_working;

import com.hybe.larva.dto.common.BaseResponse;
import com.hybe.larva.dto.common.CardContents;
import com.hybe.larva.entity.album.Album;
import com.hybe.larva.entity.album.AlbumAggregation;
import com.hybe.larva.entity.album.AlbumCard;
import com.hybe.larva.entity.album_working.AlbumWorking;
import com.hybe.larva.enums.AlbumState;
import com.hybe.larva.util.CacheUtil;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@ToString(callSuper = true)
@Getter
public class AlbumWorkingResponse extends BaseResponse {

    private final List<String> tags;

    private final String artistId;

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

    private final int version;

    private final AlbumState state;


    public AlbumWorkingResponse(AlbumWorking albumWorking) {
        super(albumWorking);
        this.tags = albumWorking.getTags();
        this.artistId = albumWorking.getArtistId();
        this.title = albumWorking.getTitle();
        this.description = albumWorking.getDescription();
        this.thumbnailKey = albumWorking.getThumbnailKey();
        this.headImageKey = albumWorking.getHeadImageKey();
        this.cards = albumWorking.getCards();
        this.reward = albumWorking.getReward();
        this.state = albumWorking.getState();
        this.version = albumWorking.getVersion();
    }
}
