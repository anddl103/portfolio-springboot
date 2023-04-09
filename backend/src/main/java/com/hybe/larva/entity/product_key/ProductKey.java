package com.hybe.larva.entity.product_key;


import com.hybe.larva.dto.product_key.ProductKeyUpdateRequest;
import com.hybe.larva.entity.common.BaseEntity;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "productKeys")
public class ProductKey extends BaseEntity {

    public static final String CODE = "code";
    public static final String PRODUCT_ORDER_ID = "productOrderId";
    public static final String ALBUM_ID = "albumId";
    public static final String CARD_ID = "cardId";
    public static final String ASSIGNED = "assigned";
    public static final String VERIFIED = "verified";
    public static final String TAGGED = "tagged";
    public static final String NFC_CARD_ID = "nfcCardId";

    // unique s/n
    @Indexed(unique = true)
    private String code;

    private byte[] secretKey;

    @Indexed
    private String productOrderId;

    @Indexed
    private String nfcCardId;


    private String uid;

    // confirmation flag
    private boolean assigned;

    private boolean verified;

    private boolean tagged;

    @Builder
    public ProductKey(String code, byte[] secretKey, String productOrderId, String nfcCardId, boolean assigned,
                      boolean verified, boolean tagged) {
        this.code = code;
        this.secretKey = secretKey;
        this.productOrderId = productOrderId;
        this.nfcCardId = nfcCardId;
        this.assigned  = assigned ;
        this.verified = verified;
        this.tagged = tagged;
    }

    public ProductKey makeUse(String uid) {
        this.tagged  = true;
        this.uid = uid;
        return this;
    }

    public ProductKey rollback() {
        this.assigned  = false;
        return this;
    }

    public static String generateUniqueCode() {
        return UUID.randomUUID().toString().replace("-", "");
    }


    public ProductKey update(ProductKeyUpdateRequest request) {

        Optional.ofNullable(request.getNfcCardId()).ifPresent(v -> this.nfcCardId = v);
        Optional.ofNullable(request.getProductOrderId()).ifPresent(v -> this.productOrderId = v);
        Optional.ofNullable(request.isTagged()).ifPresent(v -> this.tagged  = v);

        return this;
    }

    public ProductKey delete() {
        this.deleted = true;
        return this;
    }

    public ProductKey assigned() {
        this.assigned = true;
        return this;
    }

    public ProductKey verified(boolean verified) {
        this.verified = verified;
        return this;
    }
}
