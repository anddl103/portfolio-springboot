package com.hybe.larva.entity.product_order;

import com.hybe.larva.entity.album.Album;
import com.hybe.larva.entity.album.AlbumCard;
import com.hybe.larva.entity.artist.Artist;
import com.hybe.larva.entity.common.BaseEntity;
import com.hybe.larva.entity.nfc_card.NfcCard;
import com.hybe.larva.enums.ProductOrderStatus;
import com.hybe.larva.enums.ServiceRegion;
import lombok.*;

import java.util.List;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductOrderAggregation extends BaseEntity {

    private String nfcCardId;

    private Long quantity;

    private String comment;

    private ServiceRegion region;

    private ProductOrderStatus status;

    private NfcCard nfcCard;

    private Album album;

    private Artist artist;

    private int verifiedCount;

    private int assignedCount;

    @Setter
    private int count;

}
