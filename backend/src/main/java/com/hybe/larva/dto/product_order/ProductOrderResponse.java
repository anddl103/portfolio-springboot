package com.hybe.larva.dto.product_order;

import com.hybe.larva.dto.common.AlbumResponse;
import com.hybe.larva.dto.common.ArtistResponse;
import com.hybe.larva.dto.common.BaseResponse;
import com.hybe.larva.dto.common.CardResponse;
import com.hybe.larva.entity.album.AlbumCard;
import com.hybe.larva.entity.product_order.ProductOrder;
import com.hybe.larva.entity.product_order.ProductOrderAggregation;
import com.hybe.larva.enums.ProductOrderStatus;
import com.hybe.larva.enums.ServiceRegion;
import com.hybe.larva.util.CacheUtil;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@ToString(callSuper = true)
@Getter
public class ProductOrderResponse extends BaseResponse {

    private final String nfcCardId;

    private final Long quantity;

    private final String comment;

    private final ServiceRegion region;

    private final ProductOrderStatus status;

    private AlbumResponse album;

    private ArtistResponse artist;

    private List<CardResponse> cards;

    private int verifiedCount;

    private int assignedCount;

    public ProductOrderResponse(ProductOrder productOrder) {
        super(productOrder);

        this.nfcCardId = productOrder.getNfcCardId();
        this.quantity = productOrder.getQuantity();
        this.comment = productOrder.getComment();
        this.region = productOrder.getRegion();
        this.status = productOrder.getStatus();

    }

    public ProductOrderResponse(ProductOrderAggregation aggregation, CacheUtil cacheUtil) {
        super(aggregation);

        this.nfcCardId = aggregation.getNfcCardId();
        this.quantity = aggregation.getQuantity();
        this.comment = aggregation.getComment();
        this.region = aggregation.getRegion();
        this.status = aggregation.getStatus();
        this.verifiedCount = aggregation.getVerifiedCount();
        this.assignedCount = aggregation.getAssignedCount();

        if (aggregation.getAlbum() != null) {

            this.album = new AlbumResponse(aggregation.getAlbum(), cacheUtil);

            if (aggregation.getNfcCard() != null && aggregation.getNfcCard().getCardIds().size() > 0 &&
                    aggregation.getAlbum() != null && aggregation.getAlbum().getCards().size() > 0) {

                List<String> nfcCards =  aggregation.getNfcCard().getCardIds();
                List<AlbumCard> albumCards = aggregation.getAlbum().getCards().stream().filter(a -> nfcCards.contains(a.getId()))
                        .collect(Collectors.toList());

                List<CardResponse> cardList = new ArrayList<>();

                for (AlbumCard card : albumCards) {
                    cardList.add(new CardResponse(card));
                }
                this.cards = cardList;
            }

        }

        if (aggregation.getArtist() != null) {
            this.artist = new ArtistResponse(aggregation.getArtist());
        }

    }
}
