package com.hybe.larva.dto.album_working;

import com.hybe.larva.dto.common.BaseSearchOffsetRequest;
import com.hybe.larva.enums.AlbumState;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString(callSuper = true)
@Getter
public class AlbumWorkingHistorySearchRequest extends BaseSearchOffsetRequest {

    private final String albumId;
    private final Integer version;
    private final AlbumState state;


    @Builder
    public AlbumWorkingHistorySearchRequest(LocalDateTime from, LocalDateTime to, Integer offset, Integer limit, String albumId,
                                            Integer version, AlbumState state) {
        super(from, to, offset, limit);
        this.albumId = albumId;
        this.version = version;
        this.state = state;
    }
}
