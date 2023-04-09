package com.hybe.larva.dto.artist;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
public class ArtistUpdateSortOrderRequest {

    private List<ArtistUpdateSortOrder> orderList;
}
