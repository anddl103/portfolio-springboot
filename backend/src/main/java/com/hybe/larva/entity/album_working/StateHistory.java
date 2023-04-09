package com.hybe.larva.entity.album_working;

import com.hybe.larva.enums.AlbumState;
import lombok.*;
import org.springframework.data.annotation.LastModifiedBy;

import java.time.LocalDateTime;

@ToString
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StateHistory {

    private int version;

    private AlbumState state;

    private String comment;

    private LocalDateTime updatedAt;

    private String updatedBy;

    @Builder
    public StateHistory (int version, AlbumState state, String comment, LocalDateTime updatedAt, String updatedBy) {
        this.version = version;
        this.state = state;
        this.comment = comment;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }
}
