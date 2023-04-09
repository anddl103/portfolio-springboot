package com.hybe.larva.dto.album_working;

import com.hybe.larva.dto.common.CardContents;
import com.hybe.larva.entity.album_working.AlbumWorking;
import com.hybe.larva.enums.AlbumState;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.List;

@ToString
@Getter
@Builder
public class AlbumWorkingAddRequest {

    private List<String> tags;

    @NotNull
    private String artistId;

    @NotNull
    private String title;

    @NotNull
    private String description;

    private String thumbnailKey;

    private CardContents reward;

    public AlbumWorking toEntity() {
        return AlbumWorking.builder()
                .tags(tags)
                .artistId(artistId)
                .title(title)
                .description(description)
                .thumbnailKey(thumbnailKey)
                .reward(reward)
                .version(1)
                .state(AlbumState.WORKING)
                .build();
    }
}
