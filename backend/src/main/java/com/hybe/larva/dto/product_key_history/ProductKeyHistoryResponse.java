package com.hybe.larva.dto.product_key_history;

import com.hybe.larva.dto.common.BaseResponse;
import com.hybe.larva.entity.product_key_history.ProductKeyHistory;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
public class ProductKeyHistoryResponse extends BaseResponse {

    private final String productKeyId;

    private final String productOrderId;

    private final String nfcCardId;

    private final String uid;

    private final boolean tagged;

    private final String deviceInfo;

    private final String failedReason;

    public ProductKeyHistoryResponse(ProductKeyHistory productKeyHistory) {
        super(productKeyHistory);

        this.productKeyId = productKeyHistory.getProductKeyId();
        this.productOrderId = productKeyHistory.getProductOrderId();
        this.nfcCardId = productKeyHistory.getNfcCardId();
        this.uid = productKeyHistory.getUid();
        this.tagged = productKeyHistory.isTagged();
        this.deviceInfo = productKeyHistory.getDeviceInfo();
        this.failedReason = productKeyHistory.getFailedReason();
    }
}
