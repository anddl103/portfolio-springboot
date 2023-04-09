package com.hybe.larva.dto.album_working;

import com.hybe.larva.dto.common.ArtistResponse;
import com.hybe.larva.dto.common.BaseResponse;
import com.hybe.larva.entity.album_working.AlbumWorkingAggregation;
import com.hybe.larva.enums.AlbumState;
import com.hybe.larva.util.CacheUtil;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@ToString(callSuper = true)
@Getter
public class AlbumWorkingBatchSearchResponse extends BaseResponse {

    private final List<String> tags;

//    private final String artistId;

    // thumbnail image url
    private final String albumThumbnailKey;

    private ArtistResponse artist;

    private String title;

    private int version;

    private AlbumState state;

    private LocalDateTime updatedAt;

    public AlbumWorkingBatchSearchResponse(AlbumWorkingAggregation aggregation, CacheUtil cacheUtil) {
        super(aggregation);
        this.tags = aggregation.getTags();
//        this.artistId = aggregation.getArtistId();
        this.albumThumbnailKey = aggregation.getThumbnailKey();
        this.version = aggregation.getVersion();
        this.state = aggregation.getState();
        this.updatedAt = aggregation.getUpdatedAt();

        this.title = cacheUtil.getLanguageResponse("ko", aggregation.getTitle());
        if (aggregation.getArtist() != null) {
            this.artist = new ArtistResponse(aggregation.getArtist());
        }
    }
}
