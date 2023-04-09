package com.hybe.larva.dto.artist;

import com.hybe.larva.entity.artist.ArtistMember;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;

@ToString
@Getter
@Builder
public class ArtistUpdateRequest {

    @NotBlank
    private String name;

    private List<ArtistMember> members;

    private String thumbnailKey;

    private String logoKey;

    @Setter
    private Integer sortOrder;

    private boolean display;

}
