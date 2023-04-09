package com.hybe.larva.dto.nfc_card;

import com.hybe.larva.dto.common.BaseSearchOffsetRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString(callSuper = true)
@Getter
public class NfcCardSearchRequest extends BaseSearchOffsetRequest {

    private final String keyword;

    private final String albumId;

    private final String artistId;

    @Builder
    public NfcCardSearchRequest(LocalDateTime from, LocalDateTime to, Integer offset, Integer limit, String keyword,
                                String albumId, String artistId) {
        super(from, to, offset, limit);
        this.keyword = keyword;
        this.albumId = albumId;
        this.artistId = artistId;
    }
}
