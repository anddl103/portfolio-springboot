package com.hybe.larva.dto.nfc_card;

import com.hybe.larva.dto.common.BaseResponse;
import com.hybe.larva.entity.nfc_card.NfcCard;
import com.hybe.larva.enums.NfcCardStatus;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@ToString(callSuper = true)
@Getter
public class NfcCardResponse extends BaseResponse {

    private final List<String> tags;

    private final String artistId;

    private final String albumId;

    private final String comment;

    private final List<String> cardIds;

    private final NfcCardStatus status;

    public NfcCardResponse(NfcCard nfcCard) {
        super(nfcCard);

        this.tags = nfcCard.getTags();
        this.artistId = nfcCard.getArtistId();
        this.albumId = nfcCard.getAlbumId();
        this.comment = nfcCard.getComment();
        this.cardIds = nfcCard.getCardIds();
        this.status = nfcCard.getStatus();
    }
}
