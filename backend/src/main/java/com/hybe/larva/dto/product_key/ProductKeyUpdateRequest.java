package com.hybe.larva.dto.product_key;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class ProductKeyUpdateRequest {

    private String productOrderId;

    private String albumId;


    private String nfcCardId;

    // confirmation flag
    private boolean tagged;

}
