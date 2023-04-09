package com.hybe.larva.dto.product_key;

import com.hybe.larva.entity.product_key.ProductKey;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@ToString
@Getter
@Builder
public class ProductKeyAddRequest {

    @NotBlank
    private String productOrderId;

    @NotBlank
    private String nfcCardId;


    public ProductKey toEntity(byte[] secretKey, String code) {
        return ProductKey.builder()
                .secretKey(secretKey)
                .code(code)
                .productOrderId(productOrderId)
                .nfcCardId(nfcCardId)
                .build();
    }

}
