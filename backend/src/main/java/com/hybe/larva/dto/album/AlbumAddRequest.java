package com.hybe.larva.dto.album;

import com.hybe.larva.dto.common.CardContents;
import com.hybe.larva.entity.album.Album;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.List;

@ToString
@Getter
@Builder
public class AlbumAddRequest {

    private List<String> tags;

    @NotNull
    private String artistId;

    @NotNull
    private String title;

    @NotNull
    private String description;

    private String thumbnailKey;

    private CardContents reward;

    public Album toEntity() {
        return Album.builder()
                .tags(tags)
                .artistId(artistId)
                .title(title)
                .description(description)
                .thumbnailKey(thumbnailKey)
                .reward(reward)
                .version(1)
                .build();
    }
}
