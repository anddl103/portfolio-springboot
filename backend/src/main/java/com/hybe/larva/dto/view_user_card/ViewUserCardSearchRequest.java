package com.hybe.larva.dto.view_user_card;

import com.hybe.larva.dto.common.BaseSearchOffsetRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString(callSuper = true)
@Getter
public class ViewUserCardSearchRequest extends BaseSearchOffsetRequest {


    private final String artistId;

    private final Boolean favorite;

    @Builder
    public ViewUserCardSearchRequest(LocalDateTime from, LocalDateTime to, Integer offset, Integer limit, String artistId,
                                     Boolean favorite) {
        super(from, to, offset, limit);
        this.artistId = artistId;
        this.favorite = favorite;
    }
}
