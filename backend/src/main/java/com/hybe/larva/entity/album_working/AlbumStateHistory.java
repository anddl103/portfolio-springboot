package com.hybe.larva.entity.album_working;

import com.hybe.larva.entity.common.BaseEntity;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Document(collection = "albums_state_history")
public class AlbumStateHistory extends BaseEntity {

    public static final String ALBUM_ID = "albumId";
    public static final String VERSION = "version";
    public static final String STATE = "state";
    public static final String UPDATE_AT = "updatedAt";


    private String albumId;

    private List<StateHistory> history;

    @Builder
    public AlbumStateHistory(String albumId, List<StateHistory> history) {
        this.albumId = albumId;
        this.history = history;
    }
}
