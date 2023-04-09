package com.hybe.larva.dto.album_working;

import com.hybe.larva.enums.AlbumState;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Getter
@Builder
public class AlbumWorkingStateHistory {

    private String comment;

    private LocalDateTime updatedAt;
}
