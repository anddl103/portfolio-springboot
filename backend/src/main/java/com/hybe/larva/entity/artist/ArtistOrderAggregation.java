package com.hybe.larva.entity.artist;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArtistOrderAggregation {
    private int maxSortOrder;
}

