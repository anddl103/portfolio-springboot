package com.hybe.larva.dto.artist;

import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
public class ArtistUpdateSortOrder {
    private String artistId;
    private int sortOrder;
}
