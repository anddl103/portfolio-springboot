package com.hybe.larva.entity.product_order;

import com.hybe.larva.enums.ProductOrderStatus;
import com.hybe.larva.enums.ServiceRegion;
import com.hybe.larva.dto.product_order.ProductOrderUpdateRequest;
import com.hybe.larva.entity.common.BaseEntity;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Optional;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Document(collection = "productOrders")
public class ProductOrder extends BaseEntity {

    public static final String REGISTRANT = "registrant";
    public static final String QUANTITY = "quantity";
    public static final String DESCRIPTION = "comment";
    public static final String STATE = "status";
    public static final String NFC_CARD_ID = "nfcCardId";
    public static final String ARTIST_ID = "nfcCard.artistId";

    // Aggregation 사용
    public static final String AG_NFC_CARD_ID = "$nfcCardId";
    public static final String AG_NFC_CARD_FROM_COLLECTION = "nfcCards";
    public static final String AG_NFC_CARD_ADD_FIELD = "objectNfcCardId";
    public static final String AG_NFC_CARD_FOREIGN_FIELD = "_id";
    public static final String AG_NFC_CARD_AS = "nfcCard";

    public static final String AG_ALBUM_ID = "$"+AG_NFC_CARD_AS+".albumId";
    public static final String AG_ALBUM_FROM_COLLECTION = "albums";
    public static final String AG_ALBUM_ADD_FIELD = "objectAlbumId";
    public static final String AG_ALBUM_FOREIGN_FIELD = "_id";
    public static final String AG_ALBUM_AS = "album";

    public static final String AG_PRODUCT_KEY_ID = "$_id";
    public static final String AG_PRODUCT_KEY_FROM_COLLECTION = "productKeys";
    public static final String AG_PRODUCT_KEY_ADD_FIELD = "stringProductOrderId";
    public static final String AG_PRODUCT_KEY_FOREIGN_FIELD = "productOrderId";
    public static final String AG_PRODUCT_KEY_AS = "productKey";
    public static final String AG_PRODUCT_KEY_VERIFIED_COUNT = "verifiedCount";
    public static final String AG_PRODUCT_KEY_ASSIGNED_COUNT = "assignedCount";

    public static final String AG_ARTIST_ID = "$"+AG_NFC_CARD_AS+".artistId";
    public static final String AG_ARTIST_FROM_COLLECTION = "artists";
    public static final String AG_ARTIST_ADD_FIELD = "objectArtistId";
    public static final String AG_ARTIST_FOREIGN_FIELD = "_id";
    public static final String AG_ARTIST_AS = "artist";


    // Nfc_Card.id
    @Indexed
    private String nfcCardId;

    // order quantity
    private Long quantity;

    // comment
    private String comment;

    // service region
    private ServiceRegion region;


    // order status
    @Setter
    @Indexed
    private ProductOrderStatus status;


    @Builder
    public ProductOrder(String nfcCardId, Long quantity, String comment, ServiceRegion region,
                        ProductOrderStatus status) {

        this.nfcCardId = nfcCardId;
        this.quantity = quantity;
        this.comment = comment;
        this.region = region;
        this.status = status;
    }

    public ProductOrder update(ProductOrderUpdateRequest request) {
        Optional.ofNullable(request.getQuantity()).ifPresent(v -> this.quantity = v);
        Optional.ofNullable(request.getComment()).ifPresent(v -> this.comment = v);
        return this;
    }

    public ProductOrder cancelled() {
        if (this.status != ProductOrderStatus.CANCELLED) {
            this.status = ProductOrderStatus.CANCELLED;
        } else {
            throw new IllegalStateException("Cannot cancelled order: status=" + this.status);
        }
        return this;
    }

    public ProductOrder complete() {
        if (this.status == ProductOrderStatus.CREATED) {
            this.status = ProductOrderStatus.COMPLETE;
        } else {
            throw new IllegalStateException("Cannot complate order: status=" + this.status);
        }
        return this;
    }

    public ProductOrder delete() {
        this.deleted = true;
        return this;
    }
}
