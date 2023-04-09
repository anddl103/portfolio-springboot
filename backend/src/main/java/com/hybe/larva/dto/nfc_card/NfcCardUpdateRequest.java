package com.hybe.larva.dto.nfc_card;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@ToString
@Getter
@Builder
public class NfcCardUpdateRequest {

    private List<String> tags;

    @NotBlank
    private String albumId;

    private String comment;

    @NotNull
    private List<String> cardIds;
}
