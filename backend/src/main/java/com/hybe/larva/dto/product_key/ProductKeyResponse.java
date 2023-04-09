package com.hybe.larva.dto.product_key;

import com.hybe.larva.dto.common.BaseResponse;
import com.hybe.larva.entity.product_key.ProductKey;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString(callSuper = true)
@Getter
public class ProductKeyResponse extends BaseResponse {

    private final String code;

    private final String productOrderId;

    private final String nfcCardId;

    private final boolean assigned;

    private final boolean verified;

    private final boolean tagged;


    public ProductKeyResponse(ProductKey productKey) {
        super(productKey);

        this.code = productKey.getCode();
        this.productOrderId = productKey.getProductOrderId();
        this.nfcCardId = productKey.getNfcCardId();
        this.assigned = productKey.isAssigned();
        this.verified = productKey.isVerified();
        this.tagged = productKey.isTagged();
    }

    public ProductKeyResponse(ProductKey productKey, String code) {
        super(productKey);

        if (code != null) {
            this.code = code;
        } else {
            this.code = productKey.getCode();
        }

        this.productOrderId = productKey.getProductOrderId();
        this.nfcCardId = productKey.getNfcCardId();
        this.assigned = productKey.isAssigned();
        this.verified = productKey.isVerified();
        this.tagged = productKey.isTagged();
    }
}
