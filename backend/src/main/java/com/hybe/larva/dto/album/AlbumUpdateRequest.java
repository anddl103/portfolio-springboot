package com.hybe.larva.dto.album;

import com.hybe.larva.dto.common.CardContents;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.List;

@ToString
@Getter
@Builder
public class AlbumUpdateRequest {

    private List<String> tags;

    @NotNull
    private final String title;

    @NotNull
    private final String description;

    private String thumbnailKey;

    private String headImageKey;

    private CardContents reward;
}
