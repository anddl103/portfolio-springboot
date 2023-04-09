package com.hybe.larva.dto.product_order;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@ToString(callSuper = true)
@Data
@Builder
public class ProductOrderArtist {

    private String artistId;

    private String name;

    private String thumbnailKey;

    private String logoUrl;

    private String logoKey;
}
