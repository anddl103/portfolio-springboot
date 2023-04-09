package com.hybe.larva.dto.common;

import com.hybe.larva.entity.artist.Artist;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class ArtistResponse {

    private String id;

    private String name;

    private String logoKey;

    public ArtistResponse(Artist artist) {

        if (artist != null) {
            this.id = artist.getId();
            this.logoKey = artist.getLogoKey();
            this.name = artist.getName();
        }
    }
}
