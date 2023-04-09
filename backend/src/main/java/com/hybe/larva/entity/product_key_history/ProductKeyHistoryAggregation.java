package com.hybe.larva.entity.product_key_history;

import com.hybe.larva.entity.common.BaseEntity;
import com.hybe.larva.entity.nfc_card.NfcCard;
import lombok.*;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductKeyHistoryAggregation extends BaseEntity {

    private String productKeyId;

    private String productOrderId;

    private String albumId;

    private String nfcCardId;

    private String uid;

    private boolean assign;

    private String deviceInfo;

    private String failedReason;

    private NfcCard nfcCard;

    @Setter
    private int count;
}
