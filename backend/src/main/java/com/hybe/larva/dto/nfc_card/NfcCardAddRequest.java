package com.hybe.larva.dto.nfc_card;

import com.hybe.larva.entity.nfc_card.NfcCard;
import com.hybe.larva.enums.NfcCardStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@ToString
@Getter
@Builder
public class NfcCardAddRequest {

    private List<String> tags;

    @NotBlank
    private String albumId;

    private String comment;

    @NotNull
    private List<String> cardIds;

    public NfcCard toEntity(String artistId) {
        return NfcCard.builder()
                .tags(tags)
                .artistId(artistId)
                .albumId(albumId)
                .comment(comment)
                .cardIds(cardIds)
                .status(NfcCardStatus.BEFORE_ORDER)
                .build();
    }
}
