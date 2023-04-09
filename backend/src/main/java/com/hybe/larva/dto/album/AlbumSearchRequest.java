package com.hybe.larva.dto.album;

import com.hybe.larva.dto.common.BaseSearchOffsetRequest;
import com.hybe.larva.dto.common.BaseSearchRequest;
import com.hybe.larva.enums.AlbumState;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@ToString(callSuper = true)
@Getter
public class AlbumSearchRequest extends BaseSearchOffsetRequest {

    private final String keyword;
    private final String artistId;
    private final List<AlbumState> state;

    @Builder
    public AlbumSearchRequest(LocalDateTime from, LocalDateTime to, Integer offset, Integer limit, String keyword,
                              String artistId, List<AlbumState> state) {
        super(from, to, offset, limit);
        this.keyword = keyword;
        this.artistId = artistId;
        this.state = state;
    }
}
