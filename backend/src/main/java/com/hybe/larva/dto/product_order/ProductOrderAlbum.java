package com.hybe.larva.dto.product_order;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@ToString(callSuper = true)
@Data
@Builder
public class ProductOrderAlbum {

    private String albumId;

    private String title;

    private String thumbnailKey;
}
