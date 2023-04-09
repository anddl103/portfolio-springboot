package com.hybe.larva.dto.nfc_card;

import com.hybe.larva.dto.album.AlbumResponse;
import com.hybe.larva.dto.artist.ArtistSearchResponse;
import com.hybe.larva.dto.common.BaseResponse;
import com.hybe.larva.entity.album.AlbumCard;
import com.hybe.larva.entity.nfc_card.NfcCardAggregation;
import com.hybe.larva.enums.NfcCardStatus;
import com.hybe.larva.util.CacheUtil;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

@ToString(callSuper = true)
@Getter
public class NfcCardDetailResponse extends BaseResponse {

    private final List<String> tags;

    private final ArtistSearchResponse artist;

    private final AlbumResponse album;

    private final String comment;

    private final NfcCardStatus status;

    private List<AlbumCard> cards;

    public NfcCardDetailResponse(NfcCardAggregation nfcCardAggregation, CacheUtil cacheUtil) {
        super(nfcCardAggregation);

        this.tags = nfcCardAggregation.getTags();
        this.artist = new ArtistSearchResponse(nfcCardAggregation.getArtist());
        this.album = new AlbumResponse(nfcCardAggregation.getAlbum(), cacheUtil);
        this.comment = nfcCardAggregation.getComment();
        this.status = nfcCardAggregation.getStatus();

        if (nfcCardAggregation.getAlbum() != null && nfcCardAggregation.getAlbum().getCards().size() > 0) {

            List<String> nfcCards =  nfcCardAggregation.getCardIds();
            this.cards = nfcCardAggregation.getAlbum().getCards().stream().filter(a -> nfcCards.contains(a.getId()))
                    .collect(Collectors.toList());
        }
    }
}
