package com.hybe.larva.dto.product_key;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@ToString
@Getter
@Builder
public class ProductKeyValidateRequest {

    @NotBlank
    private String code;

    @NotBlank
    private String nfcCardId;

    @NotBlank
    private String productOrderId;
}
