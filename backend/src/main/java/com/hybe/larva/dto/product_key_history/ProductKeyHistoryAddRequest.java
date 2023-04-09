package com.hybe.larva.dto.product_key_history;

import com.hybe.larva.dto.common.CommonUser;
import com.hybe.larva.entity.product_key.ProductKey;
import com.hybe.larva.entity.product_key_history.ProductKeyHistory;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@ToString
@Getter
@Builder
public class ProductKeyHistoryAddRequest {

    private String productKeyId;

    private String productOrderId;

    @NotBlank
    private String nfcCardId;

    // confirmation flag
    private boolean assign;


    private String deviceInfo;

    private String failedReason;

    public ProductKeyHistory toEntity(ProductKey productKey) {
        return ProductKeyHistory.builder()
                .productKeyId(productKey.getId())
                .productOrderId(productKey.getProductOrderId())
                .nfcCardId(productKey.getNfcCardId())
                .uid(CommonUser.getUid())
                .tagged(productKey.isTagged())
                .deviceInfo(deviceInfo)
                .failedReason(failedReason)
                .build();
    }
}
