package com.hybe.larva.entity.artist;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@ToString
@Data
@Builder
public class ArtistMember {
    private String id;

    // 다국어 Id
    private String name;
}
