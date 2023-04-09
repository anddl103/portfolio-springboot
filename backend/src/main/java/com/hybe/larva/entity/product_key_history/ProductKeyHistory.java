package com.hybe.larva.entity.product_key_history;

import com.hybe.larva.entity.common.BaseEntity;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "productKeyHistory")
public class ProductKeyHistory extends BaseEntity {

    public static final String UID = "uid";
    public static final String NFC_CARD_ID = "nfcCardId";
    public static final String PRODUCT_KEY_ID = "productKeyId";

    // Aggregation 사용
    public static final String _ID = "$nfcCardId";
    public static final String FROM_COLLECTION = "nfcCards";
    public static final String ADD_FIELD = "nfcCardObjectId";
    public static final String FOREIGN_FIELD = "_id";
    public static final String AS = "nfcCard";

    @Indexed
    private String productKeyId;

    @Indexed
    private String productOrderId;

    @Indexed
    private String nfcCardId;

    @Indexed
    private String uid;

    private boolean tagged;

    private String deviceInfo;

    private String failedReason;

    @Builder
    public ProductKeyHistory(String productKeyId, String productOrderId, String nfcCardId, String uid, boolean tagged,
                             String deviceInfo, String failedReason) {
        this.productKeyId = productKeyId;
        this.productOrderId = productOrderId;
        this.nfcCardId = nfcCardId;
        this.uid = uid;
        this.tagged  = tagged;
        this.deviceInfo = deviceInfo;
        this.failedReason = failedReason;
    }

}
