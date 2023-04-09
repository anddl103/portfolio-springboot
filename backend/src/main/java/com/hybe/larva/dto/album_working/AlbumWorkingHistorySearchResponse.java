package com.hybe.larva.dto.album_working;

import com.hybe.larva.entity.album_working.StateHistory;
import com.hybe.larva.enums.AlbumState;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString(callSuper = true)
@Getter
public class AlbumWorkingHistorySearchResponse {

    private int version;

    private AlbumState state;

    private String comment;

    private LocalDateTime updatedAt;

    public AlbumWorkingHistorySearchResponse(StateHistory stateHistory) {

        this.version = stateHistory.getVersion();
        this.state = stateHistory.getState();
        this.comment = stateHistory.getComment();
        this.updatedAt = stateHistory.getUpdatedAt();
    }
}
