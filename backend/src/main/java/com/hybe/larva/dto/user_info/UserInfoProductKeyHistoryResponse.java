package com.hybe.larva.dto.user_info;

import com.hybe.larva.dto.common.BaseResponse;
import com.hybe.larva.entity.product_key_history.ProductKeyHistoryAggregation;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@ToString(callSuper = true)
@Getter
public class UserInfoProductKeyHistoryResponse extends BaseResponse {

    private final String nfcCardId;

    private final boolean assign;

    private final String deviceInfo;

    private final String failedReason;

    private final List<String> nfcCardTags;

    private final String nfcCardComment;


    public UserInfoProductKeyHistoryResponse(ProductKeyHistoryAggregation aggregation) {
        super(aggregation);

        this.nfcCardId = aggregation.getNfcCardId();
        this.assign = aggregation.isAssign();
        this.deviceInfo = aggregation.getDeviceInfo();
        this.failedReason = aggregation.getFailedReason();
        if (aggregation.getNfcCard() != null) {
            this.nfcCardComment = aggregation.getNfcCard().getComment();
            this.nfcCardTags = aggregation.getNfcCard().getTags();
        } else {
            this.nfcCardComment = null;
            this.nfcCardTags = null;
        }
    }
}
